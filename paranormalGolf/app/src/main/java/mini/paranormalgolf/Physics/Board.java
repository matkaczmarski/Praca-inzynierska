package mini.paranormalgolf.Physics;

import android.widget.ListView;

import java.util.List;

/**
 * Created by Mateusz on 2014-12-05.
 */
public class Board {

    public final int boardId;
    public Start start;
    public Finish finish;
    public List<CheckPoint> checkpoints;
    public List<Floor> floors;
    public List<Wall> walls;
    public List<Elevator> elevators;
    public List<Beam> beams;
    public List<Bonus> bonuses;
    public List<Diamond> diamonds;



    public Board(int boardId, List<Floor> floors, List<Wall> walls, List<Diamond> diamonds, List<Beam> beams){
        this.boardId = boardId;
        this.floors = floors;
        this.walls = walls;
        this.diamonds = diamonds;
        this.beams = beams;
    }

}
