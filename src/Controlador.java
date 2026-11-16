import java.time.LocalDate;
import java.util.ArrayList;
import java.util.NoSuchElementException;

public class Controlador {
    private final ArenaGT modelo;
    private final VistaConsola vista;

    public Controlador(ArenaGT modelo, VistaConsola vista) {
        if (modelo == null || vista == null) {
            throw new IllegalArgumentException("El modelo y la vista son obligatorios.");
        }
        this.modelo = modelo;
        this.vista = vista;
    }

    public void iniciar() {
        boolean continuar = true;
        while (continuar) {
            try {
                vista.mostrarMenu();
                int opcion = vista.leerEntero("Opcion: ");
                switch (opcion) {
                    case 1:
                        registrarParticipante();
                        break;
                    case 2:
                        crearTorneo();
                        break;
                    case 3:
                        cotizar();
                        break;
                    case 4:
                        inscribir();
                        break;
                    case 5:
                        cerrarInscripciones();
                        break;
                    case 6:
                        programarEvento();
                        break;
                    case 7:
                        registrarResultado();
                        break;
                    case 8:
                        consultarTorneo();
                        break;
                    case 9:
                        vista.mostrarParticipantes(modelo.getParticipantes());
                        break;
                    case 10:
                        consultarCalendarios();
                        break;
                    case 11:
                        consultarReportes();
                        break;
                    case 0:
                        continuar = false;
                        vista.mostrar("Programa finalizado.");
                        break;
                    default:
                        vista.mostrar("La opcion no existe.");
                }
            } catch (IllegalArgumentException excepcion) {
                vista.mostrar("No se pudo realizar la operacion: " + excepcion.getMessage());
            } catch (NoSuchElementException excepcion) {
                vista.mostrar("\nEntrada finalizada. Programa cerrado.");
                return;
            }
        }
    }

    private Disciplina seleccionarDisciplina() {
        ArrayList<Disciplina> disciplinas = modelo.getDisciplinas();
        for (int i = 0; i < disciplinas.size(); i++) {
            vista.mostrar((i + 1) + ". " + disciplinas.get(i));
        }
        int numero = vista.leerEntero("Disciplina: ");
        if (numero < 1 || numero > disciplinas.size()) {
            throw new IllegalArgumentException("La disciplina seleccionada no existe.");
        }
        return disciplinas.get(numero - 1);
    }

    private Torneo seleccionarTorneo() {
        vista.mostrarTorneos(modelo.getTorneos());
        return modelo.buscarTorneo(vista.leerTexto("Codigo del torneo: "));
    }

    private Participante seleccionarParticipante() {
        vista.mostrarParticipantes(modelo.getParticipantes());
        return modelo.buscarParticipante(vista.leerTexto("Codigo del participante: "));
    }

    private Evento seleccionarEvento(Torneo torneo) {
        ArrayList<Evento> eventos = torneo.getEventos();
        vista.mostrarEventos(eventos);
        int numero = vista.leerEntero("Numero del evento: ");
        if (numero < 1 || numero > eventos.size()) {
            throw new IllegalArgumentException("El evento seleccionado no existe.");
        }
        return eventos.get(numero - 1);
    }

    private void registrarParticipante() {
        String codigo = vista.leerTexto("Codigo: ");
        String nombre = vista.leerTexto("Nombre: ");
        String empresa = vista.leerTexto("Empresa: ");
        modelo.registrarParticipante(new Participante(codigo, nombre, empresa));
        vista.mostrar("Participante registrado.");
    }

    private void crearTorneo() {
        String codigo = vista.leerTexto("Codigo del nuevo torneo: ");
        String nombre = vista.leerTexto("Nombre: ");
        Disciplina disciplina = seleccionarDisciplina();
        modelo.crearTorneo(new Torneo(codigo, nombre, disciplina));
        vista.mostrar("Torneo creado en estado INSCRIPCION.");
    }

    private void cotizar() {
        Disciplina disciplina = seleccionarDisciplina();
        int cantidad = vista.leerEntero("Cantidad de participantes: ");
        vista.mostrar("Total cotizado: " + vista.dinero(modelo.cotizar(disciplina, cantidad)));
    }

    private void inscribir() {
        Torneo torneo = seleccionarTorneo();
        Participante participante = seleccionarParticipante();
        modelo.inscribir(torneo, participante);
        vista.mostrar("Inscripcion realizada. Cobro: "
                + vista.dinero(torneo.getDisciplina().getCuota()));
    }

    private void cerrarInscripciones() {
        Torneo torneo = seleccionarTorneo();
        torneo.cerrarInscripciones();
        vista.mostrar("Inscripciones cerradas. Eventos generados: " + torneo.getEventos().size());
        vista.mostrarEventos(torneo.getEventos());
    }

    private void programarEvento() {
        Torneo torneo = seleccionarTorneo();
        Evento evento = seleccionarEvento(torneo);
        LocalDate fecha = vista.leerFecha("Fecha (AAAA-MM-DD): ");
        int hora = vista.leerEntero("Hora completa (0 a 23): ");
        String sede = vista.leerTexto("Sede: ");
        modelo.programarEvento(evento, fecha, hora, sede);
        vista.mostrar("Programacion guardada.");
    }

    private void registrarResultado() {
        Torneo torneo = seleccionarTorneo();
        Evento evento = seleccionarEvento(torneo);
        if (!evento.getEstado().equals("PROGRAMADO")) {
            throw new IllegalArgumentException("Seleccione un evento programado y sin resultado.");
        }
        Disciplina disciplina = torneo.getDisciplina();
        ArrayList<Resultado> datos = new ArrayList<>();
        for (Participante participante : evento.getParticipantes()) {
            vista.mostrar("Participante: " + participante.getNombre());
            boolean descalificado = disciplina.permiteDescalificacion()
                    && vista.leerConfirmacion("Esta descalificado");
            double valor = 0;
            if (!descalificado) {
                String mensaje = disciplina.getEtiquetaValor() + ": ";
                if (disciplina.usaEnteros()) {
                    valor = vista.leerEntero(mensaje);
                } else {
                    valor = vista.leerDecimal(mensaje);
                }
            }
            datos.add(new Resultado(participante, valor, -1, descalificado));
        }
        if (disciplina.requierePenales(datos)) {
            ArrayList<Resultado> conPenales = new ArrayList<>();
            for (Resultado resultado : datos) {
                int penales = vista.leerEntero("Penales convertidos de "
                        + resultado.getParticipante().getNombre() + ": ");
                if (penales < 0) {
                    throw new IllegalArgumentException("Los penales no pueden ser negativos.");
                }
                conPenales.add(new Resultado(resultado.getParticipante(),
                        resultado.getValor(), penales, false));
            }
            datos = conPenales;
        }
        torneo.registrarResultado(evento, datos);
        vista.mostrar("Resultado registrado.");
        vista.mostrarTabla(torneo);
    }

    private void consultarTorneo() {
        Torneo torneo = seleccionarTorneo();
        vista.mostrar(torneo.getDisciplina().toString());
        vista.mostrarParticipantes(torneo.getParticipantes());
        vista.mostrarTabla(torneo);
        vista.mostrar("Calendario del torneo:");
        vista.mostrarCalendario(modelo.consultarCalendario(torneo));
        vista.mostrar("Eventos sin programar:");
        boolean hayPendientes = false;
        for (Evento evento : torneo.getEventos()) {
            if (evento.getFecha() == null) {
                vista.mostrar(evento.toString());
                hayPendientes = true;
            }
        }
        if (!hayPendientes) {
            vista.mostrar("Ninguno.");
        }
    }

    private void consultarCalendarios() {
        vista.mostrar("1. General\n2. Por torneo\n3. Por participante");
        int opcion = vista.leerEntero("Calendario: ");
        switch (opcion) {
            case 1:
                vista.mostrarCalendario(modelo.consultarCalendario());
                break;
            case 2:
                vista.mostrarCalendario(modelo.consultarCalendario(seleccionarTorneo()));
                break;
            case 3:
                vista.mostrarCalendario(modelo.consultarCalendario(seleccionarParticipante()));
                break;
            default:
                throw new IllegalArgumentException("El calendario seleccionado no existe.");
        }
    }

    private void consultarReportes() {
        vista.mostrar("\nIngresos por disciplina:");
        for (Disciplina disciplina : modelo.getDisciplinas()) {
            vista.mostrar(disciplina.getNombre() + ": "
                    + vista.dinero(modelo.calcularIngresos(disciplina)));
        }
        vista.mostrar("Ingresos totales: " + vista.dinero(modelo.calcularIngresos()));
        vista.mostrar("\nPremios registrados:");
        boolean hayPremios = false;
        for (Torneo torneo : modelo.getTorneos()) {
            if (torneo.getPremioEntregado() > 0) {
                vista.mostrar(torneo.getCodigo() + " | Campeon: "
                        + torneo.getCampeon().getNombre() + " | Premio: "
                        + vista.dinero(torneo.getPremioEntregado()));
                hayPremios = true;
            }
        }
        if (!hayPremios) {
            vista.mostrar("No se han entregado premios.");
        }
        vista.mostrar("Total de premios entregados: " + vista.dinero(modelo.calcularPremios()));
        vista.mostrar("\nRanking de empresas:");
        ArrayList<String> empresas = modelo.rankingEmpresas();
        for (int i = 0; i < empresas.size(); i++) {
            String empresa = empresas.get(i);
            vista.mostrar((i + 1) + ". " + empresa + " | Puntos: "
                    + modelo.getPuntosEmpresa(empresa));
        }
    }
}
