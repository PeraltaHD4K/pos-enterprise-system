import pandas as pd
import io

class ExcelGenerator:
    @staticmethod
    def generate_sales_report(df: pd.DataFrame) -> io.BytesIO:
        """
        Convierte un DataFrame en un archivo Excel binario en memoria.
        No guarda nada en disco (Stateless).
        """
        output = io.BytesIO()
        
        # Usamos ExcelWriter para tener control sobre el formato si quisiéramos
        with pd.ExcelWriter(output, engine='openpyxl') as writer:
            # Hoja 1: Datos Crudos
            df.to_excel(writer, index=False, sheet_name='Detalle Ventas')
            
            # (Opcional) Hoja 2: Resumen rápido
            resumen = df['total_venta'].describe()
            resumen.to_excel(writer, sheet_name='Estadísticas')

        # Rebobinamos el puntero al inicio del archivo en memoria para leerlo
        output.seek(0)
        return output
        