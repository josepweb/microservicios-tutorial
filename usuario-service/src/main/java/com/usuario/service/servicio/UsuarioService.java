package com.usuario.service.servicio;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.usuario.service.entidades.Usuario;
import com.usuario.service.feignclients.CarroFeignClients;
import com.usuario.service.feignclients.MotoFeignClients;
import com.usuario.service.modelos.Carro;
import com.usuario.service.modelos.Moto;
import com.usuario.service.repositorio.UsuarioRepository;

@Service
public class UsuarioService {
	
	@Autowired
	private RestTemplate restTemplate;
	
	@Autowired
	private UsuarioRepository usuarioRepository;
	
	@Autowired
	private CarroFeignClients carroFeignClients;
	
	@Autowired
	private MotoFeignClients motoFeignClients;
	
	public List<Carro> getCarros(int usuarioId){
		List<Carro> carros = restTemplate.getForObject("http://localhost:8082/carro/usuario/"+ usuarioId, List.class);
		return carros;
	}
	
	public List<Moto> getMotos(int usuarioId){
		List<Moto> motos = restTemplate.getForObject("http://localhost:8003/moto/usuario/"+ usuarioId, List.class);
		return motos;
	}
	
	public Carro saveCarro(int usuarioId, Carro carro) {
		carro.setUsuarioId(usuarioId);
		Carro nuevoCarro = carroFeignClients.save(carro);
		return nuevoCarro;
		
	}
	
	public Moto saveMoto(int usuarioId, Moto moto) {
		moto.setUsuarioId(usuarioId);
		Moto nuevaMoto = motoFeignClients.save(moto);
		return nuevaMoto;
	}
	
	public List<Usuario> gelAll(){
		return usuarioRepository.findAll();
	}
	
	public Usuario getUsuarioByID(int id) {
		return usuarioRepository.findById(id).orElse(null);
	}
	
	public Usuario saveUsuario(Usuario usuario) {
		Usuario nuevoUsuario = usuarioRepository.save(usuario);
		return  nuevoUsuario;
	}
	
	public Map<String, Object> getUsuariosAndVehicles(int usuarioId){
		Map<String, Object> resultado = new HashMap<>();
		Usuario usuario = usuarioRepository.findById(usuarioId).orElse(null);
		
		if(usuario == null) {
			resultado.put("Mensaje", "El usuario no existe");
			return resultado;
		}
		resultado.put("Usuario", usuario);
		List<Carro> carros = carroFeignClients.getCarros(usuarioId);
		if(carros.isEmpty()) {
			resultado.put("Carros", "El usuario no tiene carros");
		}else {
			resultado.put("Carros", carros);
		}
		List<Moto> motos = motoFeignClients.getMotos(usuarioId);
		if(motos.isEmpty()) {
			resultado.put("Motos", "El usuario no tiene motos");
		}else {
			resultado.put("Motos", motos);
		}
		return resultado;
		
	}

}
