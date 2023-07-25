package br.com.loja.florescer.model;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

import br.com.loja.florescer.exception.BusinessException;
import br.com.loja.florescer.indicador.AvaliacaoIndicador;
import br.com.loja.florescer.indicador.StatusEntregaIndicador;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "tbl_entrega")
public class Entrega {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@OneToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "id_endereco", referencedColumnName = "id", nullable = false)
	private Endereco endereco;

	@OneToOne(mappedBy = "entrega")
	private Pedido pedido;

	@Column(name = "preco", nullable = false)
	private BigDecimal valor;

	@Column(name = "avaliacao_estrela")
	private AvaliacaoIndicador avaliacao;
	
	@Enumerated(EnumType.STRING)
	@Column(name = "status", nullable = false)
	private StatusEntregaIndicador status;

	@Column(name = "observacao")
	private String observacao;
	
	@Deprecated
	public Entrega() {
	}

	public Entrega(Endereco endereco, Pedido pedido) {
		this.endereco = endereco;
		this.pedido = pedido;
		this.status = StatusEntregaIndicador.AGUARDANDO_CONFIRMACAO_DE_PAGAMENTO;
		calcularFrete();
	}

	private void calcularFrete() {
		BigDecimal valorFreteCalculado = calcularImpostoEstadual(pedido.getItens()).add(calcularImpostoInterEstadual(pedido.getItens()));
		this.valor = valorFreteCalculado;
	}

	public Endereco getEndereco() {
		return endereco;
	}

	private BigDecimal calcularImpostoEstadual(List<ItemPedido> itens) {
		
		BigDecimal impostoEstadual = itens.stream().filter(itemPedido -> {
			return itemPedido.getId().getProduto().getFilial().equals(this.endereco.getUf());
		}).collect(Collectors.toList()).stream().map(itemPedido -> itemPedido.getId().getProduto().getPreco().multiply(new BigDecimal(itemPedido.getQuantidade()))).reduce(BigDecimal.ZERO, (i,e) -> {
			return e.multiply(new BigDecimal("0.1"));
		}).setScale(2, BigDecimal.ROUND_HALF_UP);
		
		return impostoEstadual;
	}

	private BigDecimal calcularImpostoInterEstadual(List<ItemPedido> itens) {
		
		BigDecimal impostoInterEstadual = itens.stream().filter(itemPedido -> {
			return !itemPedido.getId().getProduto().getFilial().equals(this.endereco.getUf());
		}).collect(Collectors.toList()).stream().map(itemPedido -> itemPedido.getId().getProduto().getPreco().multiply(new BigDecimal(itemPedido.getQuantidade()))).reduce(BigDecimal.ZERO, (i,e) -> {
			return e.multiply(new BigDecimal("0.2"));
		}).setScale(2, BigDecimal.ROUND_HALF_UP);
		
		return impostoInterEstadual;
	}
	
	public Long getId() {
		return id;
	}

	public BigDecimal getValor() {
		return valor;
	}
	
	public StatusEntregaIndicador getStatus() {
		return status;
	}
	
	public AvaliacaoIndicador getAvaliacao() {
		return avaliacao;
	}
	
	public void atualizarStatus(StatusEntregaIndicador novoStatus) {
		this.status = novoStatus;
	}
	
	public void avaliar(AvaliacaoIndicador avaliacao) {
		if(this.avaliacao != null) {
			throw new BusinessException("Atenção!! Esta entrega já foi avaliada");
		}
		this.avaliacao = avaliacao;
	}

}
