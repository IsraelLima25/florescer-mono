package br.com.loja.florescer.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.loja.florescer.model.Pedido;

public interface PedidoRepository extends JpaRepository<Pedido, Long> {
	
	Optional<List<Pedido>> findByClienteCpf(String cpf);

}
