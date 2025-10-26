package com.xpto.financeiro.controller;

import com.xpto.financeiro.dto.ClienteRequestDTO;
import com.xpto.financeiro.dto.RelatorioSaldoClienteDTO;
import com.xpto.financeiro.model.Cliente;
import com.xpto.financeiro.service.ClienteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class ClienteController {

    @Autowired
    private ClienteService clienteService;

    @PostMapping("/clientes")
    public ResponseEntity<Cliente> criarCliente(@RequestBody ClienteRequestDTO dto) {
        if (dto == null || dto.getNome() == null || dto.getSaldoInicial() == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        try {
            Cliente novoCliente = clienteService.criarNovoCliente(dto);
            return new ResponseEntity<>(novoCliente, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/clientes/{id}")
    public ResponseEntity<Cliente> getClientePorId(@PathVariable Long id) {
        Cliente cliente = clienteService.buscarClientePorId(id);
        return ResponseEntity.ok(cliente);
    }

    @GetMapping("/relatorios/saldo/clientes/{clienteId}")
    public ResponseEntity<RelatorioSaldoClienteDTO> getRelatorioSaldo(
            @PathVariable Long clienteId) {

        RelatorioSaldoClienteDTO relatorio = clienteService.getRelatorioSaldoCliente(clienteId);
        return ResponseEntity.ok(relatorio);
    }
}