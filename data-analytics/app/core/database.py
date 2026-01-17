from sqlalchemy import create_engine
from sqlalchemy.engine import Engine
from functools import lru_cache
from app.core.config import settings

@lru_cache()
def get_db_engine() -> Engine:
    """
    Crea un singleton del Engine de SQLAlchemy.
    Se inyectará en la capa de Ingestión.
    """
    return create_engine(
        settings.SQLALCHEMY_DATABASE_URI,
        pool_pre_ping=True
    )
