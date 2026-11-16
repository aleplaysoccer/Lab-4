import java.time.LocalDate;
import java.util.ArrayList;

public abstract class Evento implements Programable {
    private LocalDate fecha;
    private int hora;
    private String sede;
    private String estado;
    private ArrayList<Resultado> resultados;
    private Torneo torneo;

    protected Evento() {
        hora = -1;
        estado = "SIN_PROGRAMAR";
        resultados = new ArrayList<>();
    }

    void asignarTorneo(Torneo torneo) {
        if (torneo == null || this.torneo != null) {
            throw new IllegalArgumentException("El evento ya tiene torneo o el torneo no es valido.");
        }
        this.torneo = torneo;
    }

    public Torneo getTorneo() {
        return torneo;
    }

    @Override
    public LocalDate getFecha() {
        return fecha;
    }

    @Override
    public int getHora() {
        return hora;
    }

    @Override
    public String getSede() {
        return sede;
    }

    public String getEstado() {
        return estado;
    }

    public ArrayList<Resultado> getResultados() {
        return new ArrayList<>(resultados);
    }

    public boolean estaFinalizado() {
        return estado.equals("FINALIZADO");
    }

    @Override
    public void programar(LocalDate fecha, int hora, String sede) {
        if (estaFinalizado()) {
            throw new IllegalArgumentException("Un evento finalizado no puede reprogramarse.");
        }
        if (fecha == null || hora < 0 || hora > 23 || sede == null || sede.trim().isEmpty()) {
            throw new IllegalArgumentException("Indique una fecha valida, hora de 0 a 23 y sede.");
        }
        if (torneo == null || !torneo.getEstado().equals("EN_JUEGO")) {
            throw new IllegalArgumentException("El evento debe pertenecer a un torneo en juego.");
        }
        this.fecha = fecha;
        this.hora = hora;
        this.sede = sede.trim();
        estado = "PROGRAMADO";
    }

    void guardarResultado(ArrayList<Resultado> datos) {
        if (!estado.equals("PROGRAMADO") || datos == null || datos.isEmpty()) {
            throw new IllegalArgumentException("Solo un evento programado puede recibir resultado.");
        }
        resultados = new ArrayList<>(datos);
        estado = "FINALIZADO";
    }

    public abstract ArrayList<Participante> getParticipantes();

    @Override
    public String toString() {
        String texto = torneo == null ? "" : torneo.getCodigo() + " | ";
        ArrayList<Participante> participantes = getParticipantes();
        for (int i = 0; i < participantes.size(); i++) {
            if (i > 0) {
                texto += " / ";
            }
            texto += participantes.get(i).getNombre();
        }
        texto += " | " + estado;
        if (fecha != null) {
            texto += " | " + fecha + " " + String.format("%02d:00", hora) + " | " + sede;
        }
        if (estaFinalizado()) {
            for (Resultado resultado : resultados) {
                texto += "\n    " + resultado;
            }
        }
        return texto;
    }
}
