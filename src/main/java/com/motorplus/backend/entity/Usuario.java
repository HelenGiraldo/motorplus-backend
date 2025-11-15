// Usuario.java
package com.motorplus.backend.entity;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data @NoArgsConstructor @AllArgsConstructor
public class Usuario {
    private Long idUsuario;
    private String username;
    private String password;
    private Long idMecanico;
}