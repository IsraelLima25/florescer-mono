package br.com.loja.florescer.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import br.com.loja.florescer.model.Cliente;
import br.com.loja.florescer.model.Endereco;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
@ActiveProfiles("test")
public class ClienteRepositoryTest {

	@Autowired
	ClienteRepository clienteRepository;
	
	@BeforeEach
	void setUp() {
		clienteRepository.save(new Cliente("Israel Filho", "05489745693", "1565478421", LocalDate.of(1991, 3, 20),
				new Endereco("41290200", "Rua dos testes cliente", "primeiro andar", "Moca", "São Paulo", "sp")));

		clienteRepository.save(new Cliente("Lewis Davila", "04563214568", "1236985273", LocalDate.of(1991, 3, 20),
				new Endereco("32698753", "Rua das integrações", "Casa", "botafogo", "Rio de Janeiro", "rj")));
	}

	@Test
	void buscarClienteExistentePorCpf() {
		
		Optional<Cliente> optionalPrimeiroCliente = clienteRepository.findByCpf("05489745693");
		Optional <Cliente> optionalSegundoCliente = clienteRepository.findByCpf("04563214568");
		
		assertTrue(optionalPrimeiroCliente.isPresent());
		assertTrue(optionalSegundoCliente.isPresent());
		
		Cliente primeiroCliente = optionalPrimeiroCliente.get();
		Cliente segundoCliente = optionalSegundoCliente.get();
		
		assertEquals("Israel Filho", primeiroCliente.getNome());
		assertEquals("Lewis Davila", segundoCliente.getNome());
	}
	
	@Test
	void buscarClienteInexistentePorCpf() {
		
		Optional<Cliente> clienteInexistente = clienteRepository.findByCpf("05489745615");

		assertTrue(clienteInexistente.isEmpty());
	}
}
