package estructuras;

/**
 * Nodo del árbol general para la jerarquía académica:
 * Universidad -> Facultad -> Carrera -> Estudiantes (M3).
 */
public class NodoArbolGeneral {
    private final String tipo;
    private String nombre;
    private final ListaEnlazada<NodoArbolGeneral> hijos;

    public NodoArbolGeneral(String tipo, String nombre) {
        if (tipo == null || tipo.trim().isEmpty()) {
            throw new IllegalArgumentException("El tipo de nodo académico no puede ser nulo ni vacío.");
        }
        if (nombre == null || nombre.trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre del nodo académico no puede ser nulo ni vacío.");
        }
        this.tipo = tipo.trim();
        this.nombre = nombre.trim();
        this.hijos = new ListaEnlazada<>();
    }

    public String getTipo() {
        return tipo;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        if (nombre == null || nombre.trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre no puede ser nulo ni vacío.");
        }
        this.nombre = nombre.trim();
    }

    public ListaEnlazada<NodoArbolGeneral> getHijos() {
        return hijos;
    }

    public NodoArbolGeneral buscarHijoPorNombre(String nombreBuscado) {
        if (nombreBuscado == null) {
            return null;
        }
        for (int i = 0; i < hijos.tamanio(); i++) {
            NodoArbolGeneral hijo = hijos.obtener(i);
            if (hijo.getNombre().equalsIgnoreCase(nombreBuscado.trim())) {
                return hijo;
            }
        }
        return null;
    }

    public void agregarHijo(NodoArbolGeneral hijo) {
        if (hijo == null) {
            throw new IllegalArgumentException("No se puede agregar un nodo hijo nulo.");
        }
        hijos.agregarAlFinal(hijo);
    }

    public boolean eliminarHijoPorNombre(String nombreBuscado) {
        NodoArbolGeneral objetivo = buscarHijoPorNombre(nombreBuscado);
        if (objetivo == null) {
            return false;
        }
        return hijos.eliminar(objetivo);
    }

    @Override
    public String toString() {
        return "[" + tipo + "] " + nombre;
    }
}
