package br.com.loja.florescer.service;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import br.com.loja.florescer.form.PedidoProdutoForm;
import br.com.loja.florescer.model.ItemPedido;
import br.com.loja.florescer.model.Pedido;
import br.com.loja.florescer.model.Produto;
import br.com.loja.florescer.repository.ItemPedidoRepository;
import br.com.loja.florescer.repository.ProdutoRepository;

@Service
public class ItemPedidoService {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(ItemPedidoService.class);

	private ItemPedidoRepository itemPedidoRepository;
	private ProdutoRepository produtoRepository;

	public ItemPedidoService(ItemPedidoRepository itemPedidoRepository, ProdutoRepository produtoRepository) {
		this.itemPedidoRepository = itemPedidoRepository;
		this.produtoRepository=produtoRepository;
	}

	public List<ItemPedido> gerarItensPedido(List<PedidoProdutoForm> produtos, Pedido pedido) {
		
		LOGGER.info("Gerando intes para o pedido com id {}", pedido.getId());
		List<ItemPedido> itens = new ArrayList<>();
		produtos.stream().forEach(produto -> {
			Produto produtoBusca = produtoRepository.findById(produto.id()).get();
			ItemPedido item = new ItemPedido(produtoBusca, pedido, produto.quantidade());
			itens.add(item);
		});

		return itens;
	}
	
	public void salvarItensPedido(List<ItemPedido> itens) {
		LOGGER.info("Salvando itens no pedido com id {} na base de dados", itens.get(0).getId().getPedido().getId());
		itens.stream().forEach(item -> {
			itemPedidoRepository.save(item);
		});
	}
}
