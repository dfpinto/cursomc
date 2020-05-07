package com.nelioalves.cursomc.services;

import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.nelioalves.cursomc.domain.Cliente;
import com.nelioalves.cursomc.repositories.ClienteRepository;
import com.nelioalves.cursomc.services.exceptions.ObjectNotFoundException;

@Service
public class AuthService {
	
	@Autowired
	private ClienteRepository cliRepo;
	
	@Autowired
	private BCryptPasswordEncoder pe;
	
	@Autowired
	private EmailService emailServ;
	
	private Random random = new Random();

	public void  sendNewPassword(String email) {
		Cliente cliente = cliRepo.findByEmail(email);
		
		if(cliente == null) {
			throw new ObjectNotFoundException("Email não encontrado");
		}
		
		String newPass = newPassword();
		cliente.setSenha(pe.encode(newPass));
		
		cliRepo.save(cliente);
		
		emailServ.sendNewPasswordEmail(cliente, newPass);
	}

	private String newPassword() {
		char[] vet = new char[10];
		for(int i = 0;i<10;i++) {
			vet[i] = randonChar();
		}
		
		return new String(vet);
	}

	private char randonChar() {
		int opt = random.nextInt(3);
		
		if(opt == 0) { // Gera um dígito
			return (char) (random.nextInt(10) + 48);
		}
		else if(opt == 1) { // Gera uma letra maiúscula
			return (char) (random.nextInt(26) + 65);
		}
		else { // Gera uma letra minúscula
			return (char) (random.nextInt(26) + 97);
		}
	}
}
