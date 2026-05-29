-- Creación de la tabla categorias
CREATE TABLE IF NOT EXISTS categoria (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(255) NOT NULL UNIQUE,
    descripcion VARCHAR(500),
    parent_id BIGINT,
    FOREIGN KEY (parent_id) REFERENCES categoria(id) ON DELETE SET NULL
);

-- Creación de la tabla productos
CREATE TABLE IF NOT EXISTS productos (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(255) NOT NULL,
    descripcion VARCHAR(500),
    precio DECIMAL(10, 2) NOT NULL,
    stock INT NOT NULL,
    imagen TEXT,
    categoria_id BIGINT,
    FOREIGN KEY (categoria_id) REFERENCES categoria(id) ON DELETE SET NULL
);

-- Creación de la tabla usuarios
CREATE TABLE IF NOT EXISTS usuarios (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    rol VARCHAR(50) NOT NULL,
    email VARCHAR(255)
);

-- Creación de la tabla movimiento
CREATE TABLE IF NOT EXISTS movimiento (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    descripcion VARCHAR(500),
    tipo_movimiento VARCHAR(50) NOT NULL,
    fecha DATETIME NOT NULL,
    producto_id BIGINT NOT NULL,
    FOREIGN KEY (producto_id) REFERENCES productos(id) ON DELETE CASCADE
);

-- Índices para mejorar el rendimiento
CREATE INDEX idx_producto_categoria ON productos(categoria_id);
CREATE INDEX idx_movimiento_producto ON movimiento(producto_id);
CREATE INDEX idx_categoria_parent ON categoria(parent_id);
