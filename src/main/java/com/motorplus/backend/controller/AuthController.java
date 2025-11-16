package com.motorplus.backend.controller;

import com.motorplus.backend.dao.UsuarioDAO;
import com.motorplus.backend.entity.Usuario;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    private UsuarioDAO usuarioDAO = new UsuarioDAO();


    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(@RequestBody Map<String, String> credentials) {
        String username = credentials.get("username");
        String password = credentials.get("password");

        Usuario usuario = usuarioDAO.findByUsername(username);

        if (usuario != null) {

            if (password.equals(usuario.getPassword())) {

                return ResponseEntity.ok(Map.of("message", "Login Exitoso", "user", usuario.getUsername()));
            }
        }

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(Map.of("error", "Credenciales inválidas"));
    }
}