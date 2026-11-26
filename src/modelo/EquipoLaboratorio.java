package modelo;

/**
 * Representa un equipo de red en el laboratorio de la FISEI (M4).
 */
public class EquipoLaboratorio {
    private final String id;
    private final String tipo;
    private final String ubicacion;

    public EquipoLaboratorio(String id, String tipo, String ubicacion) {
        if (id == null || id.trim().isEmpty()) {
            throw new IllegalArgumentException("El identificador del equipo no puede ser nulo ni vacío.");
        }
        if (tipo == null || tipo.trim().isEmpty()) {
            throw new IllegalArgumentException("El tipo de equipo no puede ser nulo ni vacío.");
        }
        this.id = id.trim();
        this.tipo = tipo.trim();
        this.ubicacion = (ubicacion == null || ubicacion.trim().isEmpty()) ? "Lab FISEI" : ubicacion.trim();
    }

    public String getId() {
        return id;
    }

    public String getTipo() {
        return tipo;
    }

    public String getUbicacion() {
        return ubicacion;
    }

    @Override
    public String toString() {
        return String.format("%s (%s - %s)", id, tipo, ubicacion);
    }
}
