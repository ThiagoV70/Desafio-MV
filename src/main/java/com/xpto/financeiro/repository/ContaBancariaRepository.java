package com.xpto.financeiro.repository;

import com.xpto.financeiro.model.ContaBancaria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ContaBancariaRepository extends JpaRepository<ContaBancaria, Long> {
    // Busca apenas contas ativas
    List<ContaBancaria> findByClienteIdAndAtivoTrue(Long clienteId);
}