package br.com.loja.florescer.controller;

import static org.hamcrest.CoreMatchers.is;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

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

import br.com.loja.florescer.exception.ResourceExceptionHandler;
import br.com.loja.florescer.form.ProdutoForm;
import br.com.loja.florescer.model.Endereco;
import br.com.loja.florescer.model.Fornecedor;
import br.com.loja.florescer.model.Produto;
import br.com.loja.florescer.repository.FornecedorRepository;
import br.com.loja.florescer.repository.ProdutoRepository;

import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest
@ContextConfiguration(classes = { ProdutoController.class,
		ResourceExceptionHandler.class }) /* Not loading data entity context */
public class ProdutoControllerTest {

	@Autowired
	MockMvc mockMvc;

	@Autowired
	ObjectMapper objectMapper;

	@MockBean
	ProdutoRepository produtoRepository;

	@MockBean
	FornecedorRepository fornecedorRepository;

	Produto produto;

	Fornecedor fornecedor;

	@BeforeEach
	void setup() {

		Endereco enderecoFornecedor = new Endereco("41290221", "Rua dos testes fornecedor", "Casa", "Moca", "São Paulo",
				"sp");
		fornecedor = new Fornecedor(1L, "Fornecedor estadual", "412902110001-87", enderecoFornecedor);
		produto = new Produto("Kit de cafe da manha", new BigDecimal("38.00"), 20, "sp", fornecedor);
	}

	@Test
	@WithMockUser
	void deveListarProdutosRetornar200() throws Exception {

		Mockito.when(produtoRepository.findAll()).thenReturn(List.of(produto));

		mockMvc.perform(get("/api/produtos").contentType(MediaType.APPLICATION_JSON)).andDo(print())
				.andExpect(jsonPath("$").isArray()).andExpect(jsonPath("$[0].descricao", is("Kit de cafe da manha")))
				.andExpect(status().isOk());
	}

	@Test
	@WithMockUser
	void deveCadastrarProdutoRetornar200() throws Exception {

		Mockito.when(fornecedorRepository.findById(1L)).thenReturn(Optional.of(fornecedor));

		ProdutoForm form = new ProdutoForm("Rosa", new BigDecimal("7.00"), 15, "sp", 1L);

		mockMvc.perform(post("/api/produtos").with(csrf()).contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(form))).andDo(print())
				.andExpect(jsonPath("$.descricao", is("Rosa"))).andExpect(status().isOk());
	}

	@Test
	@WithMockUser
	void naoDeveCadastrarProdutoFornecedorInvalidoRetornar404() throws Exception {

		Mockito.when(fornecedorRepository.findById(1L)).thenReturn(Optional.of(fornecedor));

		ProdutoForm form = new ProdutoForm("Rosa", new BigDecimal("7.00"), 15, "sp", 2L);

		mockMvc.perform(post("/api/produtos").with(csrf()).contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(form))).andDo(print()).andExpect(status().isNotFound());
	}

}
