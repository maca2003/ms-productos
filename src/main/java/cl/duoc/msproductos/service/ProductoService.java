package cl.duoc.msproductos.service;

import cl.duoc.msproductos.dto.ProductoRequest;
import cl.duoc.msproductos.dto.ProductoResponse;
import cl.duoc.msproductos.exception.RecursoNoEncontradoException;
import cl.duoc.msproductos.model.Categoria;
import cl.duoc.msproductos.model.Producto;
import cl.duoc.msproductos.repository.ProductoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ProductoService {

    private final ProductoRepository productoRepository;
    private final CategoriaService categoriaService;

    public ProductoService(ProductoRepository productoRepository, CategoriaService categoriaService) {
        this.productoRepository = productoRepository;
        this.categoriaService = categoriaService;
    }

    @Transactional(readOnly = true)
    public List<ProductoResponse> listar(Long categoriaId) {
        List<Producto> productos = (categoriaId == null)
                ? productoRepository.findAll()
                : productoRepository.findByCategoriaId(categoriaId);
        return productos.stream().map(this::aResponse).toList();
    }

    @Transactional(readOnly = true)
    public ProductoResponse obtener(Long id) {
        return aResponse(buscar(id));
    }

    @Transactional
    public ProductoResponse crear(ProductoRequest request) {
        Producto producto = new Producto();
        aplicarDatos(producto, request);
        return aResponse(productoRepository.save(producto));
    }

    @Transactional
    public ProductoResponse actualizar(Long id, ProductoRequest request) {
        Producto producto = buscar(id);
        aplicarDatos(producto, request);
        return aResponse(productoRepository.save(producto));
    }

    @Transactional
    public void eliminar(Long id) {
        productoRepository.delete(buscar(id));
    }

    private Producto buscar(Long id) {
        return productoRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Producto con id " + id + " no encontrado"));
    }

    private void aplicarDatos(Producto producto, ProductoRequest request) {
        Categoria categoria = categoriaService.buscar(request.categoriaId());
        producto.setNombre(request.nombre());
        producto.setDescripcion(request.descripcion());
        producto.setPrecio(request.precio());
        producto.setCategoria(categoria);
        if (request.activo() != null) {
            producto.setActivo(request.activo());
        }
    }

    private ProductoResponse aResponse(Producto producto) {
        return new ProductoResponse(
                producto.getId(),
                producto.getNombre(),
                producto.getDescripcion(),
                producto.getPrecio(),
                producto.isActivo(),
                producto.getCategoria().getId(),
                producto.getCategoria().getNombre());
    }
}
