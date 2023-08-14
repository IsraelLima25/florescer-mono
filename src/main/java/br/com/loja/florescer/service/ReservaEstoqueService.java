package br.com.loja.florescer.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import br.com.loja.florescer.exception.BusinessException;
import br.com.loja.florescer.model.Pedido;
import br.com.loja.florescer.model.ReservaEstoque;
import br.com.loja.florescer.repository.ProdutoRepository;
import br.com.loja.florescer.repository.ReservaEstoqueRepository;

@Service
public class ReservaEstoqueService {

	private static final Logger LOGGER = LoggerFactory.getLogger(ReservaEstoqueService.class);
	
	private ProdutoRepository produtoRepository;
	private ProdutoService produtoService;
	private ReservaEstoqueRepository reservaEstoqueRepository; 
	
	public ReservaEstoqueService(ProdutoRepository produtoRepository, ProdutoService produtoService, ReservaEstoqueRepository reservaEstoqueRepository) {
		this.produtoRepository = produtoRepository;
		this.produtoService = produtoService;
		this.reservaEstoqueRepository = reservaEstoqueRepository;
	}
	
	public void reservarEstoqueItensPedido(Pedido pedido) {
		
		LOGGER.info("Iniciando reserva de estoque para o pedido {} ", pedido.getId());
		pedido.getItens().stream().forEach(itemPedido -> {
			int quantidadeEstoque = produtoRepository.findById(itemPedido.getId().getProduto().getId()).get().getQuantidadeEstoque();
			if(itemPedido.getQuantidade() > quantidadeEstoque) {
				LOGGER.error("Atenção!! Pedido recusado. A quantidade do produto {} no estoque é {} e a quantidade pedida é de {}", 
						itemPedido.getId().getProduto().getDescricao(), 
						quantidadeEstoque, 
						itemPedido.getQuantidade());
				throw new BusinessException(String.format("Atenção!! Pedido recusado. A quantidade do produto {} no estoque é {} e a quantidade pedida é de {}",
						itemPedido.getId().getProduto().getDescricao(), 
						quantidadeEstoque, 
						itemPedido.getQuantidade()));
			}
			produtoService.abaterEstoque(itemPedido.getId().getProduto(), itemPedido.getQuantidade());
			produtoRepository.save(itemPedido.getId().getProduto());
			ReservaEstoque reservaEstoque = new ReservaEstoque(pedido, itemPedido.getId().getProduto(), itemPedido.getQuantidade());
			reservaEstoqueRepository.save(reservaEstoque);
			LOGGER.info("Estoque reservado com sucesso");
		});
	}

	public void cancelar(Pedido pedido) {
		LOGGER.info("Iniciando processo de devolução do estoque para o pedido {} ", pedido.getId());
		List<ReservaEstoque> reservaFeita = reservaEstoqueRepository.findByPedidoId(pedido.getId());
		reservaFeita.stream().forEach(reserva -> {
			reserva.getProduto().devolver(reserva.getQuantidade());
		});
		LOGGER.info("Iniciando cacelamento de pedidos com id {} ", pedido.getId());
		reservaEstoqueRepository.deleteByPedidoId(pedido.getId());
		LOGGER.info("Cancelamento efetuado com sucesso");
	}
}
