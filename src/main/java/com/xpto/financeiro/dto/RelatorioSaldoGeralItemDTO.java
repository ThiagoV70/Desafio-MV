package com.xpto.financeiro.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RelatorioSaldoGeralItemDTO {

    private String nomeCliente;
    private LocalDate clienteDesde;
    private LocalDate dataSaldo;
    private BigDecimal saldo;
}