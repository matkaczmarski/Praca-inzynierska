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
        vertexData.setVertexAttribPointer(0, ((ColorShaderProgram)colorProgram).getPositionAttributeLocation(), POSITION_COMPONENT_COUNT, 0);
    }


    public void update(float dt, Vector accelerometrData) {
        //update związany z poruszeniem się elementu

        //jeśli jest na powierzchni to liczymy następująco
        //TODO
        //olveSEquation
        SolveEquation(dt,accelerometrData);

      //  velocity.x = velocity.x + (-accelerometrData.x) * dt;
     //   velocity.y = velocity.y + (-accelerometrData.y) * dt;
     //   velocity.z = velocity.z + (-accelerometrData.z) * dt;
     //   location.x = location.x + velocity.x * dt + 0.5f * (-accelerometrData.x) * dt * dt;
    //    location.y = location.y + velocity.y * dt + 0.5f * (-accelerometrData.y) * dt * dt;
    //    location.z = location.z + velocity.z * dt + 0.5f * (-accelerometrData.z) * dt * dt;
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
        float newQ[] = new float[6];
// Compute the intermediate values of the
// dependent variables.
        for (int i = 0; i < 6; ++i) {
            newQ[i] = q[i] + qScale * deltaQ[i];
        }
// Declare some convenience variables representing
// the intermediate values of velocity.
        float vx = newQ[0];
        float vy = newQ[2];
        float vz = newQ[4];
// Compute the velocity magnitude. The 1.0e-8 term
// ensures there won't be a divide by zero later on
// if all of the velocity components are zero.
        float v = (float) (Math.sqrt(vx * vx + vy * vy + vz * vz) + 1e-8);
//
        float Fd = 0.5f * density * area * Cd * v * v;
// Compute the right-hand sides of the six ODEs.
        dQ[0] = dt * (-accelerometrData.X - Fd * vx / (mass * v));
        dQ[1] = dt * vx;
        dQ[2] = dt * (-accelerometrData.Y - Fd * vy / (mass * v));
        dQ[3] = dt * vy;
        dQ[4] = q[4];//dt * (-accelerometrData.z - Fd * vz / (mass * v));
        dQ[5] = q[5];//dt * vz;
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
