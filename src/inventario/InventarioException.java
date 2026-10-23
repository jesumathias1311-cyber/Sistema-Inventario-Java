package inventario;

/**
 * SRP: error de regla de negocio. Lleva el mensaje que se le muestra al usuario,
 * de modo que la lógica de negocio no necesita conocer la ventana.
 *
 * @author Abraham Galue
 */
public class InventarioException extends RuntimeException {

    public InventarioException(String mensaje) {
        super(mensaje);
    }
}