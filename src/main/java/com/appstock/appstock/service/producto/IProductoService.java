package com.appstock.appstock.service.producto;

import com.appstock.appstock.entity.Producto;

import java.util.List;

public interface IProductoService {
    List<Producto> getProductos();

    Producto getProductoById(Long id);

    Producto addProducto(Producto producto);

    Producto updateProducto(Producto producto);

    boolean deleteProducto(Long id) throws Exception;
}
