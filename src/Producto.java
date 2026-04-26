/**
 * CLASE PRODUCTO
 * --------------
 * Representa un monitor gamer del catálogo (POJO - Plain Old Java Object).
 *
 * ¿Por qué es un POJO?
 * Porque solo contiene datos y getters/setters. No tiene lógica de UI ni persistencia.
 * Esto separa responsabilidades: el modelo no sabe nada de ventanas ni archivos.
 *
 * ¿Por qué los atributos son private?
 * Principio de ENCAPSULACIÓN: los datos se ocultan para que solo se modifiquen
 * mediante getters/setters. Esto permite validar o cambiar la lógica interna
 * sin afectar al resto del programa.
 */
public class Producto {

    // ========== ATRIBUTOS ==========
    // Todos privados para cumplir encapsulación
    private String nombre;           // Modelo del monitor (ej: "ASUS ROG Swift")
    private double precio;           // Precio en soles (double para decimales)
    private double pulgadas;         // Tamaño de pantalla (double: puede ser 24.5")
    private int hercios;             // Frecuencia de refresco (int: siempre entero)
    private String resolucion;       // "FHD", "2K", "4K"
    private int stock;               // Unidades disponibles (int: no hay medio monitor)
    private String tipoPanel;        // "IPS", "VA", "TN"
    private int tiempoRespuesta;     // Milisegundos (int)
    private String conectividad;     // "HDMI, DP, USB-C"
    private int garantiaMeses;       // Meses de garantía

    /**
     * CONSTRUCTOR
     * Se usa para crear un producto con todos sus datos en una sola línea.
     * Recibe 10 parámetros porque el producto tiene 10 atributos.
     * 'this.' distingue el atributo de la variable del parámetro (mismo nombre).
     */
    public Producto(String nombre, double precio, double pulgadas, int hercios,
                    String resolucion, int stock, String tipoPanel,
                    int tiempoRespuesta, String conectividad, int garantiaMeses) {
        this.nombre = nombre;
        this.precio = precio;
        this.pulgadas = pulgadas;
        this.hercios = hercios;
        this.resolucion = resolucion;
        this.stock = stock;
        this.tipoPanel = tipoPanel;
        this.tiempoRespuesta = tiempoRespuesta;
        this.conectividad = conectividad;
        this.garantiaMeses = garantiaMeses;
    }

    // ========== GETTERS Y SETTERS ==========
    // Patrón estándar de Java: getXxx() para leer, setXxx() para modificar.
    // Permiten controlar el acceso a los atributos privados desde fuera.

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public double getPrecio() { return precio; }
    public void setPrecio(double precio) { this.precio = precio; }

    public double getPulgadas() { return pulgadas; }
    public void setPulgadas(double pulgadas) { this.pulgadas = pulgadas; }

    public int getHercios() { return hercios; }
    public void setHercios(int hercios) { this.hercios = hercios; }

    public String getResolucion() { return resolucion; }
    public void setResolucion(String resolucion) { this.resolucion = resolucion; }

    public int getStock() { return stock; }
    public void setStock(int stock) { this.stock = stock; }

    public String getTipoPanel() { return tipoPanel; }
    public void setTipoPanel(String tipoPanel) { this.tipoPanel = tipoPanel; }

    public int getTiempoRespuesta() { return tiempoRespuesta; }
    public void setTiempoRespuesta(int tiempoRespuesta) { this.tiempoRespuesta = tiempoRespuesta; }

    public String getConectividad() { return conectividad; }
    public void setConectividad(String conectividad) { this.conectividad = conectividad; }

    public int getGarantiaMeses() { return garantiaMeses; }
    public void setGarantiaMeses(int garantiaMeses) { this.garantiaMeses = garantiaMeses; }

    // ========== MÉTODOS DE NEGOCIO ==========

    /**
     * Resta 'cantidad' unidades del stock.
     * Se usa al confirmar una venta. Se pone dentro de la clase Producto
     * (y no en PanelVender) para que la lógica viaje con el dato (cohesión).
     */
    public void descontarStock(int cantidad) {
        this.stock -= cantidad;
    }

    /**
     * Verifica si hay stock suficiente para vender 'cantidad' unidades.
     * Devuelve boolean para usarse directamente en un if.
     * Se pone aquí porque la clase Producto es quien "sabe" cuánto stock tiene.
     */
    public boolean tieneStock(int cantidad) {
        return this.stock >= cantidad;
    }

    /**
     * toString() se llama automáticamente al mostrar el objeto (ej: en un JComboBox).
     * Devolvemos el nombre porque es lo que el usuario debe ver en la UI.
     */
    @Override
    public String toString() {
        return nombre;
    }
}
