import java.util.ArrayList;

public class Baloncesto extends Disciplina {
    public Baloncesto() {
        super("Baloncesto", 4, 6, 500);
    }

    @Override
    public String getEtiquetaValor() {
        return "Puntos anotados";
    }

    @Override
    public void validarResultado(ArrayList<Resultado> datos) {
        validarEncuentro(datos);
        validarSinPenales(datos);
        if (datos.get(0).getValor() == datos.get(1).getValor()) {
            throw new IllegalArgumentException("El baloncesto no admite empates.");
        }
    }

    @Override
    public void aplicarResultado(Torneo torneo, ArrayList<Resultado> datos) {
        int diferencia = (int) datos.get(0).getValor() - (int) datos.get(1).getValor();
        boolean ganaPrimero = diferencia > 0;
        acumularEncuentro(torneo, datos, ganaPrimero ? 2 : 1,
                ganaPrimero ? 1 : 2, diferencia, ganaPrimero, !ganaPrimero);
    }
}
