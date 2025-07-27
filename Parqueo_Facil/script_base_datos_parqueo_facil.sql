
-- Crear base de datos
CREATE DATABASE IF NOT EXISTS reservas_db;
USE reservas_db;

-- Tabla: usuarios
CREATE TABLE usuarios (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(255),
    correo VARCHAR(255),
    contrasena VARCHAR(255)
);

-- Tabla: espacios
CREATE TABLE espacios (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    numero VARCHAR(50),
    ubicacion VARCHAR(255),
    capacidad INT
);

-- Tabla: reservas
CREATE TABLE reservas (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    fecha DATETIME,
    usuario_id BIGINT,
    espacio_id BIGINT,
    FOREIGN KEY (usuario_id) REFERENCES usuarios(id),
    FOREIGN KEY (espacio_id) REFERENCES espacios(id)
);


