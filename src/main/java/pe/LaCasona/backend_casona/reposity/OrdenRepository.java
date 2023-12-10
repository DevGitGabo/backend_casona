package pe.LaCasona.backend_casona.reposity;

import org.springframework.data.jpa.repository.JpaRepository;
import pe.LaCasona.backend_casona.models.Entity.DetalleOrdenEntity;
import pe.LaCasona.backend_casona.models.Entity.OrdenEntity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

public interface OrdenRepository extends JpaRepository<OrdenEntity, Integer> {
    List<OrdenEntity> findAllByEstado(String status);
    OrdenEntity findByIdOrden (Integer id);
    List<OrdenEntity> findByFechaOrdenBetween(Date fechaInicio, Date fechaCulminacion);
}

