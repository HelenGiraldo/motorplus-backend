package com.motorplus.backend.repository;
import com.motorplus.backend.entity.OrdenTrabajo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
@Repository
public interface OrdenTrabajoRepository extends JpaRepository<OrdenTrabajo, Long> {}