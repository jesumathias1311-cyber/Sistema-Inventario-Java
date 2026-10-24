package inventario;

import java.util.List;

/**
 * SRP: única responsabilidad de esta clase, las reglas de negocio del inventario
 * (agregar, actualizar, eliminar, comprar, vender e informe). No conoce la
 * ventana: ante un problema lanza InventarioException con el mensaje para el
 * usuario. Antes estas reglas estaban dentro de Principal.
 *
 * @author Abraham Galue
 */
public class InventarioService {

    public static final String MENSAJE_DATOS_INVALIDOS
            = "Alguno de los valores digitados esta vacio o es erroneo";

    // IoC: las dependencias llegan por el constructor, ya no se crean con new aquí.
    private final BaseDatosProductos baseDatos;
    private final InformeProductos informe;

    public InventarioService(BaseDatosProductos baseDatos, InformeProductos informe) {
        this.baseDatos = baseDatos;
        this.informe = informe;
    }

    public List<Producto> listar() {
        return baseDatos.getListaProductos();
    }

    public void agregar(Producto p) {
        if (baseDatos.verificarExistencias(p.getNombre())) {
            throw new InventarioException("El producto ya existe");
        }
        if (p.getCodigo() != 0) {
            throw new InventarioException("Imposible agregar! Esta accion es permitida para Actualizar o Borrar");
        }
        if (!p.getNombre().matches("[a-zA-Z]+")) {
            throw new InventarioException("El nombre no puede contener numeros");
        }
        int codigoNuevo = baseDatos.ultimoCodigo() + 1;
        baseDatos.agregar(new Producto(codigoNuevo, p.getNombre(), p.getPrecio(), p.getInventario()));
    }

    public void actualizar(Producto p) {
        if (p.getInventario() < 0) {
            throw new InventarioException(MENSAJE_DATOS_INVALIDOS);
        }
        baseDatos.actualizar(p);
    }

    public void eliminar(Producto p) {
        baseDatos.borrar(p);
    }

    /**
     * Comprar o Vender: ajusta el inventario del producto según la operación.
     * OCP: la regla de cada operación vive en su propia clase; este método no
     * cambia cuando se agrega una operación nueva. Se guarda una copia con el
     * inventario nuevo (antes comprarProducto() modificaba el objeto compartido).
     */
    public void operar(Producto producto, OperacionInventario operacion, int cantidad) {
        if (!operacion.esValida(producto, cantidad)) {
            throw new InventarioException(MENSAJE_DATOS_INVALIDOS);
        }
        int nuevoInventario = operacion.nuevoInventario(producto, cantidad);
        baseDatos.actualizar(new Producto(producto.getCodigo(), producto.getNombre(),
                producto.getPrecio(), nuevoInventario));
    }

    public String generarInforme() {
        return informe.generar(baseDatos.getListaProductos());
    }
}