from fastapi import Security, HTTPException, status, Depends
from fastapi.security import APIKeyHeader
import secrets
from app.core.config import settings

# Definimos el nombre del Header esperado
API_KEY_NAME = "X-INTERNAL-API-KEY"
api_key_header = APIKeyHeader(name=API_KEY_NAME, auto_error=False)

async def validate_internal_key(api_key_header: str = Security(api_key_header)):
    """
    Valida que la petición incluya el header de seguridad correcto.
    """
    if not api_key_header:
        raise HTTPException(
            status_code=status.HTTP_403_FORBIDDEN,
            detail="Acceso denegado: Credenciales faltantes"
        )

    # secrets.compare_digest evita ataques de tiempo (Timing Attacks)
    if not secrets.compare_digest(api_key_header, settings.INTERNAL_API_KEY):
        raise HTTPException(
            status_code=status.HTTP_403_FORBIDDEN,
            detail="Acceso denegado: Credenciales inválidas"
        )
    
    return api_key_header
    