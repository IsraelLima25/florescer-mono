package br.com.loja.florescer.service;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import br.com.loja.florescer.model.Cliente;
import br.com.loja.florescer.repository.ClienteRepository;

@Service
public class ClienteService {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(ClienteService.class);

	private ClienteRepository clienteRepository;
	
	public ClienteService(ClienteRepository clienteRepository) {
		this.clienteRepository=clienteRepository;
	}
	
	public Optional<Cliente> buscarClientePorCpf(String cpfCliente) {
		LOGGER.info("Buscando cliente com cpf {} ", cpfCliente);
		Optional<Cliente> optionalCliente = this.clienteRepository.findByCpf(cpfCliente);
		return optionalCliente;
	}
	
}
