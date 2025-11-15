// Cliente.java
package com.motorplus.backend.entity;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.util.List;

@Data @NoArgsConstructor @AllArgsConstructor
public class Cliente {
    private Long idCliente;
    private String nombres;
    private String apellidos;
    private String documento;
    private String telefono;
    private String email;
    private String direccion;
    private List<Vehiculo> vehiculos; // Para JSON, si se necesita
}