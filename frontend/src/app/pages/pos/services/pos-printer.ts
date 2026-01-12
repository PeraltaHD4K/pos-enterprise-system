import { Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root',
})
export class PosPrinter {
  printTicket(textoTicket: string): void {
    // 1. Crear el iframe oculto
    const iframe = document.createElement('iframe');
    this.configurarIframe(iframe);
    document.body.appendChild(iframe);

    // 2. Escribir el contenido
    const doc = iframe.contentWindow?.document;
    if (doc) {
      doc.open();
      doc.write(this.generarHtml(textoTicket));
      doc.close();

      // 3. Esperar renderizado e imprimir
      setTimeout(() => {
        iframe.contentWindow?.focus();
        iframe.contentWindow?.print();

        // 4. Limpieza (Garbage Collection del DOM)
        setTimeout(() => document.body.removeChild(iframe), 2000);
      }, 500);
    }
  }

  // --- Helpers Privados ---

  private configurarIframe(iframe: HTMLIFrameElement) {
    iframe.style.position = 'fixed';
    iframe.style.right = '0';
    iframe.style.bottom = '0';
    iframe.style.width = '0';
    iframe.style.height = '0';
    iframe.style.border = '0';
  }

  private generarHtml(texto: string): string {
    return `
      <html>
        <head>
          <style>
            body { 
              margin: 0; 
              padding: 5px;
              font-family: 'Courier New', Courier, monospace; 
              font-size: 12px; 
              width: 80mm; /* Ancho estándar térmico */
              color: black;
            }
            pre {
              white-space: pre-wrap; /* Respetar saltos de línea */
              margin: 0;
            }
            @media print {
              @page { margin: 0; }
              body { margin: 0; }
            }
          </style>
        </head>
        <body>
          <pre>${texto}</pre>
        </body>
      </html>
    `;
  }
}
