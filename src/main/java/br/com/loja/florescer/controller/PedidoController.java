package br.com.loja.florescer.controller;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.loja.florescer.exception.NotFoundException;
import br.com.loja.florescer.form.PedidoForm;
import br.com.loja.florescer.model.Pedido;
import br.com.loja.florescer.repository.PedidoRepository;
import br.com.loja.florescer.service.PedidoService;
import br.com.loja.florescer.util.PedidoConverter;
import br.com.loja.florescer.view.PedidoView;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/pedidos")
public class PedidoController {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(PedidoController.class);
	
	private PedidoService pedidoService;
	private PedidoConverter pedidoConverter;
	private PedidoRepository pedidoRepository;
	
	public PedidoController(PedidoService pedidoService, PedidoRepository pedidoRepository, PedidoConverter pedidoConverter) {
		this.pedidoService = pedidoService;
		this.pedidoConverter = pedidoConverter;
		this.pedidoRepository = pedidoRepository;
	}

	@PostMapping
	@Transactional
	public ResponseEntity<PedidoView> fazerPedido(@Valid @RequestBody PedidoForm form) {
		
		LOGGER.info("Iniciando a criação do pedido de compra");
		Pedido pedido = pedidoService.criarPedido(form);
		LOGGER.info("Pedido criado com sucesso");
		PedidoView viewPedido = this.pedidoConverter.toPedidoView(pedido);
		// TODO send email generated order for client
		return ResponseEntity.ok(viewPedido);
	}
	
	@GetMapping("/cliente/{cpf}")
	public ResponseEntity<List<PedidoView>> buscarPorCpfCliente(@PathVariable("cpf") String cpf){
		LOGGER.info("Buscando pedido por cpf do cliente {} ", cpf);
		Optional<List<Pedido>> possiveisPedidos = pedidoRepository.findByClienteCpf(cpf);
		if(!possiveisPedidos.isPresent()) {
			LOGGER.warn("Atenção!! Nenhum pedido associado ao cliente de cpf {} foi encontrado!", cpf);
			throw new NotFoundException("cpfCliente", "Nenhum pedido associado ao cpf do cliente foi encontrado!");
		}
		List<Pedido> pedidos = possiveisPedidos.get();
		LOGGER.info("Pedidos encontrado");
		
		List<PedidoView> pedidosViewList = pedidos.stream().map(pedido -> {
			return pedidoConverter.toPedidoView(pedido);
		}).collect(Collectors.toList());
		
		return ResponseEntity.ok(pedidosViewList);
	}
	
	@PostMapping("/cancelar/{idPedido}")
	@Transactional
	public ResponseEntity<Void> cancelar(@PathVariable("idPedido") Long idPedido){
		Optional<Pedido> possivelPedido = pedidoRepository.findById(idPedido);
		if(!possivelPedido.isPresent()) {
			LOGGER.warn("Pedido com id {}, não existe!!", idPedido);
			throw new NotFoundException("idPedido", "O id do pedido não existe");
		}
		Pedido pedido = possivelPedido.get();
		pedidoService.cancelar(pedido);
		
		return ResponseEntity.ok().build();
		
	}
}
