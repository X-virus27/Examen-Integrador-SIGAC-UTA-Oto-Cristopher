package estructuras;

import modelo.RecomendacionContacto;

/**
 * Implementación manual de un Grafo Social no dirigido para recomendación de contactos
 * de segundo grado por amigos en común (M5).
 * No utiliza HashMap, HashSet ni bibliotecas de grafos.
 *
 * Complejidad temporal de recomendarContactos(u):
 * - O(d_u * d_max * V) en la exploración de vecinos de segundo grado con búsqueda lineal de índices
 *   y conteo de amigos en común, más O(k^2) por ordenamiento por inserción sobre k candidatos.
 */
public class GrafoSocial {

    private static class NodoUsuario {
        final String nombre;
        final ListaEnlazada<String> amigos;

        NodoUsuario(String nombre) {
            this.nombre = nombre;
            this.amigos = new ListaEnlazada<>();
        }
    }

    private final VectorDinamico<NodoUsuario> usuarios;

    public GrafoSocial() {
        this.usuarios = new VectorDinamico<>();
    }

    public void agregarUsuario(String nombre) {
        if (nombre == null || nombre.trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre del usuario no puede ser nulo ni vacío.");
        }
        if (buscarIndiceUsuario(nombre) != -1) {
            throw new IllegalArgumentException("El usuario ya existe en la red social: " + nombre.trim());
        }
        usuarios.agregar(new NodoUsuario(nombre.trim()));
    }

    /**
     * Registra una amistad bidireccional (arista no dirigida).
     */
    public void agregarAmistad(String usuarioA, String usuarioB) {
        if (usuarioA == null || usuarioB == null) {
            throw new IllegalArgumentException("Los nombres de usuario no pueden ser nulos.");
        }
        int idxA = buscarIndiceUsuario(usuarioA);
        int idxB = buscarIndiceUsuario(usuarioB);
        if (idxA == -1) {
            throw new IllegalArgumentException("El usuario no existe en la red social: " + usuarioA);
        }
        if (idxB == -1) {
            throw new IllegalArgumentException("El usuario no existe en la red social: " + usuarioB);
        }
        if (idxA == idxB) {
            throw new IllegalArgumentException("Un usuario no puede agregarse a sí mismo como amigo.");
        }
        NodoUsuario nodoA = usuarios.obtener(idxA);
        NodoUsuario nodoB = usuarios.obtener(idxB);
        if (contieneIgnoreCase(nodoA.amigos, nodoB.nombre)) {
            throw new IllegalArgumentException(
                    "La amistad entre " + nodoA.nombre + " y " + nodoB.nombre + " ya está registrada.");
        }
        nodoA.amigos.agregarAlFinal(nodoB.nombre);
        nodoB.amigos.agregarAlFinal(nodoA.nombre);
    }

    public boolean eliminarAmistad(String usuarioA, String usuarioB) {
        if (usuarioA == null || usuarioB == null) {
            throw new IllegalArgumentException("Los nombres de usuario no pueden ser nulos.");
        }
        int idxA = buscarIndiceUsuario(usuarioA);
        int idxB = buscarIndiceUsuario(usuarioB);
        if (idxA == -1 || idxB == -1) {
            return false;
        }
        NodoUsuario nodoA = usuarios.obtener(idxA);
        NodoUsuario nodoB = usuarios.obtener(idxB);
        boolean elimA = eliminarIgnoreCase(nodoA.amigos, nodoB.nombre);
        boolean elimB = eliminarIgnoreCase(nodoB.amigos, nodoA.nombre);
        return elimA && elimB;
    }

    public boolean sonAmigos(String usuarioA, String usuarioB) {
        int idxA = buscarIndiceUsuario(usuarioA);
        int idxB = buscarIndiceUsuario(usuarioB);
        if (idxA == -1 || idxB == -1) {
            return false;
        }
        return contieneIgnoreCase(usuarios.obtener(idxA).amigos, usuarios.obtener(idxB).nombre);
    }

    /**
     * Recomienda contactos de segundo grado para el usuario indicado, excluyendo
     * al propio usuario y a sus amigos directos, ordenados descendentemente por
     * número de amigos en común (desempate alfabético ascendente).
     */
    public VectorDinamico<RecomendacionContacto> recomendarContactos(String nombreUsuario) {
        if (estaVacio()) {
            throw new IllegalStateException("La red social está vacía.");
        }
        int idxUsuario = buscarIndiceUsuario(nombreUsuario);
        if (idxUsuario == -1) {
            throw new IllegalArgumentException("El usuario no existe en la red social: " + nombreUsuario);
        }
        NodoUsuario objetivo = usuarios.obtener(idxUsuario);
        int totalUsuarios = usuarios.tamanio();

        int[] conteoComunes = new int[totalUsuarios];
        StringBuilder[] nombresComunes = new StringBuilder[totalUsuarios];

        // Recorrer cada amigo directo del usuario objetivo
        for (int i = 0; i < objetivo.amigos.tamanio(); i++) {
            String nombreAmigo = objetivo.amigos.obtener(i);
            int idxAmigo = buscarIndiceUsuario(nombreAmigo);
            if (idxAmigo == -1) {
                continue;
            }
            NodoUsuario amigoDirecto = usuarios.obtener(idxAmigo);

            // Recorrer los amigos del amigo directo (contactos de segundo grado)
            for (int j = 0; j < amigoDirecto.amigos.tamanio(); j++) {
                String nombreCandidato = amigoDirecto.amigos.obtener(j);
                int idxCandidato = buscarIndiceUsuario(nombreCandidato);
                if (idxCandidato == -1) {
                    continue;
                }
                // Excluir al propio usuario
                if (idxCandidato == idxUsuario) {
                    continue;
                }
                // Excluir amigos directos del usuario objetivo
                if (contieneIgnoreCase(objetivo.amigos, nombreCandidato)) {
                    continue;
                }
                conteoComunes[idxCandidato]++;
                if (nombresComunes[idxCandidato] == null) {
                    nombresComunes[idxCandidato] = new StringBuilder(amigoDirecto.nombre);
                } else {
                    nombresComunes[idxCandidato].append(", ").append(amigoDirecto.nombre);
                }
            }
        }

        VectorDinamico<RecomendacionContacto> recomendaciones = new VectorDinamico<>();
        for (int i = 0; i < totalUsuarios; i++) {
            if (conteoComunes[i] > 0) {
                recomendaciones.agregar(new RecomendacionContacto(
                        usuarios.obtener(i).nombre,
                        conteoComunes[i],
                        nombresComunes[i].toString()
                ));
            }
        }

        // Ordenamiento manual por inserción: mayor amigosEnComun primero, desempate alfabético
        for (int i = 1; i < recomendaciones.tamanio(); i++) {
            RecomendacionContacto clave = recomendaciones.obtener(i);
            int j = i - 1;
            while (j >= 0 && debeIrAntes(clave, recomendaciones.obtener(j))) {
                recomendaciones.establecer(j + 1, recomendaciones.obtener(j));
                j--;
            }
            recomendaciones.establecer(j + 1, clave);
        }

        return recomendaciones;
    }

    private boolean debeIrAntes(RecomendacionContacto a, RecomendacionContacto b) {
        if (a.getAmigosEnComun() != b.getAmigosEnComun()) {
            return a.getAmigosEnComun() > b.getAmigosEnComun();
        }
        return a.getUsuario().compareToIgnoreCase(b.getUsuario()) < 0;
    }

    public int cantidadUsuarios() {
        return usuarios.tamanio();
    }

    public boolean estaVacio() {
        return usuarios.estaVacio();
    }

    public void limpiar() {
        usuarios.limpiar();
    }

    public String mostrarRedSocial() {
        if (estaVacio()) {
            return "(Red social vacía)";
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < usuarios.tamanio(); i++) {
            NodoUsuario u = usuarios.obtener(i);
            sb.append(u.nombre).append(" <-> ");
            if (u.amigos.estaVacia()) {
                sb.append("[sin amigos]");
            } else {
                for (int j = 0; j < u.amigos.tamanio(); j++) {
                    if (j > 0) {
                        sb.append(", ");
                    }
                    sb.append(u.amigos.obtener(j));
                }
            }
            sb.append("\n");
        }
        return sb.toString();
    }

    private int buscarIndiceUsuario(String nombre) {
        if (nombre == null) {
            return -1;
        }
        String buscado = nombre.trim();
        for (int i = 0; i < usuarios.tamanio(); i++) {
            if (usuarios.obtener(i).nombre.equalsIgnoreCase(buscado)) {
                return i;
            }
        }
        return -1;
    }

    private boolean contieneIgnoreCase(ListaEnlazada<String> lista, String objetivo) {
        for (int i = 0; i < lista.tamanio(); i++) {
            if (lista.obtener(i).equalsIgnoreCase(objetivo)) {
                return true;
            }
        }
        return false;
    }

    private boolean eliminarIgnoreCase(ListaEnlazada<String> lista, String objetivo) {
        for (int i = 0; i < lista.tamanio(); i++) {
            String actual = lista.obtener(i);
            if (actual.equalsIgnoreCase(objetivo)) {
                return lista.eliminar(actual);
            }
        }
        return false;
    }
}
