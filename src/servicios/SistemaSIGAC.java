package servicios;

import estructuras.ArbolBinarioBusqueda;
import estructuras.ArbolGeneral;
import estructuras.Cola;
import estructuras.GrafoDinamico;
import estructuras.GrafoSocial;
import estructuras.ListaEnlazada;
import estructuras.Pila;
import estructuras.VectorDinamico;
import modelo.EquipoLaboratorio;
import modelo.Estudiante;
import modelo.OperacionHistorial;
import modelo.RecomendacionContacto;
import modelo.SolicitudLaboratorio;

/**
 * Servicio integrador central de SIGAC-UTA.
 * Coordina y comparte datos entre los módulos M1, M2, M3, M4 y M5 como una sola aplicación.
 */
public class SistemaSIGAC {

    public static class ResultadoComparacionBusqueda {
        private final boolean encontrado;
        private final Estudiante estudiante;
        private final int comparacionesVector;
        private final long tiempoVectorNs;
        private final int comparacionesLista;
        private final long tiempoListaNs;
        private final int comparacionesABB;
        private final long tiempoABBNs;

        public ResultadoComparacionBusqueda(boolean encontrado, Estudiante estudiante,
                                            int comparacionesVector, long tiempoVectorNs,
                                            int comparacionesLista, long tiempoListaNs,
                                            int comparacionesABB, long tiempoABBNs) {
            this.encontrado = encontrado;
            this.estudiante = estudiante;
            this.comparacionesVector = comparacionesVector;
            this.tiempoVectorNs = tiempoVectorNs;
            this.comparacionesLista = comparacionesLista;
            this.tiempoListaNs = tiempoListaNs;
            this.comparacionesABB = comparacionesABB;
            this.tiempoABBNs = tiempoABBNs;
        }

        public boolean isEncontrado() {
            return encontrado;
        }

        public Estudiante getEstudiante() {
            return estudiante;
        }

        public int getComparacionesVector() {
            return comparacionesVector;
        }

        public int getComparacionesLista() {
            return comparacionesLista;
        }

        public int getComparacionesABB() {
            return comparacionesABB;
        }

        @Override
        public String toString() {
            String estado = encontrado ? estudiante.toString() : "No encontrado";
            return String.format(
                    "Resultado: %s%n"
                    + "  - Búsqueda secuencial (Vector)         : %d comparaciones (%d ns) [O(n)]%n"
                    + "  - Búsqueda secuencial (Lista Enlazada) : %d comparaciones (%d ns) [O(n)]%n"
                    + "  - Búsqueda indexada   (ABB)            : %d comparaciones (%d ns) [O(h) / O(log n) prom.]",
                    estado,
                    comparacionesVector, tiempoVectorNs,
                    comparacionesLista, tiempoListaNs,
                    comparacionesABB, tiempoABBNs
            );
        }
    }

    // M1: Vector dinámico y lista enlazada de estudiantes
    private final VectorDinamico<Estudiante> vectorEstudiantes;
    private final ListaEnlazada<Estudiante> listaEstudiantes;

    // M2: Cola FIFO de solicitudes y Pila LIFO de historial reversible
    private final Cola<SolicitudLaboratorio> colaSolicitudes;
    private final Pila<OperacionHistorial> historialOperaciones;
    private int contadorSolicitudes;

    // M3: Árbol general (jerarquía académica) y ABB (índice por matrícula)
    private final ArbolGeneral arbolAcademico;
    private final ArbolBinarioBusqueda abbEstudiantes;

    // M4: Grafo dinámico dirigido ponderado (red de laboratorios)
    private final GrafoDinamico redLaboratorios;

    // M5: Grafo social no dirigido (recomendaciones de segundo grado)
    private final GrafoSocial redSocial;

    public SistemaSIGAC() {
        this.vectorEstudiantes = new VectorDinamico<>();
        this.listaEstudiantes = new ListaEnlazada<>();
        this.colaSolicitudes = new Cola<>();
        this.historialOperaciones = new Pila<>();
        this.contadorSolicitudes = 1;
        this.arbolAcademico = new ArbolGeneral("Universidad Técnica de Ambato");
        this.arbolAcademico.agregarCarrera("FISEI", "Ingeniería en Software");
        this.abbEstudiantes = new ArbolBinarioBusqueda();
        this.redLaboratorios = new GrafoDinamico();
        this.redSocial = new GrafoSocial();
    }

    // ==================== M1 & M3: GESTIÓN DE ESTUDIANTES ====================

    public void registrarEstudiante(Estudiante estudiante) {
        registrarEstudianteInterno(estudiante, true);
    }

    private void registrarEstudianteInterno(Estudiante estudiante, boolean registrarEnHistorial) {
        if (estudiante == null) {
            throw new IllegalArgumentException("El estudiante no puede ser nulo.");
        }
        if (buscarPorMatriculaSecuencialVector(estudiante.getMatricula()) != null) {
            throw new IllegalArgumentException(
                    "Registro rechazado: ya existe un estudiante con matrícula " + estudiante.getMatricula());
        }
        vectorEstudiantes.agregar(estudiante);
        listaEstudiantes.agregarAlFinal(estudiante);
        abbEstudiantes.insertar(estudiante);
        arbolAcademico.registrarEstudiante(estudiante);

        if (registrarEnHistorial) {
            historialOperaciones.apilar(new OperacionHistorial(
                    OperacionHistorial.TipoOperacion.REGISTRAR_ESTUDIANTE,
                    "Registro de estudiante matrícula " + estudiante.getMatricula() + " (" + estudiante.getNombre() + ")",
                    estudiante.copiar(),
                    null,
                    null
            ));
        }
    }

    public Estudiante buscarPorMatriculaSecuencialVector(int matricula) {
        if (matricula <= 0) {
            return null;
        }
        for (int i = 0; i < vectorEstudiantes.tamanio(); i++) {
            Estudiante actual = vectorEstudiantes.obtener(i);
            if (actual.getMatricula() == matricula) {
                return actual;
            }
        }
        return null;
    }

    public Estudiante buscarPorMatriculaListaEnlazada(int matricula) {
        if (matricula <= 0) {
            return null;
        }
        for (int i = 0; i < listaEstudiantes.tamanio(); i++) {
            Estudiante actual = listaEstudiantes.obtener(i);
            if (actual.getMatricula() == matricula) {
                return actual;
            }
        }
        return null;
    }

    public Estudiante buscarPorMatriculaABB(int matricula) {
        if (matricula <= 0) {
            return null;
        }
        return abbEstudiantes.buscar(matricula);
    }

    public ResultadoComparacionBusqueda compararBusquedas(int matricula) {
        if (matricula <= 0) {
            throw new IllegalArgumentException("La matrícula a buscar debe ser un entero positivo.");
        }
        // 1. Vector secuencial
        long t0 = System.nanoTime();
        int compVector = 0;
        Estudiante encontradoVec = null;
        for (int i = 0; i < vectorEstudiantes.tamanio(); i++) {
            compVector++;
            Estudiante actual = vectorEstudiantes.obtener(i);
            if (actual.getMatricula() == matricula) {
                encontradoVec = actual;
                break;
            }
        }
        long tVector = System.nanoTime() - t0;

        // 2. Lista enlazada secuencial
        long t1 = System.nanoTime();
        int compLista = 0;
        for (int i = 0; i < listaEstudiantes.tamanio(); i++) {
            compLista++;
            if (listaEstudiantes.obtener(i).getMatricula() == matricula) {
                break;
            }
        }
        long tLista = System.nanoTime() - t1;

        // 3. ABB
        ArbolBinarioBusqueda.ResultadoBusquedaABB resABB = abbEstudiantes.buscarConMetricas(matricula);

        return new ResultadoComparacionBusqueda(
                encontradoVec != null,
                encontradoVec,
                compVector, tVector,
                compLista, tLista,
                resABB.getComparaciones(), resABB.getTiempoNanosegundos()
        );
    }

    public void actualizarEstudiante(int matricula, String nuevoNombre, String nuevaCarrera, double nuevoPromedio) {
        Estudiante actual = buscarPorMatriculaSecuencialVector(matricula);
        if (actual == null) {
            throw new IllegalArgumentException("No existe un estudiante con matrícula " + matricula);
        }
        Estudiante respaldoAnterior = actual.copiar();

        arbolAcademico.eliminarEstudiante(actual);
        actual.setNombre(nuevoNombre);
        actual.setCarrera(nuevaCarrera);
        actual.setPromedio(nuevoPromedio);
        arbolAcademico.registrarEstudiante(actual);

        historialOperaciones.apilar(new OperacionHistorial(
                OperacionHistorial.TipoOperacion.ACTUALIZAR_ESTUDIANTE,
                "Actualización de estudiante matrícula " + matricula,
                actual.copiar(),
                respaldoAnterior,
                null
        ));
    }

    public boolean eliminarEstudiante(int matricula) {
        return eliminarEstudianteInterno(matricula, true);
    }

    private boolean eliminarEstudianteInterno(int matricula, boolean registrarEnHistorial) {
        if (matricula <= 0) {
            throw new IllegalArgumentException("La matrícula debe ser un entero positivo.");
        }
        if (vectorEstudiantes.estaVacio()) {
            return false;
        }
        int indiceVector = -1;
        Estudiante objetivo = null;
        for (int i = 0; i < vectorEstudiantes.tamanio(); i++) {
            Estudiante actual = vectorEstudiantes.obtener(i);
            if (actual.getMatricula() == matricula) {
                indiceVector = i;
                objetivo = actual;
                break;
            }
        }
        if (objetivo == null) {
            return false;
        }
        Estudiante copiaRespaldo = objetivo.copiar();
        vectorEstudiantes.eliminarEn(indiceVector);
        listaEstudiantes.eliminar(objetivo);
        abbEstudiantes.eliminar(matricula);
        arbolAcademico.eliminarEstudiante(objetivo);

        if (registrarEnHistorial) {
            historialOperaciones.apilar(new OperacionHistorial(
                    OperacionHistorial.TipoOperacion.ELIMINAR_ESTUDIANTE,
                    "Eliminación de estudiante matrícula " + matricula + " (" + copiaRespaldo.getNombre() + ")",
                    copiaRespaldo,
                    null,
                    null
            ));
        }
        return true;
    }

    public VectorDinamico<Estudiante> listarEstudiantesVector() {
        return vectorEstudiantes;
    }

    public ListaEnlazada<Estudiante> listarEstudiantesLista() {
        return listaEstudiantes;
    }

    public int cantidadEstudiantes() {
        return vectorEstudiantes.tamanio();
    }

    // ==================== M2: COLA FIFO Y PILA LIFO ====================

    public SolicitudLaboratorio registrarSolicitudLaboratorio(int matriculaEstudiante, String laboratorio, String descripcion) {
        if (buscarPorMatriculaABB(matriculaEstudiante) == null) {
            throw new IllegalArgumentException(
                    "No se puede crear la solicitud: no existe estudiante con matrícula " + matriculaEstudiante);
        }
        SolicitudLaboratorio solicitud = new SolicitudLaboratorio(
                contadorSolicitudes++, matriculaEstudiante, laboratorio, descripcion);
        colaSolicitudes.encolar(solicitud);
        return solicitud;
    }

    public SolicitudLaboratorio atenderSiguienteSolicitudFIFO() {
        if (colaSolicitudes.estaVacia()) {
            throw new IllegalStateException("No hay solicitudes pendientes en la cola FIFO.");
        }
        SolicitudLaboratorio atendida = colaSolicitudes.desencolar();
        historialOperaciones.apilar(new OperacionHistorial(
                OperacionHistorial.TipoOperacion.ATENDER_SOLICITUD,
                "Atención FIFO de " + atendida.toString(),
                null,
                null,
                atendida
        ));
        return atendida;
    }

    /**
     * Recupera y revierte la última operación reversible registrada en la pila LIFO.
     */
    public OperacionHistorial deshacerUltimaOperacionLIFO() {
        if (historialOperaciones.estaVacia()) {
            throw new IllegalStateException("No hay operaciones reversibles en la pila LIFO.");
        }
        OperacionHistorial ultima = historialOperaciones.desapilar();
        switch (ultima.getTipo()) {
            case REGISTRAR_ESTUDIANTE:
                eliminarEstudianteInterno(ultima.getEstudianteAfectado().getMatricula(), false);
                break;
            case ELIMINAR_ESTUDIANTE:
                registrarEstudianteInterno(ultima.getEstudianteAfectado().copiar(), false);
                break;
            case ACTUALIZAR_ESTUDIANTE:
                Estudiante actual = buscarPorMatriculaSecuencialVector(
                        ultima.getEstadoAnterior().getMatricula());
                if (actual != null) {
                    arbolAcademico.eliminarEstudiante(actual);
                    actual.setNombre(ultima.getEstadoAnterior().getNombre());
                    actual.setFacultad(ultima.getEstadoAnterior().getFacultad());
                    actual.setCarrera(ultima.getEstadoAnterior().getCarrera());
                    actual.setPromedio(ultima.getEstadoAnterior().getPromedio());
                    arbolAcademico.registrarEstudiante(actual);
                }
                break;
            case ATENDER_SOLICITUD:
                colaSolicitudes.restaurarAlFrente(ultima.getSolicitudAfectada());
                break;
            default:
                break;
        }
        return ultima;
    }

    public Cola<SolicitudLaboratorio> getColaSolicitudes() {
        return colaSolicitudes;
    }

    public Pila<OperacionHistorial> getHistorialOperaciones() {
        return historialOperaciones;
    }

    // ==================== M3: ÁRBOL GENERAL Y ABB ====================

    public ArbolGeneral getArbolAcademico() {
        return arbolAcademico;
    }

    public ArbolBinarioBusqueda getAbbEstudiantes() {
        return abbEstudiantes;
    }

    // ==================== M4: RED DE LABORATORIOS ====================

    public GrafoDinamico getRedLaboratorios() {
        return redLaboratorios;
    }

    // ==================== M5: GRAFO SOCIAL ====================

    public GrafoSocial getRedSocial() {
        return redSocial;
    }

    // ==================== UTILIDAD DE PRECARGA ====================

    /**
     * Precarga los datos del caso integrador obligatorio para facilitar pruebas desde el menú.
     */
    public void precargarDatosCasoIntegrador() {
        int[] matriculas = {50, 30, 70, 20, 40, 60, 80};
        String[] nombres = {"Ana Torres", "Beto Salazar", "Carla Paredes", "Diego Núñez",
                            "Elena Viteri", "Fernando Paz", "Gabriela Mora"};
        for (int i = 0; i < matriculas.length; i++) {
            if (buscarPorMatriculaABB(matriculas[i]) == null) {
                registrarEstudiante(new Estudiante(matriculas[i], nombres[i], "FISEI", "Ingeniería en Software", 8.5 + (i * 0.2)));
            }
        }

        if (redLaboratorios.estaVacio()) {
            redLaboratorios.agregarEquipo(new EquipoLaboratorio("PC1", "Estación", "Lab Redes A"));
            redLaboratorios.agregarEquipo(new EquipoLaboratorio("SW1", "Switch", "Rack Principal"));
            redLaboratorios.agregarEquipo(new EquipoLaboratorio("SRV1", "Servidor", "DataCenter FISEI"));
            redLaboratorios.agregarEquipo(new EquipoLaboratorio("PC2", "Estación", "Lab Redes B"));
            redLaboratorios.agregarEquipo(new EquipoLaboratorio("PC3", "Estación", "Lab Software"));
            redLaboratorios.agregarEquipo(new EquipoLaboratorio("PC4", "Estación", "Lab Aislado"));

            redLaboratorios.agregarConexion("PC1", "SW1", 2.0);
            redLaboratorios.agregarConexion("SW1", "SRV1", 3.5);
            redLaboratorios.agregarConexion("SRV1", "PC2", 4.0);
            redLaboratorios.agregarConexion("PC1", "PC3", 5.0);
        }

        if (redSocial.estaVacio()) {
            String[] personas = {"Ana", "Beto", "Carla", "Diego", "Elena"};
            for (String p : personas) {
                redSocial.agregarUsuario(p);
            }
            redSocial.agregarAmistad("Ana", "Beto");
            redSocial.agregarAmistad("Ana", "Carla");
            redSocial.agregarAmistad("Beto", "Diego");
            redSocial.agregarAmistad("Carla", "Diego");
            redSocial.agregarAmistad("Carla", "Elena");
        }
    }

    public static String formatearMatriculas(VectorDinamico<Estudiante> lista) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < lista.tamanio(); i++) {
            if (i > 0) {
                sb.append(", ");
            }
            sb.append(lista.obtener(i).getMatricula());
        }
        return sb.toString();
    }

    public static String formatearCadenas(VectorDinamico<String> lista) {
        if (lista.estaVacio()) {
            return "(ninguno)";
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < lista.tamanio(); i++) {
            if (i > 0) {
                sb.append(", ");
            }
            sb.append(lista.obtener(i));
        }
        return sb.toString();
    }
}
