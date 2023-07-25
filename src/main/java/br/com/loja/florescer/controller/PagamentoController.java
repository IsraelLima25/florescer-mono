package br.com.loja.florescer.controller;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.loja.florescer.exception.NotFoundException;
import br.com.loja.florescer.model.Pedido;
import br.com.loja.florescer.repository.PedidoRepository;
import br.com.loja.florescer.service.PagamentoService;

@RestController
@RequestMapping("/api/pagamentos")
public class PagamentoController {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(PagamentoController.class);

	private PedidoRepository pedidoRepository;
	private PagamentoService pagamentoService;
	
	public PagamentoController(PedidoRepository pedidoRepository, PagamentoService pagamentoService) {
		this.pedidoRepository = pedidoRepository;
		this.pagamentoService = pagamentoService;
	}

	@PostMapping("/pagar/{idPedido}")
	@Transactional
	public ResponseEntity<Void> registrarPagamento(@PathVariable("idPedido") Long idPedido){
		LOGGER.info("Buscando pedido com id {} para pagar", idPedido);
		Optional<Pedido> possivelPedido = pedidoRepository.findById(idPedido);
		if(!possivelPedido.isPresent()) {
			LOGGER.warn("Nenhum pedido com id {} foi encontrado", idPedido);
			throw new NotFoundException("idPedido", "O id do pedido não existe");
		}
		Pedido pedido = possivelPedido.get();
		pagamentoService.pagar(pedido);
		return ResponseEntity.ok().build();
	}
	
}
