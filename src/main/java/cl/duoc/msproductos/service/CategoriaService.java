package cl.duoc.msproductos.service;

import cl.duoc.msproductos.dto.CategoriaRequest;
import cl.duoc.msproductos.dto.CategoriaResponse;
import cl.duoc.msproductos.exception.ConflictoException;
import cl.duoc.msproductos.exception.RecursoNoEncontradoException;
import cl.duoc.msproductos.model.Categoria;
import cl.duoc.msproductos.repository.CategoriaRepository;
import cl.duoc.msproductos.repository.ProductoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CategoriaService {

    private final CategoriaRepository categoriaRepository;
    private final ProductoRepository productoRepository;

    public CategoriaService(CategoriaRepository categoriaRepository, ProductoRepository productoRepository) {
        this.categoriaRepository = categoriaRepository;
        this.productoRepository = productoRepository;
    }

    @Transactional(readOnly = true)
    public List<CategoriaResponse> listar() {
        return categoriaRepository.findAll().stream().map(this::aResponse).toList();
    }

    @Transactional(readOnly = true)
    public CategoriaResponse obtener(Long id) {
        return aResponse(buscar(id));
    }

    @Transactional
    public CategoriaResponse crear(CategoriaRequest request) {
        if (categoriaRepository.existsByNombreIgnoreCase(request.nombre())) {
            throw new ConflictoException("Ya existe una categoría con el nombre " + request.nombre());
        }
        Categoria categoria = new Categoria(request.nombre(), request.descripcion());
        return aResponse(categoriaRepository.save(categoria));
    }

    @Transactional
    public CategoriaResponse actualizar(Long id, CategoriaRequest request) {
        Categoria categoria = buscar(id);
        if (categoriaRepository.existsByNombreIgnoreCaseAndIdNot(request.nombre(), id)) {
            throw new ConflictoException("Ya existe una categoría con el nombre " + request.nombre());
        }
        categoria.setNombre(request.nombre());
        categoria.setDescripcion(request.descripcion());
        return aResponse(categoriaRepository.save(categoria));
    }

    @Transactional
    public void eliminar(Long id) {
        Categoria categoria = buscar(id);
        if (productoRepository.existsByCategoriaId(id)) {
            throw new ConflictoException("No se puede eliminar la categoría porque tiene productos asociados");
        }
        categoriaRepository.delete(categoria);
    }

    Categoria buscar(Long id) {
        return categoriaRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Categoría con id " + id + " no encontrada"));
    }

    private CategoriaResponse aResponse(Categoria categoria) {
        return new CategoriaResponse(categoria.getId(), categoria.getNombre(), categoria.getDescripcion());
    }
}
