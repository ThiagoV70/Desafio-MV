package com.xpto.financeiro.service;

import com.xpto.financeiro.dto.RelatorioReceitaXptoDTO;
import com.xpto.financeiro.dto.RelatorioReceitaXptoItemDTO;
import com.xpto.financeiro.repository.MovimentacaoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Service
public class RelatorioService {

    @Autowired
    private MovimentacaoRepository movimentacaoRepository;

    public RelatorioReceitaXptoDTO getRelatorioReceitaXpto(LocalDate inicio, LocalDate fim) {

        LocalDateTime inicioDt = inicio.atStartOfDay();
        LocalDateTime fimDt = fim.atTime(LocalTime.MAX);

        List<RelatorioReceitaXptoItemDTO> itens =
                movimentacaoRepository.getReceitaXptoPorPeriodoAgrupado(inicioDt, fimDt);

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

}