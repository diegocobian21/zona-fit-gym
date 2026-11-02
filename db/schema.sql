-- Esquema de la base de datos de Zona Fit Gym (MySQL)
-- La aplicacion usa spring.jpa.hibernate.ddl-auto=none, por lo que la tabla debe crearse antes de ejecutarla.

CREATE DATABASE IF NOT EXISTS zona_fit_db;
USE zona_fit_db;

CREATE TABLE IF NOT EXISTS cliente (
    id        INT AUTO_INCREMENT PRIMARY KEY,
    nombre    VARCHAR(100) NOT NULL,
    apellido  VARCHAR(100),
    membresia INT NOT NULL
);

-- Datos de ejemplo (opcional)
INSERT INTO cliente (nombre, apellido, membresia) VALUES
    ('Ana', 'López', 100),
    ('Carlos', 'Pérez', 200),
    ('María', 'González', 150);
