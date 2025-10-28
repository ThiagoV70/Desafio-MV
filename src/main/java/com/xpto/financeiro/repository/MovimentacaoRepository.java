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

    /**
     * Verifica se existe alguma movimentação associada a uma conta bancária.
     * Usado na regra de negócio para não permitir alteração de conta.
     */
    boolean existsByContaBancariaId(Long contaBancariaId);

    /**
     * Conta o número de movimentações de um cliente dentro de um período específico.
     * Usado para calcular a faixa da taxa XPTO.
     */
    long countByClienteIdAndDataHoraBetween(Long clienteId, LocalDateTime inicio, LocalDateTime fim);

    /**
     * Busca todas as movimentações de um cliente (para o relatório de saldo total).
     */
    List<Movimentacao> findByClienteId(Long clienteId);

    /**
     * Busca movimentações de um cliente dentro de um período (para o relatório por período).
     */
    List<Movimentacao> findByClienteIdAndDataHoraBetween(Long clienteId, LocalDateTime inicio, LocalDateTime fim);

    //Busca dados agrupados para o Relatório de Receita da XPTO.
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