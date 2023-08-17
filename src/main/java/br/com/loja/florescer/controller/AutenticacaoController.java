package br.com.loja.florescer.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.loja.florescer.form.DadosAutenticacaoForm;
import br.com.loja.florescer.model.Usuario;
import br.com.loja.florescer.service.TokenService;
import br.com.loja.florescer.view.DadosTokenJWTView;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/login")
public class AutenticacaoController {

	private AuthenticationManager authenticationManager;
	private TokenService tokenService;
	
	public AutenticacaoController(AuthenticationManager authenticationManager, TokenService tokenService) {
		this.authenticationManager = authenticationManager;
		this.tokenService = tokenService;
	}
	
	@PostMapping
	public ResponseEntity<DadosTokenJWTView> efetuarLogin(@RequestBody @Valid DadosAutenticacaoForm dados) {
		var authenticationToken = new UsernamePasswordAuthenticationToken(dados.login(), dados.senha());
		var authentication = authenticationManager.authenticate(authenticationToken);
		
		var tokenJWT = tokenService.gerarToken((Usuario)authentication.getPrincipal());
		return ResponseEntity.ok(new DadosTokenJWTView(tokenJWT));
	}
}
