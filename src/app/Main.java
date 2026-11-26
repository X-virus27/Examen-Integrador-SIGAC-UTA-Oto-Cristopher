package app;

import java.util.Scanner;
import estructuras.VectorDinamico;
import modelo.EquipoLaboratorio;
import modelo.Estudiante;
import modelo.OperacionHistorial;
import modelo.RecomendacionContacto;
import modelo.SolicitudLaboratorio;
import pruebas.CasoIntegrador;
import pruebas.SuitePruebas;
import servicios.SistemaSIGAC;

/**
 * Punto de entrada del Sistema Inteligente de Gestión Académica y Conectividad (SIGAC-UTA).
 * Soporta menú interactivo en consola y banderas CLI (--caso-integrador, --pruebas).
 */
public class Main {

    public static void main(String[] args) {
        if (args != null && args.length > 0) {
            if ("--caso-integrador".equalsIgnoreCase(args[0])) {
                CasoIntegrador.ejecutar();
                return;
            }
            if ("--pruebas".equalsIgnoreCase(args[0])) {
                SuitePruebas.ejecutarTodas();
                return;
            }
            if ("--todo".equalsIgnoreCase(args[0])) {
                CasoIntegrador.ejecutar();
                System.out.println();
                SuitePruebas.ejecutarTodas();
                return;
            }
        }

        SistemaSIGAC sistema = new SistemaSIGAC();
        Scanner sc = new Scanner(System.in);
        boolean ejecutando = true;

        System.out.println("=================================================================");
        System.out.println("  UNIVERSIDAD TÉCNICA DE AMBATO - FISEI | SIGAC-UTA (v1.0)");
        System.out.println("  Sistema Inteligente de Gestión Académica y Conectividad");
        System.out.println("=================================================================");

        while (ejecutando) {
            mostrarMenuPrincipal();
            int opcion = leerEntero(sc, "Seleccione una opción: ");
            try {
                switch (opcion) {
                    case 1:
                        menuEstudiantes(sc, sistema);
                        break;
                    case 2:
                        menuSolicitudesHistorial(sc, sistema);
                        break;
                    case 3:
                        menuOrganizacionAcademica(sc, sistema);
                        break;
                    case 4:
                        menuRedLaboratorios(sc, sistema);
                        break;
                    case 5:
                        menuRedSocial(sc, sistema);
                        break;
                    case 6:
                        CasoIntegrador.ejecutar();
                        break;
                    case 7:
                        SuitePruebas.ejecutarTodas();
                        break;
                    case 8:
                        sistema.precargarDatosCasoIntegrador();
                        System.out.println("Datos base del caso integrador precargados en la sesión actual.");
                        break;
                    case 0:
                        ejecutando = false;
                        System.out.println("Saliendo de SIGAC-UTA. ¡Hasta pronto!");
                        break;
                    default:
                        System.out.println("Opción no válida. Intente nuevamente.");
                }
            } catch (IllegalArgumentException | IllegalStateException ex) {
                System.out.println("[VALIDACIÓN] " + ex.getMessage());
            } catch (Exception ex) {
                System.out.println("[ERROR] Ocurrió un error inesperado: " + ex.getMessage());
            }
        }
        sc.close();
    }

    private static void mostrarMenuPrincipal() {
        System.out.println("\n----------------------- MENÚ PRINCIPAL -----------------------");
        System.out.println("1. [M1] Gestión de estudiantes (Vector, Lista y comparación ABB)");
        System.out.println("2. [M2] Solicitudes de laboratorio (Cola FIFO) e Historial (Pila LIFO)");
        System.out.println("3. [M3] Organización académica (Árbol General) e Índice ABB");
        System.out.println("4. [M4] Red de laboratorios (Grafo dirigido, BFS, DFS, conectividad)");
        System.out.println("5. [M5] Red social académica (Amistades y recomendaciones 2.º grado)");
        System.out.println("6. [M6] Ejecutar Caso Integrador Obligatorio (pasos 1 a 6)");
        System.out.println("7. [M6] Ejecutar Suite de Pruebas (normales, extremas, erróneas)");
        System.out.println("8. Precargar datos de prueba en la sesión interactiva");
        System.out.println("0. Salir");
    }

    private static void menuEstudiantes(Scanner sc, SistemaSIGAC sistema) {
        System.out.println("\n--- M1: GESTIÓN DE ESTUDIANTES ---");
        System.out.println("1. Registrar estudiante");
        System.out.println("2. Buscar por matrícula y comparar Secuencial vs ABB");
        System.out.println("3. Actualizar estudiante");
        System.out.println("4. Eliminar estudiante");
        System.out.println("5. Listar todos los estudiantes");
        int op = leerEntero(sc, "Opción M1: ");
        switch (op) {
            case 1:
                int mat = leerEntero(sc, "Matrícula (entero positivo): ");
                String nom = leerTexto(sc, "Nombre completo: ");
                String fac = leerTexto(sc, "Facultad (ej. FISEI): ");
                String car = leerTexto(sc, "Carrera (ej. Ingeniería en Software): ");
                double prom = leerDouble(sc, "Promedio (0.0 - 10.0): ");
                sistema.registrarEstudiante(new Estudiante(mat, nom, fac, car, prom));
                System.out.println("Estudiante registrado correctamente.");
                break;
            case 2:
                int matBuscar = leerEntero(sc, "Matrícula a buscar: ");
                System.out.println(sistema.compararBusquedas(matBuscar));
                break;
            case 3:
                int matAct = leerEntero(sc, "Matrícula a actualizar: ");
                String nNom = leerTexto(sc, "Nuevo nombre: ");
                String nCar = leerTexto(sc, "Nueva carrera: ");
                double nProm = leerDouble(sc, "Nuevo promedio: ");
                sistema.actualizarEstudiante(matAct, nNom, nCar, nProm);
                System.out.println("Estudiante actualizado.");
                break;
            case 4:
                int matElim = leerEntero(sc, "Matrícula a eliminar: ");
                boolean ok = sistema.eliminarEstudiante(matElim);
                System.out.println(ok ? "Estudiante eliminado de forma segura." : "No se encontró esa matrícula.");
                break;
            case 5:
                VectorDinamico<Estudiante> lista = sistema.listarEstudiantesVector();
                if (lista.estaVacio()) {
                    System.out.println("(No hay estudiantes registrados)");
                } else {
                    for (int i = 0; i < lista.tamanio(); i++) {
                        System.out.println("  " + lista.obtener(i));
                    }
                }
                break;
            default:
                System.out.println("Opción M1 no válida.");
        }
    }

    private static void menuSolicitudesHistorial(Scanner sc, SistemaSIGAC sistema) {
        System.out.println("\n--- M2: ATENCIÓN FIFO E HISTORIAL LIFO ---");
        System.out.println("1. Encolar solicitud de laboratorio (FIFO)");
        System.out.println("2. Atender siguiente solicitud (FIFO)");
        System.out.println("3. Ver solicitudes pendientes");
        System.out.println("4. Deshacer última operación reversible (LIFO)");
        System.out.println("5. Ver historial de operaciones (LIFO)");
        int op = leerEntero(sc, "Opción M2: ");
        switch (op) {
            case 1:
                int mat = leerEntero(sc, "Matrícula del estudiante: ");
                String lab = leerTexto(sc, "Laboratorio: ");
                String desc = leerTexto(sc, "Descripción: ");
                SolicitudLaboratorio sol = sistema.registrarSolicitudLaboratorio(mat, lab, desc);
                System.out.println("Encolada: " + sol);
                break;
            case 2:
                SolicitudLaboratorio atendida = sistema.atenderSiguienteSolicitudFIFO();
                System.out.println("Atendida (FIFO): " + atendida);
                break;
            case 3:
                VectorDinamico<SolicitudLaboratorio> pendientes = sistema.getColaSolicitudes().listarElementos();
                if (pendientes.estaVacio()) {
                    System.out.println("(Cola FIFO vacía)");
                } else {
                    for (int i = 0; i < pendientes.tamanio(); i++) {
                        System.out.println("  " + (i + 1) + ". " + pendientes.obtener(i));
                    }
                }
                break;
            case 4:
                OperacionHistorial revertida = sistema.deshacerUltimaOperacionLIFO();
                System.out.println("Operación revertida (LIFO): " + revertida);
                break;
            case 5:
                VectorDinamico<OperacionHistorial> hist = sistema.getHistorialOperaciones().listarElementos();
                if (hist.estaVacio()) {
                    System.out.println("(Pila LIFO vacía)");
                } else {
                    for (int i = 0; i < hist.tamanio(); i++) {
                        System.out.println("  [Tope-" + i + "] " + hist.obtener(i));
                    }
                }
                break;
            default:
                System.out.println("Opción M2 no válida.");
        }
    }

    private static void menuOrganizacionAcademica(Scanner sc, SistemaSIGAC sistema) {
        System.out.println("\n--- M3: ORGANIZACIÓN ACADÉMICA Y ABB ---");
        System.out.println("1. Mostrar árbol general (Universidad -> Facultad -> Carrera -> Estudiantes)");
        System.out.println("2. Mostrar recorridos del ABB (Preorden, Inorden, Posorden)");
        int op = leerEntero(sc, "Opción M3: ");
        if (op == 1) {
            System.out.print(sistema.getArbolAcademico().mostrarJerarquia());
        } else if (op == 2) {
            if (sistema.getAbbEstudiantes().estaVacio()) {
                System.out.println("(ABB vacío)");
                return;
            }
            System.out.println("Preorden : " + SistemaSIGAC.formatearMatriculas(sistema.getAbbEstudiantes().recorridoPreorden()));
            System.out.println("Inorden  : " + SistemaSIGAC.formatearMatriculas(sistema.getAbbEstudiantes().recorridoInorden()));
            System.out.println("Posorden : " + SistemaSIGAC.formatearMatriculas(sistema.getAbbEstudiantes().recorridoPosorden()));
            System.out.println("Propiedad ABB válida: " + sistema.getAbbEstudiantes().esABBValido());
        } else {
            System.out.println("Opción M3 no válida.");
        }
    }

    private static void menuRedLaboratorios(Scanner sc, SistemaSIGAC sistema) {
        System.out.println("\n--- M4: RED DE LABORATORIOS (GRAFO DIRIGIDO PONDERADO) ---");
        System.out.println("1. Agregar equipo");
        System.out.println("2. Agregar conexión dirigida ponderada");
        System.out.println("3. Eliminar conexión (arista)");
        System.out.println("4. Eliminar equipo (y aristas entrantes)");
        System.out.println("5. Ejecutar BFS, DFS y detectar no alcanzables");
        System.out.println("6. Mostrar red completa");
        int op = leerEntero(sc, "Opción M4: ");
        switch (op) {
            case 1:
                String id = leerTexto(sc, "ID del equipo (ej. PC5): ");
                String tipo = leerTexto(sc, "Tipo (PC/Switch/Servidor): ");
                String ubi = leerTexto(sc, "Ubicación: ");
                sistema.getRedLaboratorios().agregarEquipo(new EquipoLaboratorio(id, tipo, ubi));
                System.out.println("Equipo agregado.");
                break;
            case 2:
                String ori = leerTexto(sc, "Equipo origen: ");
                String des = leerTexto(sc, "Equipo destino: ");
                double peso = leerDouble(sc, "Peso (latencia/costo > 0): ");
                sistema.getRedLaboratorios().agregarConexion(ori, des, peso);
                System.out.println("Conexión agregada.");
                break;
            case 3:
                String oE = leerTexto(sc, "Origen de la conexión: ");
                String dE = leerTexto(sc, "Destino de la conexión: ");
                boolean elimA = sistema.getRedLaboratorios().eliminarConexion(oE, dE);
                System.out.println(elimA ? "Conexión eliminada." : "No existía esa conexión.");
                break;
            case 4:
                String idElim = leerTexto(sc, "ID del equipo a eliminar: ");
                boolean elimV = sistema.getRedLaboratorios().eliminarEquipo(idElim);
                System.out.println(elimV ? "Equipo y aristas entrantes eliminados." : "Equipo no encontrado.");
                break;
            case 5:
                String inicio = leerTexto(sc, "Equipo de inicio (ej. PC1): ");
                System.out.println("BFS            : " + SistemaSIGAC.formatearCadenas(sistema.getRedLaboratorios().bfs(inicio)));
                System.out.println("DFS            : " + SistemaSIGAC.formatearCadenas(sistema.getRedLaboratorios().dfs(inicio)));
                System.out.println("No alcanzables : " + SistemaSIGAC.formatearCadenas(sistema.getRedLaboratorios().detectarNoAlcanzables(inicio)));
                break;
            case 6:
                System.out.print(sistema.getRedLaboratorios().mostrarRed());
                break;
            default:
                System.out.println("Opción M4 no válida.");
        }
    }

    private static void menuRedSocial(Scanner sc, SistemaSIGAC sistema) {
        System.out.println("\n--- M5: RED SOCIAL ACADÉMICA ---");
        System.out.println("1. Agregar usuario");
        System.out.println("2. Registrar amistad");
        System.out.println("3. Recomendar contactos de segundo grado");
        System.out.println("4. Mostrar red social");
        int op = leerEntero(sc, "Opción M5: ");
        switch (op) {
            case 1:
                String u = leerTexto(sc, "Nombre de usuario: ");
                sistema.getRedSocial().agregarUsuario(u);
                System.out.println("Usuario registrado.");
                break;
            case 2:
                String u1 = leerTexto(sc, "Usuario 1: ");
                String u2 = leerTexto(sc, "Usuario 2: ");
                sistema.getRedSocial().agregarAmistad(u1, u2);
                System.out.println("Amistad registrada.");
                break;
            case 3:
                String objetivo = leerTexto(sc, "Usuario para recomendar contactos: ");
                VectorDinamico<RecomendacionContacto> recs = sistema.getRedSocial().recomendarContactos(objetivo);
                if (recs.estaVacio()) {
                    System.out.println("No hay recomendaciones de segundo grado para " + objetivo + ".");
                } else {
                    for (int i = 0; i < recs.tamanio(); i++) {
                        System.out.println("  " + (i + 1) + ". " + recs.obtener(i));
                    }
                }
                break;
            case 4:
                System.out.print(sistema.getRedSocial().mostrarRedSocial());
                break;
            default:
                System.out.println("Opción M5 no válida.");
        }
    }

    private static int leerEntero(Scanner sc, String mensaje) {
        while (true) {
            System.out.print(mensaje);
            String linea = sc.nextLine().trim();
            try {
                return Integer.parseInt(linea);
            } catch (NumberFormatException ex) {
                System.out.println("Ingrese un número entero válido.");
            }
        }
    }

    private static double leerDouble(Scanner sc, String mensaje) {
        while (true) {
            System.out.print(mensaje);
            String linea = sc.nextLine().trim().replace(',', '.');
            try {
                return Double.parseDouble(linea);
            } catch (NumberFormatException ex) {
                System.out.println("Ingrese un número decimal válido.");
            }
        }
    }

    private static String leerTexto(Scanner sc, String mensaje) {
        System.out.print(mensaje);
        return sc.nextLine().trim();
    }
}
