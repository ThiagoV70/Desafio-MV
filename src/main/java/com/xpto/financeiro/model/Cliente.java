package com.xpto.financeiro.model;

import com.xpto.financeiro.model.enums.TipoPessoa;
import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;

@Entity
@Data
public class Cliente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nome;

    @Enumerated(EnumType.STRING)
    private TipoPessoa tipoPessoa;

    private String CPF;

    private String CNPJ;

    private String telefone;

    private LocalDate dataCadastro = LocalDate.now();
}