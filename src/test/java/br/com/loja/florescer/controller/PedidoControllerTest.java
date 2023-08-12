package br.com.loja.florescer.controller;

import static org.hamcrest.CoreMatchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import br.com.loja.florescer.exception.ResourceExceptionHandler;
import br.com.loja.florescer.form.EnderecoForm;
import br.com.loja.florescer.form.PedidoForm;
import br.com.loja.florescer.form.PedidoProdutoForm;
import br.com.loja.florescer.indicador.TipoFormaPagamentoIndicador;
import br.com.loja.florescer.model.Cliente;
import br.com.loja.florescer.model.Endereco;
import br.com.loja.florescer.model.Entrega;
import br.com.loja.florescer.model.Fornecedor;
import br.com.loja.florescer.model.ItemPedido;
import br.com.loja.florescer.model.Pagamento;
import br.com.loja.florescer.model.Pedido;
import br.com.loja.florescer.model.Produto;
import br.com.loja.florescer.repository.PedidoRepository;
import br.com.loja.florescer.service.PedidoService;
import br.com.loja.florescer.util.EnderecoConverter;
import br.com.loja.florescer.util.PedidoConverter;

@WebMvcTest
@ContextConfiguration(classes = { PedidoController.class,
		ResourceExceptionHandler.class }) /* Not loading data entity context */
public class PedidoControllerTest {

	@Autowired
	MockMvc mockMvc;

	@Autowired
	ObjectMapper objectMapper;

	@MockBean
	PedidoService pedidoService;

	@SpyBean
	PedidoConverter pedidoConverter;
	
	@SpyBean
	EnderecoConverter enderecoConverter;

	@MockBean
	PedidoRepository pedidoRepository;
	
	@Captor
	ArgumentCaptor<Pedido> argumentCaptorPedido;

	Pedido pedido;

	Cliente cliente;

	@BeforeEach
	void setup() {
		pedido = new Pedido(1L);

		cliente = new Cliente("Israel Filho", "05489745693", "1565478421", LocalDate.of(1991, 3, 20),
				new Endereco("41290200", "Rua dos testes cliente", "primeiro andar", "Moca", "São Paulo", "sp"));

		Endereco enderecoEntrega = new Endereco("41290200", "Rua dos testes cliente", "primeiro andar", "Moca",
				"São Paulo", "sp");
		Endereco enderecoFornecedor = new Endereco("41290221", "Rua dos testes fornecedor", "Casa", "Moca", "São Paulo",
				"sp");

		pedido.adicionarItem(new ItemPedido(new Produto("Rosa", new BigDecimal("15.00"), 30, "sp",
				new Fornecedor("Fornecedor estadual", "412902110001-87", enderecoFornecedor)), pedido, 2));
		pedido.adicionarItem(new ItemPedido(new Produto("Margarida", new BigDecimal("7.00"), 16, "sp",
				new Fornecedor("Fornecedor estadual", "412902110001-87", enderecoFornecedor)), pedido, 1));

		pedido.adicionarCliente(cliente);
		pedido.adicionarEntrega(new Entrega(enderecoEntrega, pedido));
		pedido.calcularValorTotal();
		pedido.adicionarFormaPagamento(new Pagamento(TipoFormaPagamentoIndicador.PIX, pedido.getValorTotalPagamento()));
	}

	@Test
	void deveCriarPedidoRetornar200() throws Exception {
		
		PedidoForm form = new PedidoForm("05489745693", new EnderecoForm("41654789"), TipoFormaPagamentoIndicador.PIX, 
				List.of(new PedidoProdutoForm(1L,2)),"");
		
		Mockito.when(pedidoService.criarPedido(form)).thenReturn(pedido);
		
		mockMvc.perform(post("/api/pedidos").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(form)))
		.andDo(print())
		.andExpect(jsonPath("$.cpfCliente", is("05489745693")))
		.andExpect(status().isOk());
	}
	
	@Test
	void deveBuscarPedidoPorCpfClienteRetornar200() throws Exception {
		
		Mockito.when(pedidoRepository.findByClienteCpf("05489745693")).thenReturn(Optional.of(List.of(pedido)));
		
		mockMvc.perform(get("/api/pedidos/cliente/{cpf}","05489745693").contentType(MediaType.APPLICATION_JSON))
		.andDo(print())
		.andExpect(jsonPath("$").isArray())
		.andExpect(jsonPath("$[0].cpfCliente", is("05489745693")))
		.andExpect(status().isOk());
	}
	
	@Test
	void naoDeveBuscarPedidoPorCpfClienteInvalidoRetornar404() throws Exception {
		
		Mockito.when(pedidoRepository.findByClienteCpf("05489400300")).thenReturn(Optional.of(List.of()));
		
		mockMvc.perform(get("/api/pedidos/cliente/{cpf}","05489400300").contentType(MediaType.APPLICATION_JSON))
		.andDo(print())
		.andExpect(status().isNotFound());
	}
	
	@Test
	void deveCancelarPedidoPorCpfClienteRetornar200() throws Exception {
		
		Mockito.when(pedidoRepository.findById(1L)).thenReturn(Optional.of(pedido));
		mockMvc.perform(post("/api/pedidos/cancelar/{idPedido}", 2).contentType(MediaType.APPLICATION_JSON))
		.andDo(print())
		.andExpect(status().isNotFound());
	}
	
	@Test
	void naoDeveCancelarPedidoPorCpfClienteInvalidoRetornar404() throws Exception {
		
		Mockito.when(pedidoRepository.findById(1L)).thenReturn(Optional.of(pedido));
		
		mockMvc.perform(post("/api/pedidos/cancelar/{idPedido}", 1).contentType(MediaType.APPLICATION_JSON))
		.andDo(print())
		.andExpect(status().isOk());
	}

}
