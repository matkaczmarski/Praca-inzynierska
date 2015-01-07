package mini.paranormalgolf.Physics;


import android.content.Context;

import mini.paranormalgolf.Graphics.GraphicsData;
import mini.paranormalgolf.Graphics.ModelBuilders.ObjectGenerator;
import mini.paranormalgolf.Graphics.ShaderPrograms.DepthMapShaderProgram;
import mini.paranormalgolf.Graphics.ShaderPrograms.TextureShaderProgram;
import mini.paranormalgolf.Graphics.ShaderPrograms.ShadowingShaderProgram;
import mini.paranormalgolf.Helpers.ResourceHelper;
import mini.paranormalgolf.Primitives.Box;
import mini.paranormalgolf.Primitives.Cylinder;
import mini.paranormalgolf.Primitives.Point;
import mini.paranormalgolf.Primitives.Sphere;
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

    private final int MESH_DIMENSION = 32;
    public final float BALL_OPACITY = 1f;
    private final int STRIDE = (POSITION_COMPONENT_COUNT + NORMAL_COMPONENT_COUNT + TEXTURE_COMPONENT_COUNT) * 4;

    private final float CD =0.4f;
    private final float DENSITY =1.225f;

    private final float radius;
    private final float mass;
    private final float area;

    private float[] rotation;

    public float getRadius(){
        return this.radius;
    }

    public float[] getRotation() {return this.rotation;}

    public Ball(Point location, float radius, Vector velocity, BallTexture ballTexture, Context context) {
        super(velocity, location);

        this.radius = radius;
        mass=5;
        area=(float)Math.PI*radius*radius;

        rotation=new float[16];
        setIdentityM(rotation,0);

        GraphicsData generatedData = ObjectGenerator.createBall(location, radius, MESH_DIMENSION);
        vertexData = new VertexArray(generatedData.vertexData);
        drawCommands = generatedData.drawCommands;

        texture = loadTexture(ballTexture, context);
    }

    public int loadTexture(BallTexture ballTextureType, Context context){
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

    public void bindShadowData(ShaderProgram shaderProgram) {
        vertexData.setVertexAttribPointer(0, ((ShadowingShaderProgram)shaderProgram).getPositionAttributeLocation(), POSITION_COMPONENT_COUNT, STRIDE);
        vertexData.setVertexAttribPointer(POSITION_COMPONENT_COUNT, ((ShadowingShaderProgram)shaderProgram).getNormalAttributeLocation(), NORMAL_COMPONENT_COUNT, STRIDE);
        vertexData.setVertexAttribPointer(POSITION_COMPONENT_COUNT + NORMAL_COMPONENT_COUNT, ((ShadowingShaderProgram)shaderProgram).getTextureCoordinatesAttributeLocation(), TEXTURE_COMPONENT_COUNT, STRIDE);
    }

    public void bindDepthMapData(ShaderProgram shaderProgram) {
        vertexData.setVertexAttribPointer(0, ((DepthMapShaderProgram) shaderProgram).getPositionAttributeLocation(), POSITION_COMPONENT_COUNT, STRIDE);
    }

    public void Update(float dt,Vector accelerometrData,float mu) {
        //Update związany z poruszeniem się elementu
        int j;
        int numEqns = 6;
        float[] dq1,dq2,dq3,dq4;

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

        lastMove =new Vector(q[1] - location.x,q[3] - location.y,q[5] - location.z);

        if (lastMove.x != 0 || lastMove.z != 0) {
            float[] tmp=new float[16];
            float[] tmp2=new float[16];

            Vector axis = new Vector(lastMove.z, 0, -lastMove.x).normalize();
            float angle = (float) (360*Math.sqrt(lastMove.x * lastMove.x + lastMove.z * lastMove.z) /(2*Math.PI*radius));

            setRotateM(tmp,0,angle,axis.x,axis.y,axis.z);
            multiplyMM(tmp2,0,tmp,0,rotation,0);
            System.arraycopy(tmp2,0,rotation,0,16);
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
        float Fd = 0.5f * DENSITY * area * CD * v * v;
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
        return Collisions.CheckSphereAABBCollsion(new Sphere(location,radius),new Box(element.getLocation(),element.getMeasurements()));
    }

    public boolean CheckCollision(Floor element){
        return Collisions.CheckSphereAABBCollsion(new Sphere(location,radius),new Box(element.getLocation(),element.getMeasurements()));
    }

    public boolean CheckCollision(Diamond diamond) {
        if (diamond == null) return false;
        return Collisions.CheckSphereCylinderCollsion(new Sphere(location, radius), new Cylinder(diamond.location, diamond.getPyramid().radius, 2 * diamond.getPyramid().height));
    }

    public boolean CheckCollision(HourGlass hourGlass) {
        if (hourGlass == null) return false;
        return Collisions.CheckSphereCylinderCollsion(new Sphere(location, radius), new Cylinder(hourGlass.location, hourGlass.getLowerCone().getBottomRadius(), 2 * hourGlass.getLowerCone().getHeight()));
    }

    public boolean CheckCollision(Finish finish) {
        if (finish == null) return false;
        return Collisions.CheckSphereCylinderCollsion(new Sphere(location, radius), new Cylinder(finish.getLocation(),
                Math.min(finish.conicalFrustum.getBottomRadius(), finish.conicalFrustum.getTopRadius()), finish.conicalFrustum.getHeight()));
    }

    public boolean CheckCollision(CheckPoint checkPoint) {
        if (checkPoint == null) return false;
        return Collisions.CheckSphereCylinderCollsion(new Sphere(location, radius), new Cylinder(checkPoint.getLocation(),
                Math.min(checkPoint.conicalFrustum.getBottomRadius(), checkPoint.conicalFrustum.getTopRadius()), checkPoint.conicalFrustum.getHeight()));
    }

    public boolean CheckCollision(Beam element){
        return Collisions.CheckSphereAABBCollsion(new Sphere(location,radius),new Box(element.getLocation(),element.getMeasurements()));
    }

    public boolean CheckCollision(Elevator element){
        return Collisions.CheckSphereAABBCollsion(new Sphere(location,radius),new Box(element.getLocation(),element.getMeasurements()));
    }

    public void ReactOnCollision(Wall element) {
        Collisions.ResponseBallAABBCollisions(this,new Box(element.getLocation(),element.getMeasurements()));
        //find time collision
//        Point startLocation = new Point(location.x - lastMove.x, location.y - lastMove.y, location.z - lastMove.z);
//        Point endLocation = new Point(location.x, location.y, location.z);
//        Point halfLocation = new Point((startLocation.x + endLocation.x) / 2, (startLocation.y + endLocation.y) / 2, (startLocation.z + endLocation.z) / 2);
//        float startTime = 0, endTime = 1, halfTime = 0.5f;
//        Box collidedWall = new Box(element.getLocation(), element.getMeasurements());
//        for (int i = 0; i < 10; i++) {
//            if (Collisions.CheckSphereAABBCollsion(new Sphere(halfLocation, radius), collidedWall)) {
//                endTime = halfTime;
//                endLocation = halfLocation;
//            } else {
//                startTime = halfTime;
//                startLocation = halfLocation;
//            }
//            halfTime = (startTime + endTime) / 2;
//            halfLocation = new Point((startLocation.x + endLocation.x) / 2, (startLocation.y + endLocation.y) / 2, (startLocation.z + endLocation.z) / 2);
//        }
//        Point wallLocation = element.getLocation();
//        BoxSize wallSize = element.getMeasurements();
//        Vector normal = new Vector(0, 0, 0);
//        Point min = new Point(wallLocation.x - wallSize.x / 2, wallLocation.y - wallSize.y / 2, wallLocation.z - wallSize.z / 2);
//        Point max = new Point(wallLocation.x + wallSize.x / 2, wallLocation.y + wallSize.y / 2, wallLocation.z + wallSize.z / 2);
//
//        if (min.x - halfLocation.x > 0 && min.x - halfLocation.x <= radius + Collisions.USER_EXPERIENCE)
//            normal.x = -1;
//        else if (halfLocation.x - max.x > 0 && halfLocation.x - max.x <= radius + Collisions.USER_EXPERIENCE)
//            normal.x = 1;
//
//        if (min.y - halfLocation.y > 0 && min.y - halfLocation.y <= radius + Collisions.USER_EXPERIENCE)
//            normal.y = -1;
//        else if (halfLocation.y - max.y > 0 && halfLocation.y - max.y <= radius + Collisions.USER_EXPERIENCE)
//            normal.y = 1;
//
//        if (min.z - halfLocation.z > 0 && min.z - halfLocation.z <= radius + Collisions.USER_EXPERIENCE)
//            normal.z = -1;
//        else if (halfLocation.z - max.z > 0 && halfLocation.z - max.z <= radius + Collisions.USER_EXPERIENCE)
//            normal.z = 1;
//
//        if (normal.length() == 1) {
//            if (Math.abs(normal.x) == 1) velocity.x = -velocity.x;
//            else if (Math.abs(normal.z) == 1) velocity.z = -velocity.z;
//        } else {
//            if (Math.abs(normal.length() - Math.sqrt(3)) < Collisions.USER_EXPERIENCE) normal.y = 0;
//            normal = normal.normalize();
//            Vector normalVelocity = velocity.normalize();
//            normalVelocity = new Vector(-normalVelocity.x, -normalVelocity.y, -normalVelocity.z);
//            Vector newNormalVelocity = new Vector(0, 0, 0);
//
//            //odtąd jest założenie zerowego y
//            if (normal.y == 0) {
//                if (Math.abs(normalVelocity.z * normal.x - normal.z * normalVelocity.x) < 0.0001)
//                    velocity = new Vector(-velocity.x, velocity.y, -velocity.z);
//                else {
//                    float alfa = (float) Math.acos(normal.dotProduct(normalVelocity));
//                    newNormalVelocity.z =
//                            ((float) (normal.x * Math.cos(2 * alfa) - normal.x * normalVelocity.y * normalVelocity.y - Math.cos(alfa) * normalVelocity.x)) /
//                                    (normalVelocity.z * normal.x - normal.z * normalVelocity.x);
//                    newNormalVelocity.x =
//                            ((float) Math.cos(alfa) - newNormalVelocity.z * normal.z) / (normal.x);
//                    newNormalVelocity.y = normalVelocity.y;
//                    float length = velocity.length();
//                    velocity = new Vector(length * newNormalVelocity.x, length * newNormalVelocity.y, length * newNormalVelocity.z);
//                }
//            } else if (normal.x == 0) {
//                if (Math.abs(normalVelocity.z * normal.y - normal.z * normalVelocity.y) < 0.0001)
//                    velocity = new Vector(velocity.x, -velocity.y, -velocity.z);
//                else {
//                    float alfa = (float) Math.acos(normal.dotProduct(normalVelocity));
//
//                    newNormalVelocity.z =
//                            ((float) (normal.y * Math.cos(2 * alfa) - normal.y * normalVelocity.x * normalVelocity.x - Math.cos(alfa) * normalVelocity.y)) /
//                                    (normalVelocity.z * normal.y - normal.z * normalVelocity.y);
//                    newNormalVelocity.y =
//                            ((float) Math.cos(alfa) - newNormalVelocity.z * normal.z) / (normal.y);
//                    newNormalVelocity.x = normalVelocity.x;
//                    float length = velocity.length();
//                    velocity = new Vector(length * newNormalVelocity.x, length * newNormalVelocity.y, length * newNormalVelocity.z);
//                }
//            } else { //normal.z==0
//                if (Math.abs(normalVelocity.y * normal.x - normal.y * normalVelocity.x) < 0.0001)
//                    velocity = new Vector(-velocity.x, -velocity.y, velocity.z);
//                else {
//                    float alfa = (float) Math.acos(normal.dotProduct(normalVelocity));
//                    newNormalVelocity.y =
//                            ((float) (normal.x * Math.cos(2 * alfa) - normal.x * normalVelocity.z * normalVelocity.z - Math.cos(alfa) * normalVelocity.x)) /
//                                    (normalVelocity.y * normal.x - normal.y * normalVelocity.x);
//                    newNormalVelocity.x =
//                            ((float) Math.cos(alfa) - newNormalVelocity.y * normal.y) / (normal.x);
//                    newNormalVelocity.z = normalVelocity.z;
//                    float length = velocity.length();
//                    velocity = new Vector(length * newNormalVelocity.x, length * newNormalVelocity.y, length * newNormalVelocity.z);
//                }
//            }
//        }
//
//        location = new Point(halfLocation.x + velocity.x * (1 - halfTime) * Updater.INTERVAL_TIME,
//                halfLocation.y + velocity.y * (1 - halfTime) * Updater.INTERVAL_TIME,
//                halfLocation.z + velocity.z * (1 - halfTime) * Updater.INTERVAL_TIME);
    }

    public void ReactOnCollision(Floor element) {
        Collisions.ResponseBallAABBCollisions(this,new Box(element.getLocation(),element.getMeasurements()));
    }

    public void ReactOnCollision(Beam beam) {
        Collisions.ResponseBallMovingAABBCollisions(this, beam);
    }

    public void ReactOnCollision(Elevator elevator) {
        Collisions.ResponseBallMovingAABBCollisions(this, elevator);
    }
}
