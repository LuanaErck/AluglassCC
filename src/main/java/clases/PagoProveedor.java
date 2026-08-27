package clases;

public class PagoProveedor {
    private final int idPagoProveedor;
    private final int idProveedor;
    private final int idCompra;
    private final String fechaPago;
    private final double importe;
    private final String formaPago;
    private final String observaciones;
    private final String estado;
    private String nombreProveedor;

    public PagoProveedor(int idPagoProveedor, int idProveedor, int idCompra, String fechaPago, double importe,
            String formaPago, String observaciones, String estado) {
        this.idPagoProveedor = idPagoProveedor;
        this.idProveedor = idProveedor;
        this.idCompra = idCompra;
        this.fechaPago = fechaPago;
        this.importe = importe;
        this.formaPago = formaPago;
        this.observaciones = observaciones;
        this.estado = estado;
    }

    public int getIdPagoProveedor() { return idPagoProveedor; }
    public int getIdProveedor() { return idProveedor; }
    public int getIdCompra() { return idCompra; }
    public String getFechaPago() { return fechaPago; }
    public double getImporte() { return importe; }
    public String getFormaPago() { return formaPago; }
    public String getObservaciones() { return observaciones; }
    public String getEstado() { return estado; }
    public String getNombreProveedor() { return nombreProveedor; }
    public void setNombreProveedor(String nombreProveedor) { this.nombreProveedor = nombreProveedor; }
}
