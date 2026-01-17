import uvicorn
import os

if __name__ == "__main__":
    # Esto corre uvicorn directamente desde python, mejorando el control de señales en Windows
    uvicorn.run(
        "app.main:app", 
        host="0.0.0.0", 
        port=8000, 
        reload=True,
        log_level="info"
    )
