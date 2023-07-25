package br.com.loja.florescer.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import br.com.loja.florescer.exception.BusinessException;
import br.com.loja.florescer.indicador.StatusPagamentoIndicador;
import br.com.loja.florescer.indicador.TipoFormaPagamentoIndicador;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "tbl_pagamento")
public class Pagamento {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@Enumerated(EnumType.STRING)
	@Column(name = "forma_pagamento", nullable = false)
	private TipoFormaPagamentoIndicador tipoPagamento;
	
	@Column(name="data_hora_local", nullable = false)
	private LocalDateTime instante;
	
	@Enumerated(EnumType.STRING)
	private StatusPagamentoIndicador status;
	
	@Column(name="observacao")
	private String observacao;
	
	@Column(name = "preco_total", nullable = false)
	private BigDecimal valorTotal;
	
	@Deprecated
	public Pagamento() {
	}

	public Pagamento(TipoFormaPagamentoIndicador formaPagamento, BigDecimal valorTotal) {
		this.tipoPagamento=formaPagamento;
		this.valorTotal=valorTotal;
		this.status = StatusPagamentoIndicador.AGUARDANDO_PROCESSAMENTO;
		this.instante = LocalDateTime.now();
	}
	
	public TipoFormaPagamentoIndicador getTipoPagamento() {
		return tipoPagamento;
	}
	
	public String getObservacao() {
		return observacao;
	}
	
	public StatusPagamentoIndicador getStatus() {
		return status;
	}
	
	public void adicionarObservacao(String observacao) {
		this.observacao = observacao;
	}
	
	public LocalDateTime getInstante() {
		return instante;
	}
	
	public void iniciarProcessamento() {
		if (this.status == StatusPagamentoIndicador.CANCELADO) {
			throw new BusinessException("Este pagamento já foi cancelado");
		}
		if (this.status == StatusPagamentoIndicador.PROCESSADO) {
			throw new BusinessException("Este pagamento já foi processado");
		}
		if (this.status == StatusPagamentoIndicador.EM_PROCESSAMENTO) {
			throw new BusinessException("Este pagamento já está em processamento");
		}
		
		this.status = StatusPagamentoIndicador.EM_PROCESSAMENTO;
	}
	
	public void processarPagamento() {
		if (this.status == StatusPagamentoIndicador.CANCELADO) {
			throw new BusinessException("Este pagamento já foi cancelado");
		}
		if (this.status == StatusPagamentoIndicador.PROCESSADO) {
			throw new BusinessException("Este pagamento já foi processado");
		}

		this.status = StatusPagamentoIndicador.PROCESSADO;
	}
	
}
