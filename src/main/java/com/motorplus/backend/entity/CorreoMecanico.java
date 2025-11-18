package com.motorplus.backend.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CorreoMecanico {
    private Integer idCorreo;
    private String email;
    private String tipo;
    private Boolean esPrincipal = false;
    private Long idMecanico;

    public Boolean getEsPrincipal() {
        return esPrincipal != null ? esPrincipal : false;
    }

    public void setEsPrincipal(Boolean esPrincipal) {
        this.esPrincipal = esPrincipal != null ? esPrincipal : false;
    }
}