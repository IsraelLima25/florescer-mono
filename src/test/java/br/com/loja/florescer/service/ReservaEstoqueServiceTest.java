package br.com.loja.florescer.service;

import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import br.com.loja.florescer.exception.BusinessException;
import br.com.loja.florescer.model.Endereco;
import br.com.loja.florescer.model.Fornecedor;
import br.com.loja.florescer.model.ItemPedido;
import br.com.loja.florescer.model.Pedido;
import br.com.loja.florescer.model.Produto;
import br.com.loja.florescer.model.ReservaEstoque;
import br.com.loja.florescer.repository.ProdutoRepository;
import br.com.loja.florescer.repository.ReservaEstoqueRepository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.times;

@ExtendWith(SpringExtension.class)
public class ReservaEstoqueServiceTest {

	@InjectMocks
	ReservaEstoqueService reservaEstoqueService;

	@Mock
	ReservaEstoqueRepository reservaEstoqueRepository;

	@Mock
	ProdutoRepository produtoRepository;
	
	@Spy
	ProdutoService produtoService;
	
	@Captor
	ArgumentCaptor<ReservaEstoque> reservaEstoqueCaptor;
	
	Pedido pedido;
	
	Endereco enderecoFornecedor;
	
	@BeforeEach
	void setup() {
		pedido = new Pedido();
		enderecoFornecedor = new Endereco("41290221", "Rua dos testes fornecedor", "Casa", "Moca", "São Paulo", "sp");
	}
	
	@Test
	void deveReservarEstoqueItens() {
		
		Produto primeiroProduto = new Produto(1L,"Rosa", new BigDecimal("15.00"), 30, "sp",
				new Fornecedor("Fornecedor estadual", "412902110001-87", enderecoFornecedor));
		Produto segundoProduto = new Produto(2L,"Margarida", new BigDecimal("7.00"), 16, "sp",
				new Fornecedor("Fornecedor estadual", "412902110001-87", enderecoFornecedor));
		
		pedido.adicionarItem(new ItemPedido(primeiroProduto, pedido, 2));
		pedido.adicionarItem(new ItemPedido(segundoProduto, pedido, 1));
		
		Mockito.when(produtoRepository.findById(1L)).thenReturn(Optional.of(primeiroProduto));
		Mockito.when(produtoRepository.findById(2L)).thenReturn(Optional.of(segundoProduto));
		
		reservaEstoqueService.reservarEstoqueItensPedido(pedido);
		
		assertEquals(28, primeiroProduto.getQuantidadeEstoque());
		assertEquals(15, segundoProduto.getQuantidadeEstoque());
		
		Mockito.verify(produtoRepository, times(1)).save(primeiroProduto);
		Mockito.verify(produtoRepository, times(1)).save(segundoProduto);
		
		Mockito.verify(reservaEstoqueRepository, times(2)).save(reservaEstoqueCaptor.capture());
		
		List<ReservaEstoque> reservas = reservaEstoqueCaptor.getAllValues();
		
		assertEquals("Rosa", reservas.get(0).getProduto().getDescricao());
		assertEquals("Margarida", reservas.get(1).getProduto().getDescricao());
	}

	@Test
	void naoDeveReservarEstoqueQuandoQuantidadeSolicitadaMaiorQueQunatidadeEstocada() {
		
		Produto primeiroProduto = new Produto(1L,"Rosa", new BigDecimal("15.00"), 30, "sp",
				new Fornecedor("Fornecedor estadual", "412902110001-87", enderecoFornecedor));
		Produto segundoProduto = new Produto(2L,"Margarida", new BigDecimal("7.00"), 16, "sp",
				new Fornecedor("Fornecedor estadual", "412902110001-87", enderecoFornecedor));
		
		pedido.adicionarItem(new ItemPedido(primeiroProduto, pedido, 40));
		pedido.adicionarItem(new ItemPedido(segundoProduto, pedido, 1));
		
		Mockito.when(produtoRepository.findById(1L)).thenReturn(Optional.of(primeiroProduto));
		Mockito.when(produtoRepository.findById(2L)).thenReturn(Optional.of(segundoProduto));
		
		assertThrows(BusinessException.class, () -> {
			reservaEstoqueService.reservarEstoqueItensPedido(pedido);
		});
	}

	@Test
	void deveCancelarReservaEstoque() {
		
		Pedido pedido = new Pedido(1L);
		Endereco enderecoFornecedor = new Endereco("41290221", "Rua dos testes fornecedor", "Casa", "Moca", "São Paulo",
				"sp");
		Produto primeiroProduto = new Produto(1L,"Rosa", new BigDecimal("15.00"), 30, "sp",
				new Fornecedor("Fornecedor estadual", "412902110001-87", enderecoFornecedor));
		Produto segundoProduto = new Produto(2L,"Margarida", new BigDecimal("7.00"), 16, "sp",
				new Fornecedor("Fornecedor estadual", "412902110001-87", enderecoFornecedor));
		
		pedido.adicionarItem(new ItemPedido(primeiroProduto, pedido, 10));
		pedido.adicionarItem(new ItemPedido(segundoProduto, pedido, 1));
		
		List<ReservaEstoque> reservas = List.of(new ReservaEstoque(pedido, primeiroProduto, pedido.getItens().get(0).getQuantidade()),
				new ReservaEstoque(pedido, segundoProduto, pedido.getItens().get(1).getQuantidade()));
		
		Mockito.when(reservaEstoqueRepository.findByPedidoId(1L)).thenReturn(reservas);
		
		reservaEstoqueService.cancelar(pedido);
		
		assertEquals(40, reservas.get(0).getProduto().getQuantidadeEstoque());
		assertEquals(17, segundoProduto.getQuantidadeEstoque());
		
		Mockito.verify(reservaEstoqueRepository, times(1)).deleteByPedidoId(pedido.getId());
	}
}
