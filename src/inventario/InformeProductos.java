package inventario;

import java.util.List;

/**
 * SRP: única responsabilidad de esta clase, armar el texto del informe.
 * OCP: qué productos entran lo decide un CriterioInforme; cambiar o agregar
 * criterios no obliga a modificar esta clase.
 *
 * @author Abraham Galue
 */
public class InformeProductos {

    private final CriterioInforme criterio;

    public InformeProductos() {
        this(new MasCostosos());
    }

    public InformeProductos(CriterioInforme criterio) {
        this.criterio = criterio;
    }

    public String generar(List<Producto> productos) {
        List<Producto> listaM = criterio.seleccionar(productos);
        StringBuilder informe = new StringBuilder();
        for (int i = 0; i < listaM.size(); i++) {
            if (i > 0) {
                informe.append(", ");
            }
            informe.append(listaM.get(i).getNombre());
        }
        return informe.append(".").toString();
    }
}