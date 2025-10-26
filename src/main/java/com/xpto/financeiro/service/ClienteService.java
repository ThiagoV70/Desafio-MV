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
    private EnderecoRepository enderecoRepository;
    @Autowired
    private ContaBancariaRepository contaBancariaRepository;

    @Transactional
    public Cliente criarNovoCliente(ClienteRequestDTO dto) {

        Cliente cliente = new Cliente();
        cliente.setNome(dto.getNome());
        cliente.setTipoPessoa(dto.getTipoPessoa());
        cliente.setDocumento(dto.getDocumento());
        cliente.setTelefone(dto.getTelefone());

        Cliente novoCliente = clienteRepository.save(cliente);

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

        ContaBancaria conta = new ContaBancaria();
        conta.setCliente(novoCliente);
        conta.setInstituicaoFinanceira(dto.getContaBancaria().getInstituicaoFinanceira());
        conta.setAgencia(dto.getContaBancaria().getAgencia());
        conta.setNumeroConta(dto.getContaBancaria().getNumeroConta());
        conta.setAtivo(true);
        ContaBancaria novaConta = contaBancariaRepository.save(conta);

        Movimentacao inicial = new Movimentacao();
        inicial.setCliente(novoCliente);
        inicial.setContaBancaria(novaConta);
        inicial.setValor(dto.getSaldoInicial());
        inicial.setTipo(TipoMovimentacao.CREDITO);

        inicial.setValorTaxaXpto(BigDecimal.ZERO);

        movimentacaoRepository.save(inicial);

        return novoCliente;
    }

    public RelatorioSaldoClienteDTO getRelatorioSaldoCliente(Long clienteId) {
        Cliente cliente = clienteRepository.findById(clienteId)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente não encontrado: " + clienteId));

        Endereco endereco = enderecoRepository.findByClienteId(clienteId)
                .stream()
                .findFirst()
                .orElse(new Endereco()); // Retorna um endereço vazio se não houver

        List<Movimentacao> movimentacoes = movimentacaoRepository.findByClienteId(clienteId);


        long movCredito = 0;
        long movDebito = 0;
        BigDecimal saldoInicial = BigDecimal.ZERO;
        BigDecimal totalCredito = BigDecimal.ZERO;
        BigDecimal totalDebito = BigDecimal.ZERO;
        BigDecimal totalTaxasPagas = BigDecimal.ZERO;

        for (Movimentacao mov : movimentacoes) {
            totalTaxasPagas = totalTaxasPagas.add(mov.getValorTaxaXpto());

            if (mov.getDataHora().toLocalDate().equals(cliente.getDataCadastro())) {
                saldoInicial = saldoInicial.add(mov.getValor());
            }

            if (mov.getTipo() == TipoMovimentacao.CREDITO) {
                movCredito++;
                totalCredito = totalCredito.add(mov.getValor());
            } else {
                movDebito++;
                totalDebito = totalDebito.add(mov.getValor());
            }
        }

        BigDecimal saldoAtual = totalCredito.subtract(totalDebito);

        RelatorioSaldoClienteDTO relatorio = new RelatorioSaldoClienteDTO();
        relatorio.setClienteNome(cliente.getNome());
        relatorio.setClienteDesde(cliente.getDataCadastro());

        String endFormatado = String.format("%s, %s, %s - %s, %s - %s, CEP: %s",
                endereco.getRua(),
                endereco.getNumero(),
                endereco.getComplemento() != null ? endereco.getComplemento() : "",
                endereco.getBairro(),
                endereco.getCidade(),
                endereco.getUf(),
                endereco.getCep());
        relatorio.setEnderecoFormatado(endFormatado);

        relatorio.setMovimentacoesCredito(movCredito);
        relatorio.setMovimentacoesDebito(movDebito);
        relatorio.setTotalMovimentacoes(movCredito + movDebito);
        relatorio.setValorPagoMovimentacoes(totalTaxasPagas);
        relatorio.setSaldoInicial(saldoInicial);
        relatorio.setSaldoAtual(saldoAtual);

        return relatorio;
    }

    public Cliente buscarClientePorId(Long clienteId) {
        return clienteRepository.findById(clienteId)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente não encontrado com id: " + clienteId));
    }

    public RelatorioSaldoClienteDTO getRelatorioSaldoCliente(Long clienteId, LocalDate inicio, LocalDate fim) {
        Cliente cliente = clienteRepository.findById(clienteId)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente não encontrado: " + clienteId));

        Endereco endereco = enderecoRepository.findByClienteId(clienteId)
                .stream()
                .findFirst()
                .orElse(new Endereco());

        LocalDateTime inicioDt = inicio.atStartOfDay();
        LocalDateTime fimDt = fim.atTime(LocalTime.MAX);
        List<Movimentacao> movimentacoes =
                movimentacaoRepository.findByClienteIdAndDataHoraBetween(clienteId, inicioDt, fimDt);

        long movCredito = 0;
        long movDebito = 0;
        BigDecimal saldoInicialPeriodo = BigDecimal.ZERO;
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

        BigDecimal saldoAtual = clienteRepository.getSaldoAtualCliente(clienteId);

        RelatorioSaldoClienteDTO relatorio = new RelatorioSaldoClienteDTO();
        relatorio.setClienteNome(cliente.getNome());

        relatorio.setMovimentacoesCredito(movCredito);
        relatorio.setMovimentacoesDebito(movDebito);
        relatorio.setTotalMovimentacoes(movCredito + movDebito);
        relatorio.setValorPagoMovimentacoes(totalTaxasPagas);
        relatorio.setSaldoInicial(saldoInicialPeriodo);
        relatorio.setSaldoAtual(saldoAtual);

        return relatorio;
    }
}