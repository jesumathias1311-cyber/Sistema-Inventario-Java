package inventario;

import java.util.ArrayList;
import java.util.List;

/**
 * SRP: única responsabilidad de esta clase, armar el informe de los productos
 * más costosos. Antes esta lógica estaba dentro de BaseDatosProductos.
 *
 * @author Abraham Galue
 */
public class InformeProductos {

    private static final int CANTIDAD_INFORME = 3;

    public String generar(List<Producto> productos) {
        List<Producto> listaM = obtenerMayores(productos);
        StringBuilder informe = new StringBuilder();
        for (int i = 0; i < listaM.size(); i++) {
            if (i > 0) {
                informe.append(", ");
            }
            informe.append(listaM.get(i).getNombre());
        }
        return informe.append(".").toString();
    }

    private List<Producto> obtenerMayores(List<Producto> productos) {
        List<Producto> lista = new ArrayList<>(productos);
        List<Producto> listaMayores = new ArrayList<>();
        for (int i = 0; i < CANTIDAD_INFORME; i++) {
            Producto p = new Producto();
            for (Producto pTemp : lista) {
                if (pTemp.getPrecio() > p.getPrecio()) {
                    p = pTemp;
                }
            }
            listaMayores.add(p);
            lista.remove(p);
        }
        return listaMayores;
    }
}