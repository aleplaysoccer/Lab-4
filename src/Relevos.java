import java.util.ArrayList;

public class Relevos extends Disciplina {
    public Relevos() {
        super("Relevos", 3, 8, 300);
    }

    @Override
    public String getEtiquetaValor() {
        return "Tiempo en segundos";
    }

    @Override
    public boolean usaEnteros() {
        return false;
    }

    @Override
    public boolean permiteDescalificacion() {
        return true;
    }

    @Override
    public ArrayList<Evento> generarEventos(Torneo torneo) {
        ArrayList<Evento> eventos = new ArrayList<>();
        eventos.add(new Carrera(torneo.getParticipantes()));
        return eventos;
    }

    @Override
    public void validarResultado(ArrayList<Resultado> datos) {
        if (datos == null || datos.size() < getMinimo() || datos.size() > getMaximo()) {
            throw new IllegalArgumentException("La carrera necesita de 3 a 8 resultados.");
        }
        for (int i = 0; i < datos.size(); i++) {
            Resultado actual = datos.get(i);
            if (actual == null || actual.getPenales() != -1) {
                throw new IllegalArgumentException("Los resultados de relevos no admiten penales.");
            }
            if (!actual.isDescalificado() && actual.getValor() <= 0) {
                throw new IllegalArgumentException("Los tiempos deben ser mayores que cero.");
            }
            for (int j = 0; j < i; j++) {
                Resultado anterior = datos.get(j);
                if (actual.getParticipante().equals(anterior.getParticipante())) {
                    throw new IllegalArgumentException("No se puede repetir un participante.");
                }
                if (!actual.isDescalificado() && !anterior.isDescalificado()
                        && actual.getValor() == anterior.getValor()) {
                    throw new IllegalArgumentException("Los tiempos no pueden repetirse.");
                }
            }
        }
    }

    @Override
    public void aplicarResultado(Torneo torneo, ArrayList<Resultado> datos) {
        int[] puntosPorLugar = {10, 8, 6, 5, 4, 3, 2, 1};
        ArrayList<Integer> puntos = new ArrayList<>();
        ArrayList<Boolean> victorias = new ArrayList<>();
        for (Resultado actual : datos) {
            int lugar = 0;
            if (!actual.isDescalificado()) {
                for (Resultado otro : datos) {
                    if (!otro.isDescalificado() && otro.getValor() < actual.getValor()) {
                        lugar++;
                    }
                }
            }
            int cantidad = actual.isDescalificado() ? 0 : puntosPorLugar[lugar];
            boolean victoria = !actual.isDescalificado() && lugar == 0;
            torneo.buscarInscripcion(actual.getParticipante()).validarAcumulacion(
                    cantidad, 0, victoria);
            puntos.add(cantidad);
            victorias.add(victoria);
        }
        for (int i = 0; i < datos.size(); i++) {
            torneo.buscarInscripcion(datos.get(i).getParticipante()).acumular(
                    puntos.get(i), 0, victorias.get(i));
        }
    }
}
