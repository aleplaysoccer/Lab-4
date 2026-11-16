public class Main {
    public static void main(String[] args) {
        ArenaGT modelo = new ArenaGT();
        modelo.cargarDatosIniciales();
        VistaConsola vista = new VistaConsola();
        Controlador controlador = new Controlador(modelo, vista);
        controlador.iniciar();
    }
}

