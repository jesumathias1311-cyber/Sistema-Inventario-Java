package inventario;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * SRP (RFC #4): concentra las reglas de inventario sin depender de Swing.
 * Los errores de entrada se comunican mediante IllegalArgumentException.
 */
public class InventarioService {
    private final BaseDatosProductos productos;

    public InventarioService(BaseDatosProductos productos) {
        this.productos = java.util.Objects.requireNonNull(productos, "productos");
    }

    // Las copias impiden que la vista modifique el almacenamiento por referencia.
    public List<Producto> listarProductos() {
        List<Producto> resultado = new ArrayList<>();
        for (Producto p : productos.getListaProductos()) {
            resultado.add(new Producto(p.getCodigo(), p.getNombre(), p.getPrecio(), p.getInventario()));
        }
        return resultado;
    }

    public void agregar(String codigo, String nombre, String precio, String inventario) {
        Producto nuevo = validarProducto(0, nombre, precio, inventario);
        if (productos.verificarExistencias(nuevo.getNombre())) {
            throw new IllegalArgumentException("El producto ya existe");
        }
        if (codigo != null && !codigo.trim().isEmpty()) {
            throw new IllegalArgumentException("Imposible agregar! Esta accion es permitida para Actualizar o Borrar");
        }
        nuevo.setCodigo(productos.ultimoCodigo() + 1);
        productos.agregar(nuevo);
    }

    public void actualizar(Integer codigo, String nombre, String precio, String inventario) {
        Producto existente = buscar(codigo);
        Producto actualizado = validarProducto(existente.getCodigo(), nombre, precio, inventario);
        for (Producto p : productos.getListaProductos()) {
            if (p.getCodigo() != codigo && p.getNombre().equalsIgnoreCase(actualizado.getNombre())) {
                throw new IllegalArgumentException("El producto ya existe");
            }
        }
        productos.actualizar(actualizado);
    }

    public void eliminar(Integer codigo) {
        productos.borrar(buscar(codigo));
    }

    /** Conserva la perspectiva original: comprar retira unidades del inventario. */
    public void comprar(Integer codigo, String cantidad) {
        cambiarInventario(codigo, cantidad, true);
    }

    /** Conserva la perspectiva original: vender incorpora unidades al inventario. */
    public void vender(Integer codigo, String cantidad) {
        cambiarInventario(codigo, cantidad, false);
    }

    private void cambiarInventario(Integer codigo, String entrada, boolean retirar) {
        Producto producto = buscar(codigo);
        int cantidad = entero(entrada, "La cantidad debe ser un numero entero.");
        if (cantidad <= 0) {
            throw new IllegalArgumentException("La cantidad debe ser mayor que 0.");
        }
        long resultado = (long) producto.getInventario() + (retirar ? -cantidad : cantidad);
        if (resultado < 0) {
            throw new IllegalArgumentException("No tenemos el suficiente inventario");
        }
        if (resultado > Integer.MAX_VALUE) {
            throw new IllegalArgumentException("El inventario supera el limite permitido.");
        }
        // Estado explícito: persistir por código, sin mutar el objeto entregado a la vista.
        if (!productos.actualizarInventario(producto.getCodigo(), (int) resultado)) {
            throw new IllegalArgumentException("El producto no existe.");
        }
    }

    public String generarInforme() {
        // SRP: el criterio del informe es una regla del servicio, no del almacenamiento.
        String nombres = listarProductos().stream()
                .sorted(Comparator.comparingDouble(Producto::getPrecio).reversed())
                .limit(3)
                .map(Producto::getNombre)
                .collect(Collectors.joining(", "));
        return nombres.isEmpty() ? "No hay productos en el inventario." : nombres + ".";
    }

    private Producto buscar(Integer codigo) {
        if (codigo == null) {
            throw new IllegalArgumentException("Seleccione un producto de la tabla.");
        }
        for (Producto p : productos.getListaProductos()) {
            if (p.getCodigo() == codigo) {
                return p;
            }
        }
        throw new IllegalArgumentException("El producto no existe.");
    }

    private Producto validarProducto(int codigo, String nombre, String precio, String inventario) {
        String normalizado = nombre == null ? "" : nombre.trim();
        if (!normalizado.matches("\\p{L}+( \\p{L}+)*")) {
            throw new IllegalArgumentException("El nombre debe contener letras y espacios.");
        }
        double valor;
        try {
            valor = Double.parseDouble(precio == null ? "" : precio.trim());
        } catch (NumberFormatException ex) {
            throw new IllegalArgumentException("El precio debe ser un numero valido.");
        }
        if (!Double.isFinite(valor) || valor <= 0) {
            throw new IllegalArgumentException("El precio debe ser mayor que 0 y finito.");
        }
        int stock = entero(inventario, "El inventario debe ser un numero entero.");
        if (stock < 0) {
            throw new IllegalArgumentException("El inventario no puede ser negativo.");
        }
        return new Producto(codigo, normalizado, valor, stock);
    }

    private int entero(String entrada, String mensaje) {
        try {
            return Integer.parseInt(entrada == null ? "" : entrada.trim());
        } catch (NumberFormatException ex) {
            throw new IllegalArgumentException(mensaje);
        }
    }
}
