# SIGAC-UTA — Sistema Inteligente de Gestión Académica y Conectividad

**Universidad Técnica de Ambato** — Facultad de Ingeniería en Sistemas, Electrónica e Industrial (FISEI)  
**Carrera:** Ingeniería en Software (Tercer Nivel)  
**Asignatura:** Estructura de Datos — Examen Práctico Integrador  
**Autor:** Cristopher Raúl Oto Cundulle *(Modalidad individual)*  
**Lenguaje:** Java 17+ (sin bibliotecas externas ni colecciones prohibidas)

---

## 1. Descripción del proyecto

**SIGAC-UTA** integra seis módulos en una sola aplicación Java que comparte datos entre estructuras implementadas manualmente desde cero:

| Módulo | Estructura manual | Funcionalidad implementada |
| :--- | :--- | :--- |
| **M1** | `VectorDinamico` y `ListaEnlazada` | CRUD de estudiantes, búsqueda por matrícula, rechazo de duplicados, eliminación segura y comparativa de rendimiento vs ABB. |
| **M2** | `Cola` (FIFO) y `Pila` (LIFO) | Atención FIFO de solicitudes de laboratorio e historial reversible LIFO que deshace altas, bajas, actualizaciones y atenciones. |
| **M3** | `ArbolGeneral` y `ArbolBinarioBusqueda` | Jerarquía `Universidad → Facultad → Carrera → Estudiantes` e índice ABB con inserción, búsqueda, eliminación y recorridos preorden, inorden y posorden. |
| **M4** | `GrafoDinamico` (dirigido ponderado) | Red de laboratorios (≥ 6 equipos), BFS con `Cola` propia, DFS, eliminación de conexiones, eliminación de equipos (incluyendo aristas entrantes) y detección de no alcanzables. |
| **M5** | `GrafoSocial` (no dirigido) | Registro de amistades y recomendación de contactos de segundo grado excluyendo al usuario y amigos directos, ordenado por amigos en común. |
| **M6** | Integración y pruebas | Menú interactivo en `Main.java`, ejecución del Caso Integrador Obligatorio y suite de 21 pruebas (normales, extremas y erróneas). |

---

## 2. Estructura del repositorio

```text
SIGAC-UTA/
├── .gitignore
├── README.md
├── docs/
│   ├── matriz-pruebas.md
│   ├── contribuciones.md
│   ├── guia-defensa-individual.md
│   └── capturas/
│       └── README_CAPTURAS.md
├── scripts/
│   └── subir_commits_github.bat
└── src/
    ├── app/
    │   └── Main.java
    ├── modelo/
    │   ├── EquipoLaboratorio.java
    │   ├── Estudiante.java
    │   ├── OperacionHistorial.java
    │   ├── RecomendacionContacto.java
    │   └── SolicitudLaboratorio.java
    ├── estructuras/
    │   ├── ArbolBinarioBusqueda.java
    │   ├── ArbolGeneral.java
    │   ├── Cola.java
    │   ├── GrafoDinamico.java
    │   ├── GrafoSocial.java
    │   ├── ListaEnlazada.java
    │   ├── NodoArbolGeneral.java
    │   ├── Pila.java
    │   └── VectorDinamico.java
    ├── servicios/
    │   └── SistemaSIGAC.java
    └── pruebas/
        ├── CasoIntegrador.java
        └── SuitePruebas.java
```

---

## 3. Decisiones de diseño para reproducibilidad

Para garantizar que el **Caso Integrador Obligatorio** y las pruebas produzcan resultados deterministas:

1. **Política de eliminación en el ABB (`ArbolBinarioBusqueda.eliminar`)**:
   - Nodo hoja: se elimina directamente.
   - Nodo con un hijo: se reemplaza por su único hijo.
   - Nodo con **dos hijos**: se sustituye por su **sucesor inorden** (el nodo mínimo del subárbol derecho) y luego se elimina ese sucesor en el subárbol derecho. Por ejemplo, al eliminar `30` (con hijos `20` y `40`), `40` ocupa su lugar y el inorden queda `20, 40, 50, 60, 70, 80`.
2. **Dirección de aristas en la red de laboratorios (`GrafoDinamico`)**:
   - Grafo **dirigido y ponderado** (`origen → destino` con peso > 0). Una arista `PC1 → SW1` no implica `SW1 → PC1`.
3. **Orden de inserción de vecinos**:
   - Cada vecino se agrega al **final** de la lista de adyacencia (`agregarAlFinal`), preservando el orden de inserción. Desde `PC1` (insertando primero `SW1` y luego `PC3`), el BFS determinista produce exactamente: `PC1, SW1, PC3, SRV1, PC2`.
4. **Criterio de ordenamiento en recomendaciones (`GrafoSocial`)**:
   - Ordenamiento manual por inserción: primero mayor cantidad de amigos en común (descendente) y, en caso de empate, orden alfabético ascendente por nombre.

---

## 4. Análisis de complejidad temporal (Big-O)

| Módulo | Operación | Mejor caso | Promedio | Peor caso | Justificación |
| :--- | :--- | :---: | :---: | :---: | :--- |
| **M1** | Búsqueda secuencial en `VectorDinamico` / `ListaEnlazada` | $O(1)$ | $O(n)$ | $O(n)$ | Recorre elemento por elemento hasta hallar la matrícula. |
| **M2** | `Cola.encolar` / `Cola.desencolar` (FIFO) | $O(1)$ | $O(1)$ | $O(1)$ | Punteros directos a `frente` y `fin`. |
| **M2** | `Pila.apilar` / `Pila.desapilar` (LIFO) | $O(1)$ | $O(1)$ | $O(1)$ | Inserción y extracción directas en el `tope`. |
| **M3** | `ArbolBinarioBusqueda.buscar` / `insertar` / `eliminar` | $O(1)$ | $O(\log n)$ | $O(n)$ | Depende de la altura $h$ del árbol ($O(\log n)$ balanceado, $O(n)$ degenerado). |
| **M3** | Recorridos ABB (preorden, inorden, posorden) | $O(n)$ | $O(n)$ | $O(n)$ | Visita cada nodo exactamente una vez. |
| **M4** | `GrafoDinamico.bfs` / `dfs` | $O(V + E)$ | $O(V + E)$* | $O(V^2 + V \cdot E)$ | Recorre vértices y aristas; la búsqueda de índice por ID sobre vector sin hash agrega factor $V$. |
| **M4** | `GrafoDinamico.eliminarEquipo` (con aristas entrantes) | $O(V + E)$ | $O(V + E)$ | $O(V + E)$ | Recorre las listas de adyacencia de los demás vértices para borrar aristas entrantes. |
| **M5** | `GrafoSocial.recomendarContactos` | $O(V)$ | $O(d_u \cdot d_{\max} \cdot V + k^2)$ | $O(V^3)$ | Explora amigos de amigos ($d_u \cdot d_{\max}$), valida exclusiones y ordena $k$ candidatos por inserción. |

*\*En la representación teórica con índices directos o tabla hash, BFS y DFS son $O(V + E)$.*

---

## 5. Instrucciones de compilación y ejecución (Windows CMD / Terminal)

Requiere **JDK 17 o superior** (`javac` y `java` en el `PATH`).

### Compilar todo el proyecto
```cmd
mkdir bin
javac -encoding UTF-8 -d bin src\modelo\*.java src\estructuras\*.java src\servicios\*.java src\pruebas\*.java src\app\Main.java
```

### Ejecutar el menú interactivo
```cmd
java -cp bin app.Main
```

### Ejecutar directamente el Caso Integrador Obligatorio
```cmd
java -cp bin app.Main --caso-integrador
```

### Ejecutar directamente la Suite de Pruebas (21 casos)
```cmd
java -cp bin app.Main --pruebas
```

### Ejecutar Caso Integrador + Suite de Pruebas
```cmd
java -cp bin app.Main --todo
```

---

## 6. Documentación adicional

- [Matriz de pruebas y resultados esperados](docs/matriz-pruebas.md)
- [Registro de contribuciones individuales en GitHub](docs/contribuciones.md)
- [Guía de comprobación individual (Sección 8)](docs/guia-defensa-individual.md)
- [Instrucciones para capturas de evidencia](docs/capturas/README_CAPTURAS.md)
