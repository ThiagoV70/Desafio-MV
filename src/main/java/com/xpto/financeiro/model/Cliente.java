package com.xpto.financeiro.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.xpto.financeiro.model.enums.TipoPessoa;
import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
public class Cliente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nome;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoPessoa tipoPessoa;

    @Column(nullable = false, unique = true)
    private String documento;

    private String telefone;

    @Column(nullable = false, updatable = false)
    private LocalDate dataCadastro = LocalDate.now();

    @OneToMany(mappedBy = "cliente", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JsonManagedReference
    private List<Endereco> enderecos = new ArrayList<>();

    @OneToMany(mappedBy = "cliente", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JsonManagedReference
    private List<ContaBancaria> contas = new ArrayList<>();

    public void addEndereco(Endereco endereco) {
        enderecos.add(endereco);
        endereco.setCliente(this);
    }

    public void addConta(ContaBancaria conta) {
        contas.add(conta);
        conta.setCliente(this);
    }
}