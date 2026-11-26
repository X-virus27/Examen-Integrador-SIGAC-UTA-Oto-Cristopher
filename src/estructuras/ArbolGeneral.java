package estructuras;

import modelo.Estudiante;

/**
 * Implementación manual de un Árbol General para la organización académica (M3):
 * Universidad -> Facultad -> Carrera -> Estudiantes.
 */
public class ArbolGeneral {
    private final NodoArbolGeneral raiz;

    public ArbolGeneral(String nombreUniversidad) {
        if (nombreUniversidad == null || nombreUniversidad.trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre de la universidad no puede ser nulo ni vacío.");
        }
        this.raiz = new NodoArbolGeneral("Universidad", nombreUniversidad.trim());
    }

    public NodoArbolGeneral getRaiz() {
        return raiz;
    }

    public NodoArbolGeneral agregarFacultad(String nombreFacultad) {
        if (nombreFacultad == null || nombreFacultad.trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre de la facultad no puede ser nulo ni vacío.");
        }
        NodoArbolGeneral existente = raiz.buscarHijoPorNombre(nombreFacultad);
        if (existente != null) {
            return existente;
        }
        NodoArbolGeneral nueva = new NodoArbolGeneral("Facultad", nombreFacultad.trim());
        raiz.agregarHijo(nueva);
        return nueva;
    }

    public NodoArbolGeneral agregarCarrera(String nombreFacultad, String nombreCarrera) {
        if (nombreCarrera == null || nombreCarrera.trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre de la carrera no puede ser nulo ni vacío.");
        }
        NodoArbolGeneral facultad = agregarFacultad(nombreFacultad);
        NodoArbolGeneral existente = facultad.buscarHijoPorNombre(nombreCarrera);
        if (existente != null) {
            return existente;
        }
        NodoArbolGeneral nueva = new NodoArbolGeneral("Carrera", nombreCarrera.trim());
        facultad.agregarHijo(nueva);
        return nueva;
    }

    public void registrarEstudiante(Estudiante estudiante) {
        if (estudiante == null) {
            throw new IllegalArgumentException("No se puede registrar un estudiante nulo en el árbol general.");
        }
        NodoArbolGeneral carrera = agregarCarrera(estudiante.getFacultad(), estudiante.getCarrera());
        String etiqueta = etiquetaEstudiante(estudiante);
        if (carrera.buscarHijoPorNombre(etiqueta) == null) {
            carrera.agregarHijo(new NodoArbolGeneral("Estudiante", etiqueta));
        }
    }

    public boolean eliminarEstudiante(Estudiante estudiante) {
        if (estudiante == null) {
            return false;
        }
        NodoArbolGeneral facultad = raiz.buscarHijoPorNombre(estudiante.getFacultad());
        if (facultad == null) {
            return false;
        }
        NodoArbolGeneral carrera = facultad.buscarHijoPorNombre(estudiante.getCarrera());
        if (carrera == null) {
            return false;
        }
        return carrera.eliminarHijoPorNombre(etiquetaEstudiante(estudiante));
    }

    public int contarNodos() {
        return contarRecursivo(raiz);
    }

    private int contarRecursivo(NodoArbolGeneral nodo) {
        int total = 1;
        ListaEnlazada<NodoArbolGeneral> hijos = nodo.getHijos();
        for (int i = 0; i < hijos.tamanio(); i++) {
            total += contarRecursivo(hijos.obtener(i));
        }
        return total;
    }

    public String mostrarJerarquia() {
        StringBuilder sb = new StringBuilder();
        construirTexto(raiz, 0, sb);
        return sb.toString();
    }

    private void construirTexto(NodoArbolGeneral nodo, int nivel, StringBuilder sb) {
        for (int i = 0; i < nivel; i++) {
            sb.append("  ");
        }
        if (nivel > 0) {
            sb.append("|- ");
        }
        sb.append(nodo.toString()).append("\n");
        ListaEnlazada<NodoArbolGeneral> hijos = nodo.getHijos();
        for (int i = 0; i < hijos.tamanio(); i++) {
            construirTexto(hijos.obtener(i), nivel + 1, sb);
        }
    }

    private String etiquetaEstudiante(Estudiante estudiante) {
        return estudiante.getMatricula() + " - " + estudiante.getNombre();
    }
}
