package mini.paranormalgolf.Physics;


import android.content.Context;

import mini.paranormalgolf.Graphics.GraphicsData;
import mini.paranormalgolf.Graphics.ModelBuilders.ObjectGenerator;
import mini.paranormalgolf.Graphics.ShaderPrograms.TextureLightShaderProgram;
import mini.paranormalgolf.Helpers.ResourceHelper;
import mini.paranormalgolf.Primitives.BoxSize;
import mini.paranormalgolf.Primitives.Point;
import mini.paranormalgolf.Primitives.Vector;
import  mini.paranormalgolf.Graphics.ShaderPrograms.ShaderProgram;
import mini.paranormalgolf.Graphics.VertexArray;
import mini.paranormalgolf.R;

import static android.opengl.Matrix.multiplyMM;
import static android.opengl.Matrix.setIdentityM;
import static android.opengl.Matrix.setRotateM;

/**
 * Created by Sławomir on 2014-12-03.
 */
public class Ball extends MovableElement {

    public enum BallTexture{
        golf,
        wooden,
        tennis,
        billard,
        red_white,
        Slawek,
        Kuba,
        cat,
        noise,
        beach,
        poziomeKreski
    }

    private final static float G=-9.81f;
    private final int MESH_DIMENSION = 32;
    public final float BALL_OPACITY = 1f;
    private final int STRIDE = (POSITION_COMPONENT_COUNT + NORMAL_COMPONENT_COUNT + TEXTURE_COMPONENT_COUNT) * 4;

    public final float[] rgba = new float[] {0.0f, 0.0f, 0.9f, 1f};
    public final float[] rotation=new float[16];
    //
    private float radius;
    private float omega;
    public Vector pole;
    public Vector onEquator;
    public Vector axis;
    public float angle;
    private float mass;
    private float area;
    private final static float Cd=0.4f;
    private final static float density=1.225f;

    public float getRadius(){
        return this.radius;
    }


    public Ball(Point location, float radius, Vector velocity, BallTexture ballTexture, Context context) {
        super(velocity, location);
        omega=0;
        pole=new Vector(0,1,0);
        onEquator=new Vector(1,0,0);
        this.radius = radius;
        mass=5;
        area=(float)Math.PI*radius*radius;
        setIdentityM(rotation,0);
        //axises=new LinkedList<Vector>();
       // angles=new LinkedList<Float>();

        GraphicsData generatedData = ObjectGenerator.createBall(location, radius, MESH_DIMENSION);
        vertexData = new VertexArray(generatedData.vertexData);
        drawCommands = generatedData.drawCommands;

        texture = textureLoader(ballTexture, context);
    }

    private int textureLoader(BallTexture ballTextureType, Context context){
        switch (ballTextureType){
            case golf:
                return ResourceHelper.loadTexture(context, R.drawable.golf_texture);
            case wooden:
                return ResourceHelper.loadTexture(context, R.drawable.wood_texture);
            case billard:
                return ResourceHelper.loadTexture(context, R.drawable.billard_texture);
            case tennis:
                return ResourceHelper.loadTexture(context, R.drawable.tennis_texture);
            case red_white:
                return ResourceHelper.loadTexture(context, R.drawable.red_white_texture);
            case Slawek:
                return ResourceHelper.loadTexture(context, R.drawable.slawek_texture);
            case Kuba:
                return ResourceHelper.loadTexture(context, R.drawable.kuba_texture);
            case cat:
                return ResourceHelper.loadTexture(context, R.drawable.cat_texture);
            case noise:
                return ResourceHelper.loadTexture(context, R.drawable.goodnoise_texture);
            case beach:
                return ResourceHelper.loadTexture(context, R.drawable.beach_ball_texture);
            case poziomeKreski:
                return ResourceHelper.loadTexture(context, R.drawable.poziome_kreski);
        }
        return -1;
    }

    public void bindData(ShaderProgram shaderProgram) {
        vertexData.setVertexAttribPointer(0, ((TextureLightShaderProgram)shaderProgram).getPositionAttributeLocation(), POSITION_COMPONENT_COUNT, STRIDE);
        vertexData.setVertexAttribPointer(POSITION_COMPONENT_COUNT, ((TextureLightShaderProgram)shaderProgram).getNormalAttributeLocation(), NORMAL_COMPONENT_COUNT, STRIDE);
        vertexData.setVertexAttribPointer(POSITION_COMPONENT_COUNT + NORMAL_COMPONENT_COUNT, ((TextureLightShaderProgram)shaderProgram).getTextureCoordinatesAttributeLocation(), TEXTURE_COMPONENT_COUNT, STRIDE);
    }

    public void Update(float dt,Vector accelerometrData,float mu) {
        //Update związany z poruszeniem się elementu
        int j;
        int numEqns = 6;
        float[] dq1 = new float[numEqns];
        float[] dq2 = new float[numEqns];
        float[] dq3 = new float[numEqns];
        float[] dq4 = new float[numEqns];


        float[] q = new float[6];
        {
            q[1] = location.X;
            q[0] = velocity.X;
            q[3] = location.Y;
            q[2] = velocity.Y;
            q[5] = location.Z;
            q[4] = velocity.Z;
        }

        dq1 = SolveEquation(q, q, dt, 0.0f, accelerometrData, mu);
        dq2 = SolveEquation(q, dq1, dt, 0.5f, accelerometrData, mu);
        dq3 = SolveEquation(q, dq2, dt, 0.5f, accelerometrData, mu);
        dq4 = SolveEquation(q, dq3, dt, 1.0f, accelerometrData, mu);
        for (j = 0; j < numEqns; ++j) {
            q[j] = q[j] + (dq1[j] + 2.0f * dq2[j] + 2.0f * dq3[j] + dq4[j]) / 6.0f;
        }

        float difX = q[1] - location.X;
        float difY = q[3] - location.Y;
        float difZ = q[5] - location.Z;

        if (difX != 0 || difZ != 0) {
            float[] helpMatrix=new float[16];
            float[] helpMatrix2=new float[16];

            axis = new Vector(difZ, 0, -difX).normalize();
            angle = (float) (360*Math.sqrt(difX * difX + difZ * difZ) /(2*Math.PI*radius));

            setRotateM(helpMatrix,0,angle,axis.X,axis.Y,axis.Z);
            multiplyMM(helpMatrix2,0,helpMatrix,0,rotation,0);
            System.arraycopy(helpMatrix2,0,rotation,0,16);
        }
        {
            location.X = q[1];
            velocity.X = q[0];
            location.Y = q[3];
            velocity.Y = q[2];
            location.Z = q[5];
            velocity.Z = q[4];
        }
    }

    private float[] SolveEquation(float[] q, float deltaQ[], float dt, float qScale, Vector accelerometrData,float mu) {


        float dQ[] = new float[6];
        float newQ[] = new float[6];
        for (int i = 0; i < 6; ++i) {
            newQ[i] = q[i] + qScale * deltaQ[i];
        }
        Vector localVelocity = new Vector(newQ[0], newQ[2], newQ[4]);
        Vector acceleration;
        if (mu < 0)
            acceleration = CountAccelerationForFlying(accelerometrData, localVelocity);
        else acceleration = CountAccelerationForRolling(accelerometrData, mu, localVelocity);
        // CountAcceleration(newVelocity,accData,mu);
        dQ[0] = dt * (acceleration.X);
        dQ[1] = dt * localVelocity.X;
        dQ[2] = dt * (acceleration.Y);
        dQ[3] = dt * localVelocity.Y;
        dQ[4] = dt * (acceleration.Z);
        dQ[5] = dt * localVelocity.Z;
        return dQ;
    }


    private Vector CountAccelerationForRolling(Vector accData,float mu,Vector actualVelocity) {
        float v = (float) (Math.sqrt(actualVelocity.X * actualVelocity.X + actualVelocity.Y * actualVelocity.Y + actualVelocity.Z * actualVelocity.Z) + 1e-8);
        float Fd = 0.5f * density * area * Cd * v * v;
        float Fr = mu * Math.abs(accData.Y) * mass;
        Vector acceleration = new Vector(
                accData.X - (Fd + Fr) * actualVelocity.X / (mass * v),
                0,
                accData.Z - (Fd + Fr) * actualVelocity.Z / (mass * v));
        return acceleration;
    }

    private Vector CountAccelerationForFlying(Vector accData,Vector localVelocity){
        return accData;
    }


    public boolean CheckCollision(Wall element){
        Point wallLocation=element.getLocation();
        BoxSize wallSize=element.getMeasurements();
        Point min=new Point(wallLocation.X-wallSize.x/2,wallLocation.Y-wallSize.y/2,wallLocation.Z-wallSize.z/2);
        Point max=new Point(wallLocation.X+wallSize.x/2,wallLocation.Y+wallSize.y/2,wallLocation.Z+wallSize.z/2);
        float d=0;
        if (location.X < min.X) {
            d += (location.X-min.X)*(location.X-min.X);
        } else if (location.X > max.X) {
            d += (location.X-max.X)*(location.X-max.X);
        }

        if (location.Y < min.Y) {
            d += (location.Y-min.Y)*(location.Y-min.Y);
        } else if (location.Y > max.Y) {
            d += (location.Y-max.Y)*(location.Y-max.Y);
        }

        if (location.Z < min.Z) {
            d += (location.Z-min.Z)*(location.Z-min.Z);
        } else if (location.Z > max.Z) {
            d += (location.Z-max.Z)*(location.Z-max.Z);
        }

        return d <= radius*radius;
    }

    public boolean CheckCollision(Bonus element){
        return false;
    }

    public void ReactOnCollision(MovableElement element){

    }

    public void ReactOnCollision(Bonus element) {

    }

    public void ReactOnCollision(Wall element) {
        velocity = new Vector(-velocity.X, -velocity.Y, -velocity.Z);
    }
}
