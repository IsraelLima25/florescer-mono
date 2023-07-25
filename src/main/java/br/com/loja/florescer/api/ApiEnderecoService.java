package br.com.loja.florescer.api;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import br.com.loja.florescer.configuration.ConfigurationFlorescer;
import br.com.loja.florescer.view.EnderecoView;

@Service
public class ApiEnderecoService {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(ApiEnderecoService.class);

	private RestTemplate restTemplate;
	private ConfigurationFlorescer configurationFlorescer;

	public ApiEnderecoService(RestTemplate restTemplate, ConfigurationFlorescer configurationFlorescer) {
		this.restTemplate = restTemplate;
		this.configurationFlorescer = configurationFlorescer;
	}

	public EnderecoView requestEnderecoJSON(String cep) {

		try {
			String urlViaCep = configurationFlorescer.getUrlViaCep();
			String urlBuilder = UriComponentsBuilder.fromUriString(urlViaCep).path("/".concat(cep)).path("/json")
					.build().toUriString();

			LOGGER.info("Buscando endereco em url {} cep {}", urlBuilder, cep);
			ResponseEntity<EnderecoView> response = restTemplate.getForEntity(urlBuilder,
					EnderecoView.class);
			LOGGER.info("Endereco recuperado com sucesso {}", response.getBody().toString());
			return response.getBody();
		} catch (RestClientResponseException ex) {
			LOGGER.error("Servidor via cep fora do ar!! {}", ex.getMessage());
			throw new IllegalArgumentException(ex.getMessage());
		}

	}

}
