package com.equipo5.proyecto.servicios;

import org.mindrot.jbcrypt.BCrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;

import com.equipo5.proyecto.modelos.LoginUsuario;
import com.equipo5.proyecto.modelos.Usuario;
import com.equipo5.proyecto.repositorios.RepositorioUsuario;


@Service
public class ServicioUsuario {

	@Autowired
	private final RepositorioUsuario resRepositorioUsuario;

	public ServicioUsuario(RepositorioUsuario resRepositorioUsuario) {
		this.resRepositorioUsuario = resRepositorioUsuario;
	}

	public BindingResult validarRegistro(BindingResult validaciones, Usuario usuario) {

		if(! usuario.getContraseña().equals(usuario.getConfirmarContraseña())) {
			validaciones.rejectValue("confirmarContraseña", "NoCoinciden", "Las contraseñas no coinciden.");
		}

		if(this.obtenerPorCorreo(usuario.getCorreo()) != null) {
			validaciones.rejectValue("correo", "Existente", "Este correo ya está en uso!");
		}

		return validaciones;
	}

	public BindingResult validarLoginVoluntario(BindingResult validaciones, LoginUsuario loginUsuario) {
        Usuario usuarioActual = this.obtenerPorCorreo(loginUsuario.getUsuarioCorreo());
        if (usuarioActual == null) {
            validaciones.rejectValue("usuarioCorreo", "NoExistente", "Este usuario no existe");
        } else if (!BCrypt.checkpw(loginUsuario.getUsuarioContraseña(), usuarioActual.getContraseña())) {
            validaciones.rejectValue("usuarioContraseña", "NoValida", "Credenciales no válidas");
        }
        return validaciones;
    }

	public Usuario insertarUsuario(Usuario usuario) {
		String contraseñaEncriptada = BCrypt.hashpw(usuario.getContraseña(), BCrypt.gensalt());
		usuario.setContraseña(contraseñaEncriptada);
		return this.resRepositorioUsuario.save(usuario);
	}
	
	public Usuario actualizarUsuario(Usuario usuario) {
		return this.resRepositorioUsuario.save(usuario);
	}

	public Usuario obtenerPorCorreo(String correo) {
		return this.resRepositorioUsuario.getByCorreo(correo);
	}

	public Usuario obtenerPorId(Long id) {
		return this.resRepositorioUsuario.findById(id).orElse(null);
	}
}