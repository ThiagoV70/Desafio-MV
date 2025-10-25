package com.xpto.financeiro.dto;

import lombok.Data;

@Data
public class EnderecoRequestDTO {
    private String rua;
    private String numero;
    private String complemento;
    private String bairro;
    private String cidade;
    private String uf;
    private String cep;
}