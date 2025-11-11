package com.appstock.appstock.service.producto;

import com.appstock.appstock.entity.Producto;
import com.appstock.appstock.repository.producto.IProductoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductoService implements IProductoService{
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
    public Producto updateProducto(Producto producto) {
        return productoRepository.save(producto);
    }

    @Override
    public boolean deleteProducto(Long id) throws Exception {
        productoRepository.findById(id).orElseThrow(() -> new Exception("No existe el producto"));
        productoRepository.deleteById(id);
        return true;
    }
}
