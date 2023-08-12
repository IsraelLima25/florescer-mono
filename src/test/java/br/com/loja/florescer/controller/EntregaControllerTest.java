package br.com.loja.florescer.controller;

import static org.hamcrest.CoreMatchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import br.com.loja.florescer.exception.ResourceExceptionHandler;

import br.com.loja.florescer.form.AvaliacaoEntregaForm;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import br.com.loja.florescer.indicador.AvaliacaoIndicador;
import br.com.loja.florescer.indicador.StatusEntregaIndicador;
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

@WebMvcTest
@ContextConfiguration(classes = { EntregaController.class, ResourceExceptionHandler.class }) /* Not loading data entity context */
public class EntregaControllerTest {

	@Autowired
	MockMvc mockMvc;

	@Autowired
	ObjectMapper objectMapper;

	@MockBean
	PedidoRepository pedidoRepository;

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
	void deveMostrarStatusRetornar200() throws Exception {

		Mockito.when(pedidoRepository.findById(1L)).thenReturn(Optional.of(pedido));

		mockMvc.perform(get("/api/entregas/pedido/{idPedido}", 1L).contentType(MediaType.APPLICATION_JSON)).andDo(print())
				.andExpect(jsonPath("$.status", is(StatusEntregaIndicador.AGUARDANDO_CONFIRMACAO_DE_PAGAMENTO.toString())))
				.andExpect(status().isOk());
	}

	@Test
	void naoDeveMostrarStatusPedidoInvalidoRetornar404() throws Exception {
		Mockito.when(pedidoRepository.findById(1L)).thenReturn(Optional.of(pedido));
		mockMvc.perform(get("/api/entregas/pedido/{idPedido}", 2L).contentType(MediaType.APPLICATION_JSON)).andDo(print())
				.andExpect(status().isNotFound());
	}

	@Test
	void deveAvaliarRetornar200() throws Exception {
		
		Mockito.when(pedidoRepository.findById(1L)).thenReturn(Optional.of(pedido));
		AvaliacaoEntregaForm form = new AvaliacaoEntregaForm(1L, AvaliacaoIndicador.THREE_STAR);
		mockMvc.perform(post("/api/entregas/avaliar").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(form))).andDo(print())
				.andExpect(jsonPath("$.status", is(StatusEntregaIndicador.AGUARDANDO_CONFIRMACAO_DE_PAGAMENTO.toString())))
				.andExpect(jsonPath("$.avaliacao", is(AvaliacaoIndicador.THREE_STAR.toString())))
				.andExpect(status().isOk());
	}

	@Test
	void naoDeveAvaliarPedidoInvalidoRetornar404() throws Exception {
		Mockito.when(pedidoRepository.findById(1L)).thenReturn(Optional.of(pedido));
		AvaliacaoEntregaForm form = new AvaliacaoEntregaForm(2L, AvaliacaoIndicador.THREE_STAR);
		mockMvc.perform(post("/api/entregas/avaliar").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(form))).andDo(print())
				.andExpect(status().isNotFound());
	}
	
}
