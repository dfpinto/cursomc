package com.nelioalves.cursomc.config;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter{
	
	@Autowired
	Environment env;

	private static final String[] PUBLIC_MATCHERS = {
					"/h2-console/**"
	};
	private static final String[] PUBLIC_MATCHERS_GET = {
			"/produtos/**",
			"/categorias/**"
};
	
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		
		// Trecho específico para liberar o uso do BD H2.
	    if(Arrays.asList(env.getActiveProfiles()).contains("test")) {
	    	http.headers().frameOptions().disable();
	    }
		
		// http.cors() - Adiciona a configuração de CORS definia pelo bean abaixo.
		// http.csfr().disable() - Desabilita o uso de cookie por não estarmos usando, ou seja, stateless.
		http.cors().and().csrf().disable();
		
		// Adiciona os endpoints que nossa aplicação irá acessar.
		http.authorizeRequests()
		.antMatchers(PUBLIC_MATCHERS).permitAll()
		.antMatchers(HttpMethod.GET, PUBLIC_MATCHERS_GET).permitAll()
		.anyRequest()
		.authenticated()
		;
		
		// Assegura que nosso backend não criará sessão de usuário.
		http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
	}
	
	// CORS: Cross-origin request security - https://dadario.com.br/csrf-o-que-e/
	@Bean
	protected CorsConfigurationSource corsConfigurationSource() {
	    final UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
	    source.registerCorsConfiguration("/**", new CorsConfiguration().applyPermitDefaultValues());
	    return source;
	  }
	
	@Bean
	public BCryptPasswordEncoder bCryptPasswordEncoder() {
		return new BCryptPasswordEncoder();
	}
}
