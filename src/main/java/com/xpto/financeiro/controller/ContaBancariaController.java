package com.xpto.financeiro.controller;

import com.xpto.financeiro.dto.ContaBancariaRequestDTO;
import com.xpto.financeiro.model.ContaBancaria;
import com.xpto.financeiro.service.ContaBancariaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class ContaBancariaController {

    @Autowired
    private ContaBancariaService contaService;

    // CRUD de Contas

    @PostMapping("/clientes/{clienteId}/contas")
    public ResponseEntity<ContaBancaria> adicionarConta(
            @PathVariable Long clienteId,
            @RequestBody ContaBancariaRequestDTO dto) {
        ContaBancaria novaConta = contaService.adicionarConta(clienteId, dto);
        return new ResponseEntity<>(novaConta, HttpStatus.CREATED);
    }

    @GetMapping("/clientes/{clienteId}/contas")
    public ResponseEntity<List<ContaBancaria>> getContasDoCliente(@PathVariable Long clienteId) {
        List<ContaBancaria> contas = contaService.getContasByCliente(clienteId);
        return ResponseEntity.ok(contas);
    }

    @PutMapping("/contas/{contaId}")
    public ResponseEntity<ContaBancaria> atualizarConta(
            @PathVariable Long contaId,
            @RequestBody ContaBancariaRequestDTO dto) {
        // A lógica de negócio  está dentro do serviço
        ContaBancaria contaAtualizada = contaService.atualizarConta(contaId, dto);
        return ResponseEntity.ok(contaAtualizada);
    }

    @DeleteMapping("/contas/{contaId}")
    public ResponseEntity<Void> deletarConta(@PathVariable Long contaId) {
        // Exclusão Lógica
        contaService.deletarContaLogicamente(contaId);
        return ResponseEntity.noContent().build();
    }
}