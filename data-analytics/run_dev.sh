#!/bin/bash
# Script para ejecutar Analytics en local usando 'uv' en lugar de 'pip'
echo "🚀 Iniciando POS Analytics Pipeline con uv..."

# Crear entorno virtual si no existe
if [ ! -d ".venv" ]; then
    echo "📦 Creando entorno virtual con uv..."
    uv venv
fi

# Activar entorno virtual
source .venv/bin/activate

# Instalar dependencias usando uv pip
echo "🔄 Instalando/Actualizando dependencias..."
uv pip install -r requirements.txt

# Iniciar el servidor
echo "🔥 Arrancando FastAPI..."
uvicorn app.main:app --host 0.0.0.0 --port 8000 --reload
