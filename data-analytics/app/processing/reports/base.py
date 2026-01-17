from abc import ABC, abstractmethod
import pandas as pd
from typing import Dict, Any

class BaseReportGenerator(ABC):
    """
    Interface que todos los reportes deben implementar.
    """
    
    @abstractmethod
    def generate_data(self, filters: Dict[str, Any]) -> pd.DataFrame:
        """Obtiene y limpia los datos."""
        pass

    @abstractmethod
    def get_filename_prefix(self) -> str:
        """Prefijo para el archivo descargado (ej: 'ventas', 'inventario')."""
        pass
    