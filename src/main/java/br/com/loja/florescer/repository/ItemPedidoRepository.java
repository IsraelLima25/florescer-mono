package br.com.loja.florescer.repository;
import org.springframework.data.jpa.repository.JpaRepository;

import br.com.loja.florescer.model.ItemPedido;

public interface ItemPedidoRepository extends JpaRepository<ItemPedido, Long> { }
