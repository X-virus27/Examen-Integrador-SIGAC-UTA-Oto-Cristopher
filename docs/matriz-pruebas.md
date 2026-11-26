# Matriz de Pruebas — SIGAC-UTA

**Autor:** Cristopher Raúl Oto Cundulle  
**Clase ejecutora:** `pruebas.SuitePruebas` y `pruebas.CasoIntegrador`

---

## 1. Caso Integrador Obligatorio (`CasoIntegrador.java`)

**Configuración reproducible:**
- Estudiantes: `50, 30, 70, 20, 40, 60, 80`
- Política de eliminación ABB (2 hijos): **sucesor inorden** (mínimo del subárbol derecho)
- Grafo de laboratorios: **dirigido ponderado** (`PC1 → SW1`, `SW1 → SRV1`, `SRV1 → PC2`, `PC1 → PC3`, `PC4` aislado)
- Orden de inserción de vecinos: **al final** de la lista de adyacencia (desde `PC1`: primero `SW1`, luego `PC3`)
- Red social (no dirigida): `Ana–Beto`, `Ana–Carla`, `Beto–Diego`, `Carla–Diego`, `Carla–Elena`

| Paso | Operación ejecutada | Resultado esperado | Evidencia |
| :---: | :--- | :--- | :--- |
| **1** | Mostrar inorden del ABB tras insertar `50, 30, 70, 20, 40, 60, 80` | `20, 30, 40, 50, 60, 70, 80` y `esABBValido() == true` | `docs/capturas/01_caso_integrador.png` |
| **2** | Eliminar matrícula `30` (dos hijos: `20` y `40`) | Reemplazo por sucesor `40`; nuevo inorden: `20, 40, 50, 60, 70, 80`; `esABBValido() == true` | `docs/capturas/01_caso_integrador.png` |
| **3** | Procesar 3 solicitudes FIFO y recuperar 2 operaciones LIFO | Se atienden en orden `#1, #2, #3`; luego se revierten `#3` y `#2` (quedan 2 pendientes en cola) | `docs/capturas/01_caso_integrador.png` |
| **4** | Ejecutar BFS desde `PC1` y detectar no alcanzables | BFS: `PC1, SW1, PC3, SRV1, PC2`; No alcanzables: `PC4` | `docs/capturas/01_caso_integrador.png` |
| **5** | Recomendar contactos de segundo grado para `Ana` | 1. `Diego (2 amigos en común: Beto, Carla)` · 2. `Elena (1 amigo en común: Carla)` | `docs/capturas/01_caso_integrador.png` |
| **6** | Eliminar arista `SW1 → SRV1` y recalcular conectividad desde `PC1` | Nuevo BFS: `PC1, SW1, PC3`; No alcanzables: `SRV1, PC2, PC4` | `docs/capturas/01_caso_integrador.png` |

---

## 2. Suite de Pruebas Unitarias y de Integración (`SuitePruebas.java`)

| ID | Módulo | Tipo | Caso de prueba | Entrada / Acción | Resultado esperado |
| :---: | :---: | :---: | :--- | :--- | :--- |
| **T01** | M1 | Normal | Registro y búsqueda por matrícula | Registrar `101` y `102`; buscar en Vector y Lista | Ambos encontrados, `cantidad == 2` |
| **T02** | M1 | Normal | Eliminación segura | Eliminar matrícula `101` | Retorna `true`, búsqueda retorna `null`, `cantidad == 1` |
| **T03** | M1 | Errónea | Rechazo de matrícula duplicada | Registrar nuevamente `102` | Lanza `IllegalArgumentException` |
| **T04** | M1 | Errónea | Rechazo de referencia nula | `registrarEstudiante(null)` | Lanza `IllegalArgumentException` |
| **T05** | M1 | Extrema | Redimensionamiento dinámico y baja inexistente | Insertar 20 registros (> capacidad inicial 8) y eliminar `9999` | `cantidad == 21`, `eliminar(9999) == false` |
| **T06** | M2 | Normal | Atención FIFO estricta | Encolar 2 solicitudes y atender ambas | Salen en el mismo orden en que ingresaron |
| **T07** | M2 | Normal | Deshacer LIFO de atenciones | Ejecutar `deshacerUltimaOperacionLIFO()` dos veces | Revierte en orden inverso y restaura 2 solicitudes en cola |
| **T08** | M2 | Errónea | Desencolar en cola vacía | `colaVacia.desencolar()` | Lanza `IllegalStateException` |
| **T09** | M2 | Errónea | Desapilar en pila vacía | `pilaVacia.desapilar()` | Lanza `IllegalStateException` |
| **T10** | M2 | Errónea | Solicitud de matrícula inexistente | Registrar solicitud para matrícula `999` | Lanza `IllegalArgumentException` |
| **T11** | M3 | Normal | Recorrido inorden del ABB | Insertar `50, 30, 70, 20, 40, 60, 80` | `20, 30, 40, 50, 60, 70, 80` y ABB válido |
| **T12** | M3 | Extrema | Eliminación de nodo interno (`30`) y raíz (`50`) con 2 hijos | `abb.eliminar(30)` y `abb.eliminar(50)` | Inorden `20, 40, 60, 70, 80` y `esABBValido() == true` |
| **T13** | M3 | Extrema | ABB degenerado (inserción ascendente `1..10`) | Insertar `1` a `10` y buscar `10` | Encontrado, `tamanio == 10` y `esABBValido() == true` |
| **T14** | M3 | Errónea | Duplicado en ABB y baja en ABB vacío | Insertar `40` repetido; `abbVacio.eliminar(10)` | Duplicado lanza excepción; vacío retorna `false` |
| **T15** | M4 | Normal | BFS determinista y nodo aislado | BFS desde `PC1` en red base | `PC1, SW1, PC3, SRV1, PC2`; no alcanzable: `PC4` |
| **T16** | M4 | Normal | Eliminación de arista y reconectividad | Eliminar `SW1 → SRV1` y detectar no alcanzables | `SRV1`, `PC2` y `PC4` quedan no alcanzables |
| **T17** | M4 | Extrema | Eliminación de vértice con aristas entrantes | `eliminarEquipo("SW1")` y ejecutar BFS desde `PC1` | Arista `PC1 → SW1` eliminada; BFS: `PC1, PC3` |
| **T18** | M4 | Errónea | Peso negativo y BFS en grafo vacío | Arista con peso `-1.0`; `vacio.bfs("PC1")` | Lanza `IllegalArgumentException` e `IllegalStateException` |
| **T19** | M5 | Normal | Recomendaciones de segundo grado ordenadas | `recomendarContactos("Ana")` | `1. Diego (2)`, `2. Elena (1)`; excluye `Ana`, `Beto` y `Carla` |
| **T20** | M5 | Extrema | Usuario aislado sin amigos | `recomendarContactos("Aislado")` | Retorna vector vacío sin errores |
| **T21** | M5 | Errónea | Autoamistad y usuario inexistente | `agregarAmistad("Ana", "Ana")`; `recomendar("NoExiste")` | Ambos lanzan `IllegalArgumentException` |

---

## 3. Capturas de evidencia

Una vez compilado el proyecto en tu equipo con JDK 17+, guarda las capturas de consola en `docs/capturas/` siguiendo `docs/capturas/README_CAPTURAS.md`:

- `docs/capturas/01_caso_integrador.png` — Salida completa de `java -cp bin app.Main --caso-integrador`
- `docs/capturas/02_suite_pruebas.png` — Salida completa de `java -cp bin app.Main --pruebas` (21/21 PASS)
- `docs/capturas/03_menu_interactivo.png` — Demostración del menú interactivo `java -cp bin app.Main`
