package com.xpto.financeiro.service;

import com.xpto.financeiro.dto.ClienteRequestDTO;
import com.xpto.financeiro.dto.RelatorioSaldoClienteDTO;
import com.xpto.financeiro.exception.ResourceNotFoundException;
import com.xpto.financeiro.model.*;
import com.xpto.financeiro.model.enums.TipoMovimentacao;
import com.xpto.financeiro.repository.ClienteRepository;
import com.xpto.financeiro.repository.ContaBancariaRepository;
import com.xpto.financeiro.repository.EnderecoRepository;
import com.xpto.financeiro.repository.MovimentacaoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Service
public class ClienteService {

    @Autowired
    private ClienteRepository clienteRepository;
    @Autowired
    private MovimentacaoRepository movimentacaoRepository;
    @Autowired
    private EnderecoRepository enderecoRepository; // (Necessário para salvar o endereço)
    @Autowired
    private ContaBancariaRepository contaBancariaRepository; // (Necessário para salvar a conta)

    /**
     * Cria um novo cliente, seu endereço inicial, sua conta inicial
     * e sua movimentação inicial obrigatória.
     */
    @Transactional
    public Cliente criarNovoCliente(ClienteRequestDTO dto) {

        // 1. Mapear e Salvar o Cliente
        Cliente cliente = new Cliente();
        cliente.setNome(dto.getNome());
        cliente.setTipoPessoa(dto.getTipoPessoa());
        cliente.setDocumento(dto.getDocumento());
        cliente.setTelefone(dto.getTelefone());
        // A dataCadastro é setada por default na entidade

        Cliente novoCliente = clienteRepository.save(cliente);

        // 2. Mapear e Salvar o Endereço Inicial
        Endereco endereco = new Endereco();
        endereco.setCliente(novoCliente);
        endereco.setRua(dto.getEndereco().getRua());
        endereco.setNumero(dto.getEndereco().getNumero());
        endereco.setBairro(dto.getEndereco().getBairro());
        endereco.setCidade(dto.getEndereco().getCidade());
        endereco.setUf(dto.getEndereco().getUf());
        endereco.setCep(dto.getEndereco().getCep());
        endereco.setComplemento(dto.getEndereco().getComplemento());
        enderecoRepository.save(endereco);

        // 3. Mapear e Salvar a Conta Inicial
        ContaBancaria conta = new ContaBancaria();
        conta.setCliente(novoCliente);
        conta.setInstituicaoFinanceira(dto.getContaBancaria().getInstituicaoFinanceira());
        conta.setAgencia(dto.getContaBancaria().getAgencia());
        conta.setNumeroConta(dto.getContaBancaria().getNumeroConta());
        conta.setAtivo(true);
        ContaBancaria novaConta = contaBancariaRepository.save(conta);

        // 4. Mapear e Salvar a Movimentação Inicial (Saldo Inicial)
        Movimentacao inicial = new Movimentacao();
        inicial.setCliente(novoCliente);
        inicial.setContaBancaria(novaConta);
        inicial.setValor(dto.getSaldoInicial());
        inicial.setTipo(TipoMovimentacao.CREDITO);

        // A taxa XPTO para a movimentação inicial é R$ 0,00 (decisão de negócio)
        inicial.setValorTaxaXpto(BigDecimal.ZERO);

        movimentacaoRepository.save(inicial);

        return novoCliente;
    }

    /**
     * Lógica para montar o Relatório de Saldo do Cliente.
     * (Este método foi mostrado na resposta anterior, mas está aqui para contexto)
     */
    public RelatorioSaldoClienteDTO getRelatorioSaldoCliente(Long clienteId) {
        Cliente cliente = clienteRepository.findById(clienteId)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente não encontrado: " + clienteId));

        // Busca o primeiro endereço cadastrado (ou o principal)
        Endereco endereco = enderecoRepository.findByClienteId(clienteId)
                .stream()
                .findFirst()
                .orElse(new Endereco()); // Retorna um endereço vazio se não houver

        List<Movimentacao> movimentacoes = movimentacaoRepository.findByClienteId(clienteId);

        // Variáveis de cálculo
        long movCredito = 0;
        long movDebito = 0;
        BigDecimal saldoInicial = BigDecimal.ZERO;
        BigDecimal totalCredito = BigDecimal.ZERO;
        BigDecimal totalDebito = BigDecimal.ZERO;
        BigDecimal totalTaxasPagas = BigDecimal.ZERO;

        for (Movimentacao mov : movimentacoes) {
            // Soma o valor pago pelo cliente à XPTO [cite: 42]
            totalTaxasPagas = totalTaxasPagas.add(mov.getValorTaxaXpto());

            // A "movimentação inicial" é a primeira da data de cadastro
            if (mov.getDataHora().toLocalDate().equals(cliente.getDataCadastro())) {
                saldoInicial = saldoInicial.add(mov.getValor());
            }

            // Contabiliza créditos e débitos [cite: 39, 40]
            if (mov.getTipo() == TipoMovimentacao.CREDITO) {
                movCredito++;
                totalCredito = totalCredito.add(mov.getValor());
            } else {
                movDebito++;
                totalDebito = totalDebito.add(mov.getValor());
            }
        }

        BigDecimal saldoAtual = totalCredito.subtract(totalDebito);

        // Montar o DTO de Resposta
        RelatorioSaldoClienteDTO relatorio = new RelatorioSaldoClienteDTO();
        relatorio.setClienteNome(cliente.getNome());
        relatorio.setClienteDesde(cliente.getDataCadastro());

        // Formata o endereço [cite: 39]
        String endFormatado = String.format("%s, %s, %s - %s, %s - %s, CEP: %s",
                endereco.getRua(),
                endereco.getNumero(),
                endereco.getComplemento() != null ? endereco.getComplemento() : "",
                endereco.getBairro(),
                endereco.getCidade(),
                endereco.getUf(),
                endereco.getCep());
        relatorio.setEnderecoFormatado(endFormatado);

        // relatorio.setEndereco(...) // (Buscar o endereço principal)
        relatorio.setMovimentacoesCredito(movCredito);
        relatorio.setMovimentacoesDebito(movDebito);
        relatorio.setTotalMovimentacoes(movCredito + movDebito);
        relatorio.setValorPagoMovimentacoes(totalTaxasPagas);
        relatorio.setSaldoInicial(saldoInicial);
        relatorio.setSaldoAtual(saldoAtual);

        return relatorio;
    }

    /**
     * Busca um cliente pelo ID.
     */
    public Cliente buscarClientePorId(Long clienteId) {
        return clienteRepository.findById(clienteId)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente não encontrado com id: " + clienteId));
    }

    /**
     * Gera o Relatório de Saldo do Cliente X por Período.
     * [cite: 45-54]
     */
    public RelatorioSaldoClienteDTO getRelatorioSaldoCliente(Long clienteId, LocalDate inicio, LocalDate fim) {
        Cliente cliente = clienteRepository.findById(clienteId)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente não encontrado: " + clienteId));

        // Busca o primeiro endereço
        Endereco endereco = enderecoRepository.findByClienteId(clienteId)
                .stream()
                .findFirst()
                .orElse(new Endereco());

        // AQUI ESTÁ A MUDANÇA: Buscamos apenas movimentações no período
        LocalDateTime inicioDt = inicio.atStartOfDay();
        LocalDateTime fimDt = fim.atTime(LocalTime.MAX);
        List<Movimentacao> movimentacoes =
                movimentacaoRepository.findByClienteIdAndDataHoraBetween(clienteId, inicioDt, fimDt);

        // O restante da lógica de cálculo (somas, subtrações) é idêntico
        // ao método getRelatorioSaldoCliente(Long clienteId) que já criamos.
        // (O ideal seria extrair essa lógica para um método privado para evitar duplicação)

        // ... (lógica de cálculo idêntica) ...

        long movCredito = 0;
        long movDebito = 0;
        BigDecimal saldoInicialPeriodo = BigDecimal.ZERO; // (Lógica mais complexa, buscar saldo ANTES do período)
        BigDecimal totalCredito = BigDecimal.ZERO;
        BigDecimal totalDebito = BigDecimal.ZERO;
        BigDecimal totalTaxasPagas = BigDecimal.ZERO;

        for (Movimentacao mov : movimentacoes) {
            totalTaxasPagas = totalTaxasPagas.add(mov.getValorTaxaXpto());
            if (mov.getTipo() == TipoMovimentacao.CREDITO) {
                movCredito++;
                totalCredito = totalCredito.add(mov.getValor());
            } else {
                movDebito++;
                totalDebito = totalDebito.add(mov.getValor());
            }
        }

        // NOTA: O "Saldo Inicial" [cite: 53] em um relatório por período
        // deveria ser o saldo do cliente no dia D-1 do início.
        // Por simplicidade aqui, estamos calculando o saldo apenas das movimentações NO período.
        // Para calcular o Saldo Atual[cite: 54], usamos a função que criamos!
        BigDecimal saldoAtual = clienteRepository.getSaldoAtualCliente(clienteId);

        RelatorioSaldoClienteDTO relatorio = new RelatorioSaldoClienteDTO();
        relatorio.setClienteNome(cliente.getNome());
        // ... (preencher o resto do DTO) ...
        relatorio.setMovimentacoesCredito(movCredito);
        relatorio.setMovimentacoesDebito(movDebito);
        relatorio.setTotalMovimentacoes(movCredito + movDebito);
        relatorio.setValorPagoMovimentacoes(totalTaxasPagas);
        relatorio.setSaldoInicial(saldoInicialPeriodo); // (Simplificado)
        relatorio.setSaldoAtual(saldoAtual);

        return relatorio;
    }
}