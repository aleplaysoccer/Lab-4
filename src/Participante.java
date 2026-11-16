import java.util.Locale;
import java.util.Objects;

public final class Participante {
    private final String codigo;
    private final String nombre;
    private final String empresa;

    public Participante(String codigo, String nombre, String empresa) {
        if (codigo == null || codigo.trim().isEmpty()
                || nombre == null || nombre.trim().isEmpty()
                || empresa == null || empresa.trim().isEmpty()) {
            throw new IllegalArgumentException("El codigo, nombre y empresa son obligatorios.");
        }
        this.codigo = codigo.trim().toUpperCase(Locale.ROOT);
        this.nombre = nombre.trim();
        this.empresa = empresa.trim();
    }

    public String getCodigo() {
        return codigo;
    }

    public String getNombre() {
        return nombre;
    }

    public String getEmpresa() {
        return empresa;
    }

    @Override
    public boolean equals(Object objeto) {
        if (this == objeto) {
            return true;
        }
        if (objeto == null || getClass() != objeto.getClass()) {
            return false;
        }
        Participante otro = (Participante) objeto;
        return codigo.equals(otro.codigo);
    }

    @Override
    public int hashCode() {
        return Objects.hash(codigo);
    }

    @Override
    public String toString() {
        return codigo + " | " + nombre + " | Empresa: " + empresa;
    }
}
