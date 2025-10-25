package com.xpto.financeiro.repository;

import com.xpto.financeiro.model.Movimentacao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
public interface MovimentacaoRepository extends JpaRepository<Movimentacao, Long> {
    long countByClienteIdAndDataHoraBetween(Long clienteId, LocalDateTime inicio, LocalDateTime fim);
}