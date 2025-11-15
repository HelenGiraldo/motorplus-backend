package com.motorplus.backend.repository;
import com.motorplus.backend.entity.OrdenMecanico;
import com.motorplus.backend.entity.OrdenMecanicoId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
@Repository
public interface OrdenMecanicoRepository extends JpaRepository<OrdenMecanico, OrdenMecanicoId> {}