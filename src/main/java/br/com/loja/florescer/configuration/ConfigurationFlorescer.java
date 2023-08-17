package br.com.loja.florescer.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ConfigurationFlorescer {

	@Value("${url.via.cep}")
	private String urlViaCep;
	
	@Value("${secret.token.jwt}")
	private String secretTokenJWT;
	
	public String getUrlViaCep() {
		return urlViaCep;
	}
	
	public String getSecretTokenJWT() {
		return secretTokenJWT;
	}
	
}
