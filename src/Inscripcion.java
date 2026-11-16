public class Inscripcion implements Comparable<Inscripcion> {
    private final Participante participante;
    private int puntos;
    private int diferencia;
    private int victorias;

    public Inscripcion(Participante participante) {
        if (participante == null) {
            throw new IllegalArgumentException("El participante es obligatorio.");
        }
        this.participante = participante;
    }

    public Participante getParticipante() {
        return participante;
    }

    public int getPuntos() {
        return puntos;
    }

    public int getDiferencia() {
        return diferencia;
    }

    public int getVictorias() {
        return victorias;
    }

    void validarAcumulacion(int puntos, int diferencia, boolean victoria) {
        long nuevosPuntos = (long) this.puntos + puntos;
        long nuevaDiferencia = (long) this.diferencia + diferencia;
        if (puntos < 0 || nuevosPuntos > Integer.MAX_VALUE
                || nuevaDiferencia > Integer.MAX_VALUE
                || nuevaDiferencia < Integer.MIN_VALUE
                || (victoria && victorias == Integer.MAX_VALUE)) {
            throw new IllegalArgumentException("El resultado supera el rango de las estadisticas.");
        }
    }

    public void acumular(int puntos, int diferencia, boolean victoria) {
        validarAcumulacion(puntos, diferencia, victoria);
        this.puntos += puntos;
        this.diferencia += diferencia;
        if (victoria) {
            victorias++;
        }
    }

    @Override
    public int compareTo(Inscripcion otra) {
        int orden = Integer.compare(otra.puntos, puntos);
        if (orden == 0) {
            orden = Integer.compare(otra.diferencia, diferencia);
        }
        if (orden == 0) {
            orden = Integer.compare(otra.victorias, victorias);
        }
        if (orden == 0) {
            orden = participante.getNombre().compareToIgnoreCase(
                    otra.participante.getNombre());
        }
        return orden;
    }

    @Override
    public String toString() {
        return participante.getNombre() + " | Puntos: " + puntos
                + " | Diferencia: " + diferencia + " | Victorias: " + victorias;
    }
}
