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

@RestController
@RequestMapping("/api/movimentacoes")
public class MovimentacaoController {

    @Autowired
    private MovimentacaoService movimentacaoService;

    @PostMapping
    public ResponseEntity<Movimentacao> registrarMovimentacao(
            @RequestBody MovimentacaoRequestDTO dto) {

        try {
            Movimentacao novaMovimentacao = movimentacaoService.registrarMovimentacao(dto);
            return new ResponseEntity<>(novaMovimentacao, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
    }
}