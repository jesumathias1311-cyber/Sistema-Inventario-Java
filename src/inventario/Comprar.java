package inventario;

/**
 * OCP: operación Comprar. Descuenta unidades (comportamiento original del
 * sistema) y no permite sacar más de lo que hay en inventario.
 *
 * @author Abraham Galue
 */
public class Comprar implements OperacionInventario {

    @Override
    public boolean esValida(Producto producto, int cantidad) {
        return cantidad > 0 && cantidad <= producto.getInventario();
    }

    @Override
    public int nuevoInventario(Producto producto, int cantidad) {
        return producto.getInventario() - cantidad;
    }

    @Override
    public String getMensaje() {
        return "Producto comprado Exitosamente";
    }
}