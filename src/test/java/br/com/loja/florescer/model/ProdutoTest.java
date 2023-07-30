package br.com.loja.florescer.model;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;

import br.com.loja.florescer.exception.BusinessException;

import static org.junit.jupiter.api.Assertions.*;

public class ProdutoTest {
	
	@Test
	void deveAbaterEstoque() {
		Endereco enderecoFornecedor = new Endereco("41290221", "Rua dos testes fornecedor", "Casa", "Moca", "São Paulo",
				"sp");
		Produto produto = new Produto("Kit de cafe da manha", new BigDecimal("38.00"), 20, "sp",
				new Fornecedor("Fornecedor estadual", "412902110001-87", enderecoFornecedor));
		
		produto.abater(10);
		
		assertEquals(10, produto.getQuantidadeEstoque());
	}
	
	@Test
	void deveDevolverEstoque() { 
		
		Endereco enderecoFornecedor = new Endereco("41290221", "Rua dos testes fornecedor", "Casa", "Moca", "São Paulo",
				"sp");
		Produto produto = new Produto("Kit de cafe da manha", new BigDecimal("38.00"), 20, "sp",
				new Fornecedor("Fornecedor estadual", "412902110001-87", enderecoFornecedor));
		
		produto.devolver(10);
		
		assertEquals(30, produto.getQuantidadeEstoque());
	}
	
	@Test
	void naoDeveAbaterEstoqueQuantidadeInvalida() {
		
		Endereco enderecoFornecedor = new Endereco("41290221", "Rua dos testes fornecedor", "Casa", "Moca", "São Paulo",
				"sp");
		Produto produto = new Produto("Kit de cafe da manha", new BigDecimal("38.00"), 20, "sp",
				new Fornecedor("Fornecedor estadual", "412902110001-87", enderecoFornecedor));
		
		BusinessException businessException = assertThrows(BusinessException.class, () -> {
			produto.abater(0);
		});
		
		assertEquals(20, produto.getQuantidadeEstoque());
		assertEquals("Quantidade de abatimento inválida", businessException.getMessage());
	}
	
	@Test
	void naoDeveDevolverEstoqueQuantidadeInvalida() {
		
		Endereco enderecoFornecedor = new Endereco("41290221", "Rua dos testes fornecedor", "Casa", "Moca", "São Paulo",
				"sp");
		Produto produto = new Produto("Kit de cafe da manha", new BigDecimal("38.00"), 20, "sp",
				new Fornecedor("Fornecedor estadual", "412902110001-87", enderecoFornecedor));
		
		BusinessException businessException = assertThrows(BusinessException.class, () -> {
			produto.devolver(0);
		});
		
		assertEquals(20, produto.getQuantidadeEstoque());
		assertEquals("Quantidade de devolução inválida", businessException.getMessage());
	}
}
