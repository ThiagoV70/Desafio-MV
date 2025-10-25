package com.xpto.financeiro.dto;

import lombok.Data;

@Data
public class ContaBancariaRequestDTO {
    private String instituicaoFinanceira;
    private String agencia;
    private String numeroConta;
}