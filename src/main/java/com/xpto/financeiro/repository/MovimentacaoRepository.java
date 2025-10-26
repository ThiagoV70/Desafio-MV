package com.xpto.financeiro.repository;

import com.xpto.financeiro.model.Movimentacao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface MovimentacaoRepository extends JpaRepository<Movimentacao, Long> {

    boolean existsByContaBancariaId(Long contaBancariaId);

    long countByClienteIdAndDataHoraBetween(Long clienteId, LocalDateTime inicio, LocalDateTime fim);

    List<Movimentacao> findByClienteId(Long clienteId);

    List<Movimentacao> findByClienteIdAndDataHoraBetween(Long clienteId, LocalDateTime inicio, LocalDateTime fim);
}