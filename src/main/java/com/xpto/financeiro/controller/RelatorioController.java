package com.xpto.financeiro.controller;

import com.xpto.financeiro.dto.RelatorioReceitaXptoDTO;
import com.xpto.financeiro.dto.RelatorioSaldoClienteDTO;
import com.xpto.financeiro.dto.RelatorioSaldoGeralItemDTO;
import com.xpto.financeiro.service.ClienteService;
import com.xpto.financeiro.service.RelatorioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/relatorios")
public class RelatorioController {

    @Autowired
    private RelatorioService relatorioService; // (Usado para o relatório por período)

    @Autowired
    private ClienteService clienteService;

    /**
     * Relatório de receita da empresa (XPTO) por período.
     *
     */
    @GetMapping("/receita-xpto")
    public ResponseEntity<RelatorioReceitaXptoDTO> getReceitaXpto(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate inicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fim) {

        return ResponseEntity.ok(relatorioService.getRelatorioReceitaXpto(inicio, fim));
    }

    /**
     * Relatório de saldo do cliente X e período.
     *
     */
    @GetMapping("/saldo/clientes/{clienteId}/periodo")
    public ResponseEntity<RelatorioSaldoClienteDTO> getSaldoClientePorPeriodo(
            @PathVariable Long clienteId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate inicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fim) {

        return ResponseEntity.ok(clienteService.getRelatorioSaldoCliente(clienteId, inicio, fim));
    }

    /**
     * Relatório de saldo de todos os clientes.
     * [cite: 55]
     */
    @GetMapping("/saldo/clientes/todos")
    public ResponseEntity<List<RelatorioSaldoGeralItemDTO>> getSaldoGeral() {
        return ResponseEntity.ok(relatorioService.getRelatorioSaldoGeral());
    }
}