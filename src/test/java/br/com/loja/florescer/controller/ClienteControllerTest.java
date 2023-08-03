package br.com.loja.florescer.controller;

import static org.hamcrest.CoreMatchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import br.com.loja.florescer.api.ApiEnderecoService;
import br.com.loja.florescer.form.ClienteForm;import br.com.loja.florescer.form.EnderecoForm;
import br.com.loja.florescer.model.Cliente;
import br.com.loja.florescer.model.Endereco;
import br.com.loja.florescer.repository.ClienteRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import br.com.loja.florescer.service.ClienteService;
import br.com.loja.florescer.util.ClienteConverter;
import br.com.loja.florescer.util.EnderecoConverter;
import br.com.loja.florescer.view.EnderecoView;

@WebMvcTest
@ContextConfiguration(classes = { ClienteController.class }) /* Not loading data entity context */
public class ClienteControllerTest {

	@Autowired
	MockMvc mockMvc;
	
	@Autowired
	ObjectMapper objectMapper;

	@MockBean
	ClienteRepository clienteRepository;

	@MockBean
	ApiEnderecoService apiEnderecoService;

	@SpyBean
	ClienteConverter clienteConverter;

	@SpyBean
	EnderecoConverter enderecoConverter;

	@MockBean
	ClienteService clienteService;

	List<Cliente> clientes = new ArrayList<>();

	@BeforeEach
	void setup() {

		clientes.addAll(List.of(
				new Cliente("Israel Filho", "05489745693", "1565478421", LocalDate.of(1991, 3, 20),
						new Endereco("41290200", "Rua dos testes cliente", "primeiro andar", "Moca", "São Paulo",
								"sp")),
				new Cliente("Lewis Davila", "04563214568", "1236985273", LocalDate.of(1991, 3, 20),
						new Endereco("32698753", "Rua das integrações", "Casa", "botafogo", "Rio de Janeiro", "rj"))));
	}

	@Test
	void deveListarTodosClientesRetornarStatus200() throws Exception {
		
		Mockito.when(clienteRepository.findAll()).thenReturn(clientes);

		mockMvc.perform(get("/api/clientes").contentType(MediaType.APPLICATION_JSON)).andDo(print())
				.andExpect(jsonPath("$").isArray())
				.andExpect(jsonPath("$[0].cpf", is("05489745693")))
				.andExpect(jsonPath("$[1].cpf", is("04563214568")))
				.andExpect(status().isOk());
	}
	
	@Test
	void deveBuscarClientePorCpfRetornarStatus200() throws Exception {
		
		Mockito.when(clienteService.buscarClientePorCpf("05489745693")).thenReturn(Optional.of(clientes.get(0)));

		mockMvc.perform(get("/api/clientes/{cpf}", "05489745693").contentType(MediaType.APPLICATION_JSON))
				.andDo(print())
				.andExpect(jsonPath("$.cpf", is("05489745693")))
				.andExpect(status().isOk());
	}
	
	@Test
	void naoDeveRetornarClienteCpfInvalidoRetornarStatus404() throws Exception {
		
		Mockito.when(clienteService.buscarClientePorCpf("05489745693")).thenReturn(Optional.of(clientes.get(0)));

		mockMvc.perform(get("/api/clientes/{cpf}", "1236985273").contentType(MediaType.APPLICATION_JSON))
				.andDo(print())
				.andExpect(jsonPath("$").doesNotExist())
				.andExpect(status().isNotFound());
	}
	
	@Test
	void deveCadastrarClienteRetornarStatus200() throws Exception {
		
		EnderecoView viewEndereco = new EnderecoView("45693654", "Rua dos testes cliente", "primeiro andar", "Moca",
				"São Paulo", "sp");
		Mockito.when(apiEnderecoService.requestEnderecoJSON("41654789")).thenReturn(viewEndereco);
		
		ClienteForm clienteForm = new ClienteForm("Lewis Norton", "04563214520","1565478300", 
				LocalDate.of(1989, 4, 10), new EnderecoForm("41654789"));
		
		mockMvc.perform(post("/api/clientes").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(clienteForm)))
		.andDo(print())
		.andExpect(jsonPath("$.cpf", is("04563214520")))
		.andExpect(jsonPath("$.rg", is("1565478300")))
		.andExpect(status().isOk());
		
	}
	
	@Test
	void deveAtualizarClienteRetornarStatus200() throws Exception {
		
		EnderecoView viewEndereco = new EnderecoView("41654789", "Rua dos testes cliente", "primeiro andar", "Moca",
				"São Paulo", "sp");
		
		Mockito.when(apiEnderecoService.requestEnderecoJSON("41654789")).thenReturn(viewEndereco);
		Mockito.when(clienteRepository.findByCpf("05489745693")).thenReturn(Optional.of(clientes.get(0)));
		
		ClienteForm clienteForm = new ClienteForm("Lewis Norton", "04563214520","1565478300", 
				LocalDate.of(1989, 4, 10), new EnderecoForm("41654789"));
		
		mockMvc.perform(put("/api/clientes/{cpf}", "05489745693").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(clienteForm)))
		.andDo(print())
		.andExpect(jsonPath("$.nome", is("Lewis Norton")))
		.andExpect(jsonPath("$.cpf", is("04563214520")))
		.andExpect(jsonPath("$.rg", is("1565478300")))
		.andExpect(status().isOk());
		
	}
	
	@Test
	void naoDeveAtualizarClienteRetornarStatus404() throws Exception {
		
		EnderecoView viewEndereco = new EnderecoView("41654789", "Rua dos testes cliente", "primeiro andar", "Moca",
				"São Paulo", "sp");
		
		Mockito.when(apiEnderecoService.requestEnderecoJSON("41654789")).thenReturn(viewEndereco);
		Mockito.when(clienteRepository.findByCpf("05489745693")).thenReturn(Optional.of(clientes.get(0)));
		
		ClienteForm clienteForm = new ClienteForm("Lewis Norton", "04563214520","1565478300", 
				LocalDate.of(1989, 4, 10), new EnderecoForm("41654789"));
		
		mockMvc.perform(put("/api/clientes/{cpf}", "12365412389").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(clienteForm)))
		.andDo(print())
		.andExpect(jsonPath("$").doesNotExist())
		.andExpect(status().isNotFound());
		
	}

}
