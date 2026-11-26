package estructuras;

/**
 * Implementación manual de una Pila LIFO (Last-In, First-Out) mediante nodos enlazados.
 * Utilizada en el módulo M2 para el historial reversible de operaciones (deshacer).
 *
 * Complejidad temporal:
 * - apilar (push): O(1)
 * - desapilar (pop): O(1)
 * - verTope (peek): O(1)
 *
 * @param <T> Tipo de elemento almacenado.
 */
public class Pila<T> {

    private static class Nodo<T> {
        T dato;
        Nodo<T> siguiente;

        Nodo(T dato, Nodo<T> siguiente) {
            this.dato = dato;
            this.siguiente = siguiente;
        }
    }

    private Nodo<T> tope;
    private int tamanio;

    public Pila() {
        this.tope = null;
        this.tamanio = 0;
    }

    public void apilar(T elemento) {
        if (elemento == null) {
            throw new IllegalArgumentException("No se puede apilar una referencia nula.");
        }
        tope = new Nodo<>(elemento, tope);
        tamanio++;
    }

    public T desapilar() {
        if (estaVacia()) {
            throw new IllegalStateException("La pila está vacía; no hay operaciones para recuperar.");
        }
        T dato = tope.dato;
        tope = tope.siguiente;
        tamanio--;
        return dato;
    }

    public T verTope() {
        if (estaVacia()) {
            throw new IllegalStateException("La pila está vacía.");
        }
        return tope.dato;
    }

    public boolean estaVacia() {
        return tamanio == 0;
    }

    public int tamanio() {
        return tamanio;
    }

    public void limpiar() {
        tope = null;
        tamanio = 0;
    }

    /**
     * Devuelve los elementos desde el tope hacia la base sin modificar la pila.
     */
    public VectorDinamico<T> listarElementos() {
        VectorDinamico<T> resultado = new VectorDinamico<>();
        Nodo<T> actual = tope;
        while (actual != null) {
            resultado.agregar(actual.dato);
            actual = actual.siguiente;
        }
        return resultado;
    }
}
