package com.xpto.financeiro.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.xpto.financeiro.model.enums.TipoMovimentacao;
import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Data
public class Movimentacao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoMovimentacao tipo;

    @Column(nullable = false)
    private BigDecimal valor;

    @Column(nullable = false, updatable = false)
    private LocalDateTime dataHora = LocalDateTime.now();

    @Column(nullable = false)
    private BigDecimal valorTaxaXpto = BigDecimal.ZERO;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cliente_id", nullable = false)
    @JsonBackReference("cliente-movimentacao")
    private Cliente cliente;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "conta_bancaria_id", nullable = false)
    @JsonBackReference("conta-movimentacao")
    private ContaBancaria contaBancaria;
}