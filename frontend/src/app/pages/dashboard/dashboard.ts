import { Component, inject, ViewChild, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Sale, ReporteGanancias, ProductoTop } from '../../core/services/sale';
import { KpiCard } from './components/kpi-card/kpi-card';
import { DashboardFilters, FilterData } from './components/dashboard-filters/dashboard-filters';
import { ToastService } from '../../core/services/toast';
import { BaseChartDirective } from 'ng2-charts';
import { ChartConfiguration, ChartData, ChartType, Chart, registerables } from 'chart.js';

Chart.register(...registerables);

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [CommonModule, KpiCard, BaseChartDirective, FormsModule, DashboardFilters],
  templateUrl: './dashboard.html',
  styleUrl: './dashboard.css',
})
export class Dashboard {
  private saleService = inject(Sale);
  private cdr = inject(ChangeDetectorRef);
  private toastService = inject(ToastService);

  isLoading = true;
  reporte: ReporteGanancias | null = null;
  topProductos: ProductoTop[] = [];

  currentFilter: FilterData | null = null;

  // CONFIGURACIÓN DE LA GRÁFICA
  @ViewChild(BaseChartDirective) chart: BaseChartDirective | undefined;

  // 👇 1. VARIABLE PARA EL TIPO DE GRÁFICA (Default: Bar, porque es más honesta)
  public currentChartType: ChartType = 'bar';

  public chartData: ChartData<'line'> = {
    labels: [],
    datasets: [
      {
        data: [],
        label: 'Ventas ($)',
        backgroundColor: 'rgba(79, 70, 229, 0.6)', // Un poco más sólido para barras
        borderColor: 'rgba(79, 70, 229, 1)',
        hoverBackgroundColor: 'rgba(67, 56, 202, 1)',
        fill: 'origin',
        tension: 0,
        pointBackgroundColor: '#fff',
        pointBorderColor: 'rgba(79, 70, 229, 1)',
      }
    ]
  };

  public chartOptions: ChartConfiguration['options'] = {
    responsive: true,
    maintainAspectRatio: false,
    plugins: {
      legend: { display: false },
      tooltip: {
        mode: 'index',
        intersect: false,
      }
    },
    scales: {
      x: { grid: { display: false } },
      y: {
        beginAtZero: true,
        ticks: { callback: (val) => '$' + val }
      }
    }
  };

  onFilterChange(filtro: FilterData) {
    this.currentFilter = filtro;
    this.cargarDatos();
  }

  cargarDatos() {
    if (!this.currentFilter) return;

    this.isLoading = true;
    this.cdr.detectChanges();

    const { start, end } = this.currentFilter;

    // 1. Reporte Financiero
    this.saleService.obtenerReporteGanancias(start, end).subscribe({
      next: (data) => {
        this.reporte = data;
        this.actualizarGrafica(data);
        this.checkLoading();
      },
      error: () => {
        this.isLoading = false;
        this.toastService.error('Error al cargar reporte financiero', 'Error de Carga');
      }
    });

    // 2. Top Productos
    this.saleService.obtenerTopProductos(start, end).subscribe({
      next: (data) => {
        this.topProductos = data;
        this.checkLoading();
      },
      error: () => {
        this.toastService.error('Error al cargar top productos', 'Datos Incompletos');
      }
    });
  }

  checkLoading() {
    if (this.reporte && this.topProductos) {
      this.isLoading = false;

      setTimeout(() => {
        this.cdr.detectChanges();
      }, 0);
    }
  }

  actualizarGrafica(data: ReporteGanancias) {
    if (!data.grafica) return;
    if (!this.currentFilter) return;

    let etiquetas: string[] = [];
    let valores: number[] = [];

    if (this.currentFilter.isSingleDay) {
      // Bucle Horas 00-23
      for (let i = 0; i < 24; i++) {
        const hora = i.toString().padStart(2, '0') + ':00';
        etiquetas.push(hora);
        const dato = data.grafica.find(p => p.etiqueta === hora);
        valores.push(dato ? dato.totalVentas : 0);
      }
    } else {
      // Bucle Días
      let actual = new Date(this.currentFilter.start + 'T00:00:00');
      const fin = new Date(this.currentFilter.end + 'T00:00:00');

      while (actual <= fin) {
        // Generador de fecha manual para evitar problemas de timezone
        const year = actual.getFullYear();
        const month = (actual.getMonth() + 1).toString().padStart(2, '0');
        const day = actual.getDate().toString().padStart(2, '0');
        const diaStr = `${year}-${month}-${day}`;

        etiquetas.push(diaStr);
        const dato = data.grafica.find(p => p.etiqueta === diaStr);
        valores.push(dato ? dato.totalVentas : 0);

        actual.setDate(actual.getDate() + 1);
      }
    }

    this.chartData.labels = etiquetas;
    this.chartData.datasets[0].data = valores;

    // 👇 FIX CHARTJS: Forzar update y luego trigger de Angular
    this.chart?.update();
    setTimeout(() => this.cdr.detectChanges(), 0);
  }

  toggleChartType(type: 'line' | 'bar') {
    this.currentChartType = type;
    const dataset = this.chartData.datasets[0] as any;

    if (type === 'line') {
      dataset.backgroundColor = 'rgba(79, 70, 229, 0.2)';
      dataset.fill = 'origin';
    } else {
      dataset.backgroundColor = 'rgba(79, 70, 229, 0.7)';
      dataset.fill = false;
    }
    this.chart?.update();
  }
}
