package modelo;

/**
 * Representa una operación reversible registrada en la pila LIFO de historial (M2).
 */
public class OperacionHistorial {
    public enum TipoOperacion {
        REGISTRAR_ESTUDIANTE,
        ELIMINAR_ESTUDIANTE,
        ACTUALIZAR_ESTUDIANTE,
        ATENDER_SOLICITUD
    }

    private final TipoOperacion tipo;
    private final String descripcion;
    private final Estudiante estudianteAfectado;
    private final Estudiante estadoAnterior;
    private final SolicitudLaboratorio solicitudAfectada;

    public OperacionHistorial(TipoOperacion tipo, String descripcion,
                              Estudiante estudianteAfectado, Estudiante estadoAnterior,
                              SolicitudLaboratorio solicitudAfectada) {
        if (tipo == null) {
            throw new IllegalArgumentException("El tipo de operación no puede ser nulo.");
        }
        if (descripcion == null || descripcion.trim().isEmpty()) {
            throw new IllegalArgumentException("La descripción de la operación no puede ser nula ni vacía.");
        }
        this.tipo = tipo;
        this.descripcion = descripcion.trim();
        this.estudianteAfectado = estudianteAfectado;
        this.estadoAnterior = estadoAnterior;
        this.solicitudAfectada = solicitudAfectada;
    }

    public TipoOperacion getTipo() {
        return tipo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public Estudiante getEstudianteAfectado() {
        return estudianteAfectado;
    }

    public Estudiante getEstadoAnterior() {
        return estadoAnterior;
    }

    public SolicitudLaboratorio getSolicitudAfectada() {
        return solicitudAfectada;
    }

    @Override
    public String toString() {
        return String.format("[%s] %s", tipo, descripcion);
    }
}
