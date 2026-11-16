import java.util.ArrayList;

public class FutbolSalaPenales extends FutbolSala {
    public FutbolSalaPenales() {
        super("Futbol sala con penales");
    }

    @Override
    public double getCuota() {
        return super.getCuota() + 150;
    }

    @Override
    public boolean requierePenales(ArrayList<Resultado> datos) {
        return datos != null && datos.size() == 2
                && datos.get(0).getValor() == datos.get(1).getValor();
    }

    @Override
    public void validarResultado(ArrayList<Resultado> datos) {
        validarEncuentro(datos);
        if (requierePenales(datos)) {
            int primera = datos.get(0).getPenales();
            int segunda = datos.get(1).getPenales();
            if (primera < 0 || segunda < 0 || primera == segunda) {
                throw new IllegalArgumentException("El empate necesita penales no negativos y diferentes.");
            }
        } else {
            validarSinPenales(datos);
        }
    }

    @Override
    public void aplicarResultado(Torneo torneo, ArrayList<Resultado> datos) {
        if (requierePenales(datos)) {
            boolean ganaPrimero = datos.get(0).getPenales() > datos.get(1).getPenales();
            acumularEncuentro(torneo, datos, ganaPrimero ? 2 : 1,
                    ganaPrimero ? 1 : 2, 0, ganaPrimero, !ganaPrimero);
        } else {
            super.aplicarResultado(torneo, datos);
        }
    }
}
