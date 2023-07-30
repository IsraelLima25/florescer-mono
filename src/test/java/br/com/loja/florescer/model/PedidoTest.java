package br.com.loja.florescer.model;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class PedidoTest {

	@Test
	void deveCalcularValorTotalPedido() {

		Pedido pedido = new Pedido();
		Endereco enderecoClienteEstadual = new Endereco("41290200", "Rua dos testes cliente", "primeiro andar", "Moca",
				"São Paulo", "sp");
		Endereco enderecoFornecedor = new Endereco("41290221", "Rua dos testes fornecedor", "Casa", "Moca", "São Paulo",
				"sp");
		pedido.adicionarItem(new ItemPedido(new Produto("Rosa", new BigDecimal("15.00"), 30, "sp",
				new Fornecedor("Fornecedor estadual", "412902110001-87", enderecoFornecedor)), pedido, 2));
		pedido.adicionarItem(new ItemPedido(new Produto("Margarida", new BigDecimal("7.00"), 16, "sp",
				new Fornecedor("Fornecedor estadual", "412902110001-87", enderecoFornecedor)), pedido, 1));

		Entrega entrega = new Entrega(enderecoClienteEstadual, pedido);
		pedido.adicionarEntrega(entrega);
		
		pedido.calcularValorTotal();
		
		BigDecimal valorTotalItensComprado = pedido.getValorTotalItens();
		BigDecimal valorTotalPagamento = pedido.getValorTotalPagamento();
		
		assertEquals(new BigDecimal("37.00"), valorTotalItensComprado);
		assertEquals(new BigDecimal("40.70"), valorTotalPagamento);

	}

}
