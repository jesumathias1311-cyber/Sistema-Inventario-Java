package inventario;

/**
 * OCP: operación Vender. Suma unidades (comportamiento original del sistema).
 *
 * @author Abraham Galue
 */
public class Vender implements OperacionInventario {

    @Override
    public boolean esValida(Producto producto, int cantidad) {
        return cantidad > 0;
    }

    @Override
    public int nuevoInventario(Producto producto, int cantidad) {
        return producto.getInventario() + cantidad;
    }

    @Override
    public String getMensaje() {
        return "Producto vendido Exitosamente";
    }
}