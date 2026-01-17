import pandera.pandas as pa
from pandera.typing import Series

class SalesInputSchema(pa.DataFrameModel):
    """
    Define las reglas de calidad para los datos de ventas crudos.
    """
    # Validamos que la columna fecha exista y sea datetime
    fecha: Series[pa.DateTime] = pa.Field(coerce=True)
    
    # Validamos que total_venta sea numérico y no negativo
    # coerce=True intenta convertir strings a números automáticamente
    total_venta: Series[float] = pa.Field(ge=0, coerce=True)

    class Config:
        # Si vienen columnas extra que no esperamos, las descartamos (strict=False)
        # o lanzamos error (strict='filter'). Usaremos 'filter' para limpieza.
        strict = "filter"
