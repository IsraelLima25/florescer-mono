package br.com.loja.florescer.util;

import org.springframework.stereotype.Component;

import br.com.loja.florescer.model.Endereco;
import br.com.loja.florescer.view.EnderecoView;

@Component
public class EnderecoConverter {

	public EnderecoView toEnderecoView(Endereco endereco) {

		return new EnderecoView(endereco.getCep(), endereco.getLogradouro(), endereco.getComplemento(),
				endereco.getBairro(), endereco.getLocalidade(), endereco.getUf());
	}
	
	public Endereco toEndereco(EnderecoView enderecoView) {
		return new Endereco(enderecoView.cep(), enderecoView.logradouro(), enderecoView.complemento(), enderecoView.bairro(), enderecoView.localidade(), enderecoView.uf());
	}

}
