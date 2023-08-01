package br.com.loja.florescer.service;

import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import br.com.loja.florescer.api.ApiEnderecoService;
import br.com.loja.florescer.indicador.StatusEntregaIndicador;
import br.com.loja.florescer.model.Endereco;
import br.com.loja.florescer.model.Entrega;
import br.com.loja.florescer.model.Fornecedor;
import br.com.loja.florescer.model.ItemPedido;
import br.com.loja.florescer.model.Pedido;
import br.com.loja.florescer.model.Produto;
import br.com.loja.florescer.util.EnderecoConverter;
import br.com.loja.florescer.view.EnderecoView;

@ExtendWith(SpringExtension.class)
public class EntregaServiceTest {

	@InjectMocks
	EntregaService entregaService;

	@Mock
	ApiEnderecoService apiEnderecoService;

	@Mock
	EnderecoConverter enderecoConverter;

	@Test
	void deveGerarEntrega() {

		EnderecoView viewEndereco = new EnderecoView("45693654", "Rua dos testes cliente", "primeiro andar", "Moca",
				"São Paulo", "sp");

		Mockito.when(apiEnderecoService.requestEnderecoJSON("45693654")).thenReturn(viewEndereco);

		Mockito.when(enderecoConverter.toEndereco(viewEndereco))
				.thenReturn(new Endereco("41290221", "Rua dos testes cliente", "Casa", "Moca", "São Paulo", "sp"));

		Pedido pedido = new Pedido();
		Endereco enderecoFornecedor = new Endereco("41290221", "Rua dos testes fornecedor", "Casa", "Moca", "São Paulo",
				"sp");
		pedido.adicionarItem(new ItemPedido(new Produto("Rosa", new BigDecimal("15.00"), 30, "sp",
				new Fornecedor("Fornecedor estadual", "412902110001-87", enderecoFornecedor)), pedido, 2));
		pedido.adicionarItem(new ItemPedido(new Produto("Margarida", new BigDecimal("7.00"), 16, "sp",
				new Fornecedor("Fornecedor estadual", "412902110001-87", enderecoFornecedor)), pedido, 1));

		Entrega entregaGerada = entregaService.gerarEntrega("45693654", pedido);

		assertNotNull(entregaGerada);
		assertEquals("Rua dos testes cliente", entregaGerada.getEndereco().getLogradouro());
		assertTrue(entregaGerada.getStatus().equals(StatusEntregaIndicador.AGUARDANDO_CONFIRMACAO_DE_PAGAMENTO));
		assertEquals(new BigDecimal("3.70"), entregaGerada.getValor());
	}

}
