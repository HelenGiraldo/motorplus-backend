package com.motorplus.backend.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TelefonoProveedor {
    private Integer idTelefono;
    private String numero;
    private String tipo;
    private Boolean esPrincipal;
    private Integer idProveedor; // Cambiado: idCliente → idProveedor
}