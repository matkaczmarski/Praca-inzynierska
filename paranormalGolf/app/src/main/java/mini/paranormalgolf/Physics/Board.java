package mini.paranormalgolf.Physics;

import android.widget.ListView;

import java.util.List;

import mini.paranormalgolf.Primitives.Point;

/**
 * Created by Mateusz on 2014-12-05.
 */
public class Board {

    public final int boardId;
 //   public int time;
    public Finish finish;
    public List<CheckPoint> checkpoints;
    public List<Floor> floors;
    public List<Wall> walls;
    public List<Elevator> elevators;
    public List<Beam> beams;
    public List<Diamond> diamonds;
    public List<HourGlass> hourGlasses;

    public Point ballLocation;



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
