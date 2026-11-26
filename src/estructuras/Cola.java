package estructuras;

/**
 * Implementación manual de una Cola FIFO (First-In, First-Out) mediante nodos enlazados.
 * Utilizada en el módulo M2 (solicitudes de laboratorio) y en el módulo M4 (recorrido BFS).
 *
 * Complejidad temporal:
 * - encolar (enqueue): O(1)
 * - desencolar (dequeue): O(1)
 * - verFrente (peek): O(1)
 *
 * @param <T> Tipo de elemento almacenado.
 */
public class Cola<T> {

    private static class Nodo<T> {
        T dato;
        Nodo<T> siguiente;

        Nodo(T dato) {
            this.dato = dato;
            this.siguiente = null;
        }
    }

    private Nodo<T> frente;
    private Nodo<T> fin;
    private int tamanio;

    public Cola() {
        this.frente = null;
        this.fin = null;
        this.tamanio = 0;
    }

    public void encolar(T elemento) {
        if (elemento == null) {
            throw new IllegalArgumentException("No se puede encolar una referencia nula.");
        }
        Nodo<T> nuevo = new Nodo<>(elemento);
        if (estaVacia()) {
            frente = nuevo;
            fin = nuevo;
        } else {
            fin.siguiente = nuevo;
            fin = nuevo;
        }
        tamanio++;
    }

    /**
     * Inserta un elemento al frente de la cola (útil exclusivamente al revertir una atención FIFO).
     */
    public void restaurarAlFrente(T elemento) {
        if (elemento == null) {
            throw new IllegalArgumentException("No se puede restaurar una referencia nula.");
        }
        Nodo<T> nuevo = new Nodo<>(elemento);
        if (estaVacia()) {
            frente = nuevo;
            fin = nuevo;
        } else {
            nuevo.siguiente = frente;
            frente = nuevo;
        }
        tamanio++;
    }

    public T desencolar() {
        if (estaVacia()) {
            throw new IllegalStateException("La cola está vacía; no hay solicitudes pendientes.");
        }
        T dato = frente.dato;
        frente = frente.siguiente;
        if (frente == null) {
            fin = null;
        }
        tamanio--;
        return dato;
    }

    public T verFrente() {
        if (estaVacia()) {
            throw new IllegalStateException("La cola está vacía.");
        }
        return frente.dato;
    }

    public boolean estaVacia() {
        return tamanio == 0;
    }

    public int tamanio() {
        return tamanio;
    }

    public void limpiar() {
        frente = null;
        fin = null;
        tamanio = 0;
    }

    /**
     * Devuelve los elementos en orden FIFO sin alterar la cola.
     */
    public VectorDinamico<T> listarElementos() {
        VectorDinamico<T> resultado = new VectorDinamico<>();
        Nodo<T> actual = frente;
        while (actual != null) {
            resultado.agregar(actual.dato);
            actual = actual.siguiente;
        }
        return resultado;
    }
}
