import com.victor.commons.enums.EstadoRegistro;
import feign.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface CitaRepository extends JpaRepository<Cita, Long> {
    
    List<Cita>findByEstadoRegistro(EstadoRegistro estadoRegistro);
    
    Optional<Cita>findByIdAndEstadoRegistro(Long id, EstadoRegistro estadoRegistro);
    
    boolean existsByIdPacienteAndEstadoCitaInAndIdNot(Long idPaciente, Collection<EstadoCita> estadoCitas, Long id);

    boolean existsByIdMedicoAndEstadoCitaInAndIdNot(Long idMedico, Collection<EstadoCita> estados, Long id
    );
    boolean existsByIdPacienteAndEstadoCitaIn(Long idPaciente, List<EstadoCita> estados
    );
    boolean existsByIdMedicoAndEstadoCitaIn(Long idMedico, List<EstadoCita> estados
    );
}