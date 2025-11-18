package com.motorplus.backend.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Proveedor {
    private Integer idProveedor;
    private String nombre;
    private String nit;
    private String telefono;
    private String correo;
    private String direccion;

    private List<TelefonoProveedor> telefonos;
    private List<CorreoProveedor> correos;
    private List<DireccionProveedor> direcciones;
}