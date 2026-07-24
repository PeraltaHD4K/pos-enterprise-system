FROM python:3.10.19-slim

# 2. Evitamos que Python genere archivos .pyc y forzamos logs en tiempo real
ENV PYTHONDONTWRITEBYTECODE=1
ENV PYTHONUNBUFFERED=1

# 3. Directorio de trabajo dentro del contenedor
WORKDIR /app

# 4. Instalamos dependencias del sistema necesarias para compilar algunas libs de Python
# (gcc y libpq-dev son comunes para drivers de Postgres como psycopg2)
RUN apt-get update && apt-get install -y \
    gcc \
    libpq-dev \
    && rm -rf /var/lib/apt/lists/*

# 5. Copiamos y e instalamos dependencias usando uv
COPY requirements.txt .
RUN pip install uv && uv pip install --system --no-cache-dir -r requirements.txt

# 6. Copiamos el código fuente
COPY . .

# 7. Usuario no-root por seguridad (Best Practice)
RUN useradd -m appuser && chown -R appuser /app
USER appuser

# 8. Comando de arranque
# Usamos --host 0.0.0.0 porque dentro de Docker "localhost" es aislado
CMD ["uvicorn", "app.main:app", "--host", "0.0.0.0", "--port", "8000"]