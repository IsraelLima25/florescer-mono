package br.com.loja.florescer.util;

import org.springframework.stereotype.Component;

import br.com.loja.florescer.model.Cliente;
import br.com.loja.florescer.view.ClienteView;

@Component
public class ClienteConverter {
	
	private EnderecoConverter enderecoConverter;
	
	public ClienteConverter(EnderecoConverter enderecoConverter) {
		this.enderecoConverter=enderecoConverter;
	}
	
	public ClienteView toClienteView(Cliente cliente) {
		
		return new ClienteView(cliente.getNome(), cliente.getCpf(), cliente.getRg(), cliente.getDataNascimento(), 
				enderecoConverter.toEnderecoView(cliente.getEndereco()));
	}

}
