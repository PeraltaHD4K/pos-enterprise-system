import structlog
import logging
import sys

def configure_logger():
    """
    Configura structlog para trabajar junto con el logging nativo de Python.
    En producción, los logs saldrán en formato JSON.
    """
    
    # Configuración de procesadores compartidos
    shared_processors = [
        structlog.contextvars.merge_contextvars,
        structlog.processors.add_log_level,
        structlog.processors.TimeStamper(fmt="iso"),
    ]

    # Configurar structlog
    structlog.configure(
        processors=shared_processors + [
            structlog.processors.CallsiteParameterAdder(
                [structlog.processors.CallsiteParameter.MODULE, structlog.processors.CallsiteParameter.FUNC_NAME]
            ),
            structlog.processors.JSONRenderer() # Salida JSON siempre
        ],
        logger_factory=structlog.stdlib.LoggerFactory(),
        wrapper_class=structlog.stdlib.BoundLogger,
        cache_logger_on_first_use=True,
    )

    logging.getLogger().setLevel(logging.INFO)

def get_logger(name: str):
    return structlog.get_logger(name)
