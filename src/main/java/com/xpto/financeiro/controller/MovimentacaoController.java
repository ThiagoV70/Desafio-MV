package com.xpto.financeiro.controller;

import com.xpto.financeiro.dto.MovimentacaoRequestDTO;
import com.xpto.financeiro.model.Movimentacao;
import com.xpto.financeiro.service.MovimentacaoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

//Controller para simular o recebimento de movimentações (integração).
@RestController
@RequestMapping("/api/movimentacoes")
public class MovimentacaoController {

    @Autowired
    private MovimentacaoService movimentacaoService;

    /**
     * Endpoint para registrar uma nova movimentação (simulação de integração).
     * A XPTO recebe a movimentação, identifica o cliente e efetua o cadastro.
     * [cite: 21, 22, 23]
     * A lógica de cálculo da taxa (receita XPTO) é acionada dentro do serviço.
     * [cite: 25-29]
     */
    @PostMapping
    public ResponseEntity<Movimentacao> registrarMovimentacao(
            @RequestBody MovimentacaoRequestDTO dto) {

        try {
            Movimentacao novaMovimentacao = movimentacaoService.registrarMovimentacao(dto);
            // A 'novaMovimentacao' retornada já contém o 'valorTaxaXpto' calculado.
            return new ResponseEntity<>(novaMovimentacao, HttpStatus.CREATED);
        } catch (Exception e) {
            // (BusinessException e ResourceNotFoundException serão tratadas aqui)
            // Em um projeto robusto, usaríamos @ControllerAdvice
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
    }
}