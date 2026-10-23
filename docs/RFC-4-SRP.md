# RFC #4: separar negocio y presentación

## Responsabilidades

- `Principal`: captura texto y selección, invoca el servicio, presenta errores y
  resultados, limpia el formulario y refresca la tabla.
- `InventarioService`: valida entradas, aplica reglas de altas, modificaciones,
  bajas y movimientos, y calcula el informe de los tres productos más caros.
- `BaseDatosProductos`: almacena productos y realiza actualizaciones explícitas
  por código. `actualizarInventario()` reemplaza el registro y devuelve si existía.

El servicio recibe el almacenamiento por constructor y no importa Swing ni AWT.
`listarProductos()` devuelve copias de los productos: modificar una fila desde
la vista no modifica el inventario. Los errores de negocio se comunican con
`IllegalArgumentException`; la ventana los presenta con `JOptionPane`.

## Contrato de las operaciones

Se conserva la perspectiva de `main`: **Comprar resta stock; Vender suma stock**.
El campo Inventario sigue sirviendo como cantidad al ejecutar estas operaciones.
La selección identifica el producto; modificar el nombre o precio en pantalla
no altera un movimiento de stock.

- Nombre obligatorio, con letras Unicode y espacios; se recortan extremos.
- Precio finito y mayor que cero; inventario entero mayor o igual que cero.
- Nombres duplicados ignorando mayúsculas se rechazan en altas y modificaciones.
- Cantidad de movimiento entera y positiva; se rechazan stock insuficiente y
  desbordamiento antes de modificar el almacenamiento.
- Actualizar, eliminar y mover stock requieren un producto existente.
- El informe acepta inventarios vacíos o con menos de tres productos.

Estas reglas quedan centralizadas y también se aplican cuando se utiliza el
servicio sin una ventana. Las validaciones y los informes de listas pequeñas
son diferencias funcionales explícitas respecto del código original.

## Verificación reproducible (PowerShell, JDK 17)

```powershell
New-Item -ItemType Directory -Force out | Out-Null
javac -encoding UTF-8 -d out src\inventario\*.java test\inventario\InventarioServiceTest.java
java "-Djava.awt.headless=true" -cp out inventario.InventarioServiceTest
java -cp out inventario.Principal
```

En la ventana: seleccionar un producto, comprar/vender indicando unidades,
agregar un producto, modificarlo, eliminarlo y consultar el informe. Probar
también falta de selección, cantidad inválida y stock insuficiente: deben
mostrarse advertencias y conservarse el estado.

## Integración

La rama parte de `main` y el PR cierra #4. Los PR #7, #8 y #9 también modifican
`Principal`: su integración requiere revisar los handlers, validaciones y la
perspectiva Comprar/Vender. Al integrar #9, el modelo único debe consumir
`inventarioService.listarProductos()`. El informe trasladado al servicio se
solapa con esa parte del RFC #3; la generación de códigos sigue pendiente allí.
