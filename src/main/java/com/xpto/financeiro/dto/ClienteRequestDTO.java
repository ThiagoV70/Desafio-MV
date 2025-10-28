package com.xpto.financeiro.dto;

import com.xpto.financeiro.model.enums.TipoPessoa;
import lombok.Data;
import java.math.BigDecimal;

/**
 * DTO para o request de criação de um novo Cliente.
 * Agrega os dados do cliente e a movimentação inicial obrigatória.
 */
@Data
public class ClienteRequestDTO {

    // --- Dados do Cliente ---
    private String nome;
    private TipoPessoa tipoPessoa;
    private String documento;
    private String telefone;

    // --- Dados do Endereço Inicial ---
    // (Usando o DTO que criei anteriormente)
    private EnderecoRequestDTO endereco;

    // --- Dados da Conta Inicial ---
    // (Usando o DTO que criei anteriormente)
    private ContaBancariaRequestDTO contaBancaria;

    // --- Dados da Movimentação Inicial ---
    private BigDecimal saldoInicial;
}