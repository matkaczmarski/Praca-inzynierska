package mini.paranormalgolf.Physics;


import android.content.Context;

import mini.paranormalgolf.Graphics.GraphicsData;
import mini.paranormalgolf.Graphics.ModelBuilders.ObjectGenerator;
import mini.paranormalgolf.Graphics.ShaderPrograms.TextureShaderProgram;
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
        cat,
        noise,
        beach,
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
                return ResourceHelper.loadTexture(context, R.drawable.ball_texture_golf);
            case wooden:
                return ResourceHelper.loadTexture(context, R.drawable.ball_texture_wooden);
            case billard:
                return ResourceHelper.loadTexture(context, R.drawable.ball_texture_billard);
            case tennis:
                return ResourceHelper.loadTexture(context, R.drawable.ball_texture_tennis);
            case red_white:
                return ResourceHelper.loadTexture(context, R.drawable.ball_texture_red_white_dots);
            case cat:
                return ResourceHelper.loadTexture(context, R.drawable.ball_texture_cat);
            case noise:
                return ResourceHelper.loadTexture(context, R.drawable.ball_texture_noise);
            case beach:
                return ResourceHelper.loadTexture(context, R.drawable.ball_texture_beachball);
        }
        return -1;
    }

    public void bindData(ShaderProgram shaderProgram) {
        vertexData.setVertexAttribPointer(0, ((TextureShaderProgram)shaderProgram).getPositionAttributeLocation(), POSITION_COMPONENT_COUNT, STRIDE);
        vertexData.setVertexAttribPointer(POSITION_COMPONENT_COUNT, ((TextureShaderProgram)shaderProgram).getNormalAttributeLocation(), NORMAL_COMPONENT_COUNT, STRIDE);
        vertexData.setVertexAttribPointer(POSITION_COMPONENT_COUNT + NORMAL_COMPONENT_COUNT, ((TextureShaderProgram)shaderProgram).getTextureCoordinatesAttributeLocation(), TEXTURE_COMPONENT_COUNT, STRIDE);
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
            q[1] = location.x;
            q[0] = velocity.x;
            q[3] = location.y;
            q[2] = velocity.y;
            q[5] = location.z;
            q[4] = velocity.z;
        }

        dq1 = SolveEquation(q, q, dt, 0.0f, accelerometrData, mu);
        dq2 = SolveEquation(q, dq1, dt, 0.5f, accelerometrData, mu);
        dq3 = SolveEquation(q, dq2, dt, 0.5f, accelerometrData, mu);
        dq4 = SolveEquation(q, dq3, dt, 1.0f, accelerometrData, mu);
        for (j = 0; j < numEqns; ++j) {
            q[j] = q[j] + (dq1[j] + 2.0f * dq2[j] + 2.0f * dq3[j] + dq4[j]) / 6.0f;
        }

        float difX = q[1] - location.x;
        float difY = q[3] - location.y;
        float difZ = q[5] - location.z;

        if (difX != 0 || difZ != 0) {
            float[] helpMatrix=new float[16];
            float[] helpMatrix2=new float[16];

            axis = new Vector(difZ, 0, -difX).normalize();
            angle = (float) (360*Math.sqrt(difX * difX + difZ * difZ) /(2*Math.PI*radius));

            setRotateM(helpMatrix,0,angle,axis.x,axis.y,axis.z);
            multiplyMM(helpMatrix2,0,helpMatrix,0,rotation,0);
            System.arraycopy(helpMatrix2,0,rotation,0,16);
        }
        {
            location.x = q[1];
            velocity.x = q[0];
            location.y = q[3];
            velocity.y = q[2];
            location.z = q[5];
            velocity.z = q[4];
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
        dQ[0] = dt * (acceleration.x);
        dQ[1] = dt * localVelocity.x;
        dQ[2] = dt * (acceleration.y);
        dQ[3] = dt * localVelocity.y;
        dQ[4] = dt * (acceleration.z);
        dQ[5] = dt * localVelocity.z;
        return dQ;
    }


    private Vector CountAccelerationForRolling(Vector accData,float mu,Vector actualVelocity) {
        float v = (float) (Math.sqrt(actualVelocity.x * actualVelocity.x + actualVelocity.y * actualVelocity.y + actualVelocity.z * actualVelocity.z) + 1e-8);
        float Fd = 0.5f * density * area * Cd * v * v;
        float Fr = mu * Math.abs(accData.y) * mass;
        Vector acceleration = new Vector(
                accData.x - (Fd + Fr) * actualVelocity.x / (mass * v),
                0,
                accData.z - (Fd + Fr) * actualVelocity.z / (mass * v));
        return acceleration;
    }

    private Vector CountAccelerationForFlying(Vector accData,Vector localVelocity){
        return accData;
    }


    public boolean CheckCollision(Wall element){
        Point wallLocation=element.getLocation();
        BoxSize wallSize=element.getMeasurements();
        Point min=new Point(wallLocation.x -wallSize.x/2,wallLocation.y -wallSize.y/2,wallLocation.z -wallSize.z/2);
        Point max=new Point(wallLocation.x +wallSize.x/2,wallLocation.y +wallSize.y/2,wallLocation.z +wallSize.z/2);
        float d=0;
        if (location.x < min.x) {
            d += (location.x -min.x)*(location.x -min.x);
        } else if (location.x > max.x) {
            d += (location.x -max.x)*(location.x -max.x);
        }

        if (location.y < min.y) {
            d += (location.y -min.y)*(location.y -min.y);
        } else if (location.y > max.y) {
            d += (location.y -max.y)*(location.y -max.y);
        }

        if (location.z < min.z) {
            d += (location.z -min.z)*(location.z -min.z);
        } else if (location.z > max.z) {
            d += (location.z -max.z)*(location.z -max.z);
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
        Point wallLocation = element.getLocation();
        BoxSize wallSize = element.getMeasurements();
        Point min = new Point(wallLocation.x - wallSize.x / 2, wallLocation.y - wallSize.y / 2, wallLocation.z - wallSize.z / 2);
        Point max = new Point(wallLocation.x + wallSize.x / 2, wallLocation.y + wallSize.y / 2, wallLocation.z + wallSize.z / 2);
        Vector difference=new Vector(0.2f,0f,0.4f);
        Point lastLocation=new Point(location.x-difference.x,location.y-difference.y,location.z-difference.z);
        Point granica=new Point(0,0,0);
        granica.x=difference.x>0?max.x:min.x;
        granica.y=difference.y>0?max.y:min.y;
        granica.z=difference.z>0?max.z:min.z;

        float percentX=(granica.x-lastLocation.x)/difference.x;
        float percentY=(granica.y-lastLocation.y)/difference.y;
        float percentZ=(granica.z-lastLocation.z)/difference.z;


        if (min.x <= location.x && location.x <= max.x && min.y <= location.y && location.y <= max.y &&
                (max.z >= location.z - radius || min.z <= location.z + radius))
            velocity.z = -velocity.z;
        if (min.x <= location.x && location.x <= max.x && min.z <= location.z && location.z <= max.z &&
                (max.y >= location.y - radius || min.y <= location.y + radius))
            velocity.y = -velocity.y;
        if (min.y <= location.y && location.y <= max.y && min.z <= location.z && location.z <= max.z &&
                (max.x >= location.x - radius || min.x <= location.x + radius))
            velocity.x = -velocity.x;
    }
}
