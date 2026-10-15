package cl.duoc.msproductos.service;

import cl.duoc.msproductos.dto.ProductoRequest;
import cl.duoc.msproductos.dto.ProductoResponse;
import cl.duoc.msproductos.exception.RecursoNoEncontradoException;
import cl.duoc.msproductos.model.Categoria;
import cl.duoc.msproductos.model.Producto;
import cl.duoc.msproductos.repository.ProductoRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProductoServiceTest {

    @Mock
    private ProductoRepository productoRepository;

    @Mock
    private CategoriaService categoriaService;

    @InjectMocks
    private ProductoService productoService;

    @Test
    void crearProductoAsociaLaCategoria() {
        Categoria categoria = new Categoria("Lácteos", "Productos lácteos");
        categoria.setId(1L);
        when(categoriaService.buscar(1L)).thenReturn(categoria);
        when(productoRepository.save(any(Producto.class))).thenAnswer(inv -> {
            Producto p = inv.getArgument(0);
            p.setId(10L);
            return p;
        });

        ProductoRequest request = new ProductoRequest("Leche", "Leche entera 1L", new BigDecimal("1200"), 1L, null);
        ProductoResponse response = productoService.crear(request);

        assertEquals(10L, response.id());
        assertEquals("Lácteos", response.categoriaNombre());
        assertEquals(new BigDecimal("1200"), response.precio());
    }

    @Test
    void obtenerProductoInexistenteLanzaExcepcion() {
        when(productoRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(RecursoNoEncontradoException.class, () -> productoService.obtener(99L));
    }
}
