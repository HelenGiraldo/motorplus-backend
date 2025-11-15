package com.motorplus.backend.repository;
import com.motorplus.backend.entity.Mecanico;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
@Repository
public interface MecanicoRepository extends JpaRepository<Mecanico, Long> {}