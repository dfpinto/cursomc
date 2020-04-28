package com.nelioalves.cursomc.security;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

// Filtro que intercepta os endpoints não autorizados pela classe de configuração SecurityConfig.configure.
public class JWTAuthorizationFilter extends BasicAuthenticationFilter {

	private JWTUtil jwtUtil;
	private UserDetailsService userDetailService;
	
	@Autowired
	public JWTAuthorizationFilter(AuthenticationManager authenticationManager, JWTUtil jwtUtil, UserDetailsService userDetailService) {
		super(authenticationManager);
		
		this.jwtUtil = jwtUtil;
		this.userDetailService = userDetailService;
	}

	@Override
	protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain chain) throws IOException, ServletException{
		String header = req.getHeader("Authorization");
		
		System.out.println(header);
		
		if(header != null && header.startsWith("Bearer ")) {
			UsernamePasswordAuthenticationToken auth = getAuthentication(header.substring(7));
			if(auth != null) {
				// Libera a autorização e segue a execução propagando o token.
				SecurityContextHolder.getContext().setAuthentication(auth);
			}
		}
		
		chain.doFilter(req, res);
	}

	private UsernamePasswordAuthenticationToken getAuthentication(String token) {
		
		// Verifica se o token não expirou.
		if(jwtUtil.tokenValido(token)) {
			String userName = jwtUtil.getUserName(token);
			UserDetails user = userDetailService.loadUserByUsername(userName);
			
			// Retorna o usuário e seus perfis.
			return new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());		
		}
		return null;
	}
	
}
