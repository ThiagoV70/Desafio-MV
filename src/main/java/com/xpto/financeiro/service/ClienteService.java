package com.xpto.financeiro.service;

import com.xpto.financeiro.dto.ClienteRequestDTO;
import com.xpto.financeiro.dto.RelatorioSaldoClienteDTO; // (Deve ser criado)
import com.xpto.financeiro.exception.ResourceNotFoundException;
import com.xpto.financeiro.model.Cliente;
import com.xpto.financeiro.model.Movimentacao;
import com.xpto.financeiro.model.enums.TipoMovimentacao;
import com.xpto.financeiro.repository.ClienteRepository;
import com.xpto.financeiro.repository.MovimentacaoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
public class ClienteService {

    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private MovimentacaoRepository movimentacaoRepository;

    @Transactional
    public Cliente criarNovoCliente(Cliente cliente, BigDecimal saldoInicial) {

        Cliente novoCliente = clienteRepository.save(cliente);

        Movimentacao inicial = new Movimentacao();
        inicial.setCliente(novoCliente);
        inicial.setValor(saldoInicial);
        inicial.setTipo(TipoMovimentacao.CREDITO);

        inicial.setValorTaxaXpto(BigDecimal.ZERO);


        return novoCliente;
    }

    public RelatorioSaldoClienteDTO getRelatorioSaldoCliente(Long clienteId) {
        Cliente cliente = clienteRepository.findById(clienteId)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente não encontrado: " + clienteId));

        List<Movimentacao> movimentacoes = movimentacaoRepository.findByClienteId(clienteId);

        long movCredito = 0;
        long movDebito = 0;
        BigDecimal saldoInicial = BigDecimal.ZERO;
        BigDecimal totalCredito = BigDecimal.ZERO;
        BigDecimal totalDebito = BigDecimal.ZERO;
        BigDecimal totalTaxasPagas = BigDecimal.ZERO;

        for (Movimentacao mov : movimentacoes) {
            // Soma o valor pago pelo cliente à XPTO [cite: 42]
            totalTaxasPagas = totalTaxasPagas.add(mov.getValorTaxaXpto());

            // Verifica se é a movimentação inicial [cite: 7, 43]
            if (mov.getDataHora().toLocalDate().equals(cliente.getDataCadastro())) {
                // (Assumindo que a primeira movimentação é o saldo inicial)
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

        BigDecimal saldoAtual = totalCredito.subtract(totalDebito); // [cite: 44]

        // 3. Montar o DTO de Resposta (RelatorioSaldoClienteDTO)
        RelatorioSaldoClienteDTO relatorio = new RelatorioSaldoClienteDTO();
        relatorio.setClienteNome(cliente.getNome());
        relatorio.setClienteDesde(cliente.getDataCadastro());
        // relatorio.setEndereco(...) // (Buscar o endereço principal) [cite: 39]
        relatorio.setMovimentacoesCredito(movCredito);
        relatorio.setMovimentacoesDebito(movDebito);
        relatorio.setTotalMovimentacoes(movCredito + movDebito); // [cite: 41]
        relatorio.setValorPagoMovimentacoes(totalTaxasPagas);
        relatorio.setSaldoInicial(saldoInicial);
        relatorio.setSaldoAtual(saldoAtual);

        return relatorio;
    }
}