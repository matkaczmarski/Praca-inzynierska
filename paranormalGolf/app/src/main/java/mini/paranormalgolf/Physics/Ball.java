package mini.paranormalgolf.Physics;

import mini.paranormalgolf.Graphics.GraphicsData;
import mini.paranormalgolf.Graphics.ModelBuilders.ObjectGenerator;
import mini.paranormalgolf.Graphics.ShaderPrograms.LightColorShaderProgram;
import mini.paranormalgolf.Primitives.Point;
import mini.paranormalgolf.Primitives.Vector;
import  mini.paranormalgolf.Graphics.ShaderPrograms.ShaderProgram;
import  mini.paranormalgolf.Graphics.ShaderPrograms.ColorShaderProgram;
import mini.paranormalgolf.Graphics.VertexArray;

/**
 * Created by Sławomir on 2014-12-03.
 */
public class Ball extends MovableElement {

    private final static float G=-9.81f;
    private final int MESH_DIMENSION = 32;

    public final float[] rgba = new float[] {0.0f, 0.0f, 0.9f, 1f};
    //
    private float radius;
    private float omega;
    private Vector axis;
    private float mass;
    private float area;
    private final static float Cd=0.4f;
    private final static float density=1.225f;

    public float getRadius(){
        return this.radius;
    }


    public Ball(Point location, float radius, Vector velocity) {
        super(velocity, location);
        omega=0;
        axis=new Vector(0,0,1);
        this.radius = radius;
        mass=5;
        area=(float)Math.PI*radius*radius;

        GraphicsData generatedData = ObjectGenerator.createBall(location, radius, MESH_DIMENSION);
        vertexData = new VertexArray(generatedData.vertexData);
        drawCommands = generatedData.drawCommands;

    }

    public void bindData(ShaderProgram colorProgram) {
        vertexData.setVertexAttribPointer(0, ((LightColorShaderProgram)colorProgram).getPositionAttributeLocation(), POSITION_COMPONENT_COUNT, STRIDE);
        vertexData.setVertexAttribPointer(POSITION_COMPONENT_COUNT, ((LightColorShaderProgram)colorProgram).getNormalAttributeLocation(), NORMAL_COMPONENT_COUNT, STRIDE);
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

        dq1 = SolveEquation(q, q, dt, 0.0f, accelerometrData,mu);
        dq2 = SolveEquation(q, dq1, dt, 0.5f, accelerometrData,mu);
        dq3 = SolveEquation(q, dq2, dt, 0.5f, accelerometrData,mu);
        dq4 = SolveEquation(q, dq3, dt, 1.0f, accelerometrData,mu);
        for (j = 0; j < numEqns; ++j) {
            q[j] = q[j] + (dq1[j] + 2.0f * dq2[j] + 2.0f * dq3[j] + dq4[j]) / 6.0f;
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
        if (mu < 0) {
            acceleration = CountAccelerationForFlying(accelerometrData, localVelocity);
        }
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
/*
    }

    public boolean CheckCollision(MovableElement element){
        return false;
    }
  //  public boolean CheckCollision(Bonus element){
   //     return false;
   // }
    public boolean CheckCollision(Wall element) {
        return false;
    }

    public void ReactOnCollision(MovableElement element){

    }

 //   public void ReactOnCollision(Bonus element){
//
 //   }

    public void ReactOnCollision(Wall element){

    }*/
}
