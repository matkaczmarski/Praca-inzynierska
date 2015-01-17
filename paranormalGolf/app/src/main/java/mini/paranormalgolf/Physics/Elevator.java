package mini.paranormalgolf.Physics;

import android.content.Context;

import mini.paranormalgolf.Graphics.GraphicsData;
import mini.paranormalgolf.Graphics.ModelBuilders.ObjectGenerator;
import mini.paranormalgolf.Graphics.VertexArray;
import mini.paranormalgolf.Helpers.ResourceHelper;
import mini.paranormalgolf.Primitives.BoxSize;
import mini.paranormalgolf.Primitives.Point;
import mini.paranormalgolf.Primitives.Vector;
import mini.paranormalgolf.R;

/**
 * Created by Sławomir on 2014-12-03.
 */
public class Elevator extends MovableElement {

    private static final float ELEVATOR_TEXTURE_UNIT = 5f;
    public final float ELEVATOR_OPACITY = 1f;

    private BoxSize measurements;

    //punkty oznaczjące do jakiego miejsca ma dochodzić środek elementu
    private Point patrolFrom;
    private Point patrolTo;
    private float mu;

    private boolean moveToPatrolTo;

    private static float wait_time = 2.0f;

    private float time_left;

    private static int elevatorTextureId;

    public static int getTexture(){return elevatorTextureId;}

    private boolean change = false;

    public float getMu() {
        return mu;
    }

    public BoxSize getMeasurements() {
        return measurements;
    }

    public Elevator(Point location, Vector velocity, BoxSize measure, Point from, Point to, float mu) {
        super(velocity, location);
        this.measurements = measure;
        this.patrolFrom = from;
        this.patrolTo = to;
        this.mu = mu;

        moveToPatrolTo = findMovementDirection(from, to, velocity);

        GraphicsData generatedData = ObjectGenerator.createBoxModel(measure, ELEVATOR_TEXTURE_UNIT);
        vertexData = new VertexArray(generatedData.vertexData);
        drawCommands = generatedData.drawCommands;
    }

    public void Update(float dt)
    {
        if (change)
        {
            time_left -= dt;
            if (time_left <= 0)
            {
                velocity.x = -velocity.x;
                velocity.y = -velocity.y;
                velocity.z = -velocity.z;
                moveToPatrolTo = !moveToPatrolTo;
                change = false;
            }
            else
                return;
        }

        lastMove = new Vector(velocity.x *dt, velocity.y * dt, velocity.z * dt);
        location.x = location.x + lastMove.x;
        location.y = location.y + lastMove.y;
        location.z = location.z + lastMove.z;

        if ((moveToPatrolTo && ((location.x > patrolTo.x) || (location.y > patrolTo.y) || location.z > patrolTo.z)) || (!moveToPatrolTo && ((location.x < patrolFrom.x) || (location.y < patrolFrom.y) || location.z < patrolFrom.z)))
        {
            change = true;
            time_left = wait_time;
            lastMove = new Vector(0,0,0);
            /*if (moveToPatrolTo)
                location = patrolTo;
            else
                location = patrolFrom;*/
        }
    }

    private boolean findMovementDirection(Point from, Point to, Vector velocity)
    {
        if (velocity.x != 0)
        {
            if (from.x > to.x)
            {
                patrolFrom = to;
                patrolTo = from;
            }

            return velocity.x > 0;
        }
        else if (velocity.y != 0)
        {
            if (from.y > to.y)
            {
                patrolFrom = to;
                patrolTo = from;
            }

            return velocity.y > 0;
        }
        else if (velocity.z != 0)
        {
            if (from.z > to.z)
            {
                patrolFrom = to;
                patrolTo = from;
            }

            return velocity.z > 0;
        }

        return true;
    }

    public static void initTextures(Context context){
        elevatorTextureId = ResourceHelper.loadTexture(context, R.drawable.elevator_texture);
    }
}
