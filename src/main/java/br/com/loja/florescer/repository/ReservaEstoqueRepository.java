package br.com.loja.florescer.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.loja.florescer.model.ReservaEstoque;

public interface ReservaEstoqueRepository extends JpaRepository<ReservaEstoque, Long> {
	
	List<ReservaEstoque> findByPedidoId(Long idPedido);
	void deleteByPedidoId(Long idPedido);

}
