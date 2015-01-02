package mini.paranormalgolf.Physics;

import mini.paranormalgolf.Graphics.ShaderPrograms.ShaderProgram;
import mini.paranormalgolf.Primitives.Point;

/**
 * Created by Sławomir on 2014-12-08.
 */
public abstract class Bonus extends Element {

    protected float ROTATION_SPEED = 1f;
    protected float UP_DOWN_SPEED = 0.002f;
    private float currentRotationAngle;
    private int value;
    protected float yShiftFrom;
    protected float yShiftTo;

    @Override
    public Point getLocation(){return lift();}

    public Bonus(Point location, int value, float yShift) {
        super(location);
        this.value = value;
        this.yShiftFrom = location.y;
        this.yShiftTo = location.y + yShift;
        currentRotationAngle = 0f;
    }

    public float rotate(){
        currentRotationAngle = (currentRotationAngle + ROTATION_SPEED) % 360f;
        return  currentRotationAngle;
    }

    private Point lift(){
        if(location.y < yShiftFrom || location.y >= yShiftTo)
            UP_DOWN_SPEED *= (-1f);
        location = new Point(location.x, location.y + UP_DOWN_SPEED, location.z);
        return location;
    }

    public int getValue()
    {
        return value;
    }
}
