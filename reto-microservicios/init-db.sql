-- Este script se ejecuta automáticamente al crear el contenedor por primera vez.
-- PostgreSQL ya crea 'authdb' via POSTGRES_DB, aquí creamos las otras 2.

CREATE DATABASE catalogdb;
CREATE DATABASE orderdb;

-- Dar permisos al usuario reto sobre las nuevas bases
GRANT ALL PRIVILEGES ON DATABASE catalogdb TO reto;
GRANT ALL PRIVILEGES ON DATABASE orderdb TO reto;
