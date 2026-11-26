package estructuras;

import modelo.EquipoLaboratorio;

/**
 * Implementación manual de un Grafo Dinámico Dirigido y Ponderado mediante listas de adyacencia (M4).
 *
 * Decisiones documentadas para reproducibilidad:
 * - Dirección de aristas: DIRIGIDA (origen -> destino con peso positivo).
 * - Orden de inserción de vecinos: al FINAL de la lista de adyacencia (orden FIFO de inserción),
 *   garantizando recorridos BFS y DFS deterministas.
 * - Eliminación de vértice: elimina el vértice y recorre todos los demás vértices para suprimir
 *   todas las aristas entrantes hacia el vértice eliminado.
 *
 * Complejidad temporal:
 * - BFS / DFS: O(V + E) en recorridos sobre listas de adyacencia (con búsqueda de índice O(V)
 *   por vértice en arreglo dinámico: O(V^2 + V*E) en esta implementación sin tabla hash).
 * - eliminarArista: O(V + gradoSalida(origen)).
 * - eliminarEquipo (con aristas entrantes): O(V + E).
 */
public class GrafoDinamico {

    public static class Arista {
        private final String destinoId;
        private final double peso;

        public Arista(String destinoId, double peso) {
            this.destinoId = destinoId;
            this.peso = peso;
        }

        public String getDestinoId() {
            return destinoId;
        }

        public double getPeso() {
            return peso;
        }

        @Override
        public String toString() {
            return destinoId + " (peso: " + peso + ")";
        }
    }

    private static class Vertice {
        final EquipoLaboratorio equipo;
        final ListaEnlazada<Arista> adyacentes;

        Vertice(EquipoLaboratorio equipo) {
            this.equipo = equipo;
            this.adyacentes = new ListaEnlazada<>();
        }
    }

    private final VectorDinamico<Vertice> vertices;

    public GrafoDinamico() {
        this.vertices = new VectorDinamico<>();
    }

    public void agregarEquipo(EquipoLaboratorio equipo) {
        if (equipo == null) {
            throw new IllegalArgumentException("El equipo no puede ser nulo.");
        }
        if (buscarIndiceVertice(equipo.getId()) != -1) {
            throw new IllegalArgumentException("Ya existe un equipo con el ID: " + equipo.getId());
        }
        vertices.agregar(new Vertice(equipo));
    }

    public void agregarConexion(String origenId, String destinoId, double peso) {
        if (origenId == null || destinoId == null) {
            throw new IllegalArgumentException("Los identificadores de origen y destino no pueden ser nulos.");
        }
        if (peso <= 0) {
            throw new IllegalArgumentException("El peso de la arista debe ser mayor que cero.");
        }
        int idxOrigen = buscarIndiceVertice(origenId);
        int idxDestino = buscarIndiceVertice(destinoId);
        if (idxOrigen == -1) {
            throw new IllegalArgumentException("El equipo origen no existe en la red: " + origenId);
        }
        if (idxDestino == -1) {
            throw new IllegalArgumentException("El equipo destino no existe en la red: " + destinoId);
        }
        if (idxOrigen == idxDestino) {
            throw new IllegalArgumentException("No se permiten lazos (conexión de un equipo consigo mismo).");
        }
        Vertice origen = vertices.obtener(idxOrigen);
        String idDestinoCanonico = vertices.obtener(idxDestino).equipo.getId();
        for (int i = 0; i < origen.adyacentes.tamanio(); i++) {
            if (origen.adyacentes.obtener(i).getDestinoId().equalsIgnoreCase(idDestinoCanonico)) {
                throw new IllegalArgumentException(
                        "Ya existe una conexión dirigida " + origen.equipo.getId() + " -> " + idDestinoCanonico);
            }
        }
        // Inserción al final para mantener orden determinista de vecinos
        origen.adyacentes.agregarAlFinal(new Arista(idDestinoCanonico, peso));
    }

    public boolean eliminarConexion(String origenId, String destinoId) {
        if (origenId == null || destinoId == null) {
            throw new IllegalArgumentException("Los identificadores no pueden ser nulos.");
        }
        int idxOrigen = buscarIndiceVertice(origenId);
        if (idxOrigen == -1) {
            throw new IllegalArgumentException("El equipo origen no existe: " + origenId);
        }
        Vertice origen = vertices.obtener(idxOrigen);
        for (int i = 0; i < origen.adyacentes.tamanio(); i++) {
            Arista arista = origen.adyacentes.obtener(i);
            if (arista.getDestinoId().equalsIgnoreCase(destinoId.trim())) {
                return origen.adyacentes.eliminar(arista);
            }
        }
        return false;
    }

    /**
     * Elimina un equipo y todas las aristas entrantes que apuntan hacia él desde otros vértices.
     */
    public boolean eliminarEquipo(String id) {
        if (id == null || id.trim().isEmpty()) {
            throw new IllegalArgumentException("El identificador del equipo no puede ser nulo ni vacío.");
        }
        int idxObjetivo = buscarIndiceVertice(id);
        if (idxObjetivo == -1) {
            return false;
        }
        String idCanonico = vertices.obtener(idxObjetivo).equipo.getId();
        // 1. Eliminar todas las aristas entrantes desde otros vértices
        for (int i = 0; i < vertices.tamanio(); i++) {
            if (i == idxObjetivo) {
                continue;
            }
            Vertice v = vertices.obtener(i);
            for (int j = 0; j < v.adyacentes.tamanio(); j++) {
                Arista arista = v.adyacentes.obtener(j);
                if (arista.getDestinoId().equalsIgnoreCase(idCanonico)) {
                    v.adyacentes.eliminar(arista);
                    break;
                }
            }
        }
        // 2. Eliminar el vértice y sus aristas salientes
        vertices.eliminarEn(idxObjetivo);
        return true;
    }

    /**
     * Recorrido en anchura (BFS) utilizando la estructura manual Cola<Integer>.
     */
    public VectorDinamico<String> bfs(String origenId) {
        if (estaVacio()) {
            throw new IllegalStateException("La red de laboratorios está vacía.");
        }
        int idxOrigen = buscarIndiceVertice(origenId);
        if (idxOrigen == -1) {
            throw new IllegalArgumentException("El equipo origen no existe en la red: " + origenId);
        }
        boolean[] visitado = new boolean[vertices.tamanio()];
        Cola<Integer> cola = new Cola<>();
        VectorDinamico<String> recorrido = new VectorDinamico<>();

        visitado[idxOrigen] = true;
        cola.encolar(idxOrigen);

        while (!cola.estaVacia()) {
            int actual = cola.desencolar();
            Vertice v = vertices.obtener(actual);
            recorrido.agregar(v.equipo.getId());

            for (int i = 0; i < v.adyacentes.tamanio(); i++) {
                String vecinoId = v.adyacentes.obtener(i).getDestinoId();
                int idxVecino = buscarIndiceVertice(vecinoId);
                if (idxVecino != -1 && !visitado[idxVecino]) {
                    visitado[idxVecino] = true;
                    cola.encolar(idxVecino);
                }
            }
        }
        return recorrido;
    }

    /**
     * Recorrido en profundidad (DFS) determinista según el orden de inserción de vecinos.
     */
    public VectorDinamico<String> dfs(String origenId) {
        if (estaVacio()) {
            throw new IllegalStateException("La red de laboratorios está vacía.");
        }
        int idxOrigen = buscarIndiceVertice(origenId);
        if (idxOrigen == -1) {
            throw new IllegalArgumentException("El equipo origen no existe en la red: " + origenId);
        }
        boolean[] visitado = new boolean[vertices.tamanio()];
        VectorDinamico<String> recorrido = new VectorDinamico<>();
        dfsRecursivo(idxOrigen, visitado, recorrido);
        return recorrido;
    }

    private void dfsRecursivo(int idxActual, boolean[] visitado, VectorDinamico<String> recorrido) {
        visitado[idxActual] = true;
        Vertice v = vertices.obtener(idxActual);
        recorrido.agregar(v.equipo.getId());
        for (int i = 0; i < v.adyacentes.tamanio(); i++) {
            String vecinoId = v.adyacentes.obtener(i).getDestinoId();
            int idxVecino = buscarIndiceVertice(vecinoId);
            if (idxVecino != -1 && !visitado[idxVecino]) {
                dfsRecursivo(idxVecino, visitado, recorrido);
            }
        }
    }

    /**
     * Detecta todos los equipos no alcanzables desde un equipo origen.
     */
    public VectorDinamico<String> detectarNoAlcanzables(String origenId) {
        VectorDinamico<String> alcanzables = bfs(origenId);
        VectorDinamico<String> noAlcanzables = new VectorDinamico<>();
        for (int i = 0; i < vertices.tamanio(); i++) {
            String id = vertices.obtener(i).equipo.getId();
            if (!alcanzables.contiene(id)) {
                noAlcanzables.agregar(id);
            }
        }
        return noAlcanzables;
    }

    public int cantidadEquipos() {
        return vertices.tamanio();
    }

    public boolean estaVacio() {
        return vertices.estaVacio();
    }

    public void limpiar() {
        vertices.limpiar();
    }

    public String mostrarRed() {
        if (estaVacio()) {
            return "(Red de laboratorios vacía)";
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < vertices.tamanio(); i++) {
            Vertice v = vertices.obtener(i);
            sb.append(v.equipo.getId()).append(" -> ");
            if (v.adyacentes.estaVacia()) {
                sb.append("[sin conexiones salientes]");
            } else {
                for (int j = 0; j < v.adyacentes.tamanio(); j++) {
                    if (j > 0) {
                        sb.append(", ");
                    }
                    sb.append(v.adyacentes.obtener(j).toString());
                }
            }
            sb.append("\n");
        }
        return sb.toString();
    }

    private int buscarIndiceVertice(String id) {
        if (id == null) {
            return -1;
        }
        String buscado = id.trim();
        for (int i = 0; i < vertices.tamanio(); i++) {
            if (vertices.obtener(i).equipo.getId().equalsIgnoreCase(buscado)) {
                return i;
            }
        }
        return -1;
    }
}
