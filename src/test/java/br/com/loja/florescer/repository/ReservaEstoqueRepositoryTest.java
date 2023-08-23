package br.com.loja.florescer.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import br.com.loja.florescer.indicador.TipoFormaPagamentoIndicador;
import br.com.loja.florescer.model.Cliente;
import br.com.loja.florescer.model.Endereco;
import br.com.loja.florescer.model.Entrega;
import br.com.loja.florescer.model.Fornecedor;
import br.com.loja.florescer.model.ItemPedido;
import br.com.loja.florescer.model.Pagamento;
import br.com.loja.florescer.model.Pedido;
import br.com.loja.florescer.model.Produto;
import br.com.loja.florescer.model.ReservaEstoque;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
@ActiveProfiles("test")
public class ReservaEstoqueRepositoryTest {
	
	@PersistenceContext
	EntityManager entityManager;
	
	@Autowired
	PedidoRepository pedidoRepository;
	
	@Autowired
	ClienteRepository clienteRepository;
	
	@Autowired
	ProdutoRepository produtoRepository;
	
	@Autowired
	ReservaEstoqueRepository reservaEstoqueRepository;
	
	@Autowired
	FornecedorRepository fornecedorRepository;
	
	Pedido pedido;
	
	@BeforeEach
	void setUp() {
		
		resetIncrement();
		
		pedido = new Pedido();
		Endereco endereco = new Endereco("41290221", "Rua dos testes endereco", "Casa", "Moca", "São Paulo",
				"sp");
		clienteRepository.saveAndFlush(new Cliente("Israel Filho", "05489745693", "1565478421", LocalDate.of(1991, 3, 20),
				new Endereco("41290200", "Rua dos testes cliente", "primeiro andar", "Moca", "São Paulo", "sp")));
		Endereco enderecoFornecedor = new Endereco("41290221", "Rua dos testes fornecedor", "Casa", "Moca", "São Paulo",
				"sp");
		
		Fornecedor primeiroFornecedor = new Fornecedor("Fornecedor estadual", "45331331000142", enderecoFornecedor);
		Fornecedor segundoFornecedor = new Fornecedor("Fornecedor xyz", "78886123000169", enderecoFornecedor);
		
		fornecedorRepository.saveAll(List.of(primeiroFornecedor, segundoFornecedor));
		
		produtoRepository.saveAndFlush(new Produto("Rosa", new BigDecimal("30.00"), 30, "sp",
				primeiroFornecedor));
		
		produtoRepository.saveAndFlush(new Produto("Margarida", new BigDecimal("15.00"), 30, "sp",
				segundoFornecedor));
		
		pedido.adicionarFormaPagamento(new Pagamento(TipoFormaPagamentoIndicador.PIX, 
				pedido.getValorTotalPagamento()));
		
		pedido.adicionarCliente(clienteRepository.findByCpf("05489745693").get());
		
		Produto primeiroProdutoCapturado = produtoRepository.findById(1L).get();
		Produto segundoProdutoCapturado = produtoRepository.findById(2L).get();
		
		pedido.adicionarItem(new ItemPedido(primeiroProdutoCapturado, pedido, 2));
		pedido.adicionarItem(new ItemPedido(segundoProdutoCapturado, pedido, 1));
		
		pedido.adicionarEntrega(new Entrega(endereco, pedido));
		pedido.calcularValorTotal();
		pedidoRepository.save(pedido);
		
		ReservaEstoque primeiraReserva = new ReservaEstoque(pedido,primeiroProdutoCapturado, 2);
		ReservaEstoque segundaReserva = new ReservaEstoque(pedido,segundoProdutoCapturado, 1);
		
		reservaEstoqueRepository.save(primeiraReserva);
		reservaEstoqueRepository.save(segundaReserva);
	}
	
	//@Test
	void buscarReservasItensPedido() { 
		
		List<Pedido> pedidos = pedidoRepository.findByClienteCpf("05489745693").get();
		List<ReservaEstoque> reservas = reservaEstoqueRepository.findByPedidoId(pedidos.get(0).getId());
		
		assertTrue(reservas.size() == 2);
		
		ReservaEstoque primeiraReserva = reservas.get(0);
		ReservaEstoque segundaReserva = reservas.get(1);
		
		assertEquals("Rosa", primeiraReserva.getProduto().getDescricao());
		assertEquals("Margarida", segundaReserva.getProduto().getDescricao());
		
	}
	
	//@Test
	void deletarReservasItensPedido() { 
		
		reservaEstoqueRepository.deleteByPedidoId(pedido.getId());
		List<ReservaEstoque> reservas = reservaEstoqueRepository.findByPedidoId(pedido.getId());
		
		assertTrue(reservas.size() == 0);
	}
	
	private void resetIncrement() {
		entityManager.createNativeQuery("ALTER TABLE db_test_florescer.tbl_produto AUTO_INCREMENT = 1")
	    .executeUpdate(); 
	}
}
