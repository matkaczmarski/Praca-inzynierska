package mini.paranormalgolf.Physics;

import mini.paranormalgolf.Graphics.ShaderPrograms.ShaderProgram;
import mini.paranormalgolf.Primitives.Point;

/**
 * Created by Sławomir on 2014-12-08.
 */
public abstract class Bonus extends Element {

    private int value;

    protected float ROTATION_SPEED = 1f;
    private float currentRotationAngle;

    public Bonus(Point location, int value) {
        super(location);
        this.value = value;
        currentRotationAngle = 0f;
    }

    public float rotate(){
        currentRotationAngle = (currentRotationAngle + ROTATION_SPEED) % 360f;
        return  currentRotationAngle;
    }
}
