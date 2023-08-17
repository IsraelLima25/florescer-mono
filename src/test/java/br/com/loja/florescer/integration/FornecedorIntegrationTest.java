package br.com.loja.florescer.integration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.http.HttpStatus;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.transaction.TestTransaction;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.util.UriComponentsBuilder;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import br.com.loja.florescer.form.EnderecoForm;
import br.com.loja.florescer.form.FornecedorForm;
import br.com.loja.florescer.model.Endereco;
import br.com.loja.florescer.model.Fornecedor;
import br.com.loja.florescer.model.Perfil;
import br.com.loja.florescer.model.Usuario;
import br.com.loja.florescer.repository.FornecedorRepository;
import br.com.loja.florescer.repository.UsuarioRepository;
import br.com.loja.florescer.security.LoginService;
import br.com.loja.florescer.view.FornecedorView;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Transactional
public class FornecedorIntegrationTest {

	@LocalServerPort
	int port;

	@Autowired
	TestRestTemplate testRestTemplate;

	@Autowired
	ObjectMapper objectMapper;

	@Autowired
	FornecedorRepository fornecedorRepository;
	
	@Autowired
	UsuarioRepository usuarioRepository;

	@Autowired
	LoginService loginService;
	
	@Value("${user.login}")
	String login;
	
	@Value("${user.password.criptografada}")
	String senhaCriptografada;
	
	@Value("${user.password.descriptografada}")
	String senhaDescriptografada;
	
	private static HttpHeaders headers;
	
	@BeforeAll
	static void setup() {
		headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
	}

	@BeforeEach
	void init() {

		TestTransaction.flagForCommit(); // need this, otherwise the next line does a rollback
		usuarioRepository.deleteAll();
		fornecedorRepository.deleteAll();
		Fornecedor fornecedor = new Fornecedor("Campo florido", "93867462000191",
				new Endereco("41290221", "Rua Guia Lopes 225", "CASA", "Centro", "Agulha", "SP"));
		fornecedorRepository.save(fornecedor);
		
		Usuario usuario = new Usuario(login, senhaCriptografada);
		Perfil perfilADM = new Perfil("ROLE_ADMIN");
		usuario.adicionarPerfil(perfilADM);
		
		usuarioRepository.save(usuario);
		TestTransaction.end();
	}

	@AfterEach
	void destroy() {
		TestTransaction.start();
		TestTransaction.flagForCommit();
		fornecedorRepository.deleteAll();
		usuarioRepository.deleteAll();
		TestTransaction.end();
	}

	private String getHost() {
		return UriComponentsBuilder.fromHttpUrl("http://localhost:" + port + "/api").toUriString();
	}
	
	private String getURI() {
		return UriComponentsBuilder.fromHttpUrl("http://localhost:" + port + "/api/fornecedores").toUriString();
	}

	/*
	 * @Sql(statements =
	 * {"ALTER TABLE db_test_florescer.tbl_endereco AUTO_INCREMENT = 1",
	 * "ALTER TABLE db_test_florescer.tbl_fornecedor AUTO_INCREMENT = 1",
	 * "INSERT db_test_florescer.tbl_endereco (cep,logradouro,complemento,bairro,localidade,uf) VALUES ('01001000','Praça da Sé','lado ímpar','Sé','São Paulo', 'sp')"
	 * ,
	 * "INSERT db_test_florescer.tbl_fornecedor (nome,cnpj,id_endereco) VALUES ('Flores plantações e sementes ltda','03538913000154',1)"
	 * }, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
	 * 
	 * @Sql(statements = {"DELETE FROM db_test_florescer.tbl_endereco WHERE id=1",
	 * "DELETE FROM db_test_florescer.tbl_fornecedor WHERE id=1"}, executionPhase =
	 * Sql.ExecutionPhase.AFTER_TEST_METHOD)
	 */

	@Test
	void deveListarFornecedores() throws JsonProcessingException {

		String token = loginService.fazerLogin(getHost(), login, senhaDescriptografada);
		headers.add("Authorization", token);
		
		HttpEntity<String> entity = new HttpEntity<>(null, headers);
		ResponseEntity<List<Fornecedor>> response = testRestTemplate.exchange(getURI(), HttpMethod.GET, entity,
				new ParameterizedTypeReference<List<Fornecedor>>() {
				});
		List<Fornecedor> fornecedorList = response.getBody();
		Integer statusCode = response.getStatusCode().value();

		assertTrue(response != null);
		assertEquals(fornecedorList.size(), 1);
		assertEquals(fornecedorList.get(0).getCnpj(), "93867462000191");
		assertEquals(HttpStatus.OK.value(), statusCode);
	}

	@Test
	void deveCadastrarFornecedor() throws JsonProcessingException {

		String token = loginService.fazerLogin(getHost(), login, senhaDescriptografada);
		headers.add("Authorization", token);
		
		FornecedorForm form = new FornecedorForm("Campo agreste", "93657487000291", new EnderecoForm("41290221"));

		HttpEntity<String> entity = new HttpEntity<>(objectMapper.writeValueAsString(form), headers);
		ResponseEntity<FornecedorView> response = testRestTemplate.exchange(getURI(), HttpMethod.POST, entity,
				FornecedorView.class);

		FornecedorView fornecedorView = response.getBody();
		Integer statusCode = response.getStatusCode().value();

		assertTrue(fornecedorView != null);
		assertEquals(HttpStatus.OK.value(), statusCode);
		assertEquals(fornecedorView.cnpj(), "93657487000291");
		assertEquals(fornecedorView.nome(), "Campo agreste");
	}

}
