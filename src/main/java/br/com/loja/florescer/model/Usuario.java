package br.com.loja.florescer.model;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.HashSet;
import java.util.Objects;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinTable;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "tbl_usuario")
public class Usuario implements UserDetails{
	
	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private String login;
	private String senha;
	
	@ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.PERSIST)
	@JoinTable(name = "tbl_usuario_perfil", joinColumns = @JoinColumn(name = "id_usuario"), 
	inverseJoinColumns = @JoinColumn(name = "id_perfil"))
	private Set<Perfil> perfis = new HashSet<>();

	@Deprecated
	public Usuario() {
	}

	public Usuario(String login, String senha) {
		this.login = login;
		this.senha = senha;
	}

	public Long getId() {
		return id;
	}

	public String getLogin() {
		return login;
	}

	public String getSenha() {
		return senha;
	}
	
	public void adicionarPerfil(Perfil perfil) {
		this.perfis.add(perfil);
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
		Usuario other = (Usuario) obj;
		return Objects.equals(id, other.id);
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		
		return perfis.stream().map(p -> new SimpleGrantedAuthority(p.getDescricao()))
		.collect(Collectors.toSet());
	}

	@Override
	public String getPassword() {
		return senha;
	}

	@Override
	public String getUsername() {
		// TODO Auto-generated method stub
		return login;
	}

	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public boolean isEnabled() {
		// TODO Auto-generated method stub
		return true;
	}
}
