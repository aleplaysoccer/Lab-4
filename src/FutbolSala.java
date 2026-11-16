import java.util.ArrayList;

public class FutbolSala extends Disciplina {
    public FutbolSala() {
        this("Futbol sala");
    }

    protected FutbolSala(String nombre) {
        super(nombre, 4, 8, 600);
    }

    @Override
    public String getEtiquetaValor() {
        return "Goles";
    }

    @Override
    public void validarResultado(ArrayList<Resultado> datos) {
        validarEncuentro(datos);
        validarSinPenales(datos);
    }

    @Override
    public void aplicarResultado(Torneo torneo, ArrayList<Resultado> datos) {
        int diferencia = (int) datos.get(0).getValor() - (int) datos.get(1).getValor();
        if (diferencia > 0) {
            acumularEncuentro(torneo, datos, 3, 0, diferencia, true, false);
        } else if (diferencia < 0) {
            acumularEncuentro(torneo, datos, 0, 3, diferencia, false, true);
        } else {
            acumularEncuentro(torneo, datos, 1, 1, 0, false, false);
        }
    }
}
