package br.com.loja.florescer.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration {
	
	private SecurityFilter securityFilter;
	
	public SecurityConfiguration(SecurityFilter securityFilter) {
		this.securityFilter = securityFilter;
	}

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		
		return http.csrf(csrf -> csrf.disable())
				.sessionManagement(sessionManagement -> sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
				.authorizeHttpRequests(authorizeHttpRequests -> authorizeHttpRequests.requestMatchers(HttpMethod.POST, "/api/login").permitAll()
						.requestMatchers(HttpMethod.POST, "/api/clientes").hasRole("ADMIN")
						.requestMatchers(HttpMethod.PUT, "/api/clientes").hasRole("ADMIN")
						.requestMatchers(HttpMethod.POST, "/api/fornecedores").hasRole("ADMIN")
						.requestMatchers(HttpMethod.POST, "/api/pedidos/cancelar/**").hasRole("ADMIN")
						.requestMatchers(HttpMethod.POST, "/api/produtos").hasRole("ADMIN")
						.requestMatchers("/v3/api-docs/**", "/swagger-ui.html", "/swagger-ui/**").permitAll()
						.anyRequest().authenticated())
				.addFilterBefore(securityFilter, UsernamePasswordAuthenticationFilter.class)
				.build();
	}
	
	@Bean
	public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
		
		return configuration.getAuthenticationManager();
	}
	
	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
	
}
