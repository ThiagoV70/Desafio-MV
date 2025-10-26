package com.xpto.financeiro.service;

import com.xpto.financeiro.dto.ContaBancariaRequestDTO;
import com.xpto.financeiro.exception.BusinessException; // (Criar esta exceção)
import com.xpto.financeiro.exception.ResourceNotFoundException;
import com.xpto.financeiro.model.Cliente;
import com.xpto.financeiro.model.ContaBancaria;
import com.xpto.financeiro.repository.ClienteRepository;
import com.xpto.financeiro.repository.ContaBancariaRepository;
import com.xpto.financeiro.repository.MovimentacaoRepository; // (Do passo anterior)
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ContaBancariaService {

    @Autowired
    private ContaBancariaRepository contaRepository;

    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private MovimentacaoRepository movimentacaoRepository; // (Do passo anterior)

    public List<ContaBancaria> getContasByCliente(Long clienteId) {
        if (!clienteRepository.existsById(clienteId)) {
            throw new ResourceNotFoundException("Cliente não encontrado com id: " + clienteId);
        }
        return contaRepository.findByClienteIdAndAtivoTrue(clienteId);
    }

    public ContaBancaria adicionarConta(Long clienteId, ContaBancariaRequestDTO dto) {
        Cliente cliente = clienteRepository.findById(clienteId)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente não encontrado com id: " + clienteId));

        ContaBancaria conta = new ContaBancaria();
        conta.setInstituicaoFinanceira(dto.getInstituicaoFinanceira());
        conta.setAgencia(dto.getAgencia());
        conta.setNumeroConta(dto.getNumeroConta());
        conta.setAtivo(true);
        conta.setCliente(cliente);

        return contaRepository.save(conta);
    }

    public ContaBancaria atualizarConta(Long contaId, ContaBancariaRequestDTO dto) {
        boolean existeMovimentacao = movimentacaoRepository.existsByContaBancariaId(contaId);

        if (existeMovimentacao) {
            throw new BusinessException("Não é possível alterar a conta. Já existem movimentações associadas.");
        }

        ContaBancaria conta = contaRepository.findById(contaId)
                .orElseThrow(() -> new ResourceNotFoundException("Conta não encontrada com id: " + contaId));

        // Permite alteração apenas se não houver movimentação
        conta.setInstituicaoFinanceira(dto.getInstituicaoFinanceira());
        conta.setAgencia(dto.getAgencia());
        conta.setNumeroConta(dto.getNumeroConta());

        return contaRepository.save(conta);
    }

    public void deletarContaLogicamente(Long contaId) {
        ContaBancaria conta = contaRepository.findById(contaId)
                .orElseThrow(() -> new ResourceNotFoundException("Conta não encontrada com id: " + contaId));

        conta.setAtivo(false);
        contaRepository.save(conta);
    }
}