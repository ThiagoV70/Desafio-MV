package com.xpto.financeiro.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
public class RelatorioReceitaXptoDTO {

    private LocalDate periodoInicio;
    private LocalDate periodoFim;
    private List<RelatorioReceitaXptoItemDTO> itens;
    private BigDecimal totalReceitas;
}