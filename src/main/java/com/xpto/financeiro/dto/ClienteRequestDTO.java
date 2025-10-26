package com.xpto.financeiro.dto;

import com.xpto.financeiro.model.enums.TipoPessoa;
import lombok.Data;
import java.math.BigDecimal;

@Data
public class ClienteRequestDTO {

    private String nome;
    private TipoPessoa tipoPessoa;
    private String documento;
    private String telefone;

    private EnderecoRequestDTO endereco;

    private ContaBancariaRequestDTO contaBancaria;

    private BigDecimal saldoInicial;
}