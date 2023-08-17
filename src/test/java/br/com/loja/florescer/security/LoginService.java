package br.com.loja.florescer.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import br.com.loja.florescer.form.DadosAutenticacaoForm;
import br.com.loja.florescer.view.DadosTokenJWTView;

@Service
public class LoginService {
	
	@Autowired
	ObjectMapper objectMapper;
	
	@Autowired
	RestTemplate restTemplate;
	
	public String fazerLogin(String host, String user, String password) throws JsonProcessingException {
		
		String URL = host.concat("/login");
		
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		
		DadosAutenticacaoForm dadosAutenticacaoForm = new DadosAutenticacaoForm(user, password);
		
		HttpEntity<String> entity = new HttpEntity<>(objectMapper.writeValueAsString(dadosAutenticacaoForm), headers);
		
		ResponseEntity<DadosTokenJWTView> response = restTemplate.postForEntity(URL, entity, DadosTokenJWTView.class);
		
		return "Bearer ".concat(response.getBody().token());
	}
}
