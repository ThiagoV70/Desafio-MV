package com.xpto.financeiro.repository;

import com.xpto.financeiro.dto.RelatorioReceitaXptoItemDTO;
import com.xpto.financeiro.model.Movimentacao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface MovimentacaoRepository extends JpaRepository<Movimentacao, Long> {

    boolean existsByContaBancariaId(Long contaBancariaId);

    long countByClienteIdAndDataHoraBetween(Long clienteId, LocalDateTime inicio, LocalDateTime fim);

    List<Movimentacao> findByClienteId(Long clienteId);

    List<Movimentacao> findByClienteIdAndDataHoraBetween(Long clienteId, LocalDateTime inicio, LocalDateTime fim);

    // Dentro de com.xpto.financeiro.repository.MovimentacaoRepository

    @Query("SELECT new com.xpto.financeiro.dto.RelatorioReceitaXptoItemDTO(" +
            "m.cliente.nome, " +
            "COUNT(m.id), " +
            "SUM(m.valorTaxaXpto)) " +
            "FROM Movimentacao m " +
            "WHERE m.dataHora BETWEEN :inicio AND :fim " +
            "GROUP BY m.cliente.nome")
    List<RelatorioReceitaXptoItemDTO> getReceitaXptoPorPeriodoAgrupado(
            @Param("inicio") LocalDateTime inicio,
            @Param("fim") LocalDateTime fim);
}