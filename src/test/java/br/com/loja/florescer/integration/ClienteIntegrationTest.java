package br.com.loja.florescer.integration;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.transaction.TestTransaction;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.util.UriComponentsBuilder;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import br.com.loja.florescer.view.ClienteView;
import br.com.loja.florescer.form.EnderecoForm;
import br.com.loja.florescer.form.ClienteForm;
import br.com.loja.florescer.model.Cliente;
import br.com.loja.florescer.model.Endereco;
import br.com.loja.florescer.repository.ClienteRepository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Transactional
public class ClienteIntegrationTest {

	@LocalServerPort
	int port;
	
	@PersistenceContext
	EntityManager entityManager;
	
	@Autowired
	TestRestTemplate testRestTemplate;
	
	@Autowired
	ObjectMapper objectMapper;
	
	@Autowired
	ClienteRepository clienteRepository;
	
	private static HttpHeaders headers;
	
	@BeforeAll
	static void setup() {
		headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
	}

	
	@BeforeEach
	void init() {
	
		resetIncrement();
		TestTransaction.flagForCommit();
		
		Cliente primeiroCliente = new Cliente("Luan Carlos", "12365489763", "2565478937", LocalDate.of(1980, 3, 5),
				new Endereco("15945970", "Rua Guia Lopes 225", "CASA", "Centro", "Agulha", "SP"));
		
		Cliente segundoCliente = new Cliente("Paulo Castro", "12366698026", "3065479037", LocalDate.of(1982, 4, 10),
				new Endereco("69995300", "Rua das margaridas", "CASA", "Centro", "Centro", "RJ"));
		
		clienteRepository.saveAll(List.of(primeiroCliente, segundoCliente));
		
		TestTransaction.end();
	}
	
	@AfterEach
	void destroy() {
		TestTransaction.start();
		TestTransaction.flagForCommit();
		clienteRepository.deleteAll();
		TestTransaction.end();
	}
	
	private String getURI() {
		return UriComponentsBuilder.fromHttpUrl("http://localhost:" + port + "/api/clientes").toUriString();
	}
	
	@Test
	void deveListarTodosClientes() { 
		
		HttpEntity<String> entity = new HttpEntity<>(null, headers);
		ResponseEntity<List<ClienteView>> response = testRestTemplate.exchange(getURI(), 
				HttpMethod.GET, entity,
				new ParameterizedTypeReference<List<ClienteView>>() {
				});
		List<ClienteView> body = response.getBody();
		Integer statusCode = response.getStatusCode().value();

		assertTrue(response != null);
		assertEquals(2, body.size());
		assertEquals("Luan Carlos", body.get(0).nome());
		assertEquals("Paulo Castro", body.get(1).nome());
		assertEquals(HttpStatus.OK.value(), statusCode);
	}
	
	@Test
	void deveBuscarClientePorCpf() { 
		
		HttpEntity<String> entity = new HttpEntity<>(null, headers);
		ResponseEntity<ClienteView> response = testRestTemplate.exchange(getURI().concat("/12365489763"), 
				HttpMethod.GET, entity,
				new ParameterizedTypeReference<ClienteView>() {
				});
		ClienteView body = response.getBody();
		Integer statusCode = response.getStatusCode().value();

		assertTrue(response != null);
		assertEquals("Luan Carlos", body.nome());
		assertEquals("12365489763", body.cpf());
		assertEquals(HttpStatus.OK.value(), statusCode);
	}
	
	@Test
	void naoDeveBuscarClientePorCpfInvalido() {
		
		HttpEntity<String> entity = new HttpEntity<>(null, headers);
		ResponseEntity<ClienteView> response = testRestTemplate.exchange(getURI().concat("/12365482063"), 
				HttpMethod.GET, entity,
				new ParameterizedTypeReference<ClienteView>() {
				});
		ClienteView body = response.getBody();
		Integer statusCode = response.getStatusCode().value();

		assertTrue(response != null);
		assertEquals(HttpStatus.NOT_FOUND.value(), statusCode);
	}
	
	@Test
	void deveCadastrarCliente() throws JsonProcessingException {
		
		ClienteForm form = new ClienteForm("Roberto Costa", "23654827863", "1456988712",LocalDate.of(1990, 2, 13), 
				new EnderecoForm("41290200"));
		
		HttpEntity<String> entity = new HttpEntity<>(objectMapper.writeValueAsString(form), headers);
		ResponseEntity<ClienteView> response = testRestTemplate.exchange(getURI(), HttpMethod.POST, entity,
				ClienteView.class);

		ClienteView clienteView = response.getBody();
		Integer statusCode = response.getStatusCode().value();

		assertTrue(clienteView != null);
		assertEquals(HttpStatus.OK.value(), statusCode);
		assertEquals(clienteView.nome(), "Roberto Costa");
		assertEquals(clienteView.cpf(), "23654827863");
		assertEquals(clienteView.rg(), "1456988712");
	}
	
	@Test
	void deveAtualizarClienteValido() throws JsonProcessingException { 
		
		ClienteForm form = new ClienteForm("Roberto Costa", "23654827863", "1456988712",LocalDate.of(1990, 2, 13), 
				new EnderecoForm("41290200"));
		
		HttpEntity<String> entity = new HttpEntity<>(objectMapper.writeValueAsString(form), headers);
		ResponseEntity<ClienteView> response = testRestTemplate.exchange(getURI().concat("/12365489763"), HttpMethod.PUT, entity,
				ClienteView.class);

		ClienteView clienteView = response.getBody();
		Integer statusCode = response.getStatusCode().value();

		assertTrue(clienteView != null);
		assertEquals(HttpStatus.OK.value(), statusCode);
		assertEquals(clienteView.nome(), "Roberto Costa");
		assertEquals(clienteView.cpf(), "23654827863");
		assertEquals(clienteView.rg(), "1456988712");
	}
	
	@Test
	void naoDeveAtualizarClienteInvalido() throws JsonProcessingException { 
		
		ClienteForm form = new ClienteForm("Roberto Costa", "23654827863", "1456988712",LocalDate.of(1990, 2, 13), 
				new EnderecoForm("41290200"));
		
		HttpEntity<String> entity = new HttpEntity<>(objectMapper.writeValueAsString(form), headers);
		ResponseEntity<ClienteView> response = testRestTemplate.exchange(getURI().concat("/12365489300"), HttpMethod.PUT, entity,
				ClienteView.class);

		assertEquals(HttpStatus.NOT_FOUND.value(), response.getStatusCode().value());
	}
	
	private void resetIncrement() {
		entityManager.createNativeQuery("ALTER TABLE db_test_florescer.tbl_cliente AUTO_INCREMENT = 1")
	    .executeUpdate(); 
	}
	
}
