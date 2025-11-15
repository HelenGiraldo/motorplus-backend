package com.motorplus.backend.repository;
import com.motorplus.backend.entity.Repuesto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
@Repository
public interface RepuestoRepository extends JpaRepository<Repuesto, Long> {}