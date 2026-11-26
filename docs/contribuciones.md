# Registro de Contribuciones — SIGAC-UTA

**Modalidad:** Individual  
**Estudiante:** Cristopher Raúl Oto Cundulle  
**Carrera:** Ingeniería en Software — Tercer Nivel (FISEI, Universidad Técnica de Ambato)

---

## Resumen de aportaciones por módulo y commit

| Orden | Commit sugerido | Módulos / Archivos | Descripción verificable |
| :---: | :--- | :--- | :--- |
| **1** | `feat(estructura): configurar proyecto base y modelos de dominio` | `.gitignore`, `src/modelo/*.java` | Estructura inicial del proyecto y modelos con validaciones de entrada (`Estudiante`, `SolicitudLaboratorio`, `OperacionHistorial`, `EquipoLaboratorio`, `RecomendacionContacto`). |
| **2** | `feat(M1): implementar VectorDinamico y ListaEnlazada manuales` | `src/estructuras/VectorDinamico.java`, `src/estructuras/ListaEnlazada.java` | Estructuras lineales manuales sin `java.util` para almacenamiento, redimensionamiento dinámico y búsqueda secuencial. |
| **3** | `feat(M2): implementar Pila LIFO y Cola FIFO enlazadas` | `src/estructuras/Pila.java`, `src/estructuras/Cola.java` | Pila LIFO para historial reversible y Cola FIFO para atención de solicitudes y soporte a BFS. |
| **4** | `feat(M3): implementar ArbolGeneral y ArbolBinarioBusqueda` | `src/estructuras/NodoArbolGeneral.java`, `src/estructuras/ArbolGeneral.java`, `src/estructuras/ArbolBinarioBusqueda.java` | Jerarquía `Universidad → Facultad → Carrera → Estudiantes` e índice ABB con eliminación por sucesor inorden y recorridos preorden, inorden y posorden. |
| **5** | `feat(M4-M5): implementar GrafoDinamico y GrafoSocial` | `src/estructuras/GrafoDinamico.java`, `src/estructuras/GrafoSocial.java` | Grafo dirigido ponderado con BFS, DFS, detección de no alcanzables y limpieza de aristas entrantes; grafo social no dirigido con recomendaciones de 2.º grado. |
| **6** | `feat(M6): integrar SistemaSIGAC, menu interactivo y suite de pruebas` | `src/servicios/SistemaSIGAC.java`, `src/app/Main.java`, `src/pruebas/CasoIntegrador.java`, `src/pruebas/SuitePruebas.java` | Integración compartida M1–M5, menú interactivo en consola, caso integrador obligatorio (6 pasos) y 21 pruebas normales, extremas y erróneas. |
| **7** | `docs: agregar README, matriz de pruebas, contribuciones y capturas` | `README.md`, `docs/matriz-pruebas.md`, `docs/contribuciones.md`, `docs/capturas/*` | Documentación técnica, análisis de complejidad Big-O, matriz de pruebas y evidencia de ejecución. |

---

