package modelo;

/**
 * Representa una recomendación de contacto de segundo grado en el grafo social (M5).
 */
public class RecomendacionContacto {
    private final String usuario;
    private final int amigosEnComun;
    private final String detalleAmigosComunes;

    public RecomendacionContacto(String usuario, int amigosEnComun, String detalleAmigosComunes) {
        if (usuario == null || usuario.trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre del usuario recomendado no puede ser nulo ni vacío.");
        }
        if (amigosEnComun <= 0) {
            throw new IllegalArgumentException("El número de amigos en común debe ser positivo.");
        }
        this.usuario = usuario.trim();
        this.amigosEnComun = amigosEnComun;
        this.detalleAmigosComunes = (detalleAmigosComunes == null) ? "" : detalleAmigosComunes;
    }

    public String getUsuario() {
        return usuario;
    }

    public int getAmigosEnComun() {
        return amigosEnComun;
    }

    public String getDetalleAmigosComunes() {
        return detalleAmigosComunes;
    }

    @Override
    public String toString() {
        String sufijo = (amigosEnComun == 1) ? "amigo en común" : "amigos en común";
        if (detalleAmigosComunes.isEmpty()) {
            return String.format("%s (%d %s)", usuario, amigosEnComun, sufijo);
        }
        return String.format("%s (%d %s: %s)", usuario, amigosEnComun, sufijo, detalleAmigosComunes);
    }
}
