package br.com.loja.florescer.service;

import br.com.loja.florescer.api.ApiEnderecoService;
import br.com.loja.florescer.indicador.StatusEntregaIndicador;
import br.com.loja.florescer.indicador.StatusPedidoIndicador;
import br.com.loja.florescer.indicador.TipoFormaPagamentoIndicador;
import br.com.loja.florescer.model.Endereco;
import br.com.loja.florescer.model.Entrega;
import br.com.loja.florescer.model.Fornecedor;
import br.com.loja.florescer.model.ItemPedido;
import br.com.loja.florescer.model.Pagamento;
import br.com.loja.florescer.model.Pedido;
import br.com.loja.florescer.model.Produto;
import br.com.loja.florescer.util.EnderecoConverter;
import br.com.loja.florescer.view.EnderecoView;

import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;

@ExtendWith(SpringExtension.class)
public class PagamentoServiceTest {

	@InjectMocks
	PagamentoService pagamentoService;
	
	@InjectMocks
	EntregaService entregaService;
	
	@Mock
	ApiEnderecoService apiEnderecoService;

	@Mock
	EnderecoConverter enderecoConverter;
	
	@Test
	void deveProcessarPagamento() {
		
		Pedido pedido = new Pedido();
		
		EnderecoView viewEndereco = new EnderecoView("45693654", "Rua dos testes cliente", "primeiro andar", "Moca",
				"São Paulo", "sp");

		Mockito.when(apiEnderecoService.requestEnderecoJSON("45693654")).thenReturn(viewEndereco);

		Mockito.when(enderecoConverter.toEndereco(viewEndereco))
				.thenReturn(new Endereco("41290221", "Rua dos testes cliente", "Casa", "Moca", "São Paulo", "sp"));
		
		Endereco enderecoFornecedor = new Endereco("41290221", "Rua dos testes fornecedor", "Casa", "Moca", "São Paulo",
				"sp");
		pedido.adicionarItem(new ItemPedido(new Produto("Rosa", new BigDecimal("15.00"), 30, "sp",
				new Fornecedor("Fornecedor estadual", "412902110001-87", enderecoFornecedor)), pedido, 2));
		pedido.adicionarItem(new ItemPedido(new Produto("Margarida", new BigDecimal("7.00"), 16, "sp",
				new Fornecedor("Fornecedor estadual", "412902110001-87", enderecoFornecedor)), pedido, 1));
		pedido.adicionarFormaPagamento(new Pagamento(TipoFormaPagamentoIndicador.PIX, new BigDecimal("40.00")));
		Entrega entregaGerada = entregaService.gerarEntrega("45693654", pedido);
		pedido.adicionarEntrega(entregaGerada);
		pedido.calcularValorTotal();
		
		pagamentoService.pagar(pedido);
		
		assertEquals(StatusPedidoIndicador.PAGAMENTO_PROCESSADO, pedido.getStatus());
		assertEquals(StatusEntregaIndicador.PREPARANDO_PACOTE, pedido.getEntrega().getStatus());
	}
}
