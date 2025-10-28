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
@RequestMapping("/api") // Movendo o /api para o nível da classe
public class ClienteController {

    @Autowired
    private ClienteService clienteService;

    /**
     * Endpoint CORRIGIDO para criar um novo cliente com sua movimentação inicial.
     * POST /api/clientes
     */
    @PostMapping("/clientes")
    public ResponseEntity<Cliente> criarCliente(@RequestBody ClienteRequestDTO dto) {
        // Validação básica
        if (dto == null || dto.getNome() == null || dto.getSaldoInicial() == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        try {
            // Agora chamamos o serviço com o DTO único
            Cliente novoCliente = clienteService.criarNovoCliente(dto);
            return new ResponseEntity<>(novoCliente, HttpStatus.CREATED);
        } catch (Exception e) {
            // Tratamento de exceções
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Endpoint para buscar um cliente pelo ID.
     * GET /api/clientes/{id}
     */
    @GetMapping("/clientes/{id}")
    public ResponseEntity<Cliente> getClientePorId(@PathVariable Long id) {
        Cliente cliente = clienteService.buscarClientePorId(id);
        return ResponseEntity.ok(cliente);
    }
    // (Outros endpoints de Cliente, como PUT e DELETE, seriam adicionados aqui)

    /**
     * Endpoint para o Relatório de Saldo do Cliente X (Requisito do PDF)
     * GET /api/relatorios/saldo/clientes/{clienteId}
     */
    @GetMapping("/relatorios/saldo/clientes/{clienteId}")
    public ResponseEntity<RelatorioSaldoClienteDTO> getRelatorioSaldo(
            @PathVariable Long clienteId) {

        RelatorioSaldoClienteDTO relatorio = clienteService.getRelatorioSaldoCliente(clienteId);
        return ResponseEntity.ok(relatorio);
    }
}