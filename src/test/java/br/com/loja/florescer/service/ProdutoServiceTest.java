package br.com.loja.florescer.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import br.com.loja.florescer.model.Endereco;
import br.com.loja.florescer.model.Fornecedor;
import br.com.loja.florescer.model.Produto;

import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;

@ExtendWith(SpringExtension.class)
public class ProdutoServiceTest {
	
	@InjectMocks
	ProdutoService produtoService;
	
	@Test
	void deveAbaterEstoque() {
		
		Endereco enderecoFornecedor = new Endereco("41290221", "Rua dos testes fornecedor", "Casa", "Moca", "São Paulo",
				"sp");
		
		Produto produto = new Produto("Kit de cafe da manha", new BigDecimal("38.00"), 20, "sp",
						new Fornecedor("Fornecedor estadual", "412902110001-87", enderecoFornecedor));
		
		produtoService.abaterEstoque(produto, 10);
		
		assertEquals(10, produto.getQuantidadeEstoque());
	}
}
