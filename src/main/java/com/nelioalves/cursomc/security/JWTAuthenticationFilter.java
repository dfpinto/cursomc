package com.nelioalves.cursomc.security;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nelioalves.cursomc.dto.CredenciaisDTO;

// Filtro que intercepta o endpoint /login
public class JWTAuthenticationFilter extends UsernamePasswordAuthenticationFilter {
	
	private AuthenticationManager authenticationManager;
	private JWTUtil jwtUtil;
	
	// Injeção pelo construtor.
	@Autowired
	public JWTAuthenticationFilter(AuthenticationManager authenticationManager, JWTUtil jwtUtil) {
		this.authenticationManager = authenticationManager;
		this.jwtUtil = jwtUtil;
	}
	
	// Mecanismo de captura do endpoint /login. Tenta autenticar o login.
	@Override
	public Authentication attemptAuthentication(HttpServletRequest req, HttpServletResponse res) {
		
		try{
			// Transforma os parâmetros user e senha do endpont login para o objeto CredenciaisDTO.
			CredenciaisDTO creds = new ObjectMapper().readValue(req.getInputStream(), CredenciaisDTO.class);
			// Gera o token do spring
			UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(creds.getEmail(), creds.getSenha());
			// Verifica se a credencial está correta chamando internamente o serviço UserDetailsServiceImpl.
			Authentication auth = authenticationManager.authenticate(authToken);
			
			return auth;
		}
		catch(IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	// Se as credenciais estão ok (autenticadas) emtão esse método é executado, gerando o token através do JWTUtil.
	@Override
	public void successfulAuthentication(HttpServletRequest req, 
										 HttpServletResponse res, 
										 FilterChain chain, 
										 // Recebe o retorno do método acima quando executado com sucesso.
										 Authentication auth) throws IOException, ServletException {
		
		String username = ((UserSS) auth.getPrincipal()).getUsername();
		String token = jwtUtil.generateToken(username);
		res.addHeader("Authorization", "Bearer " + token);
	}

}
