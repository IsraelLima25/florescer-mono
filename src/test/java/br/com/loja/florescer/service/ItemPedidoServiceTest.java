package br.com.loja.florescer.service;

import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import br.com.loja.florescer.form.PedidoProdutoForm;
import br.com.loja.florescer.model.Endereco;
import br.com.loja.florescer.model.Fornecedor;
import br.com.loja.florescer.model.ItemPedido;
import br.com.loja.florescer.model.Pedido;
import br.com.loja.florescer.model.Produto;
import br.com.loja.florescer.repository.ItemPedidoRepository;
import br.com.loja.florescer.repository.ProdutoRepository;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.times;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

@ExtendWith(SpringExtension.class)
public class ItemPedidoServiceTest {

	@InjectMocks
	ItemPedidoService itemPedidoService;

	@Mock
	ProdutoRepository produtoRepository;

	@Mock
	ItemPedidoRepository itemPedidoRepository;
	
	Pedido pedido;
	List<PedidoProdutoForm> produtos;

	@BeforeEach
	void setup() {

		pedido = new Pedido();
		produtos = List.of(new PedidoProdutoForm(1L, 3), new PedidoProdutoForm(2L, 1));

		Endereco enderecoFornecedor = new Endereco("41290221", "Rua dos testes fornecedor", "Casa", "Moca", "São Paulo",
				"sp");
		Optional<Produto> optionalPrimeiroProduto = Optional
				.of(new Produto("Kit de cafe da manha", new BigDecimal("38.00"), 20, "sp",
						new Fornecedor("Fornecedor estadual", "412902110001-87", enderecoFornecedor)));

		Optional<Produto> optionalSegundoProduto = Optional.of(new Produto("Kit de rosas", new BigDecimal("30.00"), 18,
				"sp", new Fornecedor("Fornecedor estadual", "412902110001-87", enderecoFornecedor)));

		Mockito.when(produtoRepository.findById(1L)).thenReturn(optionalPrimeiroProduto);
		Mockito.when(produtoRepository.findById(2L)).thenReturn(optionalSegundoProduto);
	}

	@Test
	void deveGerarItensPedido() {

		List<ItemPedido> itensGeradoPedido = itemPedidoService.gerarItensPedido(produtos, pedido);
		
		assertEquals(2, itensGeradoPedido.size());
		assertEquals(3, itensGeradoPedido.get(0).getQuantidade());
		assertEquals(1, itensGeradoPedido.get(1).getQuantidade());
	}

	@Test
	void deveSalvarItensPedido() {

		List<ItemPedido> itensGeradoPedido = itemPedidoService.gerarItensPedido(produtos, pedido);
		itemPedidoService.salvarItensPedido(itensGeradoPedido);

		ItemPedido itemPedido = itensGeradoPedido.get(0);
		Mockito.verify(itemPedidoRepository, times(2)).save(itemPedido);
	}
}
