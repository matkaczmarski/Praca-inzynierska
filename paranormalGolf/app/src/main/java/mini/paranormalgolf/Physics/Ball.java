package mini.paranormalgolf.Physics;

import mini.paranormalgolf.Graphics.GraphicsData;
import mini.paranormalgolf.Graphics.ModelBuilders.ObjectGenerator;
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

    public final float[] rgba = new float[] {0f, 1f, 0f, 0.4f};
    //
    private float radius;
    private float omega;
    private Vector axis;
    private float mass;
    private float area;
    private final static float mu=0.1f;
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
        vertexData.setVertexAttribPointer(0, ((ColorShaderProgram)colorProgram).getPositionAttributeLocation(), POSITION_COMPONENT_COUNT, 0);
    }

    public void Update(float dt,Vector accelerometrData) {
        //Update związany z poruszeniem się elementu

        //jeśli jest na powierzchni to liczymy następująco
        SolveEquation(dt,accelerometrData);

        //  velocity.X = velocity.X + (-accelerometrData.X) * dt;
        //   velocity.Y = velocity.Y + (-accelerometrData.Y) * dt;
        //   velocity.Z = velocity.Z + (-accelerometrData.Z) * dt;
        //   location.X = location.X + velocity.X * dt + 0.5f * (-accelerometrData.X) * dt * dt;
        //    location.Y = location.Y + velocity.Y * dt + 0.5f * (-accelerometrData.Y) * dt * dt;
        //    location.Z = location.Z + velocity.Z * dt + 0.5f * (-accelerometrData.Z) * dt * dt;
    }


    private void SolveEquation(float dt,Vector accelerometrData) {

        int j;
        int numEqns = 6;
        float[] dq1 = new float[numEqns];
        float[] dq2 = new float[numEqns];
        float[] dq3 = new float[numEqns];
        float[] dq4 = new float[numEqns];
// Retrieve the current values of the dependent
// and independent variables.
        float[] q = new float[6];
        {
            q[1] = location.x;
            q[0] = velocity.X;
            q[3] = location.y;
            q[2] = velocity.Y;
            q[5] = location.z;
            q[4] = velocity.Z;
        }
        // q=SolveEquation(q,dt,accelerometrData);
        if (true) {
            dq1 = RollingBall(q, q, dt, 0.0f, accelerometrData);
            dq2 = RollingBall(q, dq1, dt, 0.5f, accelerometrData);
            dq3 = RollingBall(q, dq2, dt, 0.5f, accelerometrData);
            dq4 = RollingBall(q, dq3, dt, 1.0f, accelerometrData);
        } else {

        }
        for (j = 0; j < numEqns; ++j) {
            q[j] = q[j] + (dq1[j] + 2.0f * dq2[j] + 2.0f * dq3[j] + dq4[j]) / 6.0f;
        }
        {
            location.x = q[1];
            velocity.X = q[0];
            location.y = q[3];
            velocity.Y = q[2];
            location.z = q[5];
            velocity.Z = q[4];
        }
        return;
    }

    private float[] RollingBall(float[] q, float deltaQ[], float dt, float qScale, Vector accelerometrData) {
        float dQ[] = new float[6];
// Compute the intermediate values of the
// dependent variables.
        for (int i = 0; i < 6; ++i) {
            q[i] = q[i] + qScale * deltaQ[i];
        }
// Declare some convenience variables representing
// the intermediate values of velocity.
        float vx = q[0];
        float vy = q[2];
        float vz = q[4];
// Compute the velocity magnitude. The 1.0e-8 term
// ensures there won't be a divide by zero later on
// if all of the velocity components are zero.
        float v = (float) (Math.sqrt(vx * vx + vy * vy + vz * vz) + 1e-8);
//
        float Fd = 0.5f * density * area * Cd * v * v;
        float Fr = mu * accelerometrData.Y * mass;
// Compute the right-hand sides of the six ODEs.
        dQ[0] = dt * (accelerometrData.X - (Fd + Fr) * vx / (mass * v));
        dQ[1] = dt * vx;
        dQ[2] = 0;//dt * (-accelerometrData.Y - Fd * vy / (mass * v));
        dQ[3] = 0;//dt * vy;
        dQ[4] = dt * (accelerometrData.Z - (Fd + Fr) * vz / (mass * v));
        dQ[5] = dt * vz;
        return dQ;
    }
/*


        //jeśli jest w powietrzu, to liczymy następująco:
        //TODO
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
