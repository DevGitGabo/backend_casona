package pe.LaCasona.backend_casona.reposity;

import org.springframework.data.jpa.repository.JpaRepository;
import pe.LaCasona.backend_casona.models.Entity.DetalleOrdenEntity;
import pe.LaCasona.backend_casona.models.Entity.OrdenEntity;
import pe.LaCasona.backend_casona.models.Entity.ProductoEntity;

import java.sql.Date;
import java.util.List;

public interface DetalleOrdenRepository extends JpaRepository<DetalleOrdenEntity, Integer> {
    List<DetalleOrdenEntity> findAllByOrden(OrdenEntity orden);
    List<DetalleOrdenEntity> findByProducto(ProductoEntity producto);
    List<DetalleOrdenEntity> findByProductoIn(List<ProductoEntity> producto);
    List<DetalleOrdenEntity> findByOrdenIn(List<OrdenEntity> ordenes);
}
