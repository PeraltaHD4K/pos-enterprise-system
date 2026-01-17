from pydantic import BaseModel
from typing import Dict

class TicketMetricsDTO(BaseModel):
    total_ventas: int = 0
    ticket_promedio: float = 0.0
    ticket_maximo: float = 0.0
    ticket_minimo: float = 0.0
    distribucion_precios: Dict[str, int] = {}
