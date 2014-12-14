package mini.paranormalgolf.Physics;

import mini.paranormalgolf.Graphics.ShaderPrograms.ShaderProgram;
import mini.paranormalgolf.Primitives.BoxSize;
import mini.paranormalgolf.Primitives.Point;
import mini.paranormalgolf.Primitives.Vector;

/**
 * Created by Sławomir on 2014-12-03.
 */
public class Bar extends MovableElement {

    private BoxSize measurements;

    //punkty oznaczjące do jakiego miejsca ma dochodzić środek elementu
    private Point patrolFrom;
    private Point patrolTo;
  //  private float mu;

    public Bar(Point location, Vector velocity,BoxSize measure, Point from, Point to/*, float mu*/) {
        super(velocity, location);
        this.measurements = measure;
        this.patrolFrom = from;
        this.patrolTo = to;
        //  this.mu = mu;
    }

    @Override
    public void bindData(ShaderProgram shaderProgram) {

    }

    public void Update(float dt) {
        //zmniejszony update ze względu na to, że to jest bar
        location.X = location.X + velocity.X * dt;
        location.Z = location.Z + velocity.Z * dt;
        if (location.X > patrolTo.X || location.X < patrolFrom.X ||
                location.Z > patrolTo.Z || location.Z < patrolFrom.Z) {
            velocity.X = -velocity.X;
            velocity.Z = -velocity.Z;
        }
    }
}
