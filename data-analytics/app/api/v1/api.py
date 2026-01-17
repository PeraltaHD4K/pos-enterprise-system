from fastapi import APIRouter
from app.api.v1.endpoints import analytics_sales

api_router = APIRouter()

api_router.include_router(analytics_sales.router, prefix="/analytics", tags=["ventas"])

# Futuro ejemplo:
# api_router.include_router(analytics_inventory.router, prefix="/analytics", tags=["inventario"])