import pandas as pd
from sqlalchemy import text
from sqlalchemy.engine import Engine
from app.core.logging import get_logger

log = get_logger(__name__)

class SalesLoader:
    def __init__(self, engine: Engine):
        self.engine = engine

    def load_raw_data(self, start_date: str = None, end_date: str = None) -> pd.DataFrame:
        """
        BRONZE LAYER: Extracción de datos crudos desde SQL.
        No aplica lógica de negocio, solo filtros de extracción.
        """
        query = "SELECT total_venta, fecha FROM ventas WHERE estado = 'COMPLETADA'"
        params = {}

        if start_date and end_date:
            # FIX CRÍTICO: Usamos DATE() por el cambio a Instant en Java
            query += " AND DATE(fecha) BETWEEN :start AND :end"
            params = {"start": start_date, "end": end_date}
        
        try:
            log.info("iniciando_ingesta_sql", start=start_date, end=end_date)
            df = pd.read_sql(text(query), self.engine, params=params)

            log.info("ingesta_sql_completa", rows=len(df))
            return df
        except Exception as e:
            log.error("ingesta_sql_fallida", error=str(e))
            return pd.DataFrame()
