-- ==========================================
-- 0. LIMPIEZA TOTAL (RESET)
-- ==========================================
DROP TABLE IF EXISTS detalle_ventas CASCADE;
DROP TABLE IF EXISTS ventas CASCADE;
DROP TABLE IF EXISTS clientes CASCADE;
DROP TABLE IF EXISTS movimientos_caja CASCADE;
DROP TABLE IF EXISTS sesiones_caja CASCADE;
DROP TABLE IF EXISTS movimientos_inventario CASCADE;
DROP TABLE IF EXISTS detalle_compras CASCADE;
DROP TABLE IF EXISTS compras CASCADE;
DROP TABLE IF EXISTS productos CASCADE;
DROP TABLE IF EXISTS proveedores CASCADE;
DROP TABLE IF EXISTS categorias CASCADE;
DROP TABLE IF EXISTS configuracion CASCADE;
DROP TABLE IF EXISTS usuarios CASCADE;
DROP TABLE IF EXISTS roles CASCADE;

-- ==========================================
-- 1. SEGURIDAD Y USUARIOS
-- ==========================================
CREATE TABLE roles (
    id SERIAL PRIMARY KEY,
    nombre VARCHAR(50) UNIQUE NOT NULL
);

CREATE TABLE usuarios (
    id SERIAL PRIMARY KEY,
    nombre_completo VARCHAR(100) NOT NULL,
    username VARCHAR(50) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    rol_id INT REFERENCES roles(id) ON DELETE RESTRICT, -- No borrar rol si hay usuarios
    activo BOOLEAN DEFAULT TRUE,
    fecha_creacion TIMESTAMP DEFAULT NOW()
);

-- ==========================================
-- 2. CONFIGURACIÓN DEL SISTEMA
-- ==========================================
CREATE TABLE configuracion (
    clave VARCHAR(50) PRIMARY KEY,
    valor VARCHAR(255)
);

-- ==========================================
-- 3. INVENTARIO (CATÁLOGOS)
-- ==========================================
CREATE TABLE categorias (
    id SERIAL PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    descripcion TEXT
);

CREATE TABLE proveedores (
    id SERIAL PRIMARY KEY,
    empresa VARCHAR(150) NOT NULL,
    contacto VARCHAR(150),
    telefono VARCHAR(20),
    email VARCHAR(100),
    dia_visita VARCHAR(20),
    activo BOOLEAN DEFAULT TRUE
);

CREATE TABLE productos (
    id SERIAL PRIMARY KEY,
    sku VARCHAR(50) UNIQUE,
    codigo_barras VARCHAR(50),
    nombre VARCHAR(200) NOT NULL,
    descripcion TEXT,
    
    -- Restricciones Financieras
    precio_venta DECIMAL(10,2) NOT NULL CHECK (precio_venta >= 0),
    costo_promedio DECIMAL(10,2) DEFAULT 0 CHECK (costo_promedio >= 0),
    
    -- Control de Stock
    stock_actual INT DEFAULT 0, -- Permitimos negativos temporalmente? Mejor NO.
    stock_minimo INT DEFAULT 5 CHECK (stock_minimo >= 0),
    
    categoria_id INT REFERENCES categorias(id) ON DELETE SET NULL, -- Si borro categoría, el producto queda sin cat.
    activo BOOLEAN DEFAULT TRUE
);

-- Índices para búsquedas rápidas en el punto de venta
CREATE INDEX idx_productos_sku ON productos(sku);
CREATE INDEX idx_productos_barras ON productos(codigo_barras);
CREATE INDEX idx_productos_nombre ON productos(nombre);

-- ==========================================
-- 4. MÓDULO DE COMPRAS (El Nuevo Diseño)
-- ==========================================
CREATE TABLE compras (
    id BIGSERIAL PRIMARY KEY,
    folio_factura VARCHAR(50),
    proveedor_id INT NOT NULL REFERENCES proveedores(id) ON DELETE RESTRICT,
    usuario_id INT NOT NULL REFERENCES usuarios(id) ON DELETE RESTRICT,
    
    -- Estados y Tiempos
    estado VARCHAR(20) DEFAULT 'COMPLETADA' CHECK (estado IN ('PENDIENTE', 'COMPLETADA', 'CANCELADA')),
    fecha_pedido TIMESTAMP DEFAULT CURRENT_TIMESTAMP, -- Cuándo se pidió (Lunes)
    fecha_recepcion TIMESTAMP, -- Cuándo llegó (Jueves)
    fecha_estimada_entrega DATE,
    
    total DECIMAL(10,2) CHECK (total >= 0),
    observaciones TEXT
);

CREATE TABLE detalle_compras (
    id BIGSERIAL PRIMARY KEY,
    -- Si borro la compra (cabecera), se borran sus detalles (CASCADE)
    compra_id BIGINT NOT NULL REFERENCES compras(id) ON DELETE CASCADE, 
    producto_id INT NOT NULL REFERENCES productos(id) ON DELETE RESTRICT,
    
    -- Lógica de Preventa vs Realidad
    cantidad_pedida INT NOT NULL CHECK (cantidad_pedida > 0),
    cantidad_recibida INT CHECK (cantidad_recibida >= 0), -- Puede ser 0 si no llegó nada
    
    -- Factor de Empaque (Cajas)
    unidades_por_caja INT DEFAULT 1 CHECK (unidades_por_caja > 0),
    
    -- Costos Financieros
    costo_total_renglon DECIMAL(10,2) CHECK (costo_total_renglon >= 0),
    costo_unitario_calculado DECIMAL(10,4) CHECK (costo_unitario_calculado >= 0)
);

CREATE INDEX idx_compras_fecha ON compras(fecha_pedido);

-- ==========================================
-- 5. KARDEX (HISTORIAL DE MOVIMIENTOS)
-- ==========================================
CREATE TABLE movimientos_inventario (
    id BIGSERIAL PRIMARY KEY,
    producto_id INT REFERENCES productos(id) ON DELETE RESTRICT,
    tipo_movimiento VARCHAR(30) NOT NULL, -- 'VENTA', 'COMPRA', 'AJUSTE', 'MERMA'
    cantidad INT NOT NULL, -- Puede ser negativo (salida) o positivo (entrada)
    stock_anterior INT NOT NULL,
    stock_resultante INT NOT NULL,
    usuario_id INT REFERENCES usuarios(id),
    referencia_id BIGINT, -- ID de Venta o Compra
    fecha TIMESTAMP DEFAULT NOW()
);

CREATE INDEX idx_movimientos_producto ON movimientos_inventario(producto_id);

-- ==========================================
-- 6. CAJA Y DINERO
-- ==========================================
CREATE TABLE sesiones_caja (
    id BIGSERIAL PRIMARY KEY,
    usuario_id INT REFERENCES usuarios(id) ON DELETE RESTRICT,
    fecha_apertura TIMESTAMP DEFAULT NOW(),
    fecha_cierre TIMESTAMP,
    saldo_inicial DECIMAL(10,2) NOT NULL CHECK (saldo_inicial >= 0),
    saldo_final_calculado DECIMAL(10,2),
    saldo_final_real DECIMAL(10,2),
    diferencia DECIMAL(10,2),
    estado VARCHAR(20) DEFAULT 'ABIERTA' CHECK (estado IN ('ABIERTA', 'CERRADA'))
);

CREATE TABLE movimientos_caja (
    id BIGSERIAL PRIMARY KEY,
    sesion_caja_id BIGINT REFERENCES sesiones_caja(id) ON DELETE CASCADE,
    usuario_id INT REFERENCES usuarios(id),
    tipo VARCHAR(20) CHECK (tipo IN ('INGRESO', 'RETIRO')),
    monto DECIMAL(10,2) NOT NULL CHECK (monto > 0),
    motivo VARCHAR(255),
    fecha TIMESTAMP DEFAULT NOW()
);

-- ==========================================
-- 7. VENTAS
-- ==========================================
CREATE TABLE clientes (
    id SERIAL PRIMARY KEY,
    nombre VARCHAR(150) NOT NULL,
    telefono VARCHAR(20),
    email VARCHAR(100),
    puntos_fidelidad INT DEFAULT 0 CHECK (puntos_fidelidad >= 0)
);

CREATE TABLE ventas (
    id BIGSERIAL PRIMARY KEY,
    folio VARCHAR(20) UNIQUE,
    sesion_caja_id BIGINT REFERENCES sesiones_caja(id) ON DELETE RESTRICT, -- No borrar sesión si tiene ventas
    cliente_id INT REFERENCES clientes(id) ON DELETE SET NULL, -- Si borro cliente, venta queda anónima
    usuario_id INT REFERENCES usuarios(id) ON DELETE RESTRICT,
    fecha TIMESTAMP DEFAULT NOW(),
    total_venta DECIMAL(10,2) NOT NULL CHECK (total_venta >= 0),
    metodo_pago VARCHAR(20) DEFAULT 'EFECTIVO',
    estado VARCHAR(20) DEFAULT 'COMPLETADA' CHECK (estado IN ('COMPLETADA', 'CANCELADA'))
);

CREATE TABLE detalle_ventas (
    id BIGSERIAL PRIMARY KEY,
    venta_id BIGINT REFERENCES ventas(id) ON DELETE CASCADE, -- Si borro venta, adiós detalles
    producto_id INT REFERENCES productos(id) ON DELETE RESTRICT,
    
    cantidad INT NOT NULL CHECK (cantidad > 0),
    precio_unitario DECIMAL(10,2) NOT NULL CHECK (precio_unitario >= 0),
    
    -- Snapshot del costo al momento de vender (para reportes de ganancia real)
    costo_unitario_snapshot DECIMAL(10,4), 
    
    subtotal DECIMAL(10,2) NOT NULL CHECK (subtotal >= 0)
);

CREATE INDEX idx_ventas_fecha ON ventas(fecha);

-- ==========================================
-- 8. SEMILLA (DATOS INICIALES)
-- ==========================================
INSERT INTO roles (nombre) VALUES ('ADMIN'), ('CAJERO'), ('ALMACENISTA');

-- Password: "password"
INSERT INTO usuarios (nombre_completo, username, password_hash, rol_id) VALUES 
('Administrador Sistema', 'admin', '$2a$10$/zOp2Q/21F5QGcpRdzCnGe3U2xjC7ZlBLHvPmWudNhoRYbwgEAnhm', 1),
('Cajero de Turno', 'cajero', '$2a$10$1XAt2KWLAPzeEGFHKTciRum2TeUcHYnmM11FcLp9UHwjwX0xNcKSu', 2);

INSERT INTO clientes (nombre, telefono) VALUES ('Público en General', '0000000000');

INSERT INTO configuracion (clave, valor) VALUES 
('NOMBRE_TIENDA', 'La Sorpresa Enterprise'),
('MONEDA', 'MXN'),
('TICKET_FOOTER', '¡Gracias por su compra!');

INSERT INTO categorias (nombre) VALUES ('Bebidas'), ('Botanas'), ('Lácteos'), ('Limpieza');

INSERT INTO proveedores (empresa, contacto, telefono, email, dia_visita) VALUES 
('Coca-Cola FEMSA', 'Agente de Ventas', '555-123-4567', 'pedidos@cocacola.com', 'Lunes'),
('Zorro Abarrotero', 'N/A (Compra Independiente)', '555-987-6543', 'sucursal@zorro.com', 'N/A'),
('Sigma Alimentos', 'Vendedor Lácteos', '555-444-3322', 'distribucion@sigma.com', 'Miércoles');

INSERT INTO productos (sku, codigo_barras, nombre, descripcion, precio_venta, costo_promedio, stock_actual, stock_minimo, categoria_id) VALUES 
('BEB-CC-600', '7501055300075', 'Coca-Cola Original 600ml', 'Refresco de cola botella PET', 18.50, 14.00, 24, 12, 1),
('BEB-AGUA-1L', '7501032401016', 'Agua Purificada 1Lt', 'Botella de agua mineralizada', 12.00, 7.50, 30, 10, 1),
('LAC-YOG-FRE', '7501032331221', 'Yoghurt de Fresa 1kg', 'Yoghurt batido sabor fresa', 35.00, 26.50, 10, 5, 3);

-- 1. Agregar la columna para recordar el último precio real
ALTER TABLE productos 
ADD COLUMN ultimo_costo_compra DECIMAL(10,2) DEFAULT 0;

-- 2. (Opcional) Inicializar con el costo promedio actual para no tener ceros
UPDATE productos SET ultimo_costo_compra = costo_promedio;