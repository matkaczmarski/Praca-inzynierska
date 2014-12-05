package mini.paranormalgolf.Physics;

import java.util.List;

/**
 * Created by Mateusz on 2014-12-05.
 */
public class Board {

    final int boardId;
    List<Floor> floors;


    public Board(int boardId, List<Floor> floors){
        this.boardId = boardId;
        this.floors = floors;


    }

}
