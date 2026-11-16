import java.util.ArrayList;

public class Encuentro extends Evento {
    private final Participante local;
    private final Participante visitante;

    public Encuentro(Participante local, Participante visitante) {
        super();
        if (local == null || visitante == null || local.equals(visitante)) {
            throw new IllegalArgumentException("El encuentro necesita dos participantes diferentes.");
        }
        this.local = local;
        this.visitante = visitante;
    }

    @Override
    public ArrayList<Participante> getParticipantes() {
        ArrayList<Participante> participantes = new ArrayList<>();
        participantes.add(local);
        participantes.add(visitante);
        return participantes;
    }
}
