package com.nelioalves.cursomc.services;

import java.util.Date;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.nelioalves.cursomc.domain.ItemPedido;
import com.nelioalves.cursomc.domain.PagamentoComBoleto;
import com.nelioalves.cursomc.domain.Pedido;
import com.nelioalves.cursomc.domain.enums.EstadoPagamento;
import com.nelioalves.cursomc.repositories.ItemPedidoRepository;
import com.nelioalves.cursomc.repositories.PagamentoRepository;
import com.nelioalves.cursomc.repositories.PedidoRepository;
import com.nelioalves.cursomc.services.exceptions.ObjectNotFoundException;

@Service
public class PedidoService {

	@Autowired
	private PedidoRepository repoPed;
	@Autowired
	private BoletoService servBol;
	@Autowired
	private PagamentoRepository repoPag;
	@Autowired
	private ProdutoService servProd;
	@Autowired
	private ItemPedidoRepository repoItem;
	@Autowired
	private ClienteService servCli;
	
	// É uma interface e portanto alguém precisa instanciá-la. Sê-lá-á em TestConfig como MockEmailService ou DevCnofig como SmtpEmailService.
	@Autowired
	private EmailService servEmail;
	
	public Pedido find(Integer id) {
		Optional<Pedido> obj = repoPed.findById(id);
		return obj.orElseThrow(() -> new ObjectNotFoundException("Objeto não encontrado. Id: "+id+", Tipo: "+Pedido.class.getName()));
	}

	@Transactional
	public Pedido insert(Pedido obj) {
		
		obj.setId(null);
		obj.setInstante(new Date());
		obj.setCliente(servCli.find(obj.getCliente().getId()));
		obj.getPagamento().setEstado(EstadoPagamento.PENDENTE);
		obj.getPagamento().setPedido(obj);
		
		if(obj.getPagamento() instanceof PagamentoComBoleto) {
			PagamentoComBoleto pagto = (PagamentoComBoleto) obj.getPagamento();
			servBol.preencherPagamentoComBoleto(pagto, obj.getInstante());
		}
		
		obj = repoPed.save(obj);
		repoPag.save(obj.getPagamento());
		
		for(ItemPedido ip : obj.getItens()) {
			ip.setDesconto(0.0);
			ip.setProduto(servProd.find(ip.getProduto().getId()));
			ip.setPreco(ip.getProduto().getPreco());
			ip.setPedido(obj);
		}
		
		repoItem.saveAll(obj.getItens());
		
		//servEmail.sendOrderConfirmationEmail(obj);
		servEmail.sendOrderConfirmationHtmlEmail(obj);
		
		return obj;
	}
}
 	