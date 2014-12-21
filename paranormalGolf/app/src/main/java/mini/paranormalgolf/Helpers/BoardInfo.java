package mini.paranormalgolf.Helpers;

/**
 * Created by Kuba on 2014-12-21.
 */
public class BoardInfo
{
    private String board_id;
    private int best_result;
    private int two_stars;
    private int three_stars;
    private boolean accomplished;
    private int time;

    public BoardInfo(String board_id, int best_result, int two_stars, int three_stars, boolean accomplished, int time)
    {
        this.board_id = board_id;
        this.best_result = best_result;
        this.two_stars = two_stars;
        this.three_stars = three_stars;
        this.accomplished = accomplished;
        this.time = time;
    }

    public String getBoard_id()
    {
        return board_id;
    }

    public void setBoard_id(String board_id)
    {
        this.board_id = board_id;
    }

    public int getBest_result()
    {
        return best_result;
    }

    public void setBest_result(int best_result)
    {
        this.best_result = best_result;
    }

    public int getTwo_stars()
    {
        return two_stars;
    }

    public void setTwo_stars(int two_stars)
    {
        this.two_stars = two_stars;
    }

    public int getThree_stars()
    {
        return three_stars;
    }

    public void setThree_stars(int three_stars)
    {
        this.three_stars = three_stars;
    }

    public boolean isAccomplished()
    {
        return accomplished;
    }

    public void setAccomplished(boolean accomplished)
    {
        this.accomplished = accomplished;
    }

    public int getTime()
    {
        return time;
    }

    public void setTime(int time)
    {
        this.time = time;
    }
}
