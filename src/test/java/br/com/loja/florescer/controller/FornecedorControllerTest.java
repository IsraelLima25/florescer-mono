package br.com.loja.florescer.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;

import com.fasterxml.jackson.databind.ObjectMapper;

import br.com.loja.florescer.form.EnderecoForm;

import br.com.loja.florescer.api.ApiEnderecoService;
import br.com.loja.florescer.form.FornecedorForm;
import br.com.loja.florescer.model.Endereco;
import br.com.loja.florescer.model.Fornecedor;
import br.com.loja.florescer.repository.FornecedorRepository;
import br.com.loja.florescer.view.EnderecoView;

@WebMvcTest
@ContextConfiguration(classes = { FornecedorController.class }) /* Not loading data entity context */
public class FornecedorControllerTest {
	
	@Autowired
	MockMvc mockMvc;

	@Autowired
	ObjectMapper objectMapper;
	
	@MockBean
	FornecedorRepository fornecedorRespository;

	@MockBean
	ApiEnderecoService apiEnderecoService;
	
	List<Fornecedor> fornecedores = new ArrayList<>();
	
	@BeforeEach
	void setup() {
		
		fornecedores.add(new Fornecedor("Fornecedor Rosa Linda", "24942435000113", 
				new Endereco("68908122", "Rua dos testes cliente", "primeiro andar", "Moca",
				"São Paulo", "sp")));
	}
	
	@Test
	@WithMockUser
	void deveListarTodosRetornar200() throws Exception {
		
		Mockito.when(fornecedorRespository.findAll()).thenReturn(fornecedores);

		mockMvc.perform(get("/api/fornecedores").contentType(MediaType.APPLICATION_JSON)).andDo(print())
				.andExpect(jsonPath("$").isArray())
				.andExpect(jsonPath("$[0].nome", is("Fornecedor Rosa Linda")))
				.andExpect(jsonPath("$[0].cnpj", is("24942435000113")))
				.andExpect(status().isOk());
	}
	
	@Test
	@WithMockUser
	void deveCadastrarRetornar200() throws Exception {
		
		Mockito.when(apiEnderecoService.requestEnderecoJSON("24210395")).thenReturn(new EnderecoView("68908122", "Rua dos testes cliente", "primeiro andar", "Moca",
				"São Paulo", "sp"));
		
		FornecedorForm form = new FornecedorForm("Fornecedor campo largo", "04265112000125", new EnderecoForm("24210395"));
		mockMvc.perform(post("/api/fornecedores").with(csrf()).contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(form))).andDo(print())
				.andExpect(jsonPath("$.nome", is("Fornecedor campo largo")))
				.andExpect(jsonPath("$.cnpj", is("04265112000125")))
				.andExpect(status().isOk());
		
		
	}

}
