import java.util.ArrayList;

public abstract class Disciplina {
    private final String nombre;
    private final int minimo;
    private final int maximo;
    private final double cuota;

    protected Disciplina(String nombre, int minimo, int maximo, double cuota) {
        if (nombre == null || nombre.trim().isEmpty() || minimo < 2
                || maximo < minimo || !Double.isFinite(cuota) || cuota <= 0) {
            throw new IllegalArgumentException("Los datos de la disciplina no son validos.");
        }
        this.nombre = nombre.trim();
        this.minimo = minimo;
        this.maximo = maximo;
        this.cuota = cuota;
    }

    public String getNombre() {
        return nombre;
    }

    public int getMinimo() {
        return minimo;
    }

    public int getMaximo() {
        return maximo;
    }

    public double getCuota() {
        return cuota;
    }

    public String getEtiquetaValor() {
        return "Marcador";
    }

    public boolean usaEnteros() {
        return true;
    }

    public boolean permiteDescalificacion() {
        return false;
    }

    public boolean requierePenales(ArrayList<Resultado> datos) {
        return false;
    }

    public ArrayList<Evento> generarEventos(Torneo torneo) {
        ArrayList<Evento> eventos = new ArrayList<>();
        ArrayList<Participante> participantes = torneo.getParticipantes();
        for (int i = 0; i < participantes.size(); i++) {
            for (int j = i + 1; j < participantes.size(); j++) {
                eventos.add(new Encuentro(participantes.get(i), participantes.get(j)));
            }
        }
        return eventos;
    }

    protected void validarEncuentro(ArrayList<Resultado> datos) {
        if (datos == null || datos.size() != 2 || datos.get(0) == null
                || datos.get(1) == null) {
            throw new IllegalArgumentException("El encuentro necesita dos resultados.");
        }
        if (datos.get(0).getParticipante().equals(datos.get(1).getParticipante())) {
            throw new IllegalArgumentException("Los participantes deben ser diferentes.");
        }
        for (Resultado resultado : datos) {
            double valor = resultado.getValor();
            if (resultado.isDescalificado() || valor != Math.floor(valor)
                    || valor > Integer.MAX_VALUE) {
                throw new IllegalArgumentException("El marcador debe ser un entero no negativo, sin descalificacion.");
            }
        }
    }

    protected void validarSinPenales(ArrayList<Resultado> datos) {
        for (Resultado resultado : datos) {
            if (resultado.getPenales() != -1) {
                throw new IllegalArgumentException("Este resultado no admite penales.");
            }
        }
    }

    protected void acumularEncuentro(Torneo torneo, ArrayList<Resultado> datos,
                                     int puntosA, int puntosB, int diferencia,
                                     boolean victoriaA, boolean victoriaB) {
        Inscripcion primera = torneo.buscarInscripcion(datos.get(0).getParticipante());
        Inscripcion segunda = torneo.buscarInscripcion(datos.get(1).getParticipante());
        primera.validarAcumulacion(puntosA, diferencia, victoriaA);
        segunda.validarAcumulacion(puntosB, -diferencia, victoriaB);
        primera.acumular(puntosA, diferencia, victoriaA);
        segunda.acumular(puntosB, -diferencia, victoriaB);
    }

    public abstract void validarResultado(ArrayList<Resultado> datos);

    public abstract void aplicarResultado(Torneo torneo, ArrayList<Resultado> datos);

    @Override
    public String toString() {
        return nombre + " | Participantes: " + minimo + " a " + maximo
                + " | Cuota: " + String.format(java.util.Locale.US, "Q%.2f", getCuota());
    }
}
