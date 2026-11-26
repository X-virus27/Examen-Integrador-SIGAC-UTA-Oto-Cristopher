package estructuras;

/**
 * Implementación manual de un vector dinámico (arreglo redimensionable).
 * No utiliza colecciones de java.util.
 *
 * Complejidades principales:
 * - agregar al final: O(1) amortizado, O(n) en redimensionamiento.
 * - obtener/establecer por índice: O(1).
 * - búsqueda secuencial (contiene / indiceDe): O(n).
 * - eliminarEn: O(n).
 *
 * @param <T> Tipo de elemento almacenado.
 */
public class VectorDinamico<T> {
    private static final int CAPACIDAD_INICIAL = 8;
    private Object[] datos;
    private int tamanio;

    public VectorDinamico() {
        this.datos = new Object[CAPACIDAD_INICIAL];
        this.tamanio = 0;
    }

    public VectorDinamico(int capacidadInicial) {
        if (capacidadInicial <= 0) {
            throw new IllegalArgumentException("La capacidad inicial debe ser mayor a cero.");
        }
        this.datos = new Object[capacidadInicial];
        this.tamanio = 0;
    }

    public void agregar(T elemento) {
        if (elemento == null) {
            throw new IllegalArgumentException("No se permiten referencias nulas en el vector.");
        }
        if (tamanio == datos.length) {
            redimensionar();
        }
        datos[tamanio++] = elemento;
    }

    @SuppressWarnings("unchecked")
    public T obtener(int indice) {
        validarIndice(indice);
        return (T) datos[indice];
    }

    public void establecer(int indice, T elemento) {
        if (elemento == null) {
            throw new IllegalArgumentException("No se permiten referencias nulas en el vector.");
        }
        validarIndice(indice);
        datos[indice] = elemento;
    }

    @SuppressWarnings("unchecked")
    public T eliminarEn(int indice) {
        validarIndice(indice);
        T eliminado = (T) datos[indice];
        for (int i = indice; i < tamanio - 1; i++) {
            datos[i] = datos[i + 1];
        }
        datos[tamanio - 1] = null;
        tamanio--;
        return eliminado;
    }

    public int indiceDe(T elemento) {
        if (elemento == null) {
            return -1;
        }
        for (int i = 0; i < tamanio; i++) {
            if (datos[i].equals(elemento)) {
                return i;
            }
        }
        return -1;
    }

    public boolean contiene(T elemento) {
        return indiceDe(elemento) != -1;
    }

    public int tamanio() {
        return tamanio;
    }

    public boolean estaVacio() {
        return tamanio == 0;
    }

    public void limpiar() {
        for (int i = 0; i < tamanio; i++) {
            datos[i] = null;
        }
        tamanio = 0;
    }

    private void redimensionar() {
        int nuevaCapacidad = datos.length * 2;
        Object[] nuevoArreglo = new Object[nuevaCapacidad];
        for (int i = 0; i < tamanio; i++) {
            nuevoArreglo[i] = datos[i];
        }
        datos = nuevoArreglo;
    }

    private void validarIndice(int indice) {
        if (indice < 0 || indice >= tamanio) {
            throw new IndexOutOfBoundsException(
                    "Índice fuera de rango: " + indice + " (tamaño actual: " + tamanio + ")");
        }
    }
}
