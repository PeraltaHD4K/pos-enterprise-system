package com.diegoperalta.pos.common.utils;

public class TicketBuilder {
    private final StringBuilder sb;
    private final int ancho;
    private static final String LINEA_DIVISORIA_CHAR = "-";

    public TicketBuilder(int ancho) {
        this.sb = new StringBuilder();
        this.ancho = ancho;
    }

    public TicketBuilder centrar(String texto) {
        if (texto == null) return this;
        int espacios = (ancho - texto.length()) / 2;
        if (espacios < 0) espacios = 0;

        sb.append(" ".repeat(espacios));
        sb.append(texto).append("\n");
        return this;
    }

    public TicketBuilder alinearDerecha(String texto) {
        if (texto == null) return this;
        int espacios = ancho - texto.length();
        if (espacios < 0) espacios = 0;

        sb.append(" ".repeat(espacios));
        sb.append(texto).append("\n");
        return this;
    }

    public TicketBuilder lineaDivisoria() {
        sb.append(LINEA_DIVISORIA_CHAR.repeat(ancho)).append("\n");
        return this;
    }

    public TicketBuilder texto(String texto) {
        if (texto != null) {
            sb.append(texto);
        }
        return this;
    }

    public TicketBuilder saltoDeLinea() {
        sb.append("\n");
        return this;
    }

    public TicketBuilder saltosDeLinea(int n) {
        sb.append("\n".repeat(Math.max(0, n)));
        return this;
    }

    public TicketBuilder itemLista(String cantidad, String producto, String subtotal) {
        String cantStr = String.format("%-4s", cantidad);
        String prodStr = producto.length() > 18 ? producto.substring(0, 18) : String.format("%-18s", producto);
        String subStr = String.format("%7s", subtotal);

        sb.append(cantStr).append(" ").append(prodStr).append(" ").append(subStr).append("\n");
        return this;
    }

    public String build() {
        return sb.toString();
    }
}
