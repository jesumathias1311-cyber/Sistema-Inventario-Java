package inventario;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 *
 * @author Abraham Galue
 */
public class BaseDatosProductos {
    
    private HashMap<Integer, Producto> listaProductos = new HashMap<>();

    public BaseDatosProductos() {
        agregar(new Producto(1, "Manzanas", 8000, 65));
        agregar(new Producto(2, "Limones", 2300, 15));
        agregar(new Producto(3, "Tomates", 2500, 33));
        agregar(new Producto(4, "Jamon", 9300, 55));
        agregar(new Producto(5, "Fresas", 2100, 42));
        agregar(new Producto(6, "Helado", 4100, 31));
        agregar(new Producto(7, "Peras", 4500, 41));
        agregar(new Producto(8, "Chocolates", 500, 55));
        agregar(new Producto(9, "Galletas", 3500, 806));
        agregar(new Producto(10, "Carnes", 15000, 10));
    }
    
    public List<Producto> getListaProductos() {
        return new ArrayList<>(this.listaProductos.values());
    }
    
    public void setListaProductos(HashMap<Integer, Producto> listaProductos) {
        this.listaProductos = listaProductos;
    }
    
    public boolean verificarExistencias(Producto producto) {
        return this.listaProductos.containsKey(producto.getCodigo());
    }
    
    public boolean verificarExistencias(String nombre) {
        for (Producto p : this.listaProductos.values()) {
            if (nombre.equalsIgnoreCase(p.getNombre())) {
                return true;
            }
        }
        return false;
    }
    
    public int ultimoCodigo() {
        int codigo = 0;
        for (Producto p : this.listaProductos.values()) {
            codigo = p.getCodigo();
        }
        return codigo;
    }
    
    public void agregar(Producto p) {
        this.listaProductos.put(p.getCodigo(), p);
    }
    
    public void actualizar(Producto p) {
        this.listaProductos.replace(p.getCodigo(), p);
    }
    
    public void borrar(Producto p) {
        this.listaProductos.remove(p.getCodigo());
    }

}