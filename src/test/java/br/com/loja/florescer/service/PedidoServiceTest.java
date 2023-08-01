package br.com.loja.florescer.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import br.com.loja.florescer.exception.BusinessException;
import br.com.loja.florescer.form.EnderecoForm;
import br.com.loja.florescer.form.PedidoForm;
import br.com.loja.florescer.form.PedidoProdutoForm;
import br.com.loja.florescer.indicador.StatusEntregaIndicador;
import br.com.loja.florescer.indicador.StatusPedidoIndicador;
import br.com.loja.florescer.indicador.TipoFormaPagamentoIndicador;
import br.com.loja.florescer.model.Cliente;
import br.com.loja.florescer.model.Endereco;
import br.com.loja.florescer.model.Entrega;
import br.com.loja.florescer.model.Fornecedor;
import br.com.loja.florescer.model.ItemPedido;
import br.com.loja.florescer.model.Pedido;
import br.com.loja.florescer.model.Produto;
import br.com.loja.florescer.repository.PedidoRepository;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.times;

@ExtendWith(SpringExtension.class)
public class PedidoServiceTest {

	@InjectMocks
	PedidoService pedidoService;

	@Mock
	ClienteService clienteService;

	@Mock
	ItemPedidoService itemPedidoService;

	@Mock
	ReservaEstoqueService reservaEstoqueService;

	@Mock
	EntregaService entregaService;

	@Mock
	PedidoRepository pedidoRepository;

	@Captor
	ArgumentCaptor<Pedido> argumentCaptorPedido;

	Cliente cliente;

	@BeforeEach
	void setup() {
		cliente = new Cliente("Israel Filho", "05489745693", "1565478421", LocalDate.of(1991, 3, 20),
				new Endereco("41290200", "Rua dos testes cliente", "primeiro andar", "Moca", "São Paulo", "sp"));
	}

	@Test
	void deveCriarPedido() {

		Pedido pedido = new Pedido();

		Endereco enderecoEntrega = new Endereco("41290200", "Rua dos testes cliente", "primeiro andar", "Moca",
				"São Paulo", "sp");
		Endereco enderecoFornecedor = new Endereco("41290221", "Rua dos testes fornecedor", "Casa", "Moca", "São Paulo",
				"sp");

		pedido.adicionarItem(new ItemPedido(new Produto("Rosa", new BigDecimal("15.00"), 30, "sp",
				new Fornecedor("Fornecedor estadual", "412902110001-87", enderecoFornecedor)), pedido, 2));
		pedido.adicionarItem(new ItemPedido(new Produto("Margarida", new BigDecimal("7.00"), 16, "sp",
				new Fornecedor("Fornecedor estadual", "412902110001-87", enderecoFornecedor)), pedido, 1));

		List<PedidoProdutoForm> listProdutosCompra = List.of(new PedidoProdutoForm(1L, 10),
				new PedidoProdutoForm(2L, 5));

		List<ItemPedido> itensPedido = List.of(
				new ItemPedido(
						new Produto("Rosa", new BigDecimal("15.00"), 30, "sp",
								new Fornecedor("Fornecedor estadual", "412902110001-87", enderecoFornecedor)),
						pedido, 2),
				new ItemPedido(
						new Produto("Margarida", new BigDecimal("7.00"), 16, "sp",
								new Fornecedor("Fornecedor estadual", "412902110001-87", enderecoFornecedor)),
						pedido, 1));

		PedidoForm formPedido = new PedidoForm("05489745693", new EnderecoForm("41290200"),
				TipoFormaPagamentoIndicador.AVISTA, listProdutosCompra, "Favor entregar com urgência!!");

		Mockito.when(clienteService.buscarClientePorCpf("05489745693")).thenReturn(Optional.of(cliente));
		Mockito.when(itemPedidoService.gerarItensPedido(listProdutosCompra, pedido)).thenReturn(itensPedido);
		Mockito.when(entregaService.gerarEntrega(formPedido.enderecoForm().cep(), pedido))
				.thenReturn(new Entrega(enderecoEntrega, pedido));

		pedidoService.criarPedido(formPedido);

		Mockito.verify(pedidoRepository, times(1)).save(argumentCaptorPedido.capture());

		Pedido pedidoCaptor = argumentCaptorPedido.getValue();

		Mockito.verify(pedidoRepository, times(1)).save(pedidoCaptor);
		Mockito.verify(itemPedidoService, times(1)).salvarItensPedido(pedidoCaptor.getItens());
		Mockito.verify(reservaEstoqueService, times(1)).reservarEstoqueItensPedido(pedidoCaptor);

		assertTrue(pedidoCaptor.getItens().size() == 2);

		assertNotNull(pedidoCaptor.getCliente());
		assertNotNull(pedidoCaptor.getEntrega());
		assertNotNull(pedidoCaptor.getPagamento());

		assertEquals(pedidoCaptor.getCliente().getCpf(), "05489745693");
		assertEquals(pedidoCaptor.getItens().get(0).getQuantidade(), 2);
		assertEquals(pedidoCaptor.getItens().get(1).getQuantidade(), 1);
		assertEquals(pedidoCaptor.getEntrega().getStatus(), StatusEntregaIndicador.AGUARDANDO_CONFIRMACAO_DE_PAGAMENTO);
		assertEquals(pedidoCaptor.getEntrega().getValor(), new BigDecimal("3.70"));
		assertEquals(pedidoCaptor.getValorTotalItens(), new BigDecimal("37.00"));
		assertEquals(pedidoCaptor.getValorTotalPagamento(), new BigDecimal("40.70"));
		assertEquals(pedidoCaptor.getObservacao(), "Favor entregar com urgência!!");
		assertEquals(pedidoCaptor.getPagamento().getTipoPagamento(), TipoFormaPagamentoIndicador.AVISTA);

	}

	@Test
	void naoDeveCriarPedidoQuandoClienteInexistente() {

		List<PedidoProdutoForm> listProdutosCompra = List.of(new PedidoProdutoForm(1L, 10),
				new PedidoProdutoForm(2L, 5));
		PedidoForm formPedido = new PedidoForm("05489745694", new EnderecoForm("41290200"),
				TipoFormaPagamentoIndicador.AVISTA, listProdutosCompra, "Favor entregar com urgência!!");

		Mockito.when(clienteService.buscarClientePorCpf("05489745693")).thenReturn(Optional.of(cliente));

		assertThrows(BusinessException.class, () -> {
			pedidoService.criarPedido(formPedido);
		});
	}

	@Test
	void deveCancelarPedido() {

		Pedido pedido = new Pedido();
		Endereco enderecoFornecedor = new Endereco("41290221", "Rua dos testes fornecedor", "Casa", "Moca", "São Paulo",
				"sp");
		pedido.adicionarItem(new ItemPedido(new Produto("Rosa", new BigDecimal("15.00"), 30, "sp",
				new Fornecedor("Fornecedor estadual", "412902110001-87", enderecoFornecedor)), pedido, 2));
		pedido.adicionarItem(new ItemPedido(new Produto("Margarida", new BigDecimal("7.00"), 16, "sp",
				new Fornecedor("Fornecedor estadual", "412902110001-87", enderecoFornecedor)), pedido, 1));

		assertEquals(pedido.getStatus(), StatusPedidoIndicador.AGUARDANDO_PAGAMENTO);

		pedidoService.cancelar(pedido);

		assertEquals(pedido.getStatus(), StatusPedidoIndicador.CANCELADO);

		Mockito.verify(reservaEstoqueService, times(1)).cancelar(pedido);
	}

}
