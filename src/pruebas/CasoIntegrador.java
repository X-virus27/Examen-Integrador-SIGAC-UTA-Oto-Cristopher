package pruebas;

import estructuras.VectorDinamico;
import modelo.EquipoLaboratorio;
import modelo.Estudiante;
import modelo.OperacionHistorial;
import modelo.RecomendacionContacto;
import modelo.SolicitudLaboratorio;
import servicios.SistemaSIGAC;

/**
 * Ejecuta los 6 pasos del Caso Integrador Obligatorio (Sección 6 del examen).
 * Todas las decisiones de reproducibilidad están fijadas:
 * - Política de eliminación ABB (2 hijos): sucesor inorden (mínimo del subárbol derecho).
 * - Grafo de laboratorio: dirigido y ponderado.
 * - Orden de inserción de vecinos: al final de la lista de adyacencia (FIFO de inserción).
 */
public class CasoIntegrador {

    public static void ejecutar() {
        System.out.println("=================================================================");
        System.out.println("   SIGAC-UTA | EJECUCIÓN DEL CASO INTEGRADOR OBLIGATORIO (M1-M6)");
        System.out.println("=================================================================");

        SistemaSIGAC sistema = new SistemaSIGAC();

        // Carga inicial de estudiantes: 50, 30, 70, 20, 40, 60, 80
        int[] matriculas = {50, 30, 70, 20, 40, 60, 80};
        String[] nombres = {"Ana Torres", "Beto Salazar", "Carla Paredes", "Diego Núñez",
                            "Elena Viteri", "Fernando Paz", "Gabriela Mora"};
        for (int i = 0; i < matriculas.length; i++) {
            sistema.registrarEstudiante(
                    new Estudiante(matriculas[i], nombres[i], "FISEI", "Ingeniería en Software", 8.5 + (i * 0.2)));
        }

        // PASO 1: Mostrar inorden del ABB: 20, 30, 40, 50, 60, 70, 80
        System.out.println("\n[PASO 1] Recorrido Inorden inicial del ABB (esperado: 20, 30, 40, 50, 60, 70, 80):");
        String inordenInicial = SistemaSIGAC.formatearMatriculas(sistema.getAbbEstudiantes().recorridoInorden());
        System.out.println("  Inorden  : " + inordenInicial);
        System.out.println("  Preorden : " + SistemaSIGAC.formatearMatriculas(sistema.getAbbEstudiantes().recorridoPreorden()));
        System.out.println("  Posorden : " + SistemaSIGAC.formatearMatriculas(sistema.getAbbEstudiantes().recorridoPosorden()));
        System.out.println("  ¿ABB válido? " + sistema.getAbbEstudiantes().esABBValido());
        System.out.println("\n  Comparación de búsqueda para matrícula 80 (extremo derecho):");
        System.out.println(sistema.compararBusquedas(80));

        // PASO 2: Eliminar matrícula 30 conservando la propiedad del ABB
        System.out.println("\n[PASO 2] Eliminar matrícula 30 (nodo con dos hijos: 20 y 40 -> sucesor inorden 40):");
        boolean eliminado30 = sistema.eliminarEstudiante(30);
        String inordenPostElim = SistemaSIGAC.formatearMatriculas(sistema.getAbbEstudiantes().recorridoInorden());
        System.out.println("  Eliminación exitosa : " + eliminado30);
        System.out.println("  Nuevo Inorden       : " + inordenPostElim);
        System.out.println("  Nuevo Preorden      : " + SistemaSIGAC.formatearMatriculas(sistema.getAbbEstudiantes().recorridoPreorden()));
        System.out.println("  ¿Propiedad ABB ok?  : " + sistema.getAbbEstudiantes().esABBValido());

        // PASO 3: Procesar tres solicitudes FIFO y recuperar dos operaciones LIFO
        System.out.println("\n[PASO 3] Procesar 3 solicitudes FIFO y recuperar 2 operaciones LIFO:");
        sistema.registrarSolicitudLaboratorio(50, "Lab Redes", "Reserva de switch Cisco");
        sistema.registrarSolicitudLaboratorio(70, "Lab Software", "Acceso a servidor Linux");
        sistema.registrarSolicitudLaboratorio(20, "Lab Electrónica", "Préstamo de osciloscopio");

        SolicitudLaboratorio s1 = sistema.atenderSiguienteSolicitudFIFO();
        SolicitudLaboratorio s2 = sistema.atenderSiguienteSolicitudFIFO();
        SolicitudLaboratorio s3 = sistema.atenderSiguienteSolicitudFIFO();
        System.out.println("  Atendida 1 (FIFO): " + s1);
        System.out.println("  Atendida 2 (FIFO): " + s2);
        System.out.println("  Atendida 3 (FIFO): " + s3);
        System.out.println("  Pendientes en cola FIFO tras atender 3: " + sistema.getColaSolicitudes().tamanio());

        OperacionHistorial undo1 = sistema.deshacerUltimaOperacionLIFO();
        OperacionHistorial undo2 = sistema.deshacerUltimaOperacionLIFO();
        System.out.println("  Recuperada 1 (LIFO): " + undo1);
        System.out.println("  Recuperada 2 (LIFO): " + undo2);
        System.out.println("  Pendientes en cola FIFO tras recuperar 2: " + sistema.getColaSolicitudes().tamanio());

        // PASO 4: Red de laboratorio PC1 -> SW1 -> SRV1 -> PC2; PC1 -> PC3; PC4 aislado. BFS desde PC1.
        System.out.println("\n[PASO 4] Red de laboratorio (dirigida ponderada) -> BFS desde PC1 y no alcanzables:");
        sistema.getRedLaboratorios().agregarEquipo(new EquipoLaboratorio("PC1", "Estación", "Lab Redes A"));
        sistema.getRedLaboratorios().agregarEquipo(new EquipoLaboratorio("SW1", "Switch", "Rack Principal"));
        sistema.getRedLaboratorios().agregarEquipo(new EquipoLaboratorio("SRV1", "Servidor", "DataCenter"));
        sistema.getRedLaboratorios().agregarEquipo(new EquipoLaboratorio("PC2", "Estación", "Lab Redes B"));
        sistema.getRedLaboratorios().agregarEquipo(new EquipoLaboratorio("PC3", "Estación", "Lab Software"));
        sistema.getRedLaboratorios().agregarEquipo(new EquipoLaboratorio("PC4", "Estación", "Lab Aislado"));

        // Orden de inserción de vecinos desde PC1: primero SW1, luego PC3
        sistema.getRedLaboratorios().agregarConexion("PC1", "SW1", 2.0);
        sistema.getRedLaboratorios().agregarConexion("SW1", "SRV1", 3.5);
        sistema.getRedLaboratorios().agregarConexion("SRV1", "PC2", 4.0);
        sistema.getRedLaboratorios().agregarConexion("PC1", "PC3", 5.0);

        VectorDinamico<String> bfsInicial = sistema.getRedLaboratorios().bfs("PC1");
        VectorDinamico<String> dfsInicial = sistema.getRedLaboratorios().dfs("PC1");
        VectorDinamico<String> noAlcanzablesInicial = sistema.getRedLaboratorios().detectarNoAlcanzables("PC1");
        System.out.println("  BFS desde PC1   : " + SistemaSIGAC.formatearCadenas(bfsInicial));
        System.out.println("  DFS desde PC1   : " + SistemaSIGAC.formatearCadenas(dfsInicial));
        System.out.println("  No alcanzables  : " + SistemaSIGAC.formatearCadenas(noAlcanzablesInicial));

        // PASO 5: Red social -> Recomendar Diego (2) y Elena (1) a Ana
        System.out.println("\n[PASO 5] Grafo social -> Recomendaciones de segundo grado para Ana:");
        String[] usuarios = {"Ana", "Beto", "Carla", "Diego", "Elena"};
        for (String u : usuarios) {
            sistema.getRedSocial().agregarUsuario(u);
        }
        sistema.getRedSocial().agregarAmistad("Ana", "Beto");
        sistema.getRedSocial().agregarAmistad("Ana", "Carla");
        sistema.getRedSocial().agregarAmistad("Beto", "Diego");
        sistema.getRedSocial().agregarAmistad("Carla", "Diego");
        sistema.getRedSocial().agregarAmistad("Carla", "Elena");

        VectorDinamico<RecomendacionContacto> recomendaciones = sistema.getRedSocial().recomendarContactos("Ana");
        for (int i = 0; i < recomendaciones.tamanio(); i++) {
            System.out.println("  " + (i + 1) + ". " + recomendaciones.obtener(i));
        }

        // PASO 6: Eliminar una arista (SW1 -> SRV1) y actualizar conectividad
        System.out.println("\n[PASO 6] Eliminar arista SW1 -> SRV1 y recalcular conectividad desde PC1:");
        boolean aristaEliminada = sistema.getRedLaboratorios().eliminarConexion("SW1", "SRV1");
        VectorDinamico<String> bfsFinal = sistema.getRedLaboratorios().bfs("PC1");
        VectorDinamico<String> noAlcanzablesFinal = sistema.getRedLaboratorios().detectarNoAlcanzables("PC1");
        System.out.println("  Arista SW1 -> SRV1 eliminada : " + aristaEliminada);
        System.out.println("  Nuevo BFS desde PC1          : " + SistemaSIGAC.formatearCadenas(bfsFinal));
        System.out.println("  Nuevos no alcanzables        : " + SistemaSIGAC.formatearCadenas(noAlcanzablesFinal));

        System.out.println("\n[EXTRA M3] Jerarquía académica actual (Árbol General):");
        System.out.print(sistema.getArbolAcademico().mostrarJerarquia());
        System.out.println("=================================================================");
    }

    public static void main(String[] args) {
        ejecutar();
    }
}
