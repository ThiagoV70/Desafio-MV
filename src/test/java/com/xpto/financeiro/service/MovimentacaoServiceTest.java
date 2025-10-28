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
        // Configura um cliente mock que se cadastrou há 15 dias
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

        // Configura um cliente mock que se cadastrou há 15 dias
        when(movimentacaoRepository.save(any(Movimentacao.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // Mock para buscar o cliente e a conta
        when(clienteRepository.findById(1L)).thenReturn(Optional.of(clienteMock));
        when(contaBancariaRepository.findById(10L)).thenReturn(Optional.of(contaMock));
    }

    @Test
    void deveCobrarTaxaFaixa1_QuandoForPrimeiraMovimentacao() {
        // [cite: 27] (Até 10 movimentações = R$ 1,00)

        // Arrange
        // Mock: Simula que não há nenhuma movimentação na janela atual (count = 0)
        when(movimentacaoRepository.countByClienteIdAndDataHoraBetween(
                eq(1L), any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(0L); // 0 movimentações existentes

        // Act
        Movimentacao result = movimentacaoService.registrarMovimentacao(requestMock);

        // Assert
        // A nova movimentação (a 1ª) deve ter a taxa de 1.00
        assertEquals(new BigDecimal("1.00"), result.getValorTaxaXpto());
    }

    @Test
    void deveCobrarTaxaFaixa2_QuandoForA11Movimentacao() {
        // [cite: 28] (De 10 a 20 movimentações = R$ 0,75)

        // Arrange
        // Mock: Simula que já existem 10 movimentações na janela
        when(movimentacaoRepository.countByClienteIdAndDataHoraBetween(
                eq(1L), any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(10L); // 10 movimentações existentes

        // Act
        Movimentacao result = movimentacaoService.registrarMovimentacao(requestMock);

        // Assert
        // A nova movimentação (a 11ª) deve ter a taxa de 0.75
        assertEquals(new BigDecimal("0.75"), result.getValorTaxaXpto());
    }

    @Test
    void deveCobrarTaxaFaixa3_QuandoForA21Movimentacao() {
        // [cite: 29] (Acima de 20 movimentações = R$ 0,50)

        // Arrange
        // Mock: Simula que já existem 20 movimentações na janela
        when(movimentacaoRepository.countByClienteIdAndDataHoraBetween(
                eq(1L), any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(20L); // 20 movimentações existentes

        // Act
        Movimentacao result = movimentacaoService.registrarMovimentacao(requestMock);

        // Assert
        // A nova movimentação (a 21ª) deve ter a taxa de 0.50
        assertEquals(new BigDecimal("0.50"), result.getValorTaxaXpto());
    }
}