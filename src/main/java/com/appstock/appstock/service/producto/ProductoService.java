package com.appstock.appstock.service.producto;

import com.appstock.appstock.dto.ProductoDTO;
import com.appstock.appstock.entity.Producto;
import com.appstock.appstock.entity.Producto;
import com.appstock.appstock.repository.producto.IProductoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

@Service
public class ProductoService implements IProductoService{

    private final Logger logger = LoggerFactory.getLogger(ProductoService.class);

    @Autowired
    IProductoRepository productoRepository;

    @Override
    public List<Producto> getProductos() {
        return (List<Producto>) productoRepository.findAll();
    }

    @Override
    public Producto getProductoById(Long id) {
        return productoRepository.findById(id).orElse(null);
    }

    @Override
    public Producto addProducto(Producto producto) {
        return productoRepository.save(producto);
    }

    @Override
    public Producto updateProducto(Long id, ProductoDTO producto) {
        Producto existingProducto = productoRepository.findById(id).orElseThrow(() -> new RuntimeException("Producto con ID: " + id + " no encontrada"));
        existingProducto.setNombre(producto.getNombre());
        existingProducto.setDescripcion(producto.getDescripcion());
        existingProducto.setPrecio(producto.getPrecio());
        existingProducto.setStock(producto.getStock());
        existingProducto.setImagen(producto.getImagen());
        return productoRepository.save(existingProducto);
    }

    @Override
    public void deleteProducto(Long id) throws Exception {
        productoRepository.findById(id).orElseThrow(() -> new Exception("No existe el producto"));
        productoRepository.deleteById(id);
    }

    @Override
    public List<Producto> getProductosPorCategoria(Long categoriaId) {
        return productoRepository.buscarPorCategoria(categoriaId);
    }
}
