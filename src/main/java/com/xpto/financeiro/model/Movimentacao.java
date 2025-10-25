package com.xpto.financeiro.model;

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

    @ManyToOne
    @JoinColumn(name = "cliente_id", nullable = false)
    private Cliente cliente;

    // Simula a conta bancária de origem/destino [cite: 6]
    private String contaBancariaId;

    @Enumerated(EnumType.STRING)
    private TipoMovimentacao tipo;

    private BigDecimal valor;

    private LocalDateTime dataHora = LocalDateTime.now();

    // Valor do cálculo da receita da XPTO referente a esta operação [cite: 9]
    private BigDecimal valorTaxaXpto;
}