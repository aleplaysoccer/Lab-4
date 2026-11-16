import java.util.ArrayList;

public class Voleibol extends Disciplina {
    public Voleibol() {
        super("Voleibol", 4, 6, 450);
    }

    @Override
    public String getEtiquetaValor() {
        return "Sets ganados";
    }

    @Override
    public void validarResultado(ArrayList<Resultado> datos) {
        validarEncuentro(datos);
        validarSinPenales(datos);
        int primera = (int) datos.get(0).getValor();
        int segunda = (int) datos.get(1).getValor();
        if (!((primera == 3 && segunda >= 0 && segunda <= 2)
                || (segunda == 3 && primera >= 0 && primera <= 2))) {
            throw new IllegalArgumentException("El ganador debe tener 3 sets y el perdedor de 0 a 2.");
        }
    }

    @Override
    public void aplicarResultado(Torneo torneo, ArrayList<Resultado> datos) {
        int primera = (int) datos.get(0).getValor();
        int segunda = (int) datos.get(1).getValor();
        boolean ganaPrimero = primera > segunda;
        int menor = Math.min(primera, segunda);
        int puntosGanador = menor == 2 ? 2 : 3;
        int puntosPerdedor = menor == 2 ? 1 : 0;
        acumularEncuentro(torneo, datos, ganaPrimero ? puntosGanador : puntosPerdedor,
                ganaPrimero ? puntosPerdedor : puntosGanador,
                primera - segunda, ganaPrimero, !ganaPrimero);
    }
}
