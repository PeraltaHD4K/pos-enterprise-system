# Graph Report - .  (2026-07-22)

## Corpus Check
- cluster-only mode — file stats not available

## Summary
- 1414 nodes · 2911 edges · 90 communities (68 shown, 22 thin omitted)
- Extraction: 94% EXTRACTED · 6% INFERRED · 0% AMBIGUOUS · INFERRED: 170 edges (avg confidence: 0.79)
- Token cost: 3,536 input · 977 output

## Graph Freshness
- Built from commit: `88b1b42c`
- Run `git rev-parse HEAD` and compare to check if the graph is stale.
- Run `graphify update .` after code changes (no API cost).

## Community Hubs (Navigation)
- Customer Management
- Cash Register Service
- Sales Reports DTOs
- Sales Analytics Service
- User Management
- Authentication Service
- Category Management
- Cash Register Client
- Supplier Management
- Product Entity
- Angular CLI Config
- Purchase Entity
- App Routing and Guards
- Purchase Client Service
- Data Repositories
- Purchase Registration
- System Configuration
- User and Role Service
- POS Interface Logic
- Sales Registration Service
- Inventory Movement Controller
- Category Client Service
- Frontend Build Dependencies
- Frontend Core Dependencies
- Exception Handling
- Analytics Dashboard Controller
- Auth and Settings Components
- Customer Selection UI
- Product and Stock Service
- Report Generation Engine
- Product Catalog UI
- Database Schema SQL
- Sales Cancellation Logic
- Auth Provider Configuration
- Security Filter Chain
- Customer Form UI
- POS Checkout Modal
- Analytics Backend Logic
- Data Processing Pipeline
- Data Ingestion Layer
- Sales Event Handling
- Maven Wrapper
- Purchase Form UI
- Data Validation and Export
- Project Metadata
- POS Product Search
- POS Cart Service
- Product Repository Queries
- Toast Notifications
- POS Cart Component
- Global Error Handler
- Excel Report Generation
- User List UI
- POS Close Modal
- POS Movement Modal
- POS Ticket Modal
- Analytics Client Config
- CORS Web Config
- Stock Adjustment DTOs
- Logging and Health
- NPM Scripts
- OpenAPI Swagger Config
- Test Setup
- Spring Boot Tests
- System Architecture Services
- Forbidden Access Page
- Category Form UI
- Product Form UI
- Stock Adjustment UI
- Supplier Form UI
- Supplier List UI
- Async Task Config
- Main Application Entry
- Database Settings
- Database Engine Config
- Data Cleaning Logic
- Main Layout Pages
- Not Found Page
- Deployment Configuration
- JWT Decoding Library
- Chart.js Integration
- Environment Config
- Angular Root
- Login Page
- Inventory Management
- Purchases Management
- Backend Project ID

## God Nodes (most connected - your core abstractions)
1. `Usuario` - 59 edges
2. `Producto` - 42 edges
3. `BusinessException` - 36 edges
4. `SesionCaja` - 35 edges
5. `UsuarioRepository` - 32 edges
6. `ProductoService` - 31 edges
7. `ToastService` - 30 edges
8. `Cliente` - 29 edges
9. `Venta` - 28 edges
10. `Pos` - 28 edges

## Surprising Connections (you probably didn't know these)
- `Analytics Service` --references--> `Analytics Python Requirements`  [INFERRED]
  docker-compose.yml → data-analytics/requirements.txt
- `SalesReportGenerator` --uses--> `SalesLoader`  [INFERRED]
  data-analytics/app/processing/reports/sales_report.py → data-analytics/app/ingestion/sales_loader.py
- `SalesReportGenerator` --uses--> `SalesTransformer`  [INFERRED]
  data-analytics/app/processing/reports/sales_report.py → data-analytics/app/processing/sales_transformer.py
- `AWS SSM Deployment Workflow` --calls--> `Docker Compose Configuration`  [EXTRACTED]
  .github/workflows/deploy.yml → docker-compose.yml
- `CajaService` --references--> `SesionCajaRepository`  [EXTRACTED]
  backend/src/main/java/com/diegoperalta/pos/modules/caja/application/CajaService.java → backend/src/main/java/com/diegoperalta/pos/modules/caja/infrastructure/SesionCajaRepository.java

## Import Cycles
- None detected.

## Hyperedges (group relationships)
- **POS Enterprise System Architecture** — docker_compose_db, docker_compose_app, docker_compose_web, docker_compose_analytics [EXTRACTED 1.00]
- **Frontend Navigation Flow** — frontend_pages_auth_login, frontend_shared_layout, frontend_pages_dashboard, frontend_pages_pos [INFERRED 0.80]

## Communities (90 total, 22 thin omitted)

### Community 0 - "Customer Management"
Cohesion: 0.05
Nodes (51): ClienteService, Cliente, Service, ClienteDTO, Data, Cliente, AllArgsConstructor, Entity (+43 more)

### Community 1 - "Cash Register Service"
Cohesion: 0.06
Nodes (44): CajaService, SesionCaja, Transactional, AperturaCajaDTO, Data, CierreCajaDTO, Data, CorteXDTO (+36 more)

### Community 2 - "Sales Reports DTOs"
Cohesion: 0.06
Nodes (45): AllArgsConstructor, Data, ProductoTopDTO, AllArgsConstructor, Data, PuntoGraficaDTO, Data, ReporteGananciasDTO (+37 more)

### Community 3 - "Sales Analytics Service"
Cohesion: 0.05
Nodes (24): Analytics, TicketMetrics, Injectable, ItemVentaRequest, ProductoTop, PuntoGrafica, ReporteGanancias, Sale (+16 more)

### Community 4 - "User Management"
Cohesion: 0.09
Nodes (30): Data, UsuarioEdicionDTO, Data, UsuarioRegistroDTO, PasswordEncoder, Service, Usuario, UsuarioService (+22 more)

### Community 5 - "Authentication Service"
Cohesion: 0.09
Nodes (29): AuthenticationService, AuthenticationManager, RequiredArgsConstructor, Service, AuthResponseDTO, AllArgsConstructor, Data, Data (+21 more)

### Community 6 - "Category Management"
Cohesion: 0.12
Nodes (25): CategoriaService, Categoria, Service, Transactional, CategoriaDTO, Data, Categoria, AllArgsConstructor (+17 more)

### Community 7 - "Cash Register Client"
Cohesion: 0.07
Nodes (19): AperturaCajaRequest, CashRegister, CierreCajaRequest, CorteX, MovimientoCajaRequest, SesionCaja, Injectable, PosHeader (+11 more)

### Community 8 - "Supplier Management"
Cohesion: 0.12
Nodes (24): Data, ProveedorDTO, Proveedor, Service, Transactional, ProveedorService, AllArgsConstructor, Entity (+16 more)

### Community 9 - "Product Entity"
Cohesion: 0.12
Nodes (22): Data, ProductoRegistroDTO, Producto, AllArgsConstructor, Entity, EqualsAndHashCode, Getter, NoArgsConstructor (+14 more)

### Community 10 - "Angular CLI Config"
Cohesion: 0.05
Nodes (39): build, serve, test, builder, configurations, defaultConfiguration, options, cli (+31 more)

### Community 11 - "Purchase Entity"
Cohesion: 0.11
Nodes (26): Compra, AllArgsConstructor, Entity, EqualsAndHashCode, Getter, NoArgsConstructor, Setter, Table (+18 more)

### Community 12 - "App Routing and Guards"
Cohesion: 0.08
Nodes (13): App, appConfig, routes, Component, authGuard(), roleGuard(), authInterceptor(), Auth (+5 more)

### Community 13 - "Purchase Client Service"
Cohesion: 0.10
Nodes (12): Compra, CompraRequest, DetalleCompra, ItemCompraRequest, Purchase, Injectable, Proveedor, ProveedorRequest (+4 more)

### Community 14 - "Data Repositories"
Cohesion: 0.21
Nodes (18): CompraService, Service, CompraRepository, Repository, Repository, ProveedorRepository, UserProvider, Query (+10 more)

### Community 15 - "Purchase Registration"
Cohesion: 0.16
Nodes (8): Compra, Transactional, CompraRegistroDTO, Data, ItemCompraDTO, Data, Test, Test

### Community 16 - "System Configuration"
Cohesion: 0.15
Nodes (17): ConfiguracionService, Service, Configuracion, AllArgsConstructor, Data, Entity, NoArgsConstructor, Table (+9 more)

### Community 17 - "User and Role Service"
Cohesion: 0.12
Nodes (10): Rol, RolService, Injectable, Rol, Injectable, User, UsuarioEdicion, UsuarioRegistro (+2 more)

### Community 18 - "POS Interface Logic"
Cohesion: 0.11
Nodes (4): Pos, Component, ViewChild, HostListener

### Community 19 - "Sales Registration Service"
Cohesion: 0.14
Nodes (13): ItemVentaDTO, Data, Data, VentaRegistroDTO, BeforeEach, Cliente, ExtendWith, Producto (+5 more)

### Community 20 - "Inventory Movement Controller"
Cohesion: 0.19
Nodes (15): AllArgsConstructor, Entity, EqualsAndHashCode, Getter, NoArgsConstructor, Setter, Table, ToString (+7 more)

### Community 21 - "Category Client Service"
Cohesion: 0.15
Nodes (7): Categoria, CategoriaRequest, Category, Injectable, Toast, CategoryList, Component

### Community 22 - "Frontend Build Dependencies"
Cohesion: 0.11
Nodes (19): @angular/build, @angular/cli, @angular/compiler-cli, autoprefixer, devDependencies, @angular/build, @angular/cli, @angular/compiler-cli (+11 more)

### Community 23 - "Frontend Core Dependencies"
Cohesion: 0.11
Nodes (19): @angular/common, @angular/compiler, @angular/core, @angular/forms, @angular/platform-browser, @angular/router, chart.js, dependencies (+11 more)

### Community 24 - "Exception Handling"
Cohesion: 0.19
Nodes (10): BusinessException, Getter, ResourceNotFoundException, Service, AutorizacionService, PasswordEncoder, Service, Component (+2 more)

### Community 25 - "Analytics Dashboard Controller"
Cohesion: 0.20
Nodes (12): Data, TicketMetricsDTO, AnalyticsClient, RestClient, Service, AnalyticsController, GetMapping, PreAuthorize (+4 more)

### Community 26 - "Auth and Settings Components"
Cohesion: 0.15
Nodes (7): Config, Injectable, Login, Component, Settings, Component, environment

### Community 27 - "Customer Selection UI"
Cohesion: 0.16
Nodes (7): Cliente, ClienteDTO, CustomerList, Component, PosCustomerSelector, Component, Output

### Community 28 - "Product and Stock Service"
Cohesion: 0.15
Nodes (5): AjusteStockRequest, MovimientoInventario, Product, ProductoRequest, Injectable

### Community 29 - "Report Generation Engine"
Cohesion: 0.22
Nodes (8): ABC, BaseReportGenerator, Prefijo para el archivo descargado (ej: 'ventas', 'inventario')., Interface que todos los reportes deben implementar., Engine, ReportFactory, Engine, SalesReportGenerator

### Community 30 - "Product Catalog UI"
Cohesion: 0.17
Nodes (5): Producto, ProductList, Component, PosCatalog, Injectable

### Community 31 - "Database Schema SQL"
Cohesion: 0.29
Nodes (14): categorias, clientes, compras, configuracion, detalle_compras, detalle_ventas, movimientos_caja, movimientos_inventario (+6 more)

### Community 32 - "Sales Cancellation Logic"
Cohesion: 0.24
Nodes (8): ApplicationEventPublisher, Repository, SesionCajaRepository, Page, Pageable, Service, Transactional, VentaService

### Community 33 - "Auth Provider Configuration"
Cohesion: 0.29
Nodes (9): AuthenticationConfiguration, ApplicationConfig, AuthenticationManager, AuthenticationProvider, Bean, Configuration, PasswordEncoder, RequiredArgsConstructor (+1 more)

### Community 34 - "Security Filter Chain"
Cohesion: 0.29
Nodes (10): AuthenticationProvider, Bean, Configuration, RequiredArgsConstructor, SecurityConfig, CorsConfigurationSource, EnableMethodSecurity, EnableWebSecurity (+2 more)

### Community 35 - "Customer Form UI"
Cohesion: 0.17
Nodes (4): Client, Injectable, CustomerForm, Component

### Community 36 - "POS Checkout Modal"
Cohesion: 0.20
Nodes (5): MetodoPago, PosCheckoutModal, Component, Input, Output

### Community 37 - "Analytics Backend Logic"
Cohesion: 0.25
Nodes (5): BaseModel, SalesAnalyzer, Valida que la petición incluya el header de seguridad correcto., validate_internal_key(), TicketMetricsDTO

### Community 38 - "Data Processing Pipeline"
Cohesion: 0.20
Nodes (8): DataFrame, TicketMetricsDTO, GOLD LAYER: Agregación final para consumo del Dashboard., get_sales_metrics(), Pipeline de Analytics para Ventas:     1. Ingesta (SQL) -> 2. Proceso (Pandas) -, DataFrame, SILVER LAYER (Paso 1): Limpieza de tipos y nulos., SILVER LAYER (Paso 2): Feature Engineering.         Creamos la columna 'rango' u

### Community 39 - "Data Ingestion Layer"
Cohesion: 0.20
Nodes (6): DataFrame, Engine, BRONZE LAYER: Extracción de datos crudos desde SQL.         No aplica lógica de, SalesLoader, Any, DataFrame

### Community 40 - "Sales Event Handling"
Cohesion: 0.33
Nodes (7): Async, Component, VentaEventListener, AllArgsConstructor, Getter, VentaCompletadaEvent, EventListener

### Community 41 - "Maven Wrapper"
Cohesion: 0.33
Nodes (6): mvnw script, clean(), die(), exec_maven(), set_java_home(), verbose()

### Community 43 - "Data Validation and Export"
Cohesion: 0.31
Nodes (5): ExcelGenerator, SalesTransformer, Config, Define las reglas de calidad para los datos de ventas crudos., SalesInputSchema

### Community 44 - "Project Metadata"
Cohesion: 0.22
Nodes (8): name, packageManager, prettier, overrides, printWidth, singleQuote, private, version

### Community 45 - "POS Product Search"
Cohesion: 0.25
Nodes (5): PosProductList, Component, Input, Output, ViewChild

### Community 49 - "POS Cart Component"
Cohesion: 0.36
Nodes (5): PosCart, Component, Input, Output, CartItem

### Community 50 - "Global Error Handler"
Cohesion: 0.52
Nodes (4): GlobalExceptionHandler, ResponseEntity, ExceptionHandler, RestControllerAdvice

### Community 51 - "Excel Report Generation"
Cohesion: 0.29
Nodes (6): BytesIO, download_report(), Engine, Endpoint Genérico: Genera cualquier reporte registrado en la Factory.     Uso: /, DataFrame, Convierte un DataFrame en un archivo Excel binario en memoria.         No guarda

### Community 52 - "User List UI"
Cohesion: 0.43
Nodes (3): Usuario, Component, Users

### Community 53 - "POS Close Modal"
Cohesion: 0.33
Nodes (4): PosCloseModal, Component, Input, Output

### Community 54 - "POS Movement Modal"
Cohesion: 0.33
Nodes (4): PosMovementModal, Component, Input, Output

### Community 55 - "POS Ticket Modal"
Cohesion: 0.33
Nodes (4): PosTicketModal, Component, Input, Output

### Community 56 - "Analytics Client Config"
Cohesion: 0.53
Nodes (4): AnalyticsConfig, Bean, Configuration, RestClient

### Community 57 - "CORS Web Config"
Cohesion: 0.53
Nodes (4): Bean, Configuration, WebConfig, WebMvcConfigurer

### Community 58 - "Stock Adjustment DTOs"
Cohesion: 0.53
Nodes (4): AutorizacionDTO, Data, AjusteStockDTO, Data

### Community 59 - "Logging and Health"
Cohesion: 0.33
Nodes (3): configure_logger(), get_logger(), Configura structlog para trabajar junto con el logging nativo de Python.     En

### Community 60 - "NPM Scripts"
Cohesion: 0.33
Nodes (6): scripts, build, ng, start, test, watch

### Community 61 - "OpenAPI Swagger Config"
Cohesion: 0.70
Nodes (4): Configuration, OpenApiConfig, OpenAPIDefinition, SecurityScheme

### Community 62 - "Test Setup"
Cohesion: 0.40
Nodes (4): BeforeEach, Producto, Proveedor, Usuario

### Community 63 - "Spring Boot Tests"
Cohesion: 0.60
Nodes (3): Test, PosSystemApplicationTests, SpringBootTest

### Community 64 - "System Architecture Services"
Cohesion: 0.50
Nodes (5): Analytics Python Requirements, Analytics Service, Backend App Service, Postgres Database Service, Frontend Web Service

### Community 71 - "Async Task Config"
Cohesion: 0.83
Nodes (3): AsyncConfig, Configuration, EnableAsync

### Community 74 - "Database Engine Config"
Cohesion: 0.50
Nodes (3): get_db_engine(), Engine, Crea un singleton del Engine de SQLAlchemy.     Se inyectará en la capa de Inges

### Community 75 - "Data Cleaning Logic"
Cohesion: 0.50
Nodes (3): Any, DataFrame, Obtiene y limpia los datos.

### Community 76 - "Main Layout Pages"
Cohesion: 0.67
Nodes (3): Dashboard Page, POS Page, Main Layout

## Knowledge Gaps
- **85 isolated node(s):** `configuracion`, `com.diegoperalta:pos-backend`, `Config`, `$schema`, `version` (+80 more)
  These have ≤1 connection - possible missing edges or undocumented components.
- **22 thin communities (<3 nodes) omitted from report** — run `graphify query` to explore isolated nodes.

## Suggested Questions
_Questions this graph is uniquely positioned to answer:_

- **Why does `Usuario` connect `User Management` to `Sales Cancellation Logic`, `Cash Register Service`, `Customer Management`, `Sales Reports DTOs`, `Purchase Entity`, `Data Repositories`, `Purchase Registration`, `Sales Registration Service`, `Inventory Movement Controller`, `Exception Handling`?**
  _High betweenness centrality (0.082) - this node is a cross-community bridge._
- **Why does `UsuarioRepository` connect `Data Repositories` to `Customer Management`, `Cash Register Service`, `Auth Provider Configuration`, `Sales Cancellation Logic`, `User Management`, `Authentication Service`, `Purchase Registration`, `Sales Registration Service`, `Exception Handling`?**
  _High betweenness centrality (0.062) - this node is a cross-community bridge._
- **Why does `BusinessException` connect `Exception Handling` to `Customer Management`, `Cash Register Service`, `Sales Cancellation Logic`, `User Management`, `Category Management`, `Product Entity`, `Data Repositories`, `Purchase Registration`, `Global Error Handler`, `Sales Registration Service`?**
  _High betweenness centrality (0.036) - this node is a cross-community bridge._
- **What connects `configuracion`, `com.diegoperalta:pos-backend`, `Config` to the rest of the system?**
  _85 weakly-connected nodes found - possible documentation gaps or missing edges._
- **Should `Customer Management` be split into smaller, more focused modules?**
  _Cohesion score 0.05030864197530864 - nodes in this community are weakly interconnected._
- **Should `Cash Register Service` be split into smaller, more focused modules?**
  _Cohesion score 0.06493506493506493 - nodes in this community are weakly interconnected._
- **Should `Sales Reports DTOs` be split into smaller, more focused modules?**
  _Cohesion score 0.05789473684210526 - nodes in this community are weakly interconnected._