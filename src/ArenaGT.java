import java.time.LocalDate;
import java.util.ArrayList;

public class ArenaGT {
    private final ArrayList<Participante> participantes;
    private final ArrayList<Torneo> torneos;
    private final ArrayList<Disciplina> disciplinas;

    public ArenaGT() {
        participantes = new ArrayList<>();
        torneos = new ArrayList<>();
        disciplinas = new ArrayList<>();
        disciplinas.add(new FutbolSala());
        disciplinas.add(new FutbolSalaPenales());
        disciplinas.add(new Baloncesto());
        disciplinas.add(new Voleibol());
        disciplinas.add(new Relevos());
    }

    public ArrayList<Participante> getParticipantes() {
        return new ArrayList<>(participantes);
    }

    public ArrayList<Torneo> getTorneos() {
        return new ArrayList<>(torneos);
    }

    public ArrayList<Disciplina> getDisciplinas() {
        return new ArrayList<>(disciplinas);
    }

    public Participante buscarParticipante(String codigo) {
        if (codigo != null) {
            for (Participante participante : participantes) {
                if (participante.getCodigo().equalsIgnoreCase(codigo.trim())) {
                    return participante;
                }
            }
        }
        throw new IllegalArgumentException("No existe un participante con ese codigo.");
    }

    public Torneo buscarTorneo(String codigo) {
        if (codigo != null) {
            for (Torneo torneo : torneos) {
                if (torneo.getCodigo().equalsIgnoreCase(codigo.trim())) {
                    return torneo;
                }
            }
        }
        throw new IllegalArgumentException("No existe un torneo con ese codigo.");
    }

    public void registrarParticipante(Participante participante) {
        if (participante == null || participantes.contains(participante)) {
            throw new IllegalArgumentException("El participante es nulo o su codigo ya existe.");
        }
        participantes.add(participante);
    }

    public void crearTorneo(Torneo torneo) {
        if (torneo == null || torneos.contains(torneo)) {
            throw new IllegalArgumentException("El torneo es nulo o su codigo ya existe.");
        }
        torneos.add(torneo);
    }

    public double cotizar(Disciplina disciplina, int cantidad) {
        if (disciplina == null || cantidad <= 0) {
            throw new IllegalArgumentException("Seleccione una disciplina y una cantidad mayor que cero.");
        }
        return disciplina.getCuota() * cantidad;
    }

    public void inscribir(Torneo torneo, Participante participante) {
        if (!torneos.contains(torneo) || !participantes.contains(participante)) {
            throw new IllegalArgumentException("El torneo y el participante deben estar registrados.");
        }
        torneo.inscribir(buscarParticipante(participante.getCodigo()));
    }

    private ArrayList<Evento> obtenerEventos() {
        ArrayList<Evento> eventos = new ArrayList<>();
        for (Torneo torneo : torneos) {
            eventos.addAll(torneo.getEventos());
        }
        return eventos;
    }

    public void programarEvento(Evento evento, LocalDate fecha, int hora, String sede) {
        ArrayList<Evento> eventos = obtenerEventos();
        if (evento == null || !eventos.contains(evento)) {
            throw new IllegalArgumentException("El evento no pertenece al sistema.");
        }
        if (fecha == null || hora < 0 || hora > 23 || sede == null || sede.trim().isEmpty()) {
            throw new IllegalArgumentException("Indique una fecha valida, hora de 0 a 23 y sede.");
        }
        if (evento.estaFinalizado()) {
            throw new IllegalArgumentException("Un evento finalizado no puede reprogramarse.");
        }
        for (Evento otro : eventos) {
            if (otro != evento && fecha.equals(otro.getFecha()) && hora == otro.getHora()) {
                if (sede.trim().equalsIgnoreCase(otro.getSede())) {
                    throw new IllegalArgumentException("La sede ya esta ocupada en esa fecha y hora.");
                }
                for (Participante participante : evento.getParticipantes()) {
                    if (otro.getParticipantes().contains(participante)) {
                        throw new IllegalArgumentException(participante.getNombre()
                                + " ya tiene otro evento en esa fecha y hora.");
                    }
                }
            }
        }
        evento.programar(fecha, hora, sede);
    }

    public Calendario consultarCalendario() {
        ArrayList<Programable> eventos = new ArrayList<>();
        eventos.addAll(obtenerEventos());
        return new Calendario(eventos);
    }

    public Calendario consultarCalendario(Torneo torneo) {
        if (!torneos.contains(torneo)) {
            throw new IllegalArgumentException("El torneo no esta registrado.");
        }
        ArrayList<Programable> eventos = new ArrayList<>();
        eventos.addAll(torneo.getEventos());
        return new Calendario(eventos);
    }

    public Calendario consultarCalendario(Participante participante) {
        if (!participantes.contains(participante)) {
            throw new IllegalArgumentException("El participante no esta registrado.");
        }
        ArrayList<Programable> eventos = new ArrayList<>();
        for (Evento evento : obtenerEventos()) {
            if (evento.getParticipantes().contains(participante)) {
                eventos.add(evento);
            }
        }
        return new Calendario(eventos);
    }

    public double calcularIngresos() {
        double total = 0;
        for (Torneo torneo : torneos) {
            total += torneo.getIngresos();
        }
        return total;
    }

    public double calcularIngresos(Disciplina disciplina) {
        if (disciplina == null) {
            throw new IllegalArgumentException("La disciplina es obligatoria.");
        }
        double total = 0;
        for (Torneo torneo : torneos) {
            if (torneo.getDisciplina().getNombre().equalsIgnoreCase(disciplina.getNombre())) {
                total += torneo.getIngresos();
            }
        }
        return total;
    }

    public double calcularPremios() {
        double total = 0;
        for (Torneo torneo : torneos) {
            total += torneo.getPremioEntregado();
        }
        return total;
    }

    public int getPuntosEmpresa(String empresa) {
        int puntos = 0;
        for (Torneo torneo : torneos) {
            for (Inscripcion inscripcion : torneo.getInscripciones()) {
                if (inscripcion.getParticipante().getEmpresa().equalsIgnoreCase(empresa)) {
                    puntos += inscripcion.getPuntos();
                }
            }
        }
        return puntos;
    }

    public ArrayList<String> rankingEmpresas() {
        ArrayList<String> empresas = new ArrayList<>();
        for (Participante participante : participantes) {
            String empresa = participante.getEmpresa();
            boolean existe = false;
            for (String actual : empresas) {
                if (actual.equalsIgnoreCase(empresa)) {
                    existe = true;
                }
            }
            if (!existe) {
                empresas.add(empresa);
            }
        }
        for (int i = 0; i < empresas.size() - 1; i++) {
            for (int j = i + 1; j < empresas.size(); j++) {
                String primera = empresas.get(i);
                String segunda = empresas.get(j);
                int puntosPrimera = getPuntosEmpresa(primera);
                int puntosSegunda = getPuntosEmpresa(segunda);
                if (puntosSegunda > puntosPrimera || (puntosSegunda == puntosPrimera
                        && segunda.compareToIgnoreCase(primera) < 0)) {
                    empresas.set(i, segunda);
                    empresas.set(j, primera);
                }
            }
        }
        return empresas;
    }

    private Torneo crearTorneoInicial(String codigo, String nombre, Disciplina disciplina,
                                      String[] codigos, boolean cerrar) {
        Torneo torneo = new Torneo(codigo, nombre, disciplina);
        for (String actual : codigos) {
            torneo.cargarParticipante(buscarParticipante(actual));
        }
        crearTorneo(torneo);
        if (cerrar) {
            torneo.cerrarInscripciones();
        }
        return torneo;
    }

    private void cargarEncuentro(Torneo torneo, String codigoA, String codigoB,
                                  int valorA, int valorB, int penalesA, int penalesB) {
        Participante primera = buscarParticipante(codigoA);
        Participante segunda = buscarParticipante(codigoB);
        for (Evento evento : torneo.getEventos()) {
            if (evento.getParticipantes().contains(primera)
                    && evento.getParticipantes().contains(segunda)) {
                ArrayList<Resultado> datos = new ArrayList<>();
                datos.add(new Resultado(primera, valorA, penalesA, false));
                datos.add(new Resultado(segunda, valorB, penalesB, false));
                torneo.cargarResultado(evento, datos);
                return;
            }
        }
        throw new IllegalArgumentException("No existe el encuentro inicial.");
    }

    public void cargarDatosIniciales() {
        if (!participantes.isEmpty() || !torneos.isEmpty()) {
            throw new IllegalArgumentException("Los datos iniciales solo pueden cargarse en un sistema vacio.");
        }
        registrarParticipante(new Participante("P01", "Alfa", "Tech GT"));
        registrarParticipante(new Participante("P02", "Beta", "Tech GT"));
        registrarParticipante(new Participante("P03", "Centinelas", "Red Maya"));
        registrarParticipante(new Participante("P04", "Delfines", "Red Maya"));
        registrarParticipante(new Participante("P05", "Estrellas", "Nova"));
        registrarParticipante(new Participante("P06", "Fenix", "Nova"));
        registrarParticipante(new Participante("P07", "Halcones", "Pixel"));
        registrarParticipante(new Participante("P08", "Jaguares", "Pixel"));

        String[] primeros = {"P01", "P02", "P03", "P04"};
        Torneo futbol = crearTorneoInicial("TFS", "Copa de futbol", disciplinas.get(0), primeros, true);
        Torneo penales = crearTorneoInicial("TFP", "Copa con penales", disciplinas.get(1),
                new String[]{"P05", "P06", "P07", "P08"}, true);
        Torneo basket = crearTorneoInicial("TBA", "Copa de baloncesto", disciplinas.get(2), primeros, true);
        Torneo volei = crearTorneoInicial("TVO", "Copa de voleibol", disciplinas.get(3),
                new String[]{"P01", "P03", "P05", "P07"}, true);
        Torneo relevos = crearTorneoInicial("TRE", "Carrera empresarial", disciplinas.get(4),
                new String[]{"P01", "P02", "P03", "P04", "P05", "P06", "P07", "P08"}, true);
        Torneo descalificados = crearTorneoInicial("TRD", "Carrera del campus", disciplinas.get(4),
                new String[]{"P01", "P03", "P05", "P07"}, true);
        crearTorneoInicial("TIN", "Nueva copa", disciplinas.get(0),
                new String[]{"P01", "P03"}, false);

        ArrayList<Evento> eventos = obtenerEventos();
        LocalDate fecha = LocalDate.of(2026, 11, 10);
        for (int i = 0; i < eventos.size(); i++) {
            programarEvento(eventos.get(i), fecha.plusDays(i / 10), 8 + i % 10,
                    "Sede " + (i % 3 + 1));
        }

        cargarEncuentro(futbol, "P01", "P02", 2, 0, -1, -1);
        cargarEncuentro(futbol, "P03", "P04", 1, 0, -1, -1);
        cargarEncuentro(futbol, "P01", "P03", 1, 1, -1, -1);
        cargarEncuentro(penales, "P05", "P06", 3, 1, -1, -1);
        cargarEncuentro(penales, "P05", "P07", 2, 2, 4, 3);
        cargarEncuentro(basket, "P02", "P01", 60, 50, -1, -1);
        cargarEncuentro(basket, "P02", "P03", 60, 50, -1, -1);
        cargarEncuentro(basket, "P01", "P03", 80, 30, -1, -1);
        cargarEncuentro(basket, "P04", "P01", 70, 50, -1, -1);
        cargarEncuentro(volei, "P01", "P03", 3, 0, -1, -1);
        cargarEncuentro(volei, "P01", "P05", 3, 1, -1, -1);
        cargarEncuentro(volei, "P03", "P07", 3, 2, -1, -1);

        ArrayList<Resultado> tiempos = new ArrayList<>();
        double[] segundos = {50.1, 51.4, 52.7, 54.0, 55.3, 56.6, 57.9, 59.2};
        for (int i = 0; i < participantes.size(); i++) {
            tiempos.add(new Resultado(participantes.get(i), segundos[i], -1, false));
        }
        relevos.cargarResultado(relevos.getEventos().get(0), tiempos);

        ArrayList<Resultado> otrosTiempos = new ArrayList<>();
        otrosTiempos.add(new Resultado(buscarParticipante("P01"), 51.2, -1, false));
        otrosTiempos.add(new Resultado(buscarParticipante("P03"), 52.4, -1, false));
        otrosTiempos.add(new Resultado(buscarParticipante("P05"), 54.0, -1, false));
        otrosTiempos.add(new Resultado(buscarParticipante("P07"), 0, -1, true));
        descalificados.cargarResultado(descalificados.getEventos().get(0), otrosTiempos);
    }
}
