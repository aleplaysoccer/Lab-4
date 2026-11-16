import java.time.LocalDate;

public interface Programable {
    void programar(LocalDate fecha, int hora, String sede);
    LocalDate getFecha();
    int getHora();
    String getSede();
}
