package mini.paranormalgolf.Physics;

import android.widget.ListView;

import java.util.List;

import mini.paranormalgolf.Primitives.Point;

/**
 * Przechowuje wszystkie elementy oprócz kuli znajdujące się na planszy.
 */
public class Board {

    /**
     * Numer id planszy.
     */
    public final int boardId;
 //   public int time;

    /**
     * Punkt mety.
     */
    public Finish finish;

    /**
     * Lista punktów kontrolnych.
     */
    public List<CheckPoint> checkpoints;

    /**
     * Lista podłóg.
     */
    public List<Floor> floors;

    /**
     * Lista ścian.
     */
    public List<Wall> walls;

    /**
     * Lista wind.
     */
    public List<Elevator> elevators;

    /**
     * Lista belek.
     */
    public List<Beam> beams;

    /**
     * Lista diamentów.
     */
    public List<Diamond> diamonds;

    /**
     * Lista klepsydr.
     */
    public List<HourGlass> hourGlasses;

    /**
     * Współrzędne punktu startowego dla kuli w globalnym ukłądzie współrzędnych.
     */
    public Point ballLocation;


    /**
     * Tworzy obiekt typu plansza.
     * @param boardId Numer id planszy.
     * @param floors Lista podłóg.
     * @param walls Lista ścian.
     * @param diamonds Lista diamentów.
     * @param beams Lista belek.
     * @param elevators Lista wind.
     * @param checkPoints Lista punktów kontrolnych.
     * @param hourGlasses Lista klepsydr.
     * @param finish Punkt mety.
     * @param ballLocation Współrzędne punktu startowego dla kuli w globalnym ukłądzie współrzędnych.
     */
    public Board(int boardId, List<Floor> floors, List<Wall> walls, List<Diamond> diamonds, List<Beam> beams, List<Elevator> elevators, List<CheckPoint> checkPoints, List<HourGlass> hourGlasses, Finish finish, Point ballLocation){
        this.boardId = boardId;
        this.floors = floors;
        this.walls = walls;
        this.diamonds = diamonds;
        this.beams = beams;
        this.elevators = elevators;
        this.checkpoints = checkPoints;
        this.hourGlasses = hourGlasses;
        this.finish = finish;
        this.ballLocation = ballLocation;
    }

}
