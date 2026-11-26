package modelo;

/**
 * Representa una solicitud de laboratorio atendida en orden FIFO (M2).
 */
public class SolicitudLaboratorio {
    private final int idSolicitud;
    private final int matriculaEstudiante;
    private final String laboratorio;
    private final String descripcion;

    public SolicitudLaboratorio(int idSolicitud, int matriculaEstudiante, String laboratorio, String descripcion) {
        if (idSolicitud <= 0) {
            throw new IllegalArgumentException("El ID de solicitud debe ser positivo.");
        }
        if (matriculaEstudiante <= 0) {
            throw new IllegalArgumentException("La matrícula del solicitante debe ser positiva.");
        }
        if (laboratorio == null || laboratorio.trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre del laboratorio no puede ser nulo ni vacío.");
        }
        if (descripcion == null || descripcion.trim().isEmpty()) {
            throw new IllegalArgumentException("La descripción de la solicitud no puede ser nula ni vacía.");
        }
        this.idSolicitud = idSolicitud;
        this.matriculaEstudiante = matriculaEstudiante;
        this.laboratorio = laboratorio.trim();
        this.descripcion = descripcion.trim();
    }

    public int getIdSolicitud() {
        return idSolicitud;
    }

    public int getMatriculaEstudiante() {
        return matriculaEstudiante;
    }

    public String getLaboratorio() {
        return laboratorio;
    }

    public String getDescripcion() {
        return descripcion;
    }

    @Override
    public String toString() {
        return String.format("Solicitud #%d [Matrícula: %d | Lab: %s] - %s",
                idSolicitud, matriculaEstudiante, laboratorio, descripcion);
    }
}
