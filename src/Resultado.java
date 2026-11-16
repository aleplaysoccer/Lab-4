public final class Resultado {
    private final Participante participante;
    private final double valor;
    private final int penales;
    private final boolean descalificado;

    public Resultado(Participante participante, double valor, int penales,
                     boolean descalificado) {
        if (participante == null || !Double.isFinite(valor) || valor < 0
                || penales < -1) {
            throw new IllegalArgumentException("Los datos del resultado no son validos.");
        }
        this.participante = participante;
        this.valor = valor;
        this.penales = penales;
        this.descalificado = descalificado;
    }

    public Participante getParticipante() {
        return participante;
    }

    public double getValor() {
        return valor;
    }

    public int getPenales() {
        return penales;
    }

    public boolean isDescalificado() {
        return descalificado;
    }

    @Override
    public String toString() {
        if (descalificado) {
            return participante.getNombre() + ": descalificado";
        }
        String texto = participante.getNombre() + ": " + valor;
        if (penales >= 0) {
            texto += " (penales: " + penales + ")";
        }
        return texto;
    }
}
