import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Locale;
import java.util.NoSuchElementException;
import java.util.Scanner;

public class VistaConsola {
    private final Scanner scanner;

    public VistaConsola() {
        scanner = new Scanner(System.in, "UTF-8");
    }

    public void mostrar(String mensaje) {
        System.out.println(mensaje);
    }

    public void mostrarMenu() {
        mostrar("\nArenaGT");
        mostrar("1. Registrar participante");
        mostrar("2. Crear torneo");
        mostrar("3. Cotizar inscripcion");
        mostrar("4. Inscribir participante");
        mostrar("5. Cerrar inscripciones");
        mostrar("6. Programar o reprogramar evento");
        mostrar("7. Registrar resultado");
        mostrar("8. Consultar torneos y tablas");
        mostrar("9. Consultar participantes");
        mostrar("10. Consultar calendarios");
        mostrar("11. Consultar ingresos, premios y ranking");
        mostrar("0. Salir");
    }

    public String leerTexto(String mensaje) {
        System.out.print(mensaje);
        if (!scanner.hasNextLine()) {
            throw new NoSuchElementException("Entrada finalizada.");
        }
        return scanner.nextLine().trim();
    }

    public int leerEntero(String mensaje) {
        while (true) {
            try {
                return Integer.parseInt(leerTexto(mensaje));
            } catch (NumberFormatException excepcion) {
                mostrar("Escriba un numero entero dentro del rango permitido.");
            }
        }
    }

    public double leerDecimal(String mensaje) {
        while (true) {
            try {
                double valor = Double.parseDouble(leerTexto(mensaje).replace(',', '.'));
                if (!Double.isFinite(valor)) {
                    throw new NumberFormatException();
                }
                return valor;
            } catch (NumberFormatException excepcion) {
                mostrar("Escriba un numero valido.");
            }
        }
    }

    public LocalDate leerFecha(String mensaje) {
        while (true) {
            try {
                return LocalDate.parse(leerTexto(mensaje));
            } catch (DateTimeParseException excepcion) {
                mostrar("Use una fecha valida con formato AAAA-MM-DD.");
            }
        }
    }

    public boolean leerConfirmacion(String mensaje) {
        while (true) {
            String respuesta = leerTexto(mensaje + " (s/n): ");
            if (respuesta.equalsIgnoreCase("s")) {
                return true;
            }
            if (respuesta.equalsIgnoreCase("n")) {
                return false;
            }
            mostrar("Escriba s o n.");
        }
    }

    public String dinero(double monto) {
        return String.format(Locale.US, "Q%.2f", monto);
    }

    public void mostrarParticipantes(ArrayList<Participante> participantes) {
        if (participantes.isEmpty()) {
            mostrar("No hay participantes registrados.");
        }
        for (Participante participante : participantes) {
            mostrar(participante.toString());
        }
    }

    public void mostrarTorneos(ArrayList<Torneo> torneos) {
        if (torneos.isEmpty()) {
            mostrar("No hay torneos registrados.");
        }
        for (Torneo torneo : torneos) {
            mostrar(torneo.toString());
        }
    }

    public void mostrarEventos(ArrayList<Evento> eventos) {
        if (eventos.isEmpty()) {
            mostrar("El torneo todavia no tiene eventos.");
        }
        ArrayList<Programable> programables = new ArrayList<>();
        programables.addAll(eventos);
        Calendario calendario = new Calendario(programables);
        for (Programable evento : calendario) {
            mostrar((eventos.indexOf(evento) + 1) + ". " + evento);
        }
        for (int i = 0; i < eventos.size(); i++) {
            if (eventos.get(i).getFecha() == null) {
                mostrar((i + 1) + ". " + eventos.get(i));
            }
        }
    }

    public void mostrarTabla(Torneo torneo) {
        mostrar("\n" + torneo);
        System.out.printf("%-5s %-24s %8s %12s %10s%n",
                "Lugar", "Participante", "Puntos", "Diferencia", "Victorias");
        ArrayList<Inscripcion> tabla = torneo.obtenerTabla();
        for (int i = 0; i < tabla.size(); i++) {
            Inscripcion actual = tabla.get(i);
            System.out.printf("%-5d %-24s %8d %12d %10d%n", i + 1,
                    actual.getParticipante().getNombre(), actual.getPuntos(),
                    actual.getDiferencia(), actual.getVictorias());
        }
        if (torneo.getEstado().equals("FINALIZADO")) {
            mostrar("Campeon: " + torneo.getCampeon().getNombre());
            mostrar("Premio registrado: " + dinero(torneo.getPremioEntregado()));
        }
    }

    public void mostrarCalendario(Calendario calendario) {
        boolean vacio = true;
        for (Programable evento : calendario) {
            mostrar(evento.toString());
            vacio = false;
        }
        if (vacio) {
            mostrar("No hay eventos programados para esta consulta.");
        }
    }
}
