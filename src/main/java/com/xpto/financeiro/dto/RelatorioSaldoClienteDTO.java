package com.xpto.financeiro.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;


@Data
public class RelatorioSaldoClienteDTO {

    private String clienteNome;
    private LocalDate clienteDesde;

    private String enderecoFormatado;

    private long movimentacoesCredito;

    private long movimentacoesDebito;

    private long totalMovimentacoes;

    private BigDecimal valorPagoMovimentacoes;

    private BigDecimal saldoInicial;

    private BigDecimal saldoAtual;
}