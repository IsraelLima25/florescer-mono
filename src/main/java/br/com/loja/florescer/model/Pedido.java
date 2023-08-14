package br.com.loja.florescer.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

import java.util.ArrayList;

import br.com.loja.florescer.exception.BusinessException;
import br.com.loja.florescer.indicador.StatusPedidoIndicador;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "tbl_pedido")
public class Pedido {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;

	@Enumerated(EnumType.STRING)
	@Column(name = "status", nullable = false)
	private StatusPedidoIndicador status;

	@OneToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "id_pagamento", referencedColumnName = "id")
	private Pagamento pagamento;

	@OneToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "id_entrega", referencedColumnName = "id")
	private Entrega entrega;

	@Column(name = "preco_total_itens", nullable = false)
	private BigDecimal valorTotalItens;

	@Column(name = "preco_total_pagamento", nullable = false)
	private BigDecimal valorTotalPagamento;

	@Column(name = "data_hora_local", nullable = false)
	private LocalDateTime instante;

	@ManyToOne
	@JoinColumn(name = "id_cliente", referencedColumnName = "id", nullable = false)
	private Cliente cliente;

	@OneToMany(mappedBy = "id.pedido", cascade = CascadeType.ALL)
	private List<ItemPedido> itens = new ArrayList<>();

	@Column(name = "observacao")
	private String observacao;

	public Pedido() {
		this.status = StatusPedidoIndicador.AGUARDANDO_PAGAMENTO;
		this.instante = LocalDateTime.now();
		this.valorTotalItens = BigDecimal.ZERO;
		this.valorTotalPagamento = BigDecimal.ZERO;
	}
	
	public Pedido(long id) {
		this.id = id;
		this.status = StatusPedidoIndicador.AGUARDANDO_PAGAMENTO;
		this.instante = LocalDateTime.now();
		this.valorTotalItens = BigDecimal.ZERO;
		this.valorTotalPagamento = BigDecimal.ZERO;
	}

	public Cliente getCliente() {
		return cliente;
	}

	public StatusPedidoIndicador getStatus() {
		return status;
	}

	public Pagamento getPagamento() {
		return pagamento;
	}

	public Entrega getEntrega() {
		return entrega;
	}

	public long getId() {
		return id;
	}

	public LocalDateTime getInstante() {
		return instante;
	}

	public String getObservacao() {
		return observacao;
	}

	public List<ItemPedido> getItens() {
		return itens;
	}

	public List<ItemPedido> getProdutos() {
		return itens;
	}

	public void adicionarItem(ItemPedido item) {
		this.itens.add(item);
	}

	public void adicionarObservacao(String observacao) {
		this.observacao = observacao;
	}

	public void adicionarCliente(Cliente cliente) {
		this.cliente = cliente;
	}

	public void adicionarEntrega(Entrega entrega) {
		this.entrega = entrega;
	}

	public void adicionarFormaPagamento(Pagamento pagamento) {
		this.pagamento = pagamento;
	}

	public void calcularValorTotal() {

		this.valorTotalItens = this.itens.stream().map(itemPedido -> itemPedido.getId().getProduto().getPreco()
				.multiply(new BigDecimal(itemPedido.getQuantidade()))).reduce(BigDecimal.ZERO, BigDecimal::add);

		this.valorTotalPagamento = this.valorTotalItens.add(this.entrega.getValor());

	}

	public BigDecimal getValorTotalItens() {
		return valorTotalItens;
	}

	public BigDecimal getValorTotalPagamento() {
		return valorTotalPagamento;
	}

	@Override
	public int hashCode() {
		return Objects.hash(id);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Pedido other = (Pedido) obj;
		return Objects.equals(id, other.id);
	}

	public void cancelar() {
		if (this.status == StatusPedidoIndicador.CANCELADO) {
			throw new BusinessException("Este pedido já foi cancelado");
		}
		if (this.status == StatusPedidoIndicador.PAGAMENTO_PROCESSADO) {
			throw new BusinessException("Para este pedido o pagamento já foi processado");
		}

		this.status = StatusPedidoIndicador.CANCELADO;
	}
	
	public void atualizarStatus(StatusPedidoIndicador novoStatus) {
		this.status = novoStatus;
	}

}
