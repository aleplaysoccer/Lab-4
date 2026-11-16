import java.util.ArrayList;
import java.util.Iterator;

public class Calendario implements Iterable<Programable> {
    private final ArrayList<Programable> eventos;

    public Calendario(ArrayList<Programable> eventos) {
        if (eventos == null) {
            throw new IllegalArgumentException("La lista de eventos es obligatoria.");
        }
        this.eventos = new ArrayList<>();
        for (Programable evento : eventos) {
            if (evento == null) {
                throw new IllegalArgumentException("El calendario no admite eventos nulos.");
            }
            if (evento.getFecha() != null) {
                this.eventos.add(evento);
            }
        }
    }

    private int comparar(Programable primero, Programable segundo) {
        int orden = primero.getFecha().compareTo(segundo.getFecha());
        if (orden == 0) {
            orden = Integer.compare(primero.getHora(), segundo.getHora());
        }
        if (orden == 0) {
            orden = primero.getSede().compareToIgnoreCase(segundo.getSede());
        }
        return orden;
    }

    private void ordenar() {
        for (int i = 0; i < eventos.size() - 1; i++) {
            for (int j = i + 1; j < eventos.size(); j++) {
                if (comparar(eventos.get(i), eventos.get(j)) > 0) {
                    Programable temporal = eventos.get(i);
                    eventos.set(i, eventos.get(j));
                    eventos.set(j, temporal);
                }
            }
        }
    }

    @Override
    public Iterator<Programable> iterator() {
        ordenar();
        return new ArrayList<>(eventos).iterator();
    }
}
