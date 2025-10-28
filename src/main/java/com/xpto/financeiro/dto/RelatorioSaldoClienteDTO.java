package com.xpto.financeiro.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * DTO para formatar a resposta do Relatório de Saldo do Cliente[cite: 37].
 */
@Data
public class RelatorioSaldoClienteDTO {

    // Cliente: X - Cliente desde: DD/MM/YYYY [cite: 38]
    private String clienteNome;
    private LocalDate clienteDesde;

    // Endereço: Rua, número, complemento, bairro, cidade, UF, CEP [cite: 39]
    private String enderecoFormatado;

    // Movimentações de crédito: 00 [cite: 39]
    private long movimentacoesCredito;

    // Movimentações de débito: 0 [cite: 40]
    private long movimentacoesDebito;

    // Total de movimentações: 00 [cite: 41]
    private long totalMovimentacoes;

    // Valor pago pelas movimentações: 00,00 [cite: 42]
    private BigDecimal valorPagoMovimentacoes;

    // Saldo inicial: 0.000.00 [cite: 43]
    private BigDecimal saldoInicial;

    // Saldo atual: 00.000,00 [cite: 44]
    private BigDecimal saldoAtual;
}