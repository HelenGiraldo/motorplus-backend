package com.motorplus.backend.controller;

import com.motorplus.backend.dao.UsuarioDAO;
import com.motorplus.backend.entity.Usuario;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    private UsuarioDAO usuarioDAO = new UsuarioDAO();

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(@RequestBody Map<String, String> credentials) {
        String username = credentials.get("username");
        String password = credentials.get("password");

        Usuario usuario = usuarioDAO.findByUsername(username);

        if (usuario != null) {
            if (passwordEncoder.matches(password, usuario.getPassword())) {
                return ResponseEntity.ok(Map.of("message", "Login Exitoso", "user", usuario.getUsername()));
            }
        }

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(Map.of("error", "Credenciales inválidas"));
    }
}