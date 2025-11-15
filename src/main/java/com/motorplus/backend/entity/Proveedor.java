package com.motorplus.backend.entity;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data @NoArgsConstructor @AllArgsConstructor
public class Proveedor {
    private Long idProveedor;
    private String nombre;
    private String nit;
    private String telefono;
    private String direccion;
}