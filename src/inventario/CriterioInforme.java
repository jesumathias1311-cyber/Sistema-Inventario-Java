package inventario;

import java.util.List;

/**
 * OCP: contrato para elegir qué productos entran al informe. Un criterio nuevo
 * se agrega creando otra clase que implemente esta interfaz, sin modificar
 * InformeProductos.
 *
 * @author Abraham Galue
 */
public interface CriterioInforme {

    List<Producto> seleccionar(List<Producto> productos);
}