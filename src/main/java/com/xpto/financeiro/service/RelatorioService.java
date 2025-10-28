package com.xpto.financeiro.service;

import com.xpto.financeiro.dto.RelatorioReceitaXptoDTO;
import com.xpto.financeiro.dto.RelatorioReceitaXptoItemDTO;
import com.xpto.financeiro.dto.RelatorioSaldoGeralItemDTO;
import com.xpto.financeiro.model.Cliente;
import com.xpto.financeiro.repository.ClienteRepository;
import com.xpto.financeiro.repository.MovimentacaoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class RelatorioService {

    @Autowired
    private MovimentacaoRepository movimentacaoRepository;

    /**
     * Gera o Relatório de Receita da Empresa (XPTO) por período.
     *
     */
    public RelatorioReceitaXptoDTO getRelatorioReceitaXpto(LocalDate inicio, LocalDate fim) {

        LocalDateTime inicioDt = inicio.atStartOfDay();
        LocalDateTime fimDt = fim.atTime(LocalTime.MAX);

        List<RelatorioReceitaXptoItemDTO> itens =
                movimentacaoRepository.getReceitaXptoPorPeriodoAgrupado(inicioDt, fimDt);

        // Calcula o total
        BigDecimal total = itens.stream()
                .map(RelatorioReceitaXptoItemDTO::getValorDasMovimentacoes)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        RelatorioReceitaXptoDTO relatorio = new RelatorioReceitaXptoDTO();
        relatorio.setPeriodoInicio(inicio);
        relatorio.setPeriodoFim(fim);
        relatorio.setItens(itens);
        relatorio.setTotalReceitas(total);

        return relatorio;
    }

    @Autowired
    private ClienteRepository clienteRepository; // (Adicionar este Autowired)

    // (O método getRelatorioReceitaXpto que fizemos antes permanece)

    /**
     * Gera o Relatório de Saldo de Todos os Clientes.
     * [cite: 55]
     */
    public List<RelatorioSaldoGeralItemDTO> getRelatorioSaldoGeral() {
        List<Cliente> clientes = clienteRepository.findAll();
        List<RelatorioSaldoGeralItemDTO> relatorio = new ArrayList<>();
        LocalDate hoje = LocalDate.now();

        for (Cliente cliente : clientes) {
            BigDecimal saldo = clienteRepository.getSaldoAtualCliente(cliente.getId());
            // AQUI USAMOS A FUNÇÃO DO BANCO DE DADOS!
            relatorio.add(new RelatorioSaldoGeralItemDTO(
                    cliente.getNome(),
                    cliente.getDataCadastro(),
                    hoje,
                    saldo
            ));
        }
        return relatorio;
    }
}