package mini.paranormalgolf.Physics;

import mini.paranormalgolf.Graphics.ShaderPrograms.ShaderProgram;
import mini.paranormalgolf.Primitives.BoxSize;
import mini.paranormalgolf.Primitives.Point;
import mini.paranormalgolf.Primitives.Vector;

/**
 * Created by Sławomir on 2014-12-03.
 */
public class Elevator extends MovableElement {

    private BoxSize measurements;
    private Point patrolFrom;
    private Point patrolTo;
    private float mu;

    public Elevator(Point location, Vector velocity,BoxSize measure, Point from, Point to, float mu) {
        super(velocity, location);
        this.measurements = measure;
        this.patrolFrom = from;
        this.patrolTo = to;
        this.mu = mu;
    }

    @Override
    public void bindData(ShaderProgram shaderProgram) {

    }

    public void Update(float dt) {
        //zmniejszony update ze względu na to, że to jest elevator
        location.Y = location.Y + velocity.Y * dt;
        if (location.Y > patrolTo.Y || location.Y < patrolFrom.Y)
            velocity.Y = -velocity.Y;
    }

}
