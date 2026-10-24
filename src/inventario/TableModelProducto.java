package inventario;

import java.util.List;
import javax.swing.table.AbstractTableModel;

/**
 * Modelo de tabla reutilizable para los productos.
 *
 * <p>RFC #5: mantiene una única instancia, notifica los cambios y expone el
 * tipo de cada columna para que JTable use el renderizador correcto.</p>
 *
 * @author Abraham Galue
 */
public class TableModelProducto extends AbstractTableModel {

    private final String[] columnas = {"Codigo", "Nombre", "Precio", "Inventario"};
    // Tipado: Integer, String, Double e Integer permiten el renderizado nativo.
    private final Class<?>[] tiposColumnas = {Integer.class, String.class, Double.class, Integer.class};
    private List<Producto> producto;

    public TableModelProducto(List<Producto> prod) {
        this.producto = prod;
    }
        
    
    @Override
    public int getRowCount() {
        return this.producto.size();
    }

    @Override
    public int getColumnCount() {
        return this.columnas.length;
    }

    /**
     * DRY: el producto de la fila se obtiene una sola vez con detalle(fila).
     */
    @Override
    public Object getValueAt(int fila, int columna) {
        Producto p = detalle(fila);

        switch(columna) {
            case 0:
                return p.getCodigo();
            case 1:
                return p.getNombre();
            case 2:
                return p.getPrecio();
            default:
                return p.getInventario();
        }
    }
    
    @Override
    public String getColumnName(int colum) {
        return this.columnas[colum];
    }
    
    /**
     * Notifica a la vista que los datos cambiaron.
     */
    public void actualizarTabla() {
        fireTableDataChanged();
    }

    /**
     * DRY: único punto para reemplazar los datos y notificar a la vista.
     */
    public void actualizarTabla(List<Producto> productos) {
        this.producto = productos;
        actualizarTabla();
    }

    /**
     * Tipado de columnas solicitado por el RFC #5.
     */
    @Override
    public Class<?> getColumnClass(int columnIndex) {
        return tiposColumnas[columnIndex];
    }
    
    public Producto detalle(int fila) {
        return this.producto.get(fila);
    }
    
}