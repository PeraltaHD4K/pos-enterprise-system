from sqlalchemy.engine import Engine
from .base import BaseReportGenerator
from .sales_report import SalesReportGenerator

class ReportFactory:
    @staticmethod
    def create_report(report_type: str, db_engine: Engine) -> BaseReportGenerator:
        if report_type.upper() == "SALES":
            return SalesReportGenerator(db_engine)
        
        # Aquí agregarás futuros reportes:
        # elif report_type == "INVENTORY": return InventoryReportGenerator(db_engine)
        
        raise ValueError(f"Tipo de reporte desconocido: {report_type}")
        