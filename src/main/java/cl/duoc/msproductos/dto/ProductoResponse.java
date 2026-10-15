package cl.duoc.msproductos.dto;

import java.math.BigDecimal;

public record ProductoResponse(
        Long id,
        String nombre,
        String descripcion,
        BigDecimal precio,
        boolean activo,
        Long categoriaId,
        String categoriaNombre
) {
}
