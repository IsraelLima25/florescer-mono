package br.com.loja.florescer.model;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;

import br.com.loja.florescer.exception.BusinessException;
import br.com.loja.florescer.indicador.AvaliacaoIndicador;

import static org.junit.jupiter.api.Assertions.*;

public class EntregaTest {

	@Test
	void deveCalcularImpostoEstadual() {

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

		BigDecimal valorImpostoEstadualEntrega = entrega.getValor();
		assertEquals(new BigDecimal("3.70"), valorImpostoEstadualEntrega);
	}

	@Test
	void deveCalcularImpostoInterEstadual() {

		Pedido pedido = new Pedido();
		Endereco enderecoClienteInterEstadual = new Endereco("41290200", "Rua dos testes cliente", "primeiro andar",
				"Moca", "São Paulo", "ba");
		Endereco enderecoFornecedor = new Endereco("41290221", "Rua dos testes fornecedor", "Casa", "Moca", "São Paulo",
				"sp");
		pedido.adicionarItem(new ItemPedido(new Produto("Rosa", new BigDecimal("15.00"), 30, "sp",
				new Fornecedor("Fornecedor estadual", "412902110001-87", enderecoFornecedor)), pedido, 2));
		pedido.adicionarItem(new ItemPedido(new Produto("Margarida", new BigDecimal("7.00"), 16, "sp",
				new Fornecedor("Fornecedor estadual", "412902110001-87", enderecoFornecedor)), pedido, 1));

		Entrega entrega = new Entrega(enderecoClienteInterEstadual, pedido);

		BigDecimal valorImpostoEstadualEntrega = entrega.getValor();
		assertEquals(new BigDecimal("7.40"), valorImpostoEstadualEntrega);
	}

	@Test
	void deveCaluclarImpostoEstadualEInterEstadual() {

		Pedido pedido = new Pedido();
		Endereco enderecoClienteInterEstadual = new Endereco("41290200", "Rua dos testes cliente", "primeiro andar",
				"Moca", "São Paulo", "ba");
		Endereco enderecoFornecedor = new Endereco("41290221", "Rua dos testes fornecedor", "Casa", "Moca", "São Paulo",
				"sp");
		pedido.adicionarItem(new ItemPedido(new Produto("Rosa", new BigDecimal("15.00"), 30, "ba",
				new Fornecedor("Fornecedor estadual", "412902110001-87", enderecoFornecedor)), pedido, 2));
		pedido.adicionarItem(new ItemPedido(new Produto("Margarida", new BigDecimal("7.00"), 16, "sp",
				new Fornecedor("Fornecedor estadual", "412902110001-87", enderecoFornecedor)), pedido, 1));

		Entrega entrega = new Entrega(enderecoClienteInterEstadual, pedido);

		BigDecimal valorImpostoEstadualEntrega = entrega.getValor();
		assertEquals(new BigDecimal("4.40"), valorImpostoEstadualEntrega);
	}

	@Test
	void deveAvaliarEntregaAvaliada() {

		Pedido pedido = new Pedido();
		Endereco enderecoClienteInterEstadual = new Endereco("41290200", "Rua dos testes cliente", "primeiro andar",
				"Moca", "São Paulo", "ba");
		Endereco enderecoFornecedor = new Endereco("41290221", "Rua dos testes fornecedor", "Casa", "Moca", "São Paulo",
				"sp");
		pedido.adicionarItem(new ItemPedido(new Produto("Rosa", new BigDecimal("15.00"), 30, "sp",
				new Fornecedor("Fornecedor estadual", "412902110001-87", enderecoFornecedor)), pedido, 2));
		pedido.adicionarItem(new ItemPedido(new Produto("Margarida", new BigDecimal("7.00"), 16, "sp",
				new Fornecedor("Fornecedor estadual", "412902110001-87", enderecoFornecedor)), pedido, 1));

		Entrega entrega = new Entrega(enderecoClienteInterEstadual, pedido);

		entrega.avaliar(AvaliacaoIndicador.FIVE_STAR);

		assertEquals(AvaliacaoIndicador.FIVE_STAR, entrega.getAvaliacao());

	}

	@Test
	void naoDeveAvaliarEntregaAvaliada() {

		Pedido pedido = new Pedido();
		Endereco enderecoClienteInterEstadual = new Endereco("41290200", "Rua dos testes cliente", "primeiro andar",
				"Moca", "São Paulo", "ba");
		Endereco enderecoFornecedor = new Endereco("41290221", "Rua dos testes fornecedor", "Casa", "Moca", "São Paulo",
				"sp");
		pedido.adicionarItem(new ItemPedido(new Produto("Rosa", new BigDecimal("15.00"), 30, "sp",
				new Fornecedor("Fornecedor estadual", "412902110001-87", enderecoFornecedor)), pedido, 2));
		pedido.adicionarItem(new ItemPedido(new Produto("Margarida", new BigDecimal("7.00"), 16, "sp",
				new Fornecedor("Fornecedor estadual", "412902110001-87", enderecoFornecedor)), pedido, 1));

		Entrega entrega = new Entrega(enderecoClienteInterEstadual, pedido);

		entrega.avaliar(AvaliacaoIndicador.FIVE_STAR);

		BusinessException businessException = assertThrows(BusinessException.class, () -> {
			entrega.avaliar(AvaliacaoIndicador.FIVE_STAR);
		});
		
		assertEquals("Atenção!! Esta entrega já foi avaliada", businessException.getMessage());

	}

}
