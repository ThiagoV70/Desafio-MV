package com.xpto.financeiro.repository;

import com.xpto.financeiro.model.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;

@Repository
public interface ClienteRepository extends JpaRepository<Cliente, Long> {

    /**
     * Chama a função nativa do MySQL 'FN_CALCULAR_SALDO_ATUAL_CLIENTE'
     * para cumprir o requisito de integração PL/SQL.
     *
     */
    @Query(value = "SELECT FN_CALCULAR_SALDO_ATUAL_CLIENTE(:clienteId)", nativeQuery = true)
    BigDecimal getSaldoAtualCliente(@Param("clienteId") Long clienteId);
}