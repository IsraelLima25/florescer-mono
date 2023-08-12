package br.com.loja.florescer.controller;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.loja.florescer.exception.NotFoundException;
import br.com.loja.florescer.form.AvaliacaoEntregaForm;
import br.com.loja.florescer.indicador.StatusEntregaIndicador;
import br.com.loja.florescer.model.Entrega;
import br.com.loja.florescer.model.Pedido;
import br.com.loja.florescer.repository.PedidoRepository;
import br.com.loja.florescer.view.EntregaView;
import br.com.loja.florescer.view.StatusEntregaView;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/entregas")
public class EntregaController {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(EntregaController.class);
	
	private PedidoRepository pedidoRepository;
	
	public EntregaController(PedidoRepository pedidoRepository) {
		this.pedidoRepository = pedidoRepository;
	}

	@GetMapping("/pedido/{idPedido}")
	public ResponseEntity<StatusEntregaView> statusEntrega(@PathVariable("idPedido") Long idPedido){
		LOGGER.info("Buscando pedido para checar status da entrega");
		Optional<Pedido> possivelPedido = pedidoRepository.findById(idPedido);
		if(!possivelPedido.isPresent()) {
			LOGGER.warn("Pedido com id {}, não existe!!", idPedido);
			throw new NotFoundException("idPedido", "O id do pedido não existe");
		}
		StatusEntregaIndicador statusAtual = possivelPedido.get().getEntrega().getStatus();
		LOGGER.info("Pedido encontrado! E status da entrega: {}", statusAtual);
		
		return ResponseEntity.ok(new StatusEntregaView(statusAtual));
	}	
	
	@PostMapping("/avaliar")
	@Transactional
	public ResponseEntity<EntregaView> avaliarEntrega(@Valid @RequestBody AvaliacaoEntregaForm form){
		LOGGER.info("Buscando pedido para avaliar entrega");
		Optional<Pedido> possivelPedido = pedidoRepository.findById(form.idPedido());
		if(!possivelPedido.isPresent()) {
			LOGGER.warn("Pedido com id {}, não existe!!", form.idPedido());
			throw new NotFoundException("idPedido", "O id do pedido não existe");
		}
		LOGGER.info("Pedido encontrado, avaliando a entrega");
		Entrega entrega = possivelPedido.get().getEntrega();
		entrega.avaliar(form.avaliacao());
		
		LOGGER.info("Entrega avaliada");
		EntregaView entregaView = new EntregaView(entrega.getId(),entrega.getStatus(), entrega.getAvaliacao());
		
		return ResponseEntity.ok(entregaView);
	}

}
