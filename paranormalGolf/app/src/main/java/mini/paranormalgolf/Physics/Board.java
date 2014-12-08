package mini.paranormalgolf.Physics;

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
    public List<Bar> bars;
    public List<Bonus> bonuses;



    public Board(int boardId, List<Floor> floors){
        this.boardId = boardId;
        this.floors = floors;


    }

}
