package pruebas;

import estructuras.ArbolBinarioBusqueda;
import estructuras.Cola;
import estructuras.GrafoDinamico;
import estructuras.GrafoSocial;
import estructuras.Pila;
import estructuras.VectorDinamico;
import modelo.EquipoLaboratorio;
import modelo.Estudiante;
import modelo.OperacionHistorial;
import modelo.RecomendacionContacto;
import modelo.SolicitudLaboratorio;
import servicios.SistemaSIGAC;

/**
 * Suite de pruebas automatizadas (M6): cubre casos NORMALES, EXTREMOS y ERRÓNEOS
 * para todos los módulos (M1–M5) y validaciones de seguridad.
 */
public class SuitePruebas {
    private static int aprobadas = 0;
    private static int fallidas = 0;

    public static void ejecutarTodas() {
        aprobadas = 0;
        fallidas = 0;

        System.out.println("=================================================================");
        System.out.println("      SIGAC-UTA | SUITE DE PRUEBAS (NORMALES, EXTREMAS, ERRÓNEAS)");
        System.out.println("=================================================================");

        pruebasM1Estudiantes();
        pruebasM2PilaYCola();
        pruebasM3Arboles();
        pruebasM4GrafoLaboratorios();
        pruebasM5GrafoSocial();

        System.out.println("-----------------------------------------------------------------");
        System.out.printf("RESUMEN FINAL: %d aprobadas | %d fallidas | Total: %d%n",
                aprobadas, fallidas, (aprobadas + fallidas));
        System.out.println("=================================================================");
    }

    private static void pruebasM1Estudiantes() {
        System.out.println("\n--- M1: Vector Dinámico y Lista Enlazada (Estudiantes) ---");
        SistemaSIGAC s = new SistemaSIGAC();

        // Normal: registro y búsqueda
        s.registrarEstudiante(new Estudiante(101, "Ana", "FISEI", "Software", 9.2));
        s.registrarEstudiante(new Estudiante(102, "Luis", "FISEI", "Software", 8.5));
        verificar("T01 [Normal] Registro y búsqueda por matrícula en Vector y Lista",
                s.buscarPorMatriculaSecuencialVector(101) != null
                && s.buscarPorMatriculaListaEnlazada(102) != null
                && s.cantidadEstudiantes() == 2);

        // Normal: eliminación segura
        boolean elim = s.eliminarEstudiante(101);
        verificar("T02 [Normal] Eliminación segura de estudiante existente",
                elim && s.buscarPorMatriculaSecuencialVector(101) == null && s.cantidadEstudiantes() == 1);

        // Errónea: duplicado
        boolean rechazoDuplicado = false;
        try {
            s.registrarEstudiante(new Estudiante(102, "Duplicado", "FISEI", "Software", 7.0));
        } catch (IllegalArgumentException ex) {
            rechazoDuplicado = true;
        }
        verificar("T03 [Errónea] Rechazo de matrícula duplicada", rechazoDuplicado);

        // Errónea: referencia nula
        boolean rechazoNulo = false;
        try {
            s.registrarEstudiante(null);
        } catch (IllegalArgumentException ex) {
            rechazoNulo = true;
        }
        verificar("T04 [Errónea] Rechazo de referencia nula al registrar", rechazoNulo);

        // Extrema: redimensionamiento del vector (> capacidad inicial 8) y eliminar inexistente
        for (int i = 200; i < 220; i++) {
            s.registrarEstudiante(new Estudiante(i, "Est" + i, "FISEI", "TI", 8.0));
        }
        verificar("T05 [Extrema] Redimensionamiento dinámico (>20 registros) y baja de inexistente",
                s.cantidadEstudiantes() == 21 && !s.eliminarEstudiante(9999));
    }

    private static void pruebasM2PilaYCola() {
        System.out.println("\n--- M2: Cola FIFO y Pila LIFO ---");
        SistemaSIGAC s = new SistemaSIGAC();
        s.registrarEstudiante(new Estudiante(50, "Ana", "FISEI", "Software", 9.0));
        s.registrarEstudiante(new Estudiante(30, "Beto", "FISEI", "Software", 8.4));

        // Normal: FIFO estricto
        s.registrarSolicitudLaboratorio(50, "Lab1", "Solicitud 1");
        s.registrarSolicitudLaboratorio(30, "Lab2", "Solicitud 2");
        SolicitudLaboratorio primera = s.atenderSiguienteSolicitudFIFO();
        SolicitudLaboratorio segunda = s.atenderSiguienteSolicitudFIFO();
        verificar("T06 [Normal] Atención en orden FIFO estricto",
                primera.getIdSolicitud() < segunda.getIdSolicitud() && s.getColaSolicitudes().estaVacia());

        // Normal: Deshacer LIFO restaura solicitudes al frente en orden inverso
        OperacionHistorial u1 = s.deshacerUltimaOperacionLIFO();
        OperacionHistorial u2 = s.deshacerUltimaOperacionLIFO();
        verificar("T07 [Normal] Recuperación LIFO revierte las 2 últimas atenciones",
                u1.getSolicitudAfectada().getIdSolicitud() == segunda.getIdSolicitud()
                && u2.getSolicitudAfectada().getIdSolicitud() == primera.getIdSolicitud()
                && s.getColaSolicitudes().tamanio() == 2);

        // Errónea: desencolar en cola vacía
        Cola<String> colaVacia = new Cola<>();
        boolean falloColaVacia = false;
        try {
            colaVacia.desencolar();
        } catch (IllegalStateException ex) {
            falloColaVacia = true;
        }
        verificar("T08 [Errónea] Desencolar en cola vacía lanza IllegalStateException", falloColaVacia);

        // Errónea: desapilar en pila vacía
        Pila<String> pilaVacia = new Pila<>();
        boolean falloPilaVacia = false;
        try {
            pilaVacia.desapilar();
        } catch (IllegalStateException ex) {
            falloPilaVacia = true;
        }
        verificar("T09 [Errónea] Desapilar en pila vacía lanza IllegalStateException", falloPilaVacia);

        // Errónea: solicitud de estudiante inexistente
        boolean falloSolInexistente = false;
        try {
            s.registrarSolicitudLaboratorio(999, "Lab1", "Inválida");
        } catch (IllegalArgumentException ex) {
            falloSolInexistente = true;
        }
        verificar("T10 [Errónea] Solicitud de matrícula inexistente es rechazada", falloSolInexistente);
    }

    private static void pruebasM3Arboles() {
        System.out.println("\n--- M3: Árbol General y ABB ---");
        ArbolBinarioBusqueda abb = new ArbolBinarioBusqueda();
        int[] claves = {50, 30, 70, 20, 40, 60, 80};
        for (int c : claves) {
            abb.insertar(new Estudiante(c, "Est" + c, "Software"));
        }

        // Normal: inorden ascendente
        String inorden = SistemaSIGAC.formatearMatriculas(abb.recorridoInorden());
        verificar("T11 [Normal] Inorden del ABB ordenado: 20, 30, 40, 50, 60, 70, 80",
                "20, 30, 40, 50, 60, 70, 80".equals(inorden) && abb.esABBValido());

        // Extrema: eliminación de nodo con 2 hijos (30) y raíz con 2 hijos (50)
        abb.eliminar(30);
        abb.eliminar(50);
        String inordenPost = SistemaSIGAC.formatearMatriculas(abb.recorridoInorden());
        verificar("T12 [Extrema] Eliminación de nodo interno (30) y raíz (50) con 2 hijos conserva ABB",
                "20, 40, 60, 70, 80".equals(inordenPost) && abb.esABBValido());

        // Extrema: árbol degenerado (inserción ordenada) y búsqueda
        ArbolBinarioBusqueda degenerado = new ArbolBinarioBusqueda();
        for (int i = 1; i <= 10; i++) {
            degenerado.insertar(new Estudiante(i, "D" + i, "Software"));
        }
        verificar("T13 [Extrema] ABB degenerado mantiene búsquedas y propiedad ABB",
                degenerado.buscar(10) != null && degenerado.esABBValido() && degenerado.tamanio() == 10);

        // Errónea: insertar duplicado y eliminar en ABB vacío
        boolean rechazoDupABB = false;
        try {
            abb.insertar(new Estudiante(40, "Dup", "Software"));
        } catch (IllegalArgumentException ex) {
            rechazoDupABB = true;
        }
        ArbolBinarioBusqueda abbVacio = new ArbolBinarioBusqueda();
        verificar("T14 [Errónea] Duplicado en ABB rechazado y eliminar en ABB vacío retorna false",
                rechazoDupABB && !abbVacio.eliminar(10));
    }

    private static void pruebasM4GrafoLaboratorios() {
        System.out.println("\n--- M4: Grafo Dinámico de Laboratorios ---");
        GrafoDinamico red = new GrafoDinamico();
        red.agregarEquipo(new EquipoLaboratorio("PC1", "PC", "Lab A"));
        red.agregarEquipo(new EquipoLaboratorio("SW1", "Switch", "Rack"));
        red.agregarEquipo(new EquipoLaboratorio("SRV1", "Server", "DC"));
        red.agregarEquipo(new EquipoLaboratorio("PC2", "PC", "Lab B"));
        red.agregarEquipo(new EquipoLaboratorio("PC3", "PC", "Lab C"));
        red.agregarEquipo(new EquipoLaboratorio("PC4", "PC", "Aislado"));

        red.agregarConexion("PC1", "SW1", 2.0);
        red.agregarConexion("SW1", "SRV1", 3.0);
        red.agregarConexion("SRV1", "PC2", 4.0);
        red.agregarConexion("PC1", "PC3", 5.0);

        // Normal: BFS y detección de nodo aislado
        String bfsStr = SistemaSIGAC.formatearCadenas(red.bfs("PC1"));
        String noAlc = SistemaSIGAC.formatearCadenas(red.detectarNoAlcanzables("PC1"));
        verificar("T15 [Normal] BFS determinista desde PC1 y detección de PC4 aislado",
                "PC1, SW1, PC3, SRV1, PC2".equals(bfsStr) && "PC4".equals(noAlc));

        // Normal: eliminación de arista actualiza conectividad
        red.eliminarConexion("SW1", "SRV1");
        String noAlcPost = SistemaSIGAC.formatearCadenas(red.detectarNoAlcanzables("PC1"));
        verificar("T16 [Normal] Eliminación de arista SW1->SRV1 desconecta SRV1 y PC2",
                noAlcPost.contains("SRV1") && noAlcPost.contains("PC2") && noAlcPost.contains("PC4"));

        // Extrema: eliminación de vértice con aristas entrantes (SW1 tiene arista entrante PC1->SW1)
        boolean elimVert = red.eliminarEquipo("SW1");
        String bfsTrasEliminarVertice = SistemaSIGAC.formatearCadenas(red.bfs("PC1"));
        verificar("T17 [Extrema] Eliminación de vértice SW1 limpia aristas entrantes desde PC1",
                elimVert && "PC1, PC3".equals(bfsTrasEliminarVertice));

        // Errónea: peso <= 0, origen inexistente y BFS en grafo vacío
        boolean pesoInvalido = false;
        try {
            red.agregarConexion("PC1", "PC4", -1.0);
        } catch (IllegalArgumentException ex) {
            pesoInvalido = true;
        }
        GrafoDinamico vacio = new GrafoDinamico();
        boolean bfsVacio = false;
        try {
            vacio.bfs("PC1");
        } catch (IllegalStateException ex) {
            bfsVacio = true;
        }
        verificar("T18 [Errónea] Peso negativo rechazado y BFS en grafo vacío lanza excepción",
                pesoInvalido && bfsVacio);
    }

    private static void pruebasM5GrafoSocial() {
        System.out.println("\n--- M5: Grafo Social y Recomendaciones ---");
        GrafoSocial social = new GrafoSocial();
        String[] usuarios = {"Ana", "Beto", "Carla", "Diego", "Elena", "Aislado"};
        for (String u : usuarios) {
            social.agregarUsuario(u);
        }
        social.agregarAmistad("Ana", "Beto");
        social.agregarAmistad("Ana", "Carla");
        social.agregarAmistad("Beto", "Diego");
        social.agregarAmistad("Carla", "Diego");
        social.agregarAmistad("Carla", "Elena");

        // Normal: recomendaciones ordenadas y exclusión de amigos directos
        VectorDinamico<RecomendacionContacto> recs = social.recomendarContactos("Ana");
        verificar("T19 [Normal] Recomendaciones para Ana: 1. Diego (2), 2. Elena (1), sin amigos directos",
                recs.tamanio() == 2
                && "Diego".equals(recs.obtener(0).getUsuario()) && recs.obtener(0).getAmigosEnComun() == 2
                && "Elena".equals(recs.obtener(1).getUsuario()) && recs.obtener(1).getAmigosEnComun() == 1);

        // Extrema: usuario aislado sin recomendaciones
        VectorDinamico<RecomendacionContacto> recsAislado = social.recomendarContactos("Aislado");
        verificar("T20 [Extrema] Usuario sin amigos retorna 0 recomendaciones sin error",
                recsAislado.estaVacio());

        // Errónea: autoamistad y usuario inexistente
        boolean autoAmistad = false;
        try {
            social.agregarAmistad("Ana", "Ana");
        } catch (IllegalArgumentException ex) {
            autoAmistad = true;
        }
        boolean usuarioInexistente = false;
        try {
            social.recomendarContactos("NoExiste");
        } catch (IllegalArgumentException ex) {
            usuarioInexistente = true;
        }
        verificar("T21 [Errónea] Rechazo de autoamistad y recomendación para usuario inexistente",
                autoAmistad && usuarioInexistente);
    }

    private static void verificar(String nombrePrueba, boolean condicion) {
        if (condicion) {
            aprobadas++;
            System.out.println("  [PASS] " + nombrePrueba);
        } else {
            fallidas++;
            System.out.println("  [FAIL] " + nombrePrueba);
        }
    }

    public static void main(String[] args) {
        ejecutarTodas();
    }
}
