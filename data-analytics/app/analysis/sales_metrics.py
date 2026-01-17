import pandas as pd
from app.schemas.metrics import TicketMetricsDTO

class SalesAnalyzer:
    
    @staticmethod
    def calculate_kpis(df: pd.DataFrame) -> TicketMetricsDTO:
        """
        GOLD LAYER: Agregación final para consumo del Dashboard.
        """
        if df.empty:
            return TicketMetricsDTO()

        # 1. KPIs Escalares (Usa métodos vectorizados de Pandas/Numpy)
        total_count = int(len(df))
        promedio = float(df['total_venta'].mean())
        maximo = float(df['total_venta'].max())
        minimo = float(df['total_venta'].min())

        # 2. Histograma (Ya enriquecido en la capa Silver)
        # value_counts hace el conteo por categoría
        distribucion = df['rango_precio'].value_counts().sort_index().to_dict()
        
        # Limpieza de keys: Convertimos a string por si acaso
        distribucion_clean = {str(k): int(v) for k, v in distribucion.items() if v > 0}

        return TicketMetricsDTO(
            total_ventas=total_count,
            ticket_promedio=round(promedio, 2),
            ticket_maximo=round(maximo, 2),
            ticket_minimo=round(minimo, 2),
            distribucion_precios=distribucion_clean
        )
        