-- Script para reparar Flyway y recrear la base de datos

-- 1. Eliminar la tabla de historial de Flyway
DROP TABLE IF EXISTS flyway_schema_history;

-- 2. Eliminar todas las tablas existentes (en orden debido a FK)
DROP TABLE IF EXISTS movimiento;
DROP TABLE IF EXISTS productos;
DROP TABLE IF EXISTS categoria;
DROP TABLE IF EXISTS usuarios;
