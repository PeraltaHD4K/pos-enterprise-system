# Graph Report - pos-enterprise-system  (2026-07-23)

## Corpus Check
- 239 files · ~41,548 words
- Verdict: corpus is large enough that graph structure adds value.

## Summary
- 1605 nodes · 3331 edges · 102 communities (79 shown, 23 thin omitted)
- Extraction: 94% EXTRACTED · 6% INFERRED · 0% AMBIGUOUS · INFERRED: 192 edges (avg confidence: 0.79)
- Token cost: 0 input · 0 output

## Graph Freshness
- Built from commit: `5aee9412`
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
- Usuario
- DetalleVenta
- SesionCaja
- PosHistoryModal
- Frontend
- SpringSecurityUserProvider.java
- PosPrinter
- TotalesReporteDTO
- CreateUser
- rules/graphify.md
- workflows/graphify.md
- run_dev.sh

## God Nodes (most connected - your core abstractions)
1. `Usuario` - 47 edges
2. `BusinessException` - 40 edges
3. `Producto` - 38 edges
4. `ProductoService` - 37 edges
5. `UsuarioRepository` - 34 edges
6. `SesionCaja` - 33 edges
7. `VentaRepository` - 31 edges
8. `ToastService` - 30 edges
9. `Pos` - 29 edges
10. `Venta` - 28 edges

## Surprising Connections (you probably didn't know these)
- `Analytics Service` --references--> `Analytics Python Requirements`  [INFERRED]
  docker-compose.yml → data-analytics/requirements.txt
- `SalesReportGenerator` --uses--> `SalesLoader`  [INFERRED]
  data-analytics/app/processing/reports/sales_report.py → data-analytics/app/ingestion/sales_loader.py
- `SalesReportGenerator` --uses--> `SalesTransformer`  [INFERRED]
  data-analytics/app/processing/reports/sales_report.py → data-analytics/app/processing/sales_transformer.py
- `AWS SSM Deployment Workflow` --calls--> `Docker Compose Configuration`  [EXTRACTED]
  .github/workflows/deploy.yml → docker-compose.yml
- `ResourceNotFoundException` --inherits--> `BusinessException`  [EXTRACTED]
  backend/src/main/java/com/diegoperalta/pos/common/exception/ResourceNotFoundException.java → backend/src/main/java/com/diegoperalta/pos/common/exception/BusinessException.java

## Import Cycles
- None detected.

## Hyperedges (group relationships)
- **POS Enterprise System Architecture** — docker_compose_db, docker_compose_app, docker_compose_web, docker_compose_analytics [EXTRACTED 1.00]
- **Frontend Navigation Flow** — frontend_pages_auth_login, frontend_shared_layout, frontend_pages_dashboard, frontend_pages_pos [INFERRED 0.80]

## Communities (102 total, 23 thin omitted)

### Community 0 - "Customer Management"
Cohesion: 0.07
Nodes (35): AllArgsConstructor, Getter, NoArgsConstructor, Setter, RolResponseDTO, RequiredArgsConstructor, Service, RolService (+27 more)

### Community 1 - "Cash Register Service"
Cohesion: 0.17
Nodes (14): AperturaCajaDTO, Data, CorteXDTO, Data, Data, SesionCajaResponseDTO, CajaController, GetMapping (+6 more)

### Community 2 - "Sales Reports DTOs"
Cohesion: 0.17
Nodes (13): AllArgsConstructor, Entity, EqualsAndHashCode, Getter, NoArgsConstructor, Setter, Table, ToString (+5 more)

### Community 3 - "Sales Analytics Service"
Cohesion: 0.10
Nodes (14): Analytics, TicketMetrics, Injectable, ItemVentaRequest, ProductoTop, PuntoGrafica, ReporteGanancias, Sale (+6 more)

### Community 4 - "User Management"
Cohesion: 0.11
Nodes (24): Data, UsuarioEdicionDTO, Data, UsuarioRegistroDTO, AllArgsConstructor, Getter, NoArgsConstructor, Setter (+16 more)

### Community 5 - "Authentication Service"
Cohesion: 0.05
Nodes (45): AuthenticationService, AuthenticationManager, RequiredArgsConstructor, Service, AuthResponseDTO, AllArgsConstructor, Data, Data (+37 more)

### Community 6 - "Category Management"
Cohesion: 0.09
Nodes (32): CategoriaService, Categoria, RequiredArgsConstructor, Service, Transactional, CategoriaDTO, Data, CategoriaResponseDTO (+24 more)

### Community 7 - "Cash Register Client"
Cohesion: 0.08
Nodes (17): AperturaCajaRequest, CashRegister, CierreCajaRequest, CorteX, MovimientoCajaRequest, SesionCaja, Injectable, PosHeader (+9 more)

### Community 8 - "Supplier Management"
Cohesion: 0.09
Nodes (33): Data, ProveedorDTO, AllArgsConstructor, Getter, NoArgsConstructor, Setter, ProveedorResponseDTO, Proveedor (+25 more)

### Community 9 - "Product Entity"
Cohesion: 0.07
Nodes (37): AjusteStockDTO, Data, Data, ProductoRegistroDTO, Data, ProductoResponseDTO, Pageable, Producto (+29 more)

### Community 10 - "Angular CLI Config"
Cohesion: 0.05
Nodes (40): build, serve, test, builder, configurations, defaultConfiguration, options, cli (+32 more)

### Community 11 - "Purchase Entity"
Cohesion: 0.06
Nodes (48): CompraService, Compra, RequiredArgsConstructor, Service, Transactional, CompraRegistroDTO, Data, CompraResponseDTO (+40 more)

### Community 12 - "App Routing and Guards"
Cohesion: 0.08
Nodes (13): App, appConfig, routes, Component, authGuard(), roleGuard(), authInterceptor(), Auth (+5 more)

### Community 13 - "Purchase Client Service"
Cohesion: 0.14
Nodes (8): Compra, CompraRequest, DetalleCompra, ItemCompraRequest, Purchase, Injectable, PurchaseList, Component

### Community 14 - "Data Repositories"
Cohesion: 0.17
Nodes (18): CajaService, RequiredArgsConstructor, Service, MovimientoCajaRepository, Repository, SesionCajaRepository, CurrentUserProvider, Query (+10 more)

### Community 15 - "Purchase Registration"
Cohesion: 0.09
Nodes (31): ClienteService, Cliente, RequiredArgsConstructor, Service, ClienteDTO, Data, ClienteResponseDTO, AllArgsConstructor (+23 more)

### Community 16 - "System Configuration"
Cohesion: 0.13
Nodes (21): ConfiguracionService, RequiredArgsConstructor, Service, Configuracion, AllArgsConstructor, Data, Entity, NoArgsConstructor (+13 more)

### Community 17 - "User and Role Service"
Cohesion: 0.15
Nodes (8): Rol, RolService, Injectable, Rol, Injectable, User, UsuarioEdicion, UsuarioRegistro

### Community 18 - "POS Interface Logic"
Cohesion: 0.10
Nodes (4): Pos, Component, ViewChild, HostListener

### Community 19 - "Sales Registration Service"
Cohesion: 0.11
Nodes (18): Getter, ResourceNotFoundException, ItemVentaDTO, Data, Data, VentaRegistroDTO, ApplicationEventPublisher, RequiredArgsConstructor (+10 more)

### Community 20 - "Inventory Movement Controller"
Cohesion: 0.13
Nodes (22): AllArgsConstructor, Getter, NoArgsConstructor, Setter, MovimientoInventarioResponseDTO, AllArgsConstructor, Entity, EqualsAndHashCode (+14 more)

### Community 21 - "Category Client Service"
Cohesion: 0.11
Nodes (10): Categoria, CategoriaRequest, Category, Injectable, ProductoRequest, Toast, CategoryList, Component (+2 more)

### Community 22 - "Frontend Build Dependencies"
Cohesion: 0.11
Nodes (19): @angular/build, @angular/cli, @angular/compiler-cli, autoprefixer, devDependencies, @angular/build, @angular/cli, @angular/compiler-cli (+11 more)

### Community 23 - "Frontend Core Dependencies"
Cohesion: 0.11
Nodes (19): @angular/common, @angular/compiler, @angular/core, @angular/forms, @angular/platform-browser, @angular/router, chart.js, dependencies (+11 more)

### Community 24 - "Exception Handling"
Cohesion: 0.17
Nodes (14): AutorizacionService, PasswordEncoder, RequiredArgsConstructor, Service, AutorizacionDTO, Data, Data, VentaItemResponseDTO (+6 more)

### Community 25 - "Analytics Dashboard Controller"
Cohesion: 0.19
Nodes (13): Data, TicketMetricsDTO, AnalyticsClient, Logger, RestClient, Service, AnalyticsController, GetMapping (+5 more)

### Community 26 - "Auth and Settings Components"
Cohesion: 0.15
Nodes (7): Config, Injectable, Login, Component, Settings, Component, environment

### Community 27 - "Customer Selection UI"
Cohesion: 0.08
Nodes (11): Client, Cliente, ClienteDTO, Injectable, CustomerList, Component, PosCustomerSelector, Component (+3 more)

### Community 28 - "Product and Stock Service"
Cohesion: 0.07
Nodes (16): Page, AjusteStockRequest, MovimientoInventario, Product, Producto, Injectable, ProductList, Component (+8 more)

### Community 29 - "Report Generation Engine"
Cohesion: 0.22
Nodes (8): ABC, BaseReportGenerator, Prefijo para el archivo descargado (ej: 'ventas', 'inventario')., Interface que todos los reportes deben implementar., Engine, ReportFactory, Engine, SalesReportGenerator

### Community 30 - "Product Catalog UI"
Cohesion: 0.14
Nodes (15): AllArgsConstructor, Data, ProductoTopDTO, AllArgsConstructor, Data, PuntoGraficaDTO, Data, ReporteGananciasDTO (+7 more)

### Community 31 - "Database Schema SQL"
Cohesion: 0.29
Nodes (14): categorias, clientes, compras, configuracion, detalle_compras, detalle_ventas, movimientos_caja, movimientos_inventario (+6 more)

### Community 32 - "Sales Cancellation Logic"
Cohesion: 0.15
Nodes (6): DashboardFilters, FilterData, Component, Output, Dashboard, Component

### Community 33 - "Auth Provider Configuration"
Cohesion: 0.29
Nodes (9): AuthenticationConfiguration, ApplicationConfig, AuthenticationManager, AuthenticationProvider, Bean, Configuration, PasswordEncoder, RequiredArgsConstructor (+1 more)

### Community 34 - "Security Filter Chain"
Cohesion: 0.29
Nodes (10): AuthenticationProvider, Bean, Configuration, RequiredArgsConstructor, SecurityConfig, CorsConfigurationSource, EnableMethodSecurity, EnableWebSecurity (+2 more)

### Community 35 - "Customer Form UI"
Cohesion: 0.26
Nodes (6): BusinessException, SesionCaja, Transactional, CierreCajaDTO, Data, Test

### Community 36 - "POS Checkout Modal"
Cohesion: 0.20
Nodes (5): MetodoPago, PosCheckoutModal, Component, Input, Output

### Community 37 - "Analytics Backend Logic"
Cohesion: 0.24
Nodes (6): BaseModel, DataFrame, TicketMetricsDTO, GOLD LAYER: Agregación final para consumo del Dashboard., SalesAnalyzer, TicketMetricsDTO

### Community 38 - "Data Processing Pipeline"
Cohesion: 0.19
Nodes (5): TicketBuilder, RequiredArgsConstructor, Service, Transactional, TicketService

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
Cohesion: 0.21
Nodes (8): get_logger(), DataFrame, SILVER LAYER (Paso 1): Limpieza de tipos y nulos., SILVER LAYER (Paso 2): Feature Engineering.         Creamos la columna 'rango' u, SalesTransformer, Config, Define las reglas de calidad para los datos de ventas crudos., SalesInputSchema

### Community 44 - "Project Metadata"
Cohesion: 0.22
Nodes (8): name, packageManager, prettier, overrides, printWidth, singleQuote, private, version

### Community 45 - "POS Product Search"
Cohesion: 0.25
Nodes (5): PosProductList, Component, Input, Output, ViewChild

### Community 46 - "POS Cart Service"
Cohesion: 0.31
Nodes (9): GetMapping, Pageable, PostMapping, PreAuthorize, RequestMapping, RequiredArgsConstructor, ResponseEntity, RestController (+1 more)

### Community 47 - "Product Repository Queries"
Cohesion: 0.23
Nodes (4): Override, SecurityUser, GrantedAuthority, UserDetails

### Community 48 - "Toast Notifications"
Cohesion: 0.21
Nodes (4): ToastService, Injectable, CustomerForm, Component

### Community 49 - "POS Cart Component"
Cohesion: 0.27
Nodes (10): AllArgsConstructor, Entity, EqualsAndHashCode, Getter, NoArgsConstructor, Setter, Table, ToString (+2 more)

### Community 50 - "Global Error Handler"
Cohesion: 0.46
Nodes (5): GlobalExceptionHandler, Logger, ResponseEntity, ExceptionHandler, RestControllerAdvice

### Community 51 - "Excel Report Generation"
Cohesion: 0.33
Nodes (4): BytesIO, ExcelGenerator, DataFrame, Convierte un DataFrame en un archivo Excel binario en memoria.         No guarda

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
Cohesion: 0.22
Nodes (4): Proveedor, ProveedorRequest, Supplier, Injectable

### Community 60 - "NPM Scripts"
Cohesion: 0.33
Nodes (6): scripts, build, ng, start, test, watch

### Community 61 - "OpenAPI Swagger Config"
Cohesion: 0.70
Nodes (4): Configuration, OpenApiConfig, OpenAPIDefinition, SecurityScheme

### Community 62 - "Test Setup"
Cohesion: 0.23
Nodes (9): ApplicationEventPublisher, BeforeEach, Cliente, ExtendWith, Producto, SesionCaja, Test, Usuario (+1 more)

### Community 63 - "Spring Boot Tests"
Cohesion: 0.60
Nodes (3): Test, PosSystemApplicationTests, SpringBootTest

### Community 64 - "System Architecture Services"
Cohesion: 0.50
Nodes (5): Analytics Python Requirements, Analytics Service, Backend App Service, Postgres Database Service, Frontend Web Service

### Community 67 - "Product Form UI"
Cohesion: 0.29
Nodes (7): AllArgsConstructor, Getter, NoArgsConstructor, Setter, MovimientoCajaResponseDTO, Data, NuevoMovimientoCajaDTO

### Community 71 - "Async Task Config"
Cohesion: 0.83
Nodes (3): AsyncConfig, Configuration, EnableAsync

### Community 72 - "Main Application Entry"
Cohesion: 0.60
Nodes (3): PosSystemApplication, EnableCaching, SpringBootApplication

### Community 74 - "Database Engine Config"
Cohesion: 0.17
Nodes (10): download_report(), get_sales_metrics(), Engine, Pipeline de Analytics para Ventas:     1. Ingesta (SQL) -> 2. Proceso (Pandas) -, Endpoint Genérico: Genera cualquier reporte registrado en la Factory.     Uso: /, get_db_engine(), Engine, Crea un singleton del Engine de SQLAlchemy.     Se inyectará en la capa de Inges (+2 more)

### Community 75 - "Data Cleaning Logic"
Cohesion: 0.50
Nodes (3): Any, DataFrame, Obtiene y limpia los datos.

### Community 76 - "Main Layout Pages"
Cohesion: 0.67
Nodes (3): Dashboard Page, POS Page, Main Layout

### Community 90 - "Usuario"
Cohesion: 0.33
Nodes (9): AllArgsConstructor, Entity, EqualsAndHashCode, Getter, NoArgsConstructor, Setter, Table, ToString (+1 more)

### Community 91 - "DetalleVenta"
Cohesion: 0.33
Nodes (9): DetalleVenta, AllArgsConstructor, Entity, EqualsAndHashCode, Getter, NoArgsConstructor, Setter, Table (+1 more)

### Community 92 - "SesionCaja"
Cohesion: 0.38
Nodes (9): AllArgsConstructor, Entity, EqualsAndHashCode, Getter, NoArgsConstructor, Setter, Table, ToString (+1 more)

### Community 93 - "PosHistoryModal"
Cohesion: 0.28
Nodes (4): PosHistoryModal, Component, Input, Output

### Community 94 - "Frontend"
Cohesion: 0.25
Nodes (7): Additional Resources, Building, Code scaffolding, Development server, Frontend, Running end-to-end tests, Running unit tests

### Community 95 - "SpringSecurityUserProvider.java"
Cohesion: 0.48
Nodes (4): Component, Override, RequiredArgsConstructor, SpringSecurityUserProvider

### Community 97 - "TotalesReporteDTO"
Cohesion: 0.70
Nodes (4): AllArgsConstructor, Data, NoArgsConstructor, TotalesReporteDTO

## Knowledge Gaps
- **94 isolated node(s):** `configuracion`, `com.diegoperalta:pos-backend`, `Config`, `run_dev.sh script`, `$schema` (+89 more)
  These have ≤1 connection - possible missing edges or undocumented components.
- **23 thin communities (<3 nodes) omitted from report** — run `graphify query` to explore isolated nodes.

## Suggested Questions
_Questions this graph is uniquely positioned to answer:_

- **Why does `Usuario` connect `Usuario` to `Customer Management`, `Sales Reports DTOs`, `Customer Form UI`, `User Management`, `Product Entity`, `Purchase Entity`, `Data Repositories`, `Product Repository Queries`, `POS Cart Component`, `Inventory Movement Controller`, `SesionCaja`, `Test Setup`, `SpringSecurityUserProvider.java`?**
  _High betweenness centrality (0.063) - this node is a cross-community bridge._
- **Why does `UsuarioRepository` connect `Data Repositories` to `Customer Management`, `Auth Provider Configuration`, `User Management`, `Authentication Service`, `Product Entity`, `Purchase Entity`, `Sales Registration Service`, `Exception Handling`, `Usuario`, `Test Setup`, `SpringSecurityUserProvider.java`?**
  _High betweenness centrality (0.051) - this node is a cross-community bridge._
- **Why does `BusinessException` connect `Customer Form UI` to `User Management`, `Category Management`, `Product Entity`, `Purchase Entity`, `Data Repositories`, `Purchase Registration`, `Global Error Handler`, `Sales Registration Service`, `Exception Handling`, `Analytics Dashboard Controller`, `SpringSecurityUserProvider.java`?**
  _High betweenness centrality (0.046) - this node is a cross-community bridge._
- **What connects `configuracion`, `com.diegoperalta:pos-backend`, `Config` to the rest of the system?**
  _94 weakly-connected nodes found - possible documentation gaps or missing edges._
- **Should `Customer Management` be split into smaller, more focused modules?**
  _Cohesion score 0.07265306122448979 - nodes in this community are weakly interconnected._
- **Should `Sales Analytics Service` be split into smaller, more focused modules?**
  _Cohesion score 0.09788359788359788 - nodes in this community are weakly interconnected._
- **Should `User Management` be split into smaller, more focused modules?**
  _Cohesion score 0.10801393728222997 - nodes in this community are weakly interconnected._