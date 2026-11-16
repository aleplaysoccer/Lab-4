import java.util.ArrayList;

public class Carrera extends Evento {
    private final ArrayList<Participante> participantes;

    public Carrera(ArrayList<Participante> participantes) {
        super();
        if (participantes == null || participantes.size() < 2) {
            throw new IllegalArgumentException("La carrera necesita participantes.");
        }
        this.participantes = new ArrayList<>();
        for (Participante participante : participantes) {
            if (participante == null || this.participantes.contains(participante)) {
                throw new IllegalArgumentException("La carrera tiene participantes invalidos o repetidos.");
            }
            this.participantes.add(participante);
        }
    }

    @Override
    public ArrayList<Participante> getParticipantes() {
        return new ArrayList<>(participantes);
    }
}
