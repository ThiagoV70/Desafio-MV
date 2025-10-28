package com.xpto.financeiro.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.xpto.financeiro.dto.ClienteRequestDTO;
import com.xpto.financeiro.dto.ContaBancariaRequestDTO;
import com.xpto.financeiro.dto.EnderecoRequestDTO;
import com.xpto.financeiro.model.enums.TipoPessoa;
import com.xpto.financeiro.repository.ClienteRepository;
import com.xpto.financeiro.repository.MovimentacaoRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class ClienteControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper; // Usado para converter o DTO em JSON

    @Autowired
    private ClienteRepository clienteRepository;
    @Autowired
    private MovimentacaoRepository movimentacaoRepository;

    @AfterEach
    void tearDown() {
        movimentacaoRepository.deleteAll();
        clienteRepository.deleteAll();
    }

    @Test
    void deveCriarClienteComMovimentacaoInicial() throws Exception {
        //  (Teste do requisito de cadastro com movimentação inicial)

        // Arrange
        ClienteRequestDTO request = new ClienteRequestDTO();
        request.setNome("Cliente Teste Integração");
        request.setDocumento("12345678900");
        request.setTipoPessoa(TipoPessoa.PF);
        request.setSaldoInicial(new BigDecimal("1000.00"));

        EnderecoRequestDTO endereco = new EnderecoRequestDTO();
        endereco.setRua("Rua Teste");
        endereco.setCidade("Cidade Teste");
        request.setEndereco(endereco);

        ContaBancariaRequestDTO conta = new ContaBancariaRequestDTO();
        conta.setAgencia("0001");
        conta.setNumeroConta("12345");
        request.setContaBancaria(conta);

        String requestJson = objectMapper.writeValueAsString(request);

        // Act & Assert
        mockMvc.perform(post("/api/clientes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.nome").value("Cliente Teste Integração"));

        // (Verificações adicionais no banco seriam feitas aqui)
    }
}