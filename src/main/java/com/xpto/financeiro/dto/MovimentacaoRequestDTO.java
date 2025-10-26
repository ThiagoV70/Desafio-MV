package com.xpto.financeiro.dto;

import com.xpto.financeiro.model.enums.TipoMovimentacao;
import lombok.Data;
import java.math.BigDecimal;

// DTO para simular a integração recebendo uma nova movimentação
@Data
public class MovimentacaoRequestDTO {
    private Long clienteId;       // [cite: 22]
    private Long contaBancariaId; //
    private TipoMovimentacao tipo;
    private BigDecimal valor;
}