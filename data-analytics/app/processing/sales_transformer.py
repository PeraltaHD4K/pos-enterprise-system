import pandas as pd
import pandera.pandas as pa
from app.schemas.validation import SalesInputSchema
from app.core.logging import get_logger

log = get_logger(__name__)

class SalesTransformer:
    
    @staticmethod
    def clean_data(df: pd.DataFrame) -> pd.DataFrame:
        """
        SILVER LAYER (Paso 1): Limpieza de tipos y nulos.
        """
        if df.empty:
            log.warning("dataframe_vacio_recibido")
            return df

        try:
            # --- 🛡️ VALIDACIÓN PANDERA ---
            # Esto verificará tipos y reglas (ej. precio >= 0)
            # lazy=True reporta todos los errores encontrados, no solo el primero
            clean_df = SalesInputSchema.validate(df, lazy=True)
            log.info("validacion_datos_exitosa")
            return clean_df
        except Exception as e:
            log.error("error_validacion_datos", failure_cases=err.failure_cases.to_dict(orient="records"))
            # Decisión de diseño: ¿Retornamos vacío o dejamos pasar lo que sirva?
            # Por ahora, retornamos vacío para seguridad.
            return pd.DataFrame()
        
        # Aseguramos que sea numérico
        df['total_venta'] = pd.to_numeric(df['total_venta'], errors='coerce').fillna(0)
        return df

    @staticmethod
    def enrich_with_price_ranges(df: pd.DataFrame) -> pd.DataFrame:
        """
        SILVER LAYER (Paso 2): Feature Engineering.
        Creamos la columna 'rango' usada para el histograma.
        """
        if df.empty:
            return df

        # Definimos bins fijos de negocio (Mejor que np.histogram dinámico)
        # Esto permite comparar peras con peras a lo largo del tiempo
        bins = [0, 50, 100, 200, 500, 1000, 5000, 1000000]
        labels = ["0-50", "50-100", "100-200", "200-500", "500-1000", "1000+", "Mayor"]
        
        # cut crea una categoría
        df['rango_precio'] = pd.cut(df['total_venta'], bins=bins, labels=labels, right=False)
        
        return df
        