package inventario;

/**
 * OCP: contrato de una operación sobre el inventario. Cada operación trae su
 * propia regla, así InventarioService no necesita conocer cada caso: una
 * operación nueva es una clase nueva que implementa esta interfaz.
 *
 * @author Abraham Galue
 */
public interface OperacionInventario {

    boolean esValida(Producto producto, int cantidad);

    int nuevoInventario(Producto producto, int cantidad);

    String getMensaje();
}