package com.motorplus.backend.repository;
import com.motorplus.backend.entity.Proveedor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
@Repository
public interface ProveedorRepository extends JpaRepository<Proveedor, Long> {}