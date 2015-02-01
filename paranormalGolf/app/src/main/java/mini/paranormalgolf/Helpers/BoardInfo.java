package mini.paranormalgolf.Helpers;

/**
 * Klasa zawierająca informacje o poziomie.
 */
public class BoardInfo
{
    /**
     * Id poziomu.
     */
    private int board_id;

    /**
     * Punkty wymagane do zdobycia 2 gwiazdek.
     */
    private int two_stars;

    /**
     * Punkty wymagane do zdobycia 3 gwiazdek.
     */
    private int three_stars;

    /**
     * Czas jaki gracz ma na ukończenie poziomu.
     */
    private int time;

    /**
     * Konstruktor.
     * @param board_id Id poziomu.
     * @param two_stars Punkty wymagane do zdobycia 2 gwiazdek.
     * @param three_stars Punkty wymagane do zdobycia 3 gwiazdek.
     * @param time Czas jaki gracz ma na ukończenie poziomu.
     */
    public BoardInfo(int board_id, int two_stars, int three_stars, int time)
    {
        this.board_id = board_id;
        this.two_stars = two_stars;
        this.three_stars = three_stars;
        this.time = time;
    }

    /**
     * Zwraca id poziomu.
     * @return Id poziomu.
     */
    public int getBoard_id()
    {
        return board_id;
    }

    /**
     * Zwraca liczbę punktów wymaganych do zdobycia 2 gwiazdek.
     * @return Punkty wymagane do zdobycia 2 gwiazdek.
     */
    public int getTwo_stars()
    {
        return two_stars;
    }

    /**
     * Zwraca liczbę punktów wymaganych do zdobycia 3 gwiazdek.
     * @return Punkty wymagane do zdobycia 3 gwiazdek.
     */
    public int getThree_stars()
    {
        return three_stars;
    }

    /**
     * Zwraca czas jaki gracz ma na ukończenie poziomu.
     * @return Czas jaki gracz ma na ukończenie poziomu.
     */
    public int getTime()
    {
        return time;
    }

    /**
     * Ustawia czas jaki gracz ma na ukończenie poziomu.
     * @param time Czas jaki gracz ma na ukończenie poziomu.
     */
    public void setTime(int time)
    {
        this.time = time;
    }
}
