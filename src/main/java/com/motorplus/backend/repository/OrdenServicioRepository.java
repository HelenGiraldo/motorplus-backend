package com.motorplus.backend.repository;
import com.motorplus.backend.entity.OrdenServicio;
import com.motorplus.backend.entity.OrdenServicioId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
@Repository
public interface OrdenServicioRepository extends JpaRepository<OrdenServicio, OrdenServicioId> {}