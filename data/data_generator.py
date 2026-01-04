import pandas as pd
import numpy as np
from sqlalchemy import create_engine, text
from faker import Faker
import random
from datetime import datetime, timedelta

# 1. Configuración
# Conectamos al puerto 5433 que expusiste en Docker
DB_URI = "postgresql://postgres:admin@localhost:5433/db_pos"
engine = create_engine(DB_URI)
fake = Faker('es_MX')

print("🔌 Conectando a la Base de Datos Dockerizada...")

# 2. Obtener datos reales existentes (IDs de productos y usuarios)
try:
    with engine.connect() as conn:
        # Obtenemos productos reales que insertó tu init.sql
        productos = pd.read_sql("SELECT id, precio_venta, costo_promedio FROM productos", conn)
        # Obtenemos el usuario cajero (o admin)
        usuarios = pd.read_sql("SELECT id FROM usuarios", conn)
        # Obtenemos clientes (si hay, si no creamos falsos)
        clientes_ids = pd.read_sql("SELECT id FROM clientes", conn)['id'].tolist()
        
        # Obtenemos sesión de caja (necesitamos una abierta o simulada)
        # Para simplificar, insertaremos una sesión 'dummy' histórica
        sesion_id = conn.execute(text("INSERT INTO sesiones_caja (usuario_id, saldo_inicial, estado) VALUES (1, 1000, 'CERRADA') RETURNING id")).scalar()
        conn.commit()
        
    print(f"✅ Datos base cargados: {len(productos)} productos disponibles.")

except Exception as e:
    print(f"❌ Error de conexión: {e}")
    exit()

# 3. Configuración de la Simulación
FECHA_INICIO = datetime.now() - timedelta(days=365) # Hace un año
DIAS_A_SIMULAR = 365
VENTAS_PROMEDIO_DIARIA = 15  # Un negocio pequeño

ventas_totales = []
detalles_totales = []

print("🚀 Iniciando simulación de viaje en el tiempo...")

folio_counter = 1000

for dia in range(DIAS_A_SIMULAR):
    fecha_actual = FECHA_INICIO + timedelta(days=dia)
    es_fin_de_semana = fecha_actual.weekday() >= 5 # 5=Sábado, 6=Domingo
    
    # Factor de estacionalidad: Fines de semana se vende 50% más
    num_ventas_hoy = int(np.random.normal(VENTAS_PROMEDIO_DIARIA * (1.5 if es_fin_de_semana else 1.0), 5))
    num_ventas_hoy = max(0, num_ventas_hoy) # No negativos
    
    for _ in range(num_ventas_hoy):
        # Crear Venta
        cliente_id = random.choice(clientes_ids) if clientes_ids and random.random() > 0.3 else None
        usuario_id = usuarios.iloc[0]['id'] # Usamos el primer usuario encontrado
        
        # Elegir productos para esta venta (entre 1 y 5 productos)
        num_items = random.randint(1, 5)
        items_venta = productos.sample(n=num_items, replace=True)
        
        total_venta = 0
        detalles_temp = []
        
        folio = f"F-{folio_counter}"
        folio_counter += 1
        
        for _, prod in items_venta.iterrows():
            cantidad = random.randint(1, 3)
            precio = float(prod['precio_venta'])
            subtotal = cantidad * precio
            total_venta += subtotal
            
            detalles_temp.append({
                'producto_id': prod['id'],
                'cantidad': cantidad,
                'precio_unitario': precio,
                'subtotal': subtotal
            })
            
        # Agregar a la lista masiva de Ventas
        ventas_totales.append({
            'folio': folio,
            'fecha': fecha_actual,
            'total_venta': total_venta,
            'monto_pagado': total_venta, # Asumimos pago exacto para simplificar
            'cambio': 0,
            'estado': 'COMPLETADA',
            'cliente_id': cliente_id,
            'usuario_id': usuario_id,
            'sesion_caja_id': sesion_id,
            'metodo_pago': random.choice(['EFECTIVO', 'TARJETA'])
        })
        
        # Vincular detalles con el folio (luego resolveremos IDs)
        for d in detalles_temp:
            d['folio_venta'] = folio
            detalles_totales.append(d)

    if dia % 30 == 0:
        print(f"📅 Simulado mes {dia // 30 + 1}...")

# 4. Guardar en Base de Datos (Bulk Insert)
print("💾 Guardando datos en PostgreSQL (esto puede tardar unos segundos)...")

df_ventas = pd.DataFrame(ventas_totales)
df_detalles = pd.DataFrame(detalles_totales)

with engine.begin() as conn: # Transacción automática
    # Insertar Ventas
    df_ventas.to_sql('ventas', conn, if_exists='append', index=False, method='multi', chunksize=1000)
    
    # Recuperar los IDs generados de las ventas para asignarlos a los detalles
    # (Truco: Join por Folio, ya que folio es único)
    mapa_folios = pd.read_sql("SELECT id, folio FROM ventas", conn)
    
    # Merge para obtener venta_id en los detalles
    df_detalles = df_detalles.merge(mapa_folios, left_on='folio_venta', right_on='folio')
    df_detalles = df_detalles.drop(columns=['folio_venta', 'folio'])
    df_detalles = df_detalles.rename(columns={'id': 'venta_id'})
    
    # Insertar Detalles
    df_detalles.to_sql('detalle_ventas', conn, if_exists='append', index=False, method='multi', chunksize=1000)

print(f"✨ ¡Éxito! Se generaron {len(df_ventas)} ventas históricas y {len(df_detalles)} productos vendidos.")
