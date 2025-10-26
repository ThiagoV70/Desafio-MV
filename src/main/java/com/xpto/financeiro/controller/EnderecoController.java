package com.xpto.financeiro.controller;

import com.xpto.financeiro.dto.EnderecoRequestDTO;
import com.xpto.financeiro.model.Endereco;
import com.xpto.financeiro.service.EnderecoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class EnderecoController {

    @Autowired
    private EnderecoService enderecoService;


    @PostMapping("/clientes/{clienteId}/enderecos")
    public ResponseEntity<Endereco> adicionarEndereco(
            @PathVariable Long clienteId,
            @RequestBody EnderecoRequestDTO dto) {
        Endereco novoEndereco = enderecoService.adicionarEndereco(clienteId, dto);
        return new ResponseEntity<>(novoEndereco, HttpStatus.CREATED);
    }

    @GetMapping("/clientes/{clienteId}/enderecos")
    public ResponseEntity<List<Endereco>> getEnderecosDoCliente(@PathVariable Long clienteId) {
        List<Endereco> enderecos = enderecoService.getEnderecosByCliente(clienteId);
        return ResponseEntity.ok(enderecos);
    }

    @PutMapping("/enderecos/{enderecoId}")
    public ResponseEntity<Endereco> atualizarEndereco(
            @PathVariable Long enderecoId,
            @RequestBody EnderecoRequestDTO dto) {
        Endereco enderecoAtualizado = enderecoService.atualizarEndereco(enderecoId, dto);
        return ResponseEntity.ok(enderecoAtualizado);
    }
}