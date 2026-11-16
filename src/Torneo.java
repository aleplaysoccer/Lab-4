import java.util.ArrayList;
import java.util.Collections;
import java.util.Locale;
import java.util.Objects;

public final class Torneo {
    private final String codigo;
    private final String nombre;
    private final Disciplina disciplina;
    private String estado;
    private double ingresos;
    private double premioEntregado;
    private final ArrayList<Inscripcion> inscripciones;
    private ArrayList<Evento> eventos;

    public Torneo(String codigo, String nombre, Disciplina disciplina) {
        if (codigo == null || codigo.trim().isEmpty() || nombre == null
                || nombre.trim().isEmpty() || disciplina == null) {
            throw new IllegalArgumentException("El codigo, nombre y disciplina son obligatorios.");
        }
        this.codigo = codigo.trim().toUpperCase(Locale.ROOT);
        this.nombre = nombre.trim();
        this.disciplina = disciplina;
        estado = "INSCRIPCION";
        inscripciones = new ArrayList<>();
        eventos = new ArrayList<>();
    }

    public String getCodigo() {
        return codigo;
    }

    public String getNombre() {
        return nombre;
    }

    public Disciplina getDisciplina() {
        return disciplina;
    }

    public String getEstado() {
        return estado;
    }

    public double getIngresos() {
        return ingresos;
    }

    public double getPremioEntregado() {
        return premioEntregado;
    }

    public ArrayList<Inscripcion> getInscripciones() {
        return new ArrayList<>(inscripciones);
    }

    public ArrayList<Evento> getEventos() {
        return new ArrayList<>(eventos);
    }

    public ArrayList<Participante> getParticipantes() {
        ArrayList<Participante> participantes = new ArrayList<>();
        for (Inscripcion inscripcion : inscripciones) {
            participantes.add(inscripcion.getParticipante());
        }
        return participantes;
    }

    public Inscripcion buscarInscripcion(Participante participante) {
        for (Inscripcion inscripcion : inscripciones) {
            if (inscripcion.getParticipante().equals(participante)) {
                return inscripcion;
            }
        }
        throw new IllegalArgumentException("El participante no esta inscrito en este torneo.");
    }

    private void agregarInscripcion(Participante participante, boolean cobrar) {
        if (!estado.equals("INSCRIPCION")) {
            throw new IllegalArgumentException("El torneo ya no acepta inscripciones.");
        }
        if (participante == null) {
            throw new IllegalArgumentException("El participante es obligatorio.");
        }
        if (inscripciones.size() >= disciplina.getMaximo()) {
            throw new IllegalArgumentException("El torneo ya alcanzo su cupo maximo.");
        }
        int mismaEmpresa = 0;
        for (Inscripcion inscripcion : inscripciones) {
            Participante actual = inscripcion.getParticipante();
            if (actual.equals(participante)) {
                throw new IllegalArgumentException("El participante ya esta inscrito.");
            }
            if (actual.getEmpresa().equalsIgnoreCase(participante.getEmpresa())) {
                mismaEmpresa++;
            }
        }
        if (mismaEmpresa >= 2) {
            throw new IllegalArgumentException("La empresa ya tiene dos participantes en este torneo.");
        }
        inscripciones.add(new Inscripcion(participante));
        if (cobrar) {
            ingresos += disciplina.getCuota();
        }
    }

    public void inscribir(Participante participante) {
        agregarInscripcion(participante, true);
    }

    void cargarParticipante(Participante participante) {
        agregarInscripcion(participante, false);
    }

    public void cerrarInscripciones() {
        if (!estado.equals("INSCRIPCION")) {
            throw new IllegalArgumentException("Las inscripciones ya fueron cerradas.");
        }
        if (inscripciones.size() < disciplina.getMinimo()) {
            throw new IllegalArgumentException("Se necesitan al menos "
                    + disciplina.getMinimo() + " participantes.");
        }
        ArrayList<Evento> nuevos = disciplina.generarEventos(this);
        if (nuevos == null || nuevos.isEmpty()) {
            throw new IllegalArgumentException("No se generaron eventos para el torneo.");
        }
        for (Evento evento : nuevos) {
            evento.asignarTorneo(this);
        }
        eventos = nuevos;
        estado = "EN_JUEGO";
    }

    public ArrayList<Inscripcion> obtenerTabla() {
        ArrayList<Inscripcion> tabla = new ArrayList<>(inscripciones);
        Collections.sort(tabla);
        return tabla;
    }

    public Participante getCampeon() {
        if (!estado.equals("FINALIZADO")) {
            throw new IllegalArgumentException("El torneo todavia no tiene campeon.");
        }
        return obtenerTabla().get(0).getParticipante();
    }

    private void validarParticipantes(Evento evento, ArrayList<Resultado> datos) {
        if (datos == null || datos.size() != evento.getParticipantes().size()) {
            throw new IllegalArgumentException("Debe registrar un resultado por cada participante del evento.");
        }
        ArrayList<Participante> registrados = new ArrayList<>();
        for (Resultado resultado : datos) {
            if (resultado == null || !evento.getParticipantes().contains(resultado.getParticipante())
                    || registrados.contains(resultado.getParticipante())) {
                throw new IllegalArgumentException("El resultado tiene participantes incorrectos o repetidos.");
            }
            registrados.add(resultado.getParticipante());
        }
    }

    public void registrarResultado(Evento evento, ArrayList<Resultado> datos) {
        registrarResultado(evento, datos, true);
    }

    void cargarResultado(Evento evento, ArrayList<Resultado> datos) {
        registrarResultado(evento, datos, false);
    }

    private void registrarResultado(Evento evento, ArrayList<Resultado> datos,
                                     boolean entregarPremio) {
        if (!estado.equals("EN_JUEGO") || !eventos.contains(evento)) {
            throw new IllegalArgumentException("El evento no pertenece a un torneo en juego.");
        }
        if (!evento.getEstado().equals("PROGRAMADO")) {
            throw new IllegalArgumentException("El evento debe estar programado y sin resultado previo.");
        }
        validarParticipantes(evento, datos);
        disciplina.validarResultado(datos);
        disciplina.aplicarResultado(this, datos);
        evento.guardarResultado(datos);
        verificarFinalizacion(entregarPremio);
    }

    private void verificarFinalizacion(boolean entregarPremio) {
        for (Evento evento : eventos) {
            if (!evento.estaFinalizado()) {
                return;
            }
        }
        estado = "FINALIZADO";
        if (entregarPremio) {
            premioEntregado = disciplina.getCuota() * inscripciones.size() * 0.5;
        }
    }

    @Override
    public boolean equals(Object objeto) {
        if (this == objeto) {
            return true;
        }
        if (objeto == null || getClass() != objeto.getClass()) {
            return false;
        }
        Torneo otro = (Torneo) objeto;
        return codigo.equals(otro.codigo);
    }

    @Override
    public int hashCode() {
        return Objects.hash(codigo);
    }

    @Override
    public String toString() {
        return codigo + " | " + nombre + " | " + disciplina.getNombre()
                + " | " + estado + " | Inscritos: " + inscripciones.size()
                + "/" + disciplina.getMaximo();
    }
}
