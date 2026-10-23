package inventario;

import java.util.HashMap;
import java.util.List;

/** Pruebas de contrato ejecutables sin Swing ni dependencias externas. */
public class InventarioServiceTest {
    private static int comprobaciones;

    private static void verificar(boolean condicion, String mensaje) {
        comprobaciones++;
        if (!condicion) throw new AssertionError(mensaje);
    }

    private static void rechazar(Runnable operacion) {
        try {
            operacion.run();
        } catch (IllegalArgumentException esperado) {
            comprobaciones++;
            return;
        }
        throw new AssertionError("La operacion debia rechazarse");
    }

    private static Producto obtener(InventarioService servicio, int codigo) {
        return servicio.listarProductos().stream().filter(p -> p.getCodigo() == codigo)
                .findFirst().orElseThrow(() -> new AssertionError("Producto ausente"));
    }

    public static void main(String[] args) {
        BaseDatosProductos bd = new BaseDatosProductos();
        InventarioService servicio = new InventarioService(bd);
        verificar(servicio.listarProductos().size() == 10, "Datos iniciales");
        verificar(servicio.generarInforme().equals("Carnes, Jamon, Manzanas."), "Informe inicial");

        servicio.agregar("", "  Piña fresca  ", "12.50", "0");
        verificar(obtener(servicio, 11).getNombre().equals("Piña fresca"), "Normalizacion del nombre");
        servicio.actualizar(11, "Piña fresca", "15", "5");
        verificar(obtener(servicio, 11).getPrecio() == 15, "Actualizacion");
        rechazar(() -> servicio.agregar("", "piña fresca", "1", "1"));
        rechazar(() -> servicio.actualizar(11, "Manzanas", "1", "1"));
        rechazar(() -> servicio.agregar("1", "Nuevo", "1", "1"));
        for (String nombre : new String[]{null, "", "  ", "Producto123"}) {
            rechazar(() -> servicio.actualizar(11, nombre, "1", "1"));
        }
        for (String precio : new String[]{null, "", "abc", "0", "-1", "NaN", "Infinity"}) {
            rechazar(() -> servicio.actualizar(11, "Piña fresca", precio, "1"));
        }
        for (String stock : new String[]{null, "", "-1", "1.5", "2147483648"}) {
            rechazar(() -> servicio.actualizar(11, "Piña fresca", "1", stock));
        }
        verificar(obtener(servicio, 11).getInventario() == 5, "Errores no cambian estado");

        List<Producto> copia = servicio.listarProductos();
        copia.get(0).setInventario(-100);
        copia.clear();
        verificar(obtener(servicio, 1).getInventario() == 65, "Vista no muta almacenamiento");

        Producto anterior = obtener(servicio, 11);
        servicio.comprar(11, "5");
        verificar(obtener(servicio, 11).getInventario() == 0, "Comprar hasta agotar stock");
        verificar(anterior.getInventario() == 5, "Snapshot no mutado");
        rechazar(() -> servicio.comprar(11, "1"));
        servicio.vender(11, "2");
        verificar(obtener(servicio, 11).getInventario() == 2, "Vender incrementa stock");
        for (String cantidad : new String[]{null, "", "abc", "0", "-1", "1.5"}) {
            rechazar(() -> servicio.comprar(11, cantidad));
            rechazar(() -> servicio.vender(11, cantidad));
        }
        verificar(obtener(servicio, 11).getInventario() == 2, "Cantidades invalidas no mutan stock");
        servicio.actualizar(11, "Piña fresca", "15", "2147483647");
        rechazar(() -> servicio.vender(11, "1"));
        verificar(obtener(servicio, 11).getInventario() == Integer.MAX_VALUE, "Overflow no muta stock");

        for (Integer codigo : new Integer[]{null, -1, 999}) {
            rechazar(() -> servicio.actualizar(codigo, "Valido", "1", "1"));
            rechazar(() -> servicio.eliminar(codigo));
            rechazar(() -> servicio.comprar(codigo, "1"));
            rechazar(() -> servicio.vender(codigo, "1"));
        }
        servicio.eliminar(11);
        verificar(servicio.listarProductos().size() == 10, "Eliminacion");
        verificar(!bd.actualizarInventario(999, 1), "Actualizacion explicita inexistente");

        bd.setListaProductos(new HashMap<>());
        verificar(servicio.generarInforme().equals("No hay productos en el inventario."), "Informe vacio");
        servicio.agregar("", "Uno", "1", "0");
        verificar(servicio.generarInforme().equals("Uno."), "Informe con un producto");
        servicio.agregar("", "Dos", "2", "0");
        verificar(servicio.generarInforme().equals("Dos, Uno."), "Informe con dos productos");
        System.out.println("OK: " + comprobaciones + " comprobaciones de contrato sin Swing.");
    }
}
