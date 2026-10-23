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

    /**
     * Sentido de la operación sobre el inventario. Comprar descuenta unidades y
     * Vender las suma (comportamiento original del sistema).
     */
    public enum Operacion {
        COMPRAR(-1, "Producto comprado Exitosamente"),
        VENDER(1, "Producto vendido Exitosamente");

        private final int factor;
        private final String mensaje;

        Operacion(int factor, String mensaje) {
            this.factor = factor;
            this.mensaje = mensaje;
        }

        public String getMensaje() {
            return mensaje;
        }
    }

    private final BaseDatosProductos baseDatos = new BaseDatosProductos();
    private final InformeProductos informe = new InformeProductos();

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
     * Comprar o Vender: ajusta el inventario del producto según el sentido de
     * la operación. Reemplaza al antiguo comprarProducto(), que modificaba el
     * objeto compartido; ahora se guarda una copia con el inventario nuevo.
     */
    public void operar(Producto producto, Operacion operacion, int cantidad) {
        if (cantidad <= 0
                || (operacion == Operacion.COMPRAR && cantidad > producto.getInventario())) {
            throw new InventarioException(MENSAJE_DATOS_INVALIDOS);
        }
        int nuevoInventario = producto.getInventario() + operacion.factor * cantidad;
        baseDatos.actualizar(new Producto(producto.getCodigo(), producto.getNombre(),
                producto.getPrecio(), nuevoInventario));
    }

    public String generarInforme() {
        return informe.generar(baseDatos.getListaProductos());
    }
}