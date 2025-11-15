package com.motorplus.backend.repository;
import com.motorplus.backend.entity.OrdenRepuesto;
import com.motorplus.backend.entity.OrdenRepuestoId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
@Repository
public interface OrdenRepuestoRepository extends JpaRepository<OrdenRepuesto, OrdenRepuestoId> {}