package com.victor.citas.repository;

import com.victor.citas.entity.Cita;
import com.victor.comons.enums.EstadoRegistro;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
@Repository
public interface CitaRepository extends JpaRepository<Cita, Long> {
    List<Cita> findByEstadoRegistro(EstadoRegistro estadoRegistro);
    Optional<Cita> findByIdAndEstadoRegistro(Long id, EstadoRegistro estadoRegistro);
}
