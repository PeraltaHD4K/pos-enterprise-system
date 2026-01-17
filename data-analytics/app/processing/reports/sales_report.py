import pandas as pd
from sqlalchemy.engine import Engine
from typing import Dict, Any
from .base import BaseReportGenerator
from app.ingestion.sales_loader import SalesLoader
from app.processing.sales_transformer import SalesTransformer

class SalesReportGenerator(BaseReportGenerator):
    def __init__(self, db_engine: Engine):
        self.db_engine = db_engine

    def generate_data(self, filters: Dict[str, Any]) -> pd.DataFrame:
        start_date = filters.get("start_date")
        end_date = filters.get("end_date")

        # 1. Ingesta
        loader = SalesLoader(self.db_engine)
        raw_df = loader.load_raw_data(start_date, end_date)

        if raw_df.empty:
            return pd.DataFrame()

        # 2. Transformación (Silver Layer)
        return SalesTransformer.clean_data(raw_df)

    def get_filename_prefix(self) -> str:
        return "reporte_ventas"
