package com.xpto.financeiro.service;

import com.xpto.financeiro.model.Cliente;
import com.xpto.financeiro.model.Movimentacao;
import com.xpto.financeiro.model.enums.TipoMovimentacao;
import com.xpto.financeiro.repository.ClienteRepository;
import com.xpto.financeiro.repository.MovimentacaoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
public class ClienteService {

    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private MovimentacaoRepository movimentacaoRepository;

    @Transactional
    public Cliente criarNovoCliente(Cliente cliente, BigDecimal saldoInicial) {

        Cliente novoCliente = clienteRepository.save(cliente);


        Movimentacao inicial = new Movimentacao();
        inicial.setCliente(novoCliente);
        inicial.setValor(saldoInicial);
        inicial.setTipo(TipoMovimentacao.CREDITO);
        inicial.setContaBancariaId("INICIAL");

        movimentacaoRepository.save(inicial);

        return novoCliente;
    }
}