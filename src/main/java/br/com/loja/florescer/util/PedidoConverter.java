package br.com.loja.florescer.util;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import br.com.loja.florescer.model.Pedido;
import br.com.loja.florescer.view.ItemPedidoView;
import br.com.loja.florescer.view.PedidoView;

@Component
public class PedidoConverter {
	
	private EnderecoConverter enderecoConverter;

	public PedidoConverter(EnderecoConverter enderecoConverter) {
		this.enderecoConverter = enderecoConverter;
	}

	public PedidoView toPedidoView(Pedido pedido) {
		
		List<ItemPedidoView> itensPedido = pedido.getItens().stream().map(itemPedido -> {
			return new ItemPedidoView(itemPedido.getId().getProduto().getDescricao(), itemPedido.getQuantidade(), itemPedido.getId().getProduto().getPreco());
		}).collect(Collectors.toList());
		
		PedidoView viewPedido = new PedidoView(pedido.getId(),pedido.getCliente().getCpf(),
				pedido.getInstante(),itensPedido,pedido.getPagamento().getTipoPagamento(), pedido.getPagamento().getStatus(),
				enderecoConverter.toEnderecoView(pedido.getEntrega().getEndereco()),pedido.getEntrega().getValor(),
				pedido.getValorTotalItens(),pedido.getValorTotalPagamento());
		
		return viewPedido;
	}
	
}
