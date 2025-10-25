package com.xpto.financeiro.controller;

import com.xpto.financeiro.model.Cliente;
import com.xpto.financeiro.service.ClienteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api/clientes")
public class ClienteController {

    @Autowired
    private ClienteService clienteService;

    public static class ClienteRequest {
        public Cliente cliente;
        public BigDecimal saldoInicial;
    }

    @PostMapping
    public ResponseEntity<Cliente> criarCliente(@RequestBody ClienteRequest request) {
        if (request.cliente == null || request.saldoInicial == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        try {
            Cliente novoCliente = clienteService.criarNovoCliente(
                    request.cliente,
                    request.saldoInicial
            );
            return new ResponseEntity<>(novoCliente, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}