package com.xpto.financeiro.service;

import com.xpto.financeiro.dto.MovimentacaoRequestDTO;
import com.xpto.financeiro.exception.BusinessException;
import com.xpto.financeiro.exception.ResourceNotFoundException;
import com.xpto.financeiro.model.Cliente;
import com.xpto.financeiro.model.ContaBancaria;
import com.xpto.financeiro.model.Movimentacao;
import com.xpto.financeiro.repository.ClienteRepository;
import com.xpto.financeiro.repository.ContaBancariaRepository;
import com.xpto.financeiro.repository.MovimentacaoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Service
public class MovimentacaoService {

    @Autowired
    private MovimentacaoRepository movimentacaoRepository;
    @Autowired
    private ClienteRepository clienteRepository;
    @Autowired
    private ContaBancariaRepository contaRepository;

    private static final BigDecimal TAXA_FAIXA_1 = new BigDecimal("1.00");
    private static final BigDecimal TAXA_FAIXA_2 = new BigDecimal("0.75");
    private static final BigDecimal TAXA_FAIXA_3 = new BigDecimal("0.50");
    private static final int LIMITE_FAIXA_1 = 10;
    private static final int LIMITE_FAIXA_2 = 20;
    private static final int PERIODO_DIAS = 30;

    @Transactional
    public Movimentacao registrarMovimentacao(MovimentacaoRequestDTO dto) {
        Cliente cliente = clienteRepository.findById(dto.getClienteId())
                .orElseThrow(() -> new ResourceNotFoundException("Cliente não encontrado: " + dto.getClienteId()));

        ContaBancaria conta = contaRepository.findById(dto.getContaBancariaId())
                .orElseThrow(() -> new ResourceNotFoundException("Conta Bancária não encontrada: " + dto.getContaBancariaId()));

        if (!conta.getCliente().getId().equals(cliente.getId())) {
            throw new BusinessException("Esta conta não pertence ao cliente informado.");
        }
        if (!conta.isAtivo()) {
            throw new BusinessException("A conta bancária está inativa.");
        }

        Movimentacao mov = new Movimentacao();
        mov.setCliente(cliente);
        mov.setContaBancaria(conta);
        mov.setTipo(dto.getTipo());
        mov.setValor(dto.getValor());

        BigDecimal taxaCalculada = calcularTaxaMovimentacao(cliente);
        mov.setValorTaxaXpto(taxaCalculada);

        return movimentacaoRepository.save(mov);
    }

    private BigDecimal calcularTaxaMovimentacao(Cliente cliente) {
        LocalDate dataCadastro = cliente.getDataCadastro();
        LocalDate hoje = LocalDate.now();

        long diasDesdeCadastro = ChronoUnit.DAYS.between(dataCadastro, hoje);
        long periodoAtual = diasDesdeCadastro / PERIODO_DIAS; // (Ex: 0, 1, 2...)

        LocalDateTime inicioJanela = dataCadastro.plusDays(periodoAtual * PERIODO_DIAS).atStartOfDay();
        LocalDateTime fimJanela = dataCadastro.plusDays((periodoAtual + 1) * PERIODO_DIAS).atStartOfDay();

        long movsNaJanela = movimentacaoRepository.countByClienteIdAndDataHoraBetween(
                cliente.getId(),
                inicioJanela,
                fimJanela
        );

        long contagemTotal = movsNaJanela + 1;

        if (contagemTotal <= LIMITE_FAIXA_1) {
            return TAXA_FAIXA_1;
        } else if (contagemTotal <= LIMITE_FAIXA_2) {
            return TAXA_FAIXA_2;
        } else {
            return TAXA_FAIXA_3;
        }
    }
}