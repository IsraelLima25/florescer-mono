package br.com.loja.florescer.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.core.userdetails.UserDetails;

import br.com.loja.florescer.model.Usuario;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

	UserDetails findByLogin(String login);
	
}
