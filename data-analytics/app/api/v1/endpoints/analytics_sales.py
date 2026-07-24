from fastapi import APIRouter, Depends, HTTPException, Query
from fastapi.responses import StreamingResponse
from sqlalchemy.engine import Engine
from typing import Optional

from app.core.database import get_db_engine
from app.schemas import TicketMetricsDTO
from app.ingestion import SalesLoader
from app.processing import SalesTransformer
from app.analysis import SalesAnalyzer
from app.processing.export import ExcelGenerator
from app.processing.reports import ReportFactory
from app.core.security import validate_internal_key

router = APIRouter(
    dependencies=[
        Depends(validate_internal_key)
    ]
)

@router.get("/sales/tickets", response_model=TicketMetricsDTO)
async def get_sales_metrics(
    start_date: Optional[str] = Query(None, alias="start_date"),
    end_date: Optional[str] = Query(None, alias="end_date"),
    db_engine: Engine = Depends(get_db_engine)
):
    """
    Pipeline de Analytics para Ventas:
    1. Ingesta (SQL) -> 2. Proceso (Pandas) -> 3. Análisis (KPIs)
    """
    try:
        # 1. Ingesta
        loader = SalesLoader(db_engine)
        raw_df = await run_in_threadpool(loader.load_raw_data, start_date, end_date)

        # 2. Procesamiento
        clean_df = await run_in_threadpool(SalesTransformer.clean_data, raw_df)
        enriched_df = await run_in_threadpool(SalesTransformer.enrich_with_price_ranges, clean_df)

        # 3. Análisis
        metrics = await run_in_threadpool(SalesAnalyzer.calculate_kpis, enriched_df)

        return metrics

    except Exception as e:
        print(f"Error en Analytics Sales: {e}")
        raise HTTPException(status_code=500, detail="Error procesando analíticas de ventas")

from starlette.concurrency import run_in_threadpool

@router.get("/reports/export")
async def download_report(
    report_type: str = Query(..., description="Tipo de reporte (ej: SALES)"),
    start_date: Optional[str] = Query(None),
    end_date: Optional[str] = Query(None),
    db_engine: Engine = Depends(get_db_engine)
):
    """
    Endpoint Genérico: Genera cualquier reporte registrado en la Factory.
    Uso: /reports/export?report_type=SALES&start_date=...
    """
    try:
        # 1. Delegamos a la Fábrica la creación del reporte correcto
        report_generator = ReportFactory.create_report(report_type, db_engine)
        
        # 2. Generamos la data de forma asíncrona para no bloquear el Event Loop principal
        filters = {"start_date": start_date, "end_date": end_date}
        df = await run_in_threadpool(report_generator.generate_data, filters)

        if df.empty:
            raise HTTPException(status_code=404, detail="No hay datos para generar el reporte.")

        # 3. Convertimos a Excel de forma asíncrona
        excel_file = await run_in_threadpool(ExcelGenerator.generate_sales_report, df)

        # 4. Preparamos descarga
        filename = f"{report_generator.get_filename_prefix()}_{start_date}_{end_date}.xlsx"
        
        return StreamingResponse(
            excel_file, 
            media_type="application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", 
            headers={"Content-Disposition": f"attachment; filename={filename}"}
        )

    except ValueError as ve:
        raise HTTPException(status_code=400, detail=str(ve))
    except Exception as e:
        print(f"Error export: {e}")
        raise HTTPException(status_code=500, detail="Error generando reporte")
