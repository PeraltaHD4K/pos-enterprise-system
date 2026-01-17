import pandas as pd
from sqlalchemy import text
from sqlalchemy.engine import Engine
from app.core.logging import get_logger
from datetime import datetime, time
from zoneinfo import ZoneInfo
from app.core.config import settings

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
            try:
                # 1. Configuramos las zonas horarias
                # settings.APP_TIME_ZONE viene de tu .env (America/Mexico_City)
                local_tz = ZoneInfo(settings.APP_TIME_ZONE) 
                utc_tz = ZoneInfo("UTC")

                # 2. Convertimos los strings 'YYYY-MM-DD' a objetos fecha LOCALES
                # Inicio del día: 00:00:00 hora México
                start_dt = datetime.strptime(start_date, '%Y-%m-%d').replace(tzinfo=local_tz)
                start_dt = datetime.combine(start_dt.date(), time.min, tzinfo=local_tz)

                # Fin del día: 23:59:59.999999 hora México
                end_dt = datetime.strptime(end_date, '%Y-%m-%d').replace(tzinfo=local_tz)
                end_dt = datetime.combine(end_dt.date(), time.max, tzinfo=local_tz)

                # 3. Convertimos a UTC (Esto es lo que Postgres entiende)
                # Ejemplo: 23:00 México -> 05:00 (+1 día) UTC
                start_utc = start_dt.astimezone(utc_tz)
                end_utc = end_dt.astimezone(utc_tz)

                # 4. Modificamos la query para comparar timestamps exactos, NO fechas truncadas
                # Quitamos el DATE() para que use el índice de la base de datos (más rápido)
                query += " AND fecha >= :start AND fecha <= :end"
                params = {"start": start_utc, "end": end_utc}

            except Exception as e:
                log.error("error_conversion_fechas", error=str(e))
                # Si falla la conversión, fallback a la lógica simple (aunque puede fallar por timezone)
                query += " AND DATE(fecha) BETWEEN :start AND :end"
                params = {"start": start_date, "end": end_date}
        
        try:
            log.info("iniciando_ingesta_sql", start=start_date, end=end_date)
            
            # Ejecutamos la query
            df = pd.read_sql(text(query), self.engine, params=params)

            log.info("ingesta_sql_completa", rows=len(df))
            return df
        except Exception as e:
            log.error("ingesta_sql_fallida", error=str(e))
            return pd.DataFrame()
