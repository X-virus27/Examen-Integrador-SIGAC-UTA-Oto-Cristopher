# Capturas de Evidencia de Ejecución

Después de descomprimir el proyecto en tu carpeta local de GitHub, compila y ejecuta los siguientes comandos en Windows CMD y guarda las 3 capturas de pantalla en esta carpeta (`docs/capturas/`):

1. **Compilar:**
   ```cmd
   mkdir bin
   javac -encoding UTF-8 -d bin src\modelo\*.java src\estructuras\*.java src\servicios\*.java src\pruebas\*.java src\app\Main.java
   ```

2. **Captura 1 (`01_caso_integrador.png`):**
   ```cmd
   java -cp bin app.Main --caso-integrador
   ```

3. **Captura 2 (`02_suite_pruebas.png`):**
   ```cmd
   java -cp bin app.Main --pruebas
   ```

4. **Captura 3 (`03_menu_interactivo.png`):**
   ```cmd
   java -cp bin app.Main
   ```
   *(Muestra el menú principal y alguna operación interactiva, por ejemplo opción 8 para precargar y opción 3 para ver el árbol general y recorridos ABB).*
