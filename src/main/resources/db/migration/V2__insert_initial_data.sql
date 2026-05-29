-- Insertar datos iniciales de categorías
INSERT INTO categoria (nombre, descripcion) VALUES
('Electrónica', 'Productos electrónicos y tecnología'),
('Ropa', 'Prendas de vestir y accesorios'),
('Alimentos', 'Productos alimenticios'),
('Hogar', 'Artículos para el hogar');

-- Insertar usuario administrador (password: admin123)
-- BCrypt hash para "admin123"
INSERT INTO usuarios (nombre, password, rol, email) VALUES
('admin', '$2a$10$N9qo8uLOickgx2ZMRZoMye1J8L1OLNKfKbGPXNJqL5iVTqMCTJH5e', 'ROLE_ADMIN', 'admin@appstock.com');
