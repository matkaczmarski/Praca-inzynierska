package mini.paranormalgolf.Physics;

import android.content.Context;

import mini.paranormalgolf.Graphics.GraphicsData;
import mini.paranormalgolf.Graphics.ModelBuilders.ObjectGenerator;
import mini.paranormalgolf.Graphics.ShaderPrograms.DepthMapShaderProgram;
import mini.paranormalgolf.Graphics.ShaderPrograms.ShaderProgram;
import mini.paranormalgolf.Graphics.ShaderPrograms.TextureShaderProgram;
import mini.paranormalgolf.Graphics.ShaderPrograms.ShadowingShaderProgram;
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

    public float getMu() {
        return mu;
    }

    public BoxSize getMeasurements() {
        return measurements;
    }

    public Elevator(Point location, Vector velocity, BoxSize measure, Point from, Point to, float mu,Context context) {
        super(velocity, location);
        this.measurements = measure;
        this.patrolFrom = from;
        this.patrolTo = to;
        this.mu = mu;

        GraphicsData generatedData = ObjectGenerator.createBox(measure, ELEVATOR_TEXTURE_UNIT);
        vertexData = new VertexArray(generatedData.vertexData);
        drawCommands = generatedData.drawCommands;
        texture = ResourceHelper.loadTexture(context, R.drawable.elevator_texture);
    }

    public void Update(float dt) {
        lastMove.y=velocity.y*dt;
        location.y = location.y + lastMove.y;
        if (location.y > patrolTo.y || location.y < patrolFrom.y)
            velocity.y = -velocity.y;
    }

}
