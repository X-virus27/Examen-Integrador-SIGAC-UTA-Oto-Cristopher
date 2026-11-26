package estructuras;

import modelo.Estudiante;

/**
 * Implementación manual de un Árbol Binario de Búsqueda (ABB) indexado por matrícula (M3 y M1).
 * No utiliza TreeMap ni TreeSet.
 *
 * Política de eliminación documentada:
 * - Caso 1 (hoja): se elimina directamente.
 * - Caso 2 (un hijo): se reemplaza el nodo por su único hijo.
 * - Caso 3 (dos hijos): se reemplaza el dato por el SUCESOR INORDEN (mínimo del subárbol derecho)
 *   y luego se elimina recursivamente dicho sucesor en el subárbol derecho.
 *
 * Complejidad temporal:
 * - insertar / buscar / eliminar: O(h), donde h es la altura del árbol;
 *   O(log n) en promedio (árbol balanceado) y O(n) en el peor caso (árbol degenerado).
 * - recorridos (preorden, inorden, posorden): O(n).
 */
public class ArbolBinarioBusqueda {

    private static class NodoABB {
        Estudiante dato;
        NodoABB izquierdo;
        NodoABB derecho;

        NodoABB(Estudiante dato) {
            this.dato = dato;
            this.izquierdo = null;
            this.derecho = null;
        }
    }

    /**
     * Resultado de búsqueda con métricas para comparar rendimiento contra búsqueda secuencial.
     */
    public static class ResultadoBusquedaABB {
        private final Estudiante estudiante;
        private final int comparaciones;
        private final long tiempoNanosegundos;

        public ResultadoBusquedaABB(Estudiante estudiante, int comparaciones, long tiempoNanosegundos) {
            this.estudiante = estudiante;
            this.comparaciones = comparaciones;
            this.tiempoNanosegundos = tiempoNanosegundos;
        }

        public Estudiante getEstudiante() {
            return estudiante;
        }

        public int getComparaciones() {
            return comparaciones;
        }

        public long getTiempoNanosegundos() {
            return tiempoNanosegundos;
        }
    }

    private NodoABB raiz;
    private int tamanio;

    public ArbolBinarioBusqueda() {
        this.raiz = null;
        this.tamanio = 0;
    }

    public void insertar(Estudiante estudiante) {
        if (estudiante == null) {
            throw new IllegalArgumentException("No se puede insertar un estudiante nulo en el ABB.");
        }
        if (buscar(estudiante.getMatricula()) != null) {
            throw new IllegalArgumentException(
                    "Matrícula duplicada en el ABB: " + estudiante.getMatricula());
        }
        raiz = insertarRecursivo(raiz, estudiante);
        tamanio++;
    }

    private NodoABB insertarRecursivo(NodoABB actual, Estudiante estudiante) {
        if (actual == null) {
            return new NodoABB(estudiante);
        }
        if (estudiante.getMatricula() < actual.dato.getMatricula()) {
            actual.izquierdo = insertarRecursivo(actual.izquierdo, estudiante);
        } else if (estudiante.getMatricula() > actual.dato.getMatricula()) {
            actual.derecho = insertarRecursivo(actual.derecho, estudiante);
        }
        return actual;
    }

    public Estudiante buscar(int matricula) {
        return buscarConMetricas(matricula).getEstudiante();
    }

    public ResultadoBusquedaABB buscarConMetricas(int matricula) {
        long inicio = System.nanoTime();
        int comparaciones = 0;
        NodoABB actual = raiz;
        while (actual != null) {
            comparaciones++;
            int actualMat = actual.dato.getMatricula();
            if (matricula == actualMat) {
                return new ResultadoBusquedaABB(actual.dato, comparaciones, System.nanoTime() - inicio);
            } else if (matricula < actualMat) {
                actual = actual.izquierdo;
            } else {
                actual = actual.derecho;
            }
        }
        return new ResultadoBusquedaABB(null, comparaciones, System.nanoTime() - inicio);
    }

    /**
     * Elimina un estudiante por matrícula aplicando la política de sucesor inorden
     * cuando el nodo cuenta con dos hijos.
     *
     * @return true si se encontró y eliminó; false si no existía.
     */
    public boolean eliminar(int matricula) {
        if (estaVacio() || buscar(matricula) == null) {
            return false;
        }
        raiz = eliminarRecursivo(raiz, matricula);
        tamanio--;
        return true;
    }

    private NodoABB eliminarRecursivo(NodoABB actual, int matricula) {
        if (actual == null) {
            return null;
        }
        if (matricula < actual.dato.getMatricula()) {
            actual.izquierdo = eliminarRecursivo(actual.izquierdo, matricula);
        } else if (matricula > actual.dato.getMatricula()) {
            actual.derecho = eliminarRecursivo(actual.derecho, matricula);
        } else {
            // Caso 1: hoja
            if (actual.izquierdo == null && actual.derecho == null) {
                return null;
            }
            // Caso 2: un solo hijo
            if (actual.izquierdo == null) {
                return actual.derecho;
            }
            if (actual.derecho == null) {
                return actual.izquierdo;
            }
            // Caso 3: dos hijos -> reemplazo por sucesor inorden (mínimo del subárbol derecho)
            NodoABB sucesor = encontrarMinimo(actual.derecho);
            actual.dato = sucesor.dato;
            actual.derecho = eliminarRecursivo(actual.derecho, sucesor.dato.getMatricula());
        }
        return actual;
    }

    private NodoABB encontrarMinimo(NodoABB nodo) {
        NodoABB actual = nodo;
        while (actual.izquierdo != null) {
            actual = actual.izquierdo;
        }
        return actual;
    }

    public VectorDinamico<Estudiante> recorridoPreorden() {
        VectorDinamico<Estudiante> resultado = new VectorDinamico<>();
        preordenRecursivo(raiz, resultado);
        return resultado;
    }

    private void preordenRecursivo(NodoABB nodo, VectorDinamico<Estudiante> resultado) {
        if (nodo == null) {
            return;
        }
        resultado.agregar(nodo.dato);
        preordenRecursivo(nodo.izquierdo, resultado);
        preordenRecursivo(nodo.derecho, resultado);
    }

    public VectorDinamico<Estudiante> recorridoInorden() {
        VectorDinamico<Estudiante> resultado = new VectorDinamico<>();
        inordenRecursivo(raiz, resultado);
        return resultado;
    }

    private void inordenRecursivo(NodoABB nodo, VectorDinamico<Estudiante> resultado) {
        if (nodo == null) {
            return;
        }
        inordenRecursivo(nodo.izquierdo, resultado);
        resultado.agregar(nodo.dato);
        inordenRecursivo(nodo.derecho, resultado);
    }

    public VectorDinamico<Estudiante> recorridoPosorden() {
        VectorDinamico<Estudiante> resultado = new VectorDinamico<>();
        posordenRecursivo(raiz, resultado);
        return resultado;
    }

    private void posordenRecursivo(NodoABB nodo, VectorDinamico<Estudiante> resultado) {
        if (nodo == null) {
            return;
        }
        posordenRecursivo(nodo.izquierdo, resultado);
        posordenRecursivo(nodo.derecho, resultado);
        resultado.agregar(nodo.dato);
    }

    /**
     * Verifica recursivamente que el árbol cumple estrictamente la propiedad de ABB.
     */
    public boolean esABBValido() {
        return validarABB(raiz, Integer.MIN_VALUE, Integer.MAX_VALUE);
    }

    private boolean validarABB(NodoABB nodo, int min, int max) {
        if (nodo == null) {
            return true;
        }
        int clave = nodo.dato.getMatricula();
        if (clave <= min || clave >= max) {
            return false;
        }
        return validarABB(nodo.izquierdo, min, clave) && validarABB(nodo.derecho, clave, max);
    }

    public boolean estaVacio() {
        return tamanio == 0;
    }

    public int tamanio() {
        return tamanio;
    }

    public void limpiar() {
        raiz = null;
        tamanio = 0;
    }
}
