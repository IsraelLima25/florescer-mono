package br.com.loja.florescer.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import br.com.loja.florescer.model.Cliente;
import br.com.loja.florescer.model.Endereco;
import br.com.loja.florescer.repository.ClienteRepository;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.util.Optional;

@ExtendWith(SpringExtension.class)
public class ClienteServiceTest {

	@InjectMocks
	ClienteService clienteService;

	@Mock
	ClienteRepository clienteRepository;

	@Test
	void deveBuscarClientePorCpf() {
		
		Mockito.when(clienteRepository.findByCpf("05489745693")).thenReturn(Optional.of(new Cliente("Israel Filho",
				"05489745693", "1565478421", LocalDate.of(1991, 3, 20),
				new Endereco("41290200", "Rua dos testes cliente", "primeiro andar", "Moca", "São Paulo", "sp"))));
		
		Optional<Cliente> possivelCliente = clienteService.buscarClientePorCpf("05489745693");
		Cliente cliente = possivelCliente.get();
		
		assertNotNull(possivelCliente);
		assertTrue(possivelCliente.isPresent());
		
		assertEquals(cliente.getCpf(), "05489745693");
		assertEquals(cliente.getRg(), "1565478421");
		assertEquals(cliente.getNome(), "Israel Filho");
	}

}
