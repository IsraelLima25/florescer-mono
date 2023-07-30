package br.com.loja.florescer.model;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import br.com.loja.florescer.exception.BusinessException;
import br.com.loja.florescer.indicador.StatusPagamentoIndicador;
import br.com.loja.florescer.indicador.TipoFormaPagamentoIndicador;

public class PagamentoTest {

	@Test
	void deveProcessarPagamento() {
		Pagamento pagamento = new Pagamento(TipoFormaPagamentoIndicador.PIX, new BigDecimal("50.00"));
		pagamento.processarPagamento();
		assertEquals(StatusPagamentoIndicador.PROCESSADO, pagamento.getStatus());
	}

	@Test
	void naoDeveProcessarPagamentoCancelado() {
		Pagamento pagamento = new Pagamento(TipoFormaPagamentoIndicador.PIX, new BigDecimal("50.00"));
		pagamento.alterarStatusPagamento(StatusPagamentoIndicador.CANCELADO);
		BusinessException businessException = assertThrows(BusinessException.class, () -> {
			pagamento.processarPagamento();
		});

		assertEquals("Este pagamento já foi cancelado", businessException.getMessage());
	}

	@Test
	void naoDeveProcessarPagamentoProcessado() {
		Pagamento pagamento = new Pagamento(TipoFormaPagamentoIndicador.PIX, new BigDecimal("50.00"));
		pagamento.alterarStatusPagamento(StatusPagamentoIndicador.PROCESSADO);
		BusinessException businessException = assertThrows(BusinessException.class, () -> {
			pagamento.processarPagamento();
		});

		assertEquals("Este pagamento já foi processado", businessException.getMessage());
	}

}
