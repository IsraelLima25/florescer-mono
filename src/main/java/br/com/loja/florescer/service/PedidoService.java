package br.com.loja.florescer.service;

import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import br.com.loja.florescer.exception.BusinessException;
import br.com.loja.florescer.form.PedidoForm;
import br.com.loja.florescer.model.Cliente;
import br.com.loja.florescer.model.Entrega;
import br.com.loja.florescer.model.ItemPedido;
import br.com.loja.florescer.model.Pagamento;
import br.com.loja.florescer.model.Pedido;
import br.com.loja.florescer.repository.PedidoRepository;

@Service
public class PedidoService {

	private static final Logger LOGGER = LoggerFactory.getLogger(PedidoService.class);

	private ClienteService clienteService;
	private ItemPedidoService itemPedidoService;
	private EntregaService entregaService;
	private ReservaEstoqueService reservaEstoqueService;
	private PedidoRepository pedidoRepository;

	public PedidoService(PedidoRepository pedidoRepository, ClienteService clienteService,
			ItemPedidoService itemPedidoService, EntregaService entregaService, ReservaEstoqueService reservaEstoqueService) {

		this.pedidoRepository = pedidoRepository;
		this.clienteService = clienteService;
		this.itemPedidoService = itemPedidoService;
		this.entregaService = entregaService;
		this.reservaEstoqueService = reservaEstoqueService;
	}

	public Pedido criarPedido(PedidoForm form) {

		LOGGER.info("Construindo pedido");
		Pedido pedido = new Pedido();

		LOGGER.info("Adicionando cliente ao pedido");
		Optional<Cliente> possivelCliente = clienteService.buscarClientePorCpf(form.cpfCliente());
		
		if(!possivelCliente.isPresent()) {
			LOGGER.warn("Cliente com cpf {} não existe!", form.cpfCliente());
			throw new BusinessException(String.format("Cliente com cpf {} não existe!", form.cpfCliente()));
		}
		
		pedido.adicionarCliente(possivelCliente.get());

		LOGGER.info("Adicionando produtos ao pedido");
		List<ItemPedido> itensPedidoGerado = itemPedidoService.gerarItensPedido(form.produtos(), pedido);
		itensPedidoGerado.stream().forEach(item -> pedido.adicionarItem(item));

		LOGGER.info("Adicionando endereco de entrega ao pedido");
		Entrega entrega = entregaService.gerarEntrega(form.enderecoForm().cep(), pedido);
		pedido.adicionarEntrega(entrega);

		LOGGER.info("Adicionando possivel observação ao pedido");
		pedido.adicionarObservacao(form.Observacao());

		LOGGER.info("Calculando valor total do pedido");
		pedido.calcularValorTotal();

		LOGGER.info("Adicionando forma de pagamento ao pedido");
		pedido.adicionarFormaPagamento(new Pagamento(form.tipoPagamento(), pedido.getValorTotalPagamento()));
		
		pedidoRepository.save(pedido);
		itemPedidoService.salvarItensPedido(pedido.getItens());

		LOGGER.info("Pedido enviado para reserva de estoque");
		reservaEstoqueService.reservarEstoqueItensPedido(pedido);
		
		return pedido;
	}

	public void cancelar(Pedido pedido) {
		LOGGER.info("Chamando serviço de cancelamento");
		pedido.cancelar();
		reservaEstoqueService.cancelar(pedido);
		LOGGER.info("Serviço de cancelamento executado com sucesso para o pedido de id {} ", pedido.getId());
	}

}
