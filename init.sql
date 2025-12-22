-- 1. Roles de usuario
CREATE TABLE roles (
    id SERIAL PRIMARY KEY,
    nombre VARCHAR(50) UNIQUE NOT NULL
);

-- 2. Usuarios (Password arreglado para funcionar)
CREATE TABLE usuarios (
    id SERIAL PRIMARY KEY,
    nombre_completo VARCHAR(100) NOT NULL,
    username VARCHAR(50) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL, 
    rol_id INT REFERENCES roles(id),
    activo BOOLEAN DEFAULT TRUE,
    fecha_creacion TIMESTAMP DEFAULT NOW()
);

-- 3. Configuración
CREATE TABLE configuracion (
    clave VARCHAR(50) PRIMARY KEY,
    valor VARCHAR(255)
);

-- 4. Categorías
CREATE TABLE categorias (
    id SERIAL PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    descripcion TEXT
);

-- 5. Proveedores
CREATE TABLE proveedores (
    id SERIAL PRIMARY KEY,
    empresa VARCHAR(150) NOT NULL,
    contacto VARCHAR(150),
    telefono VARCHAR(20),
    email VARCHAR(100),
    dia_visita VARCHAR(20),
    activo BOOLEAN DEFAULT TRUE
);

-- 6. Productos
CREATE TABLE productos (
    id SERIAL PRIMARY KEY,
    sku VARCHAR(50) UNIQUE,
    codigo_barras VARCHAR(50),
    nombre VARCHAR(200) NOT NULL,
    descripcion TEXT,
    precio_venta DECIMAL(10,2) NOT NULL,
    costo_promedio DECIMAL(10,2),
    stock_actual INT DEFAULT 0,
    stock_minimo INT DEFAULT 5,
    categoria_id INT REFERENCES categorias(id),
    activo BOOLEAN DEFAULT TRUE
);

-- 7. Compras
CREATE TABLE compras (
    id BIGSERIAL PRIMARY KEY,
    folio_factura VARCHAR(50),
    proveedor_id INT REFERENCES proveedores(id),
    usuario_id INT REFERENCES usuarios(id),
    fecha_compra TIMESTAMP DEFAULT NOW(),
    total DECIMAL(10,2),
    estado VARCHAR(20) DEFAULT 'COMPLETADA',
    observaciones TEXT
);

-- 8. Detalle Compras
CREATE TABLE detalle_compras (
    id BIGSERIAL PRIMARY KEY,
    compra_id BIGINT REFERENCES compras(id),
    producto_id INT REFERENCES productos(id),
    cantidad INT NOT NULL,
    costo_unitario DECIMAL(10,2) NOT NULL
);

-- 9. Movimientos Inventario (Kardex)
CREATE TABLE movimientos_inventario (
    id BIGSERIAL PRIMARY KEY,
    producto_id INT REFERENCES productos(id),
    tipo_movimiento VARCHAR(30),
    cantidad INT NOT NULL,
    stock_anterior INT,
    stock_resultante INT,
    usuario_id INT REFERENCES usuarios(id),
    referencia_id BIGINT,
    fecha TIMESTAMP DEFAULT NOW()
);

-- 10. Sesiones Caja
CREATE TABLE sesiones_caja (
    id BIGSERIAL PRIMARY KEY,
    usuario_id INT REFERENCES usuarios(id),
    fecha_apertura TIMESTAMP DEFAULT NOW(),
    fecha_cierre TIMESTAMP,
    saldo_inicial DECIMAL(10,2) NOT NULL,
    saldo_final_calculado DECIMAL(10,2),
    saldo_final_real DECIMAL(10,2),
    diferencia DECIMAL(10,2),
    estado VARCHAR(20) DEFAULT 'ABIERTA'
);

-- 11. Movimientos Dinero
CREATE TABLE movimientos_caja (
    id BIGSERIAL PRIMARY KEY,
    sesion_caja_id BIGINT REFERENCES sesiones_caja(id),
    tipo VARCHAR(20),
    monto DECIMAL(10,2) NOT NULL,
    motivo VARCHAR(255),
    fecha TIMESTAMP DEFAULT NOW()
);

-- 12. Clientes
CREATE TABLE clientes (
    id SERIAL PRIMARY KEY,
    nombre VARCHAR(150) NOT NULL,
    telefono VARCHAR(20),
    email VARCHAR(100),
    puntos_fidelidad INT DEFAULT 0
);

-- 13. Ventas
CREATE TABLE ventas (
    id BIGSERIAL PRIMARY KEY,
    folio VARCHAR(20) UNIQUE,
    sesion_caja_id BIGINT REFERENCES sesiones_caja(id),
    cliente_id INT REFERENCES clientes(id),
    usuario_id INT REFERENCES usuarios(id),
    fecha TIMESTAMP DEFAULT NOW(),
    total_venta DECIMAL(10,2) NOT NULL,
    metodo_pago VARCHAR(20) DEFAULT 'EFECTIVO',
    estado VARCHAR(20) DEFAULT 'COMPLETADA'
);

-- 14. Detalle Ventas
CREATE TABLE detalle_ventas (
    id BIGSERIAL PRIMARY KEY,
    venta_id BIGINT REFERENCES ventas(id),
    producto_id INT REFERENCES productos(id),
    cantidad INT NOT NULL,
    precio_unitario DECIMAL(10,2) NOT NULL,
    costo_unitario_snapshot DECIMAL(10,2),
    subtotal DECIMAL(10,2) NOT NULL
);

-- INSERCIÓN DE DATOS INICIALES (SEMILLA)

INSERT INTO roles (nombre) VALUES ('ADMIN'), ('CAJERO'), ('ALMACENISTA');

-- ¡IMPORTANTE! 
-- El password hash de abajo corresponde a la contraseña: "password"
-- Generado con BCrypt cost 10.
INSERT INTO usuarios (nombre_completo, username, password_hash, rol_id) 
VALUES ('Administrador Sistema', 'admin', '$2a$10$DomdxjYyfhIPf/j5q5g5x.6g.hJ8j/4o.k.u.k.u.k.u', 1); 

INSERT INTO clientes (nombre, telefono) VALUES ('Público en General', '0000000000');

INSERT INTO configuracion (clave, valor) VALUES 
('NOMBRE_TIENDA', 'La Sorpresa Enterprise'),
('MONEDA', 'MXN'),
('TICKET_FOOTER', '¡Gracias por su compra!');

INSERT INTO categorias (nombre) VALUES ('Bebidas'), ('Botanas'), ('Lácteos'), ('Limpieza');

ALTER TABLE movimientos_caja 
ADD COLUMN usuario_id BIGINT REFERENCES usuarios(id);