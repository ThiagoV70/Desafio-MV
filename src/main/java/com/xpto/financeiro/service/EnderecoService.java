package com.xpto.financeiro.service;

import com.xpto.financeiro.dto.EnderecoRequestDTO;
import com.xpto.financeiro.exception.ResourceNotFoundException;
import com.xpto.financeiro.model.Cliente;
import com.xpto.financeiro.model.Endereco;
import com.xpto.financeiro.repository.ClienteRepository;
import com.xpto.financeiro.repository.EnderecoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EnderecoService {

    @Autowired
    private EnderecoRepository enderecoRepository;

    @Autowired
    private ClienteRepository clienteRepository;

    public List<Endereco> getEnderecosByCliente(Long clienteId) {
        if (!clienteRepository.existsById(clienteId)) {
            throw new ResourceNotFoundException("Cliente não encontrado com id: " + clienteId);
        }
        return enderecoRepository.findByClienteId(clienteId);
    }

    public Endereco adicionarEndereco(Long clienteId, EnderecoRequestDTO dto) {
        Cliente cliente = clienteRepository.findById(clienteId)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente não encontrado com id: " + clienteId));

        Endereco endereco = new Endereco();

        endereco.setRua(dto.getRua());
        endereco.setNumero(dto.getNumero());
        endereco.setBairro(dto.getBairro());
        endereco.setCidade(dto.getCidade());
        endereco.setUf(dto.getUf());
        endereco.setCep(dto.getCep());
        endereco.setComplemento(dto.getComplemento());

        endereco.setCliente(cliente);

        return enderecoRepository.save(endereco);
    }

    public Endereco atualizarEndereco(Long enderecoId, EnderecoRequestDTO dto) {
        Endereco endereco = enderecoRepository.findById(enderecoId)
                .orElseThrow(() -> new ResourceNotFoundException("Endereço não encontrado com id: " + enderecoId));

        endereco.setRua(dto.getRua());
        endereco.setNumero(dto.getNumero());

        return enderecoRepository.save(endereco);
    }
}