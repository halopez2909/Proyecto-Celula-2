-- Crear tabla processed_events
CREATE TABLE IF NOT EXISTS processed_events (
    id BIGSERIAL PRIMARY KEY,
    event_id VARCHAR(255) NOT NULL UNIQUE,
    processed_at TIMESTAMP NOT NULL
);

-- Crear tabla products
CREATE TABLE IF NOT EXISTS products (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description VARCHAR(1000),
    price NUMERIC(10,2) NOT NULL,
    sku VARCHAR(100) NOT NULL UNIQUE,
    stock INTEGER NOT NULL DEFAULT 0,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP
);

-- 5 productos semilla
INSERT INTO products (name, description, price, sku, stock, created_at, updated_at) VALUES
('Laptop Pro 15', 'Laptop de alto rendimiento con procesador Intel i7', 1299.99, 'LAP-001', 50, NOW(), NOW()),
('Mouse Inalambrico', 'Mouse ergonomico con conexion bluetooth', 29.99, 'MOU-001', 150, NOW(), NOW()),
('Teclado Mecanico', 'Teclado mecanico RGB con switches Cherry MX', 89.99, 'TEC-001', 75, NOW(), NOW()),
('Monitor 4K 27', 'Monitor 4K UHD con panel IPS y 144Hz', 499.99, 'MON-001', 30, NOW(), NOW()),
('Auriculares Gaming', 'Auriculares con sonido surround 7.1 y microfono', 79.99, 'AUR-001', 100, NOW(), NOW());