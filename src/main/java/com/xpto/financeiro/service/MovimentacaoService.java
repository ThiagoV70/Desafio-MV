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

    // Definição das taxas
    private static final BigDecimal TAXA_FAIXA_1 = new BigDecimal("1.00");
    private static final BigDecimal TAXA_FAIXA_2 = new BigDecimal("0.75");
    private static final BigDecimal TAXA_FAIXA_3 = new BigDecimal("0.50");
    private static final int LIMITE_FAIXA_1 = 10;
    private static final int LIMITE_FAIXA_2 = 20;
    private static final int PERIODO_DIAS = 30;

    /**
     * Simula o recebimento e cadastro da movimentação [cite: 21, 23]
     */
    @Transactional
    public Movimentacao registrarMovimentacao(MovimentacaoRequestDTO dto) {
        Cliente cliente = clienteRepository.findById(dto.getClienteId())
                .orElseThrow(() -> new ResourceNotFoundException("Cliente não encontrado: " + dto.getClienteId()));

        ContaBancaria conta = contaRepository.findById(dto.getContaBancariaId())
                .orElseThrow(() -> new ResourceNotFoundException("Conta Bancária não encontrada: " + dto.getContaBancariaId()));

        // Validação de segurança básica
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

        // 1. Calcula a taxa para ESTA movimentação
        BigDecimal taxaCalculada = calcularTaxaMovimentacao(cliente);
        mov.setValorTaxaXpto(taxaCalculada);

        // 2. Salva a movimentação
        return movimentacaoRepository.save(mov);
    }

    /**
     * LÓGICA DE NEGÓCIO PRINCIPAL: Cálculo da receita da empresa (XPTO)
     * "a cada período de 30 dias, a partir da data de cadastro"
     */
    private BigDecimal calcularTaxaMovimentacao(Cliente cliente) {
        LocalDate dataCadastro = cliente.getDataCadastro();
        LocalDate hoje = LocalDate.now();

        // 1. Descobrir em qual "janela" de 30 dias estamos
        long diasDesdeCadastro = ChronoUnit.DAYS.between(dataCadastro, hoje);
        long periodoAtual = diasDesdeCadastro / PERIODO_DIAS; // (Ex: 0, 1, 2...)

        // 2. Definir as datas de início e fim da janela atual
        LocalDateTime inicioJanela = dataCadastro.plusDays(periodoAtual * PERIODO_DIAS).atStartOfDay();
        LocalDateTime fimJanela = dataCadastro.plusDays((periodoAtual + 1) * PERIODO_DIAS).atStartOfDay();

        // 3. Contar quantas movimentações JÁ EXISTEM nesta janela
        long movsNaJanela = movimentacaoRepository.countByClienteIdAndDataHoraBetween(
                cliente.getId(),
                inicioJanela,
                fimJanela
        );

        // 4. A nova movimentação é a próxima da contagem
        long contagemTotal = movsNaJanela + 1;

        // 5. Aplicar a regra de taxação
        if (contagemTotal <= LIMITE_FAIXA_1) {
            return TAXA_FAIXA_1; // R$ 1,00
        } else if (contagemTotal <= LIMITE_FAIXA_2) {
            return TAXA_FAIXA_2; // R$ 0,75
        } else {
            return TAXA_FAIXA_3; // R$ 0,50
        }
    }
}