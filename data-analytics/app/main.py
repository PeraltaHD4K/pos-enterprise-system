from fastapi import FastAPI
from app.core.config import settings
from app.core.logging import configure_logger
from app.api.v1.api import api_router

configure_logger()

app = FastAPI(title=settings.PROJECT_NAME)

@app.get("/health")
def health_check():
    return {"status": "ok", "service": "pos-analytics-pipeline"}

app.include_router(api_router, prefix=settings.API_V1_STR)
