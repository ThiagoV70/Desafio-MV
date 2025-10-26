package com.xpto.financeiro.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RelatorioReceitaXptoItemDTO {

    private String nomeCliente;
    private long quantidadeMovimentacoes;
    private BigDecimal valorDasMovimentacoes;
}