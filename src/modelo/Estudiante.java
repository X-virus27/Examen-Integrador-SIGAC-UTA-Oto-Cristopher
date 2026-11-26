package modelo;

/**
 * Representa a un estudiante registrado en el sistema SIGAC-UTA.
 * La matrícula actúa como identificador único positivo.
 */
public class Estudiante {
    private final int matricula;
    private String nombre;
    private String facultad;
    private String carrera;
    private double promedio;

    public Estudiante(int matricula, String nombre, String facultad, String carrera, double promedio) {
        if (matricula <= 0) {
            throw new IllegalArgumentException("La matrícula debe ser un número entero positivo.");
        }
        if (nombre == null || nombre.trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre del estudiante no puede ser nulo ni vacío.");
        }
        if (facultad == null || facultad.trim().isEmpty()) {
            throw new IllegalArgumentException("La facultad no puede ser nula ni vacía.");
        }
        if (carrera == null || carrera.trim().isEmpty()) {
            throw new IllegalArgumentException("La carrera no puede ser nula ni vacía.");
        }
        if (promedio < 0.0 || promedio > 10.0) {
            throw new IllegalArgumentException("El promedio debe estar entre 0.0 y 10.0.");
        }
        this.matricula = matricula;
        this.nombre = nombre.trim();
        this.facultad = facultad.trim();
        this.carrera = carrera.trim();
        this.promedio = promedio;
    }

    public Estudiante(int matricula, String nombre, String carrera) {
        this(matricula, nombre, "FISEI", carrera, 8.5);
    }

    public int getMatricula() {
        return matricula;
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

    public String getFacultad() {
        return facultad;
    }

    public void setFacultad(String facultad) {
        if (facultad == null || facultad.trim().isEmpty()) {
            throw new IllegalArgumentException("La facultad no puede ser nula ni vacía.");
        }
        this.facultad = facultad.trim();
    }

    public String getCarrera() {
        return carrera;
    }

    public void setCarrera(String carrera) {
        if (carrera == null || carrera.trim().isEmpty()) {
            throw new IllegalArgumentException("La carrera no puede ser nula ni vacía.");
        }
        this.carrera = carrera.trim();
    }

    public double getPromedio() {
        return promedio;
    }

    public void setPromedio(double promedio) {
        if (promedio < 0.0 || promedio > 10.0) {
            throw new IllegalArgumentException("El promedio debe estar entre 0.0 y 10.0.");
        }
        this.promedio = promedio;
    }

    /**
     * Crea una copia independiente del estudiante (útil para respaldos de deshacer LIFO).
     */
    public Estudiante copiar() {
        return new Estudiante(this.matricula, this.nombre, this.facultad, this.carrera, this.promedio);
    }

    @Override
    public String toString() {
        return String.format("[%d] %s | %s - %s (Prom: %.2f)",
                matricula, nombre, facultad, carrera, promedio);
    }
}
