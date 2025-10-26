package com.xpto.financeiro.service;

import com.xpto.financeiro.dto.MovimentacaoRequestDTO;
import com.xpto.financeiro.model.Cliente;
import com.xpto.financeiro.model.ContaBancaria;
import com.xpto.financeiro.model.Movimentacao;
import com.xpto.financeiro.repository.ClienteRepository;
import com.xpto.financeiro.repository.ContaBancariaRepository;
import com.xpto.financeiro.repository.MovimentacaoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class MovimentacaoServiceTest {

    @Mock
    private MovimentacaoRepository movimentacaoRepository;
    @Mock
    private ClienteRepository clienteRepository;
    @Mock
    private ContaBancariaRepository contaBancariaRepository;

    @InjectMocks
    private MovimentacaoService movimentacaoService;

    private Cliente clienteMock;
    private ContaBancaria contaMock;
    private MovimentacaoRequestDTO requestMock;

    @BeforeEach
    void setUp() {

        clienteMock = new Cliente();
        clienteMock.setId(1L);
        clienteMock.setDataCadastro(LocalDate.now().minusDays(15));

        contaMock = new ContaBancaria();
        contaMock.setId(10L);
        contaMock.setCliente(clienteMock);
        contaMock.setAtivo(true);

        requestMock = new MovimentacaoRequestDTO();
        requestMock.setClienteId(1L);
        requestMock.setContaBancariaId(10L);

        when(movimentacaoRepository.save(any(Movimentacao.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        when(clienteRepository.findById(1L)).thenReturn(Optional.of(clienteMock));
        when(contaBancariaRepository.findById(10L)).thenReturn(Optional.of(contaMock));
    }

    @Test
    void deveCobrarTaxaFaixa1_QuandoForPrimeiraMovimentacao() {

        when(movimentacaoRepository.countByClienteIdAndDataHoraBetween(
                eq(1L), any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(0L); // 0 movimentações existentes

        Movimentacao result = movimentacaoService.registrarMovimentacao(requestMock);

        assertEquals(new BigDecimal("1.00"), result.getValorTaxaXpto());
    }

    @Test
    void deveCobrarTaxaFaixa2_QuandoForA11Movimentacao() {

        when(movimentacaoRepository.countByClienteIdAndDataHoraBetween(
                eq(1L), any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(10L); // 10 movimentações existentes

        Movimentacao result = movimentacaoService.registrarMovimentacao(requestMock);

        assertEquals(new BigDecimal("0.75"), result.getValorTaxaXpto());
    }

    @Test
    void deveCobrarTaxaFaixa3_QuandoForA21Movimentacao() {

        when(movimentacaoRepository.countByClienteIdAndDataHoraBetween(
                eq(1L), any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(20L); // 20 movimentações existentes

        Movimentacao result = movimentacaoService.registrarMovimentacao(requestMock);

        assertEquals(new BigDecimal("0.50"), result.getValorTaxaXpto());
    }
}