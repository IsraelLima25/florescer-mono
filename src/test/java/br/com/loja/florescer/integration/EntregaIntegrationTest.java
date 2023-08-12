package br.com.loja.florescer.integration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.time.LocalDate;

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

import br.com.loja.florescer.model.Pedido;
import br.com.loja.florescer.model.Produto;
import br.com.loja.florescer.repository.ClienteRepository;
import br.com.loja.florescer.repository.EntregaRepository;
import br.com.loja.florescer.repository.FornecedorRepository;
import br.com.loja.florescer.repository.ItemPedidoRepository;
import br.com.loja.florescer.repository.PedidoRepository;
import br.com.loja.florescer.repository.ProdutoRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import br.com.loja.florescer.form.AvaliacaoEntregaForm;
import br.com.loja.florescer.indicador.AvaliacaoIndicador;
import br.com.loja.florescer.indicador.StatusEntregaIndicador;
import br.com.loja.florescer.indicador.TipoFormaPagamentoIndicador;
import br.com.loja.florescer.model.Cliente;
import br.com.loja.florescer.model.Endereco;
import br.com.loja.florescer.model.Fornecedor;
import br.com.loja.florescer.model.ItemPedido;
import br.com.loja.florescer.model.Pagamento;
import br.com.loja.florescer.model.Entrega;

import br.com.loja.florescer.view.StatusEntregaView;
import br.com.loja.florescer.view.EntregaView;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Transactional
public class EntregaIntegrationTest {

	@LocalServerPort
	int port;
	
	@PersistenceContext
	EntityManager entityManager;

	@Autowired
	TestRestTemplate testRestTemplate;

	@Autowired
	ObjectMapper objectMapper;
	
	@Autowired
	PedidoRepository pedidoRepository;
	
	@Autowired
	ItemPedidoRepository itemPedidoRepository;
	
	@Autowired
	ClienteRepository clienteRepository;
	
	@Autowired
	ProdutoRepository produtoRepository;
	
	@Autowired
	FornecedorRepository fornecedorRepository;
	
	@Autowired
	EntregaRepository entregaRepository;
	
	private static HttpHeaders headers;

	@BeforeAll
	static void setup() {
		headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
	}

	@BeforeEach
	void init() {
		resetIncrement();
		TestTransaction.flagForCommit(); // need this, otherwise the next line does a rollback
		itemPedidoRepository.deleteAll();
		pedidoRepository.deleteAll();
		produtoRepository.deleteAll();
		fornecedorRepository.deleteAll();
		clienteRepository.deleteAll();
		entregaRepository.deleteAll();
		
		Cliente cliente = new Cliente("Luan Carlos", "12365489763", "2565478937", LocalDate.of(1980, 3, 5),
				new Endereco("15945970", "Rua Guia Lopes 225", "CASA", "Centro", "Agulha", "SP"));
		clienteRepository.save(cliente);

		Endereco enderecoFornecedorEstadual = new Endereco("41290221", "Rua dos testes fornecedor estadual", "Casa", "Moca", "São Paulo",
				"sp");
		
		Endereco enderecoFornecedorInterEstadual = new Endereco("41290200", "Rua dos testes fornecedor Inter estadual", "Casa", "Moca", "São Paulo",
				"sp");

		Endereco enderecoEntrega = new Endereco("41290200", "Rua dos testes emtrega", "Casa", "Moca", "São Paulo",
				"sp");
		
		Fornecedor fornecedorEstadual = new Fornecedor("Fornecedor estadual", "01956715000185", enderecoFornecedorEstadual);
		fornecedorRepository.save(fornecedorEstadual);
		
		
		Produto primeiroProduto = new Produto("Rosa", new BigDecimal("15.00"), 30, "sp", fornecedorEstadual);
		
		Fornecedor fornecedorInterEstadual = new Fornecedor("Fornecedor interestadual", "02056715000185", enderecoFornecedorInterEstadual);
		fornecedorRepository.save(fornecedorInterEstadual);
		
		Produto segundoProduto = new Produto("Margarida", new BigDecimal("7.00"), 16, "sp", fornecedorInterEstadual);

		produtoRepository.save(primeiroProduto);
		produtoRepository.save(segundoProduto);
		
		Pedido pedido = new Pedido();
		pedido.adicionarCliente(cliente);

		ItemPedido primeiroItem = new ItemPedido(primeiroProduto, pedido, 2);
		ItemPedido segundoItem = new ItemPedido(segundoProduto, pedido, 1);

		pedido.adicionarItem(primeiroItem);
		pedido.adicionarItem(segundoItem);

		Entrega entrega = new Entrega(enderecoEntrega, pedido);
		Pagamento pagamento = new Pagamento(TipoFormaPagamentoIndicador.PIX, pedido.getValorTotalPagamento());

		pedido.adicionarEntrega(entrega);
		pedido.adicionarObservacao("Entrega urgente");
		pedido.calcularValorTotal();
		pedido.adicionarFormaPagamento(pagamento);

		pedidoRepository.save(pedido);
		TestTransaction.end();

	}

	@AfterEach
	void destroy() {
		
		TestTransaction.start();
		TestTransaction.flagForCommit();
		itemPedidoRepository.deleteAll();
		pedidoRepository.deleteAll();
		produtoRepository.deleteAll();
		fornecedorRepository.deleteAll();
		clienteRepository.deleteAll();
		entregaRepository.deleteAll();
		
		TestTransaction.end();
	}

	private String getURI() {
		return UriComponentsBuilder.fromHttpUrl("http://localhost:" + port + "/api/entregas").toUriString();
	}

	@Test
	void deveRetornarStatusEntregaPedidoValido() {
		
		HttpEntity<String> entity = new HttpEntity<>(null, headers);
		ResponseEntity<StatusEntregaView> response = testRestTemplate.exchange(getURI().concat("/pedido/1"), 
				HttpMethod.GET, entity,
				new ParameterizedTypeReference<StatusEntregaView>() {
				});
		StatusEntregaView body = response.getBody();
		Integer statusCode = response.getStatusCode().value();

		assertTrue(response != null);
		assertEquals(body.status(), StatusEntregaIndicador.AGUARDANDO_CONFIRMACAO_DE_PAGAMENTO);
		assertEquals(HttpStatus.OK.value(), statusCode);
	}
	
	@Test
	void naoDeveRetornarStatusEntregaPedidoInvalido() {
		HttpEntity<String> entity = new HttpEntity<>(null, headers);
		ResponseEntity<StatusEntregaView> response = testRestTemplate.exchange(getURI().concat("/pedido/100"), 
				HttpMethod.GET, entity,
				StatusEntregaView.class);
		
		Integer statusCode = response.getStatusCode().value();

		assertEquals(HttpStatus.NOT_FOUND.value(), statusCode);
	}
	
	@Test
	void deveAvaliarEntregaPedidoValido() throws JsonProcessingException {
		AvaliacaoEntregaForm form = new AvaliacaoEntregaForm(1L, AvaliacaoIndicador.FIVE_STAR);
		HttpEntity<String> entity = new HttpEntity<>(objectMapper.writeValueAsString(form), headers);
		ResponseEntity<EntregaView> response = testRestTemplate.exchange(getURI().concat("/avaliar"), 
				HttpMethod.POST, entity, EntregaView.class);

		EntregaView entregaView = response.getBody();
		Integer statusCode = response.getStatusCode().value();

		assertTrue(entregaView != null);
		assertEquals(HttpStatus.OK.value(), statusCode);
		assertEquals(entregaView.avaliacao(), AvaliacaoIndicador.FIVE_STAR);
	}
	
	@Test
	void naoDeveAvaliarEntregaPedidoInvalido() throws JsonProcessingException {
		AvaliacaoEntregaForm form = new AvaliacaoEntregaForm(100L, AvaliacaoIndicador.FIVE_STAR);
		HttpEntity<String> entity = new HttpEntity<>(objectMapper.writeValueAsString(form), headers);
		ResponseEntity<EntregaView> response = testRestTemplate.exchange(getURI().concat("/avaliar"), 
				HttpMethod.POST, entity, EntregaView.class);

		Integer statusCode = response.getStatusCode().value();

		assertEquals(HttpStatus.NOT_FOUND.value(), statusCode);
	}
	
	private void resetIncrement() {
		entityManager.createNativeQuery("ALTER TABLE db_test_florescer.tbl_pedido AUTO_INCREMENT = 1")
	    .executeUpdate(); 
	}

}
