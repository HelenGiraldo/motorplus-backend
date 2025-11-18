package com.motorplus.backend.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TelefonoCliente {
    private Integer idTelefono;
    private String numero;
    private String tipo; // 'FIJO', 'MOVIL', 'TRABAJO'
    private Boolean esPrincipal;
    private Integer idCliente;
}