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
public class Beam extends MovableElement {

    private static final float BEAM_TEXTURE_UNIT = 5f;
    public final float BEAM_OPACITY = 1f;

    private BoxSize measurements;
    private boolean moveToPatrolTo;
    private static int beamTextureId;

    //punkty oznaczjące do jakiego miejsca ma dochodzić środek elementu
    private Point patrolFrom;
    private Point patrolTo;

    public static int getTexture(){return beamTextureId;}

    public Beam(Point location, Vector velocity, BoxSize measure, Point from, Point to){
        super(velocity, location);
        this.measurements = measure;
        this.patrolFrom = from;
        this.patrolTo = to;

        moveToPatrolTo = findMovementDirection(from, to, velocity);

        GraphicsData generatedData = ObjectGenerator.createBoxModel(measure, BEAM_TEXTURE_UNIT);
        vertexData = new VertexArray(generatedData.vertexData);
        drawCommands = generatedData.drawCommands;
    }

    public void Update(float dt){
        lastMove = new Vector(velocity.x * dt, 0, velocity.z * dt);
        location.x = location.x + lastMove.x;
        location.z = location.z + lastMove.z;
        if ((moveToPatrolTo && (location.x > patrolTo.x || location.z > patrolTo.z)) || (!moveToPatrolTo && (location.x < patrolFrom.x || location.z < patrolFrom.z)))
        {
            velocity.x = -velocity.x;
            velocity.z = -velocity.z;

            moveToPatrolTo = !moveToPatrolTo;
        }
    }

    private boolean findMovementDirection(Point from, Point to, Vector velocity){
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
        beamTextureId = ResourceHelper.loadTexture(context, R.drawable.beam_texture);
    }

    public BoxSize getMeasurements() {
        return measurements;
    }
}
