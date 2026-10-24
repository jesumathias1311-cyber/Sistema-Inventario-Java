package inventario;

import java.util.ArrayList;
import java.util.List;

/**
 * OCP: criterio actual del informe, los productos de mayor precio. Es la lógica
 * que antes estaba fija dentro de InformeProductos.
 *
 * @author Abraham Galue
 */
public class MasCostosos implements CriterioInforme {

    private static final int CANTIDAD_INFORME = 3;

    @Override
    public List<Producto> seleccionar(List<Producto> productos) {
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