package br.com.loja.florescer.repository;

import br.com.loja.florescer.model.Pedido;
import br.com.loja.florescer.model.Produto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

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

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
@ActiveProfiles("test")
public class PedidoRepositoryTest {
	
	@Autowired
	PedidoRepository pedidoRepository;
	
	@Autowired
	ClienteRepository clienteRepository;
	
	@Autowired
	ProdutoRepository produtoRepository;
	
	@BeforeEach
	void setUp() {
		
		Pedido pedido = new Pedido();
		Endereco endereco = new Endereco("41290221", "Rua dos testes endereco", "Casa", "Moca", "São Paulo",
				"sp");
		clienteRepository.saveAndFlush(new Cliente("Israel Filho", "05489745693", "1565478421", LocalDate.of(1991, 3, 20),
				new Endereco("41290200", "Rua dos testes cliente", "primeiro andar", "Moca", "São Paulo", "sp")));
		Endereco enderecoFornecedor = new Endereco("41290221", "Rua dos testes fornecedor", "Casa", "Moca", "São Paulo",
				"sp");
		
		produtoRepository.saveAndFlush(new Produto("Rosa", new BigDecimal("30.00"), 30, "sp",
				new Fornecedor("Fornecedor estadual", "45331331000142", enderecoFornecedor)));
		
		produtoRepository.saveAndFlush(new Produto("Margarida", new BigDecimal("15.00"), 25, "sp",
				new Fornecedor("Fornecedor estadual", "78886123000169", enderecoFornecedor)));
		
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
	}
	
	@Test
	void buscarPedidoPorCpfCliente() {
		
		Optional<List<Pedido>> possiveisPedido = pedidoRepository.findByClienteCpf("05489745693");
		
		assertTrue(possiveisPedido.isPresent());
		
		List<Pedido> pedidos = possiveisPedido.get();
		
		assertEquals(pedidos.size(), 1);
		assertEquals(pedidos.get(0).getCliente().getNome(), "Israel Filho");
		assertEquals(pedidos.get(0).getCliente().getCpf(), "05489745693");
		assertEquals(pedidos.get(0).getCliente().getRg(), "1565478421");
	}
	
	@Test
	void buscarPedidoPorCpfInexistente() {
		Optional<List<Pedido>> possivelPedido = pedidoRepository.findByClienteCpf("76867395031");

		assertTrue(possivelPedido.get().isEmpty());
	}
}
