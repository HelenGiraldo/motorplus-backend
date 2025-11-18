package com.motorplus.backend.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Cliente {
    private Integer idCliente;
    private String nombres;
    private String apellidos;
    private String documento;
    private String telefono;
    private String email;
    private String direccion;
    private List<Vehiculo> vehiculos;
    private List<TelefonoCliente> telefonos;
    private List<CorreoCliente> correos;
    private List<DireccionCliente> direcciones;
}