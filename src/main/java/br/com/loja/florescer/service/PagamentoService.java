package br.com.loja.florescer.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import br.com.loja.florescer.indicador.StatusEntregaIndicador;
import br.com.loja.florescer.indicador.StatusPedidoIndicador;
import br.com.loja.florescer.model.Entrega;
import br.com.loja.florescer.model.Pagamento;
import br.com.loja.florescer.model.Pedido;

@Service
public class PagamentoService {

	private static final Logger LOGGER = LoggerFactory.getLogger(PagamentoService.class);
	
	public void pagar(Pedido pedido) {
		Pagamento pagamento = pedido.getPagamento();
		LOGGER.info("Processando pagamento do pedido {} ", pedido.getId());
		pagamento.processarPagamento();
		LOGGER.info("Pagamento do pedido com id {} processado com sucesso", pedido.getId());
		pedido.atualizarStatus(StatusPedidoIndicador.PAGAMENTO_PROCESSADO);
		Entrega entrega = pedido.getEntrega();
		entrega.atualizarStatus(StatusEntregaIndicador.PREPARANDO_PACOTE);
	}
	
}
