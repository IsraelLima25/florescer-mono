package br.com.loja.florescer.integration;

import java.math.BigDecimal;
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

import br.com.loja.florescer.view.ProdutoView;
import br.com.loja.florescer.form.ProdutoForm;
import br.com.loja.florescer.model.Endereco;
import br.com.loja.florescer.model.Produto;
import br.com.loja.florescer.model.Fornecedor;
import br.com.loja.florescer.repository.FornecedorRepository;
import br.com.loja.florescer.repository.ProdutoRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Transactional
public class ProdutoIntegrationTest {
	
	@LocalServerPort
	int port;
	
	@PersistenceContext
	EntityManager entityManager;

	@Autowired
	TestRestTemplate testRestTemplate;

	@Autowired
	ObjectMapper objectMapper;

	@Autowired
	ProdutoRepository produtoRepository;
	
	@Autowired
	FornecedorRepository fornecedorRepository;
	
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
		
		produtoRepository.deleteAll();
		fornecedorRepository.deleteAll();
		
		
		Endereco enderecoFornecedorEstadual = new Endereco("41290221", "Rua dos testes fornecedor estadual", "Casa", "Moca", "São Paulo",
				"sp");
		
		Fornecedor fornecedorEstadual = new Fornecedor("Fornecedor estadual", "01956715000185", enderecoFornecedorEstadual);
		fornecedorRepository.save(fornecedorEstadual);
		
		Produto primeiroProduto = new Produto("Rosa", new BigDecimal("15.00"), 30, "sp", fornecedorEstadual);
		Produto segundoProduto = new Produto("Margarida", new BigDecimal("7.00"), 16, "sp", fornecedorEstadual);
		
		produtoRepository.saveAll(List.of(primeiroProduto, segundoProduto));
		TestTransaction.end();
	}
	
	@AfterEach
	void destroy() { 
		
		TestTransaction.start();
		TestTransaction.flagForCommit();
		produtoRepository.deleteAll();
		fornecedorRepository.deleteAll();
		TestTransaction.end();
	}
	
	private String getURI() {
		return UriComponentsBuilder.fromHttpUrl("http://localhost:" + port + "/api/produtos").toUriString();
	}

	
	@Test
	void deveListarTodosProdutos() { 
		
		HttpEntity<String> entity = new HttpEntity<>(null, headers);
		ResponseEntity<List<ProdutoView>> response = testRestTemplate.exchange(getURI(), 
				HttpMethod.GET, entity,
				new ParameterizedTypeReference<List<ProdutoView>>() {
				});
		List<ProdutoView> body = response.getBody();
		Integer statusCode = response.getStatusCode().value();

		assertTrue(response != null);
		assertEquals(2, body.size());
		assertEquals("Rosa", body.get(0).descricao());
		assertEquals("Margarida", body.get(1).descricao());
		assertEquals(HttpStatus.OK.value(), statusCode);
	}
	
	@Test
	void deveCadastrarProduto() throws JsonProcessingException {
		
		ProdutoForm form = new ProdutoForm("Kit cafe da manha", new BigDecimal("30.00"),20,"ba",1L);
		
		HttpEntity<String> entity = new HttpEntity<>(objectMapper.writeValueAsString(form), headers);
		ResponseEntity<ProdutoView> response = testRestTemplate.exchange(getURI(), HttpMethod.POST, entity,
				ProdutoView.class);

		ProdutoView produtoView = response.getBody();
		Integer statusCode = response.getStatusCode().value();

		assertTrue(produtoView != null);
		assertEquals(HttpStatus.OK.value(), statusCode);
		assertEquals(produtoView.descricao(), "Kit cafe da manha");
		assertEquals(produtoView.quantidadeEstoque(), 20);
		assertEquals(produtoView.filial(), "ba");
	}
	
	@Test
	void naoDeveCadastrarProdutoComFornecedorInvalido() throws JsonProcessingException {
		
		ProdutoForm form = new ProdutoForm("Kit cafe da manha", new BigDecimal("30.00"),20,"ba",30L);
		
		HttpEntity<String> entity = new HttpEntity<>(objectMapper.writeValueAsString(form), headers);
		ResponseEntity<ProdutoView> response = testRestTemplate.exchange(getURI(), HttpMethod.POST, entity,
				ProdutoView.class);

		ProdutoView produtoView = response.getBody();
		Integer statusCode = response.getStatusCode().value();

		assertTrue(produtoView != null);
		assertEquals(HttpStatus.NOT_FOUND.value(), statusCode);
	}
	
	private void resetIncrement() {
		entityManager.createNativeQuery("ALTER TABLE db_test_florescer.tbl_fornecedor AUTO_INCREMENT = 1")
	    .executeUpdate(); 
	}

}
