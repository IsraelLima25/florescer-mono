package br.com.loja.florescer.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import br.com.loja.florescer.api.ApiEnderecoService;
import br.com.loja.florescer.model.Endereco;
import br.com.loja.florescer.model.Entrega;
import br.com.loja.florescer.model.Pedido;
import br.com.loja.florescer.util.EnderecoConverter;

@Service
public class EntregaService {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(EntregaService.class);
	
	private ApiEnderecoService apiEnderecoService;
	private EnderecoConverter enderecoConverter;
	
	public EntregaService(ApiEnderecoService apiEnderecoService, EnderecoConverter enderecoConverter) {
		this.apiEnderecoService=apiEnderecoService;
		this.enderecoConverter=enderecoConverter;
	}
	
	public Entrega gerarEntrega(String cep, Pedido pedido) {
		LOGGER.info("Processando entrega para o pedido com id {}", pedido.getId());
		Endereco endereco = enderecoConverter.toEndereco(apiEnderecoService.requestEnderecoJSON(cep));
		Entrega entrega = new Entrega(endereco, pedido);
		return entrega;		
	}

}
