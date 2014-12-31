package mini.paranormalgolf.Physics;


import android.content.Context;

import mini.paranormalgolf.Graphics.GraphicsData;
import mini.paranormalgolf.Graphics.ModelBuilders.ObjectGenerator;
import mini.paranormalgolf.Graphics.ShaderPrograms.TextureShaderProgram;
import mini.paranormalgolf.Helpers.ResourceHelper;
import mini.paranormalgolf.Primitives.Box;
import mini.paranormalgolf.Primitives.BoxSize;
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

    private final static float G=-9.81f;
    private final int MESH_DIMENSION = 32;
    public final float BALL_OPACITY = 1f;
    private final int STRIDE = (POSITION_COMPONENT_COUNT + NORMAL_COMPONENT_COUNT + TEXTURE_COMPONENT_COUNT) * 4;
    public final static float USER_EXPERIENCE=4e-4f;

    public final float[] rgba = new float[] {0.0f, 0.0f, 0.9f, 1f};
    public final float[] rotation=new float[16];
    //
    private float radius;
    private float omega;
    public Vector axis;
    public float angle;
    public Vector difference;

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
        this.radius = radius;
        mass=5;
        area=(float)Math.PI*radius*radius;
        setIdentityM(rotation,0);
        difference=new Vector(0,0,0);

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

        difference=new Vector(q[1] - location.x,q[3] - location.y,q[5] - location.z);

        if (difference.x != 0 || difference.z != 0) {
            float[] helpMatrix=new float[16];
            float[] helpMatrix2=new float[16];

            axis = new Vector(difference.z, 0, -difference.x).normalize();
            angle = (float) (360*Math.sqrt(difference.x * difference.x + difference.z * difference.z) /(2*Math.PI*radius));

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

    public void ReactOnCollision(MovableElement element){

    }

    public void ReactOnCollision(Bonus element) {

    }

    public void ReactOnCollision(Wall element) {
        //find time collision
        Point startLocation=new Point(location.x-difference.x,location.y-difference.y,location.z-difference.z);
        Point endLocation=new Point(location.x,location.y,location.z);
        Point halfLocation= new Point((startLocation.x + endLocation.x) / 2, (startLocation.y + endLocation.y) / 2, (startLocation.z + endLocation.z) / 2);
        float startTime=0,endTime=1,halfTime=0.5f;
        Box collidedWall=new Box(element.getLocation(),element.getMeasurements());
        for(int i=0;i<10;i++) {
            if (Collisions.CheckSphereAABBCollsion(new Sphere(halfLocation, radius), collidedWall)) {
                endTime = halfTime;
                endLocation = halfLocation;
            } else {
                startTime = halfTime;
                startLocation = halfLocation;
            }
            halfTime = (startTime + endTime) / 2;
            halfLocation = new Point((startLocation.x + endLocation.x) / 2, (startLocation.y + endLocation.y) / 2, (startLocation.z + endLocation.z) / 2);
        }
        Point wallLocation = element.getLocation();
        BoxSize wallSize = element.getMeasurements();
        Vector normal=new Vector(0,0,0);
        Point min = new Point(wallLocation.x - wallSize.x / 2, wallLocation.y - wallSize.y / 2, wallLocation.z - wallSize.z / 2);
        Point max = new Point(wallLocation.x + wallSize.x / 2, wallLocation.y + wallSize.y / 2, wallLocation.z + wallSize.z / 2);

        if(min.x-halfLocation.x>0&&min.x-halfLocation.x<=radius+USER_EXPERIENCE)normal.x=-1;
        else if(halfLocation.x-max.x>0&&halfLocation.x-max.x<=radius+USER_EXPERIENCE)normal.x=1;

        if(min.y-halfLocation.y>0&&min.y-halfLocation.y<=radius+USER_EXPERIENCE)normal.y=-1;
        else if(halfLocation.y-max.y>0&&halfLocation.y-max.y<=radius+USER_EXPERIENCE)normal.y=1;

        if(min.z-halfLocation.z>0&&min.z-halfLocation.z<=radius+USER_EXPERIENCE)normal.z=-1;
        else if(halfLocation.z-max.z>0&&halfLocation.z-max.z<=radius+USER_EXPERIENCE) normal.z=1;

        if(normal.length()==1) {
            if (Math.abs(normal.x) == 1) velocity.x = -velocity.x;
            else if (Math.abs(normal.z) == 1) velocity.z = -velocity.z;
        }
        else {
            if (Math.abs(normal.length() - Math.sqrt(3)) < USER_EXPERIENCE) normal.y = 0;
            normal = normal.normalize();
            Vector normalVelocity = velocity.normalize();
            normalVelocity = new Vector(-normalVelocity.x, -normalVelocity.y, -normalVelocity.z);
            Vector newNormalVelocity = new Vector(0, 0, 0);

            //odtąd jest założenie zerowego y
            if (normal.y == 0) {
                if (Math.abs(normalVelocity.z * normal.x - normal.z * normalVelocity.x) < 0.0001)
                    velocity = new Vector(-velocity.x, velocity.y, -velocity.z);
                else {
                    float alfa = (float) Math.acos(normal.dotProduct(normalVelocity));
                    newNormalVelocity.z =
                            ((float) (normal.x * Math.cos(2 * alfa) - normal.x * normalVelocity.y * normalVelocity.y - Math.cos(alfa) * normalVelocity.x)) /
                                    (normalVelocity.z * normal.x - normal.z * normalVelocity.x);
                    newNormalVelocity.x =
                            ((float) Math.cos(alfa) - newNormalVelocity.z * normal.z) / (normal.x);
                    newNormalVelocity.y = normalVelocity.y;
                    float length = velocity.length();
                    velocity = new Vector(length * newNormalVelocity.x, length * newNormalVelocity.y, length * newNormalVelocity.z);
                }
            }
            else if(normal.x==0){
                if (Math.abs(normalVelocity.z * normal.y - normal.z * normalVelocity.y) < 0.0001)
                    velocity = new Vector(velocity.x, -velocity.y, -velocity.z);
                else {
                    float alfa = (float) Math.acos(normal.dotProduct(normalVelocity));

                    newNormalVelocity.z =
                            ((float) (normal.y * Math.cos(2 * alfa) - normal.y * normalVelocity.x * normalVelocity.x - Math.cos(alfa) * normalVelocity.y)) /
                                    (normalVelocity.z * normal.y - normal.z * normalVelocity.y);
                    newNormalVelocity.y =
                            ((float) Math.cos(alfa) - newNormalVelocity.z * normal.z) / (normal.y);
                    newNormalVelocity.x = normalVelocity.x;
                    float length = velocity.length();
                    velocity = new Vector(length * newNormalVelocity.x, length * newNormalVelocity.y, length * newNormalVelocity.z);
                }
            }
            else { //normal.z==0
                if (Math.abs(normalVelocity.y * normal.x - normal.y * normalVelocity.x) < 0.0001)
                    velocity = new Vector(-velocity.x, -velocity.y, velocity.z);
                else {
                    float alfa = (float) Math.acos(normal.dotProduct(normalVelocity));
                    newNormalVelocity.y =
                            ((float) (normal.x * Math.cos(2 * alfa) - normal.x * normalVelocity.z * normalVelocity.z - Math.cos(alfa) * normalVelocity.x)) /
                                    (normalVelocity.y * normal.x - normal.y * normalVelocity.x);
                    newNormalVelocity.x =
                            ((float) Math.cos(alfa) - newNormalVelocity.y * normal.y) / (normal.x);
                    newNormalVelocity.z = normalVelocity.z;
                    float length = velocity.length();
                    velocity = new Vector(length * newNormalVelocity.x, length * newNormalVelocity.y, length * newNormalVelocity.z);
                }
            }
        }

        location=new Point(halfLocation.x+velocity.x*(1-halfTime)*0.035f,
                halfLocation.y+velocity.y*(1-halfTime)*0.035f,
                halfLocation.z+velocity.z*(1-halfTime)*0.035f);

//        Point limit=new Point(difference.x>0?min.x:max.x,difference.y>0?min.y:max.y,difference.z>0?min.z:max.z);
//
//        float percentToX=(Math.abs(limit.x-lastLocation.x)-radius)/Math.abs(difference.x);
//        float percentToY=(Math.abs(limit.y-lastLocation.y)-radius)/Math.abs(difference.y);
//        float percentToZ=(Math.abs(limit.z-lastLocation.z)-radius)/Math.abs(difference.z);
//        float newX=-10000,newY=-10000,newZ=-10000;
//        Vector normal=new Vector(0,0,0);
//        if(percentToZ>0&&percentToZ<1)
//            newX=lastLocation.x+percentToZ*difference.x;
//        if(percentToX>0&&percentToX<1)
//            newZ=lastLocation.z+percentToX*difference.z;
//        if(newX>=min.x&&newX<=max.x)
//            normal.z=-Math.signum(difference.z);
//        else if(newZ>=min.z&&newZ<=max.z)
//            normal.x=-Math.signum(difference.x);
//        else {
//            normal.x = newX < min.x ? -1 : 1;
//            normal.z = newZ < min.z ? -1 : 1;
//            normal = normal.normalize();
//        }
//        Vector tangential=new Vector(-normal.z,0,normal.x);
//        float b=(velocity.x*tangential.z-velocity.z*tangential.x)/(normal.x*tangential.z-normal.z*tangential.x);
//        velocity=new Vector(velocity.x-2*b*normal.x,0,velocity.z-2*b*normal.z);

//        if (min.x <= location.x && location.x <= max.x && min.y <= location.y && location.y <= max.y &&
//                (max.z >= location.z - radius || min.z <= location.z + radius))
//            velocity.z = -velocity.z;
//        if (min.x <= location.x && location.x <= max.x && min.z <= location.z && location.z <= max.z &&
//                (max.y >= location.y - radius || min.y <= location.y + radius))
//            velocity.y = -velocity.y;
//        if (min.y <= location.y && location.y <= max.y && min.z <= location.z && location.z <= max.z &&
//                (max.x >= location.x - radius || min.x <= location.x + radius))
//            velocity.x = -velocity.x;
    }

    public void ReactOnCollision(Floor element) {
        //find time collision
        Point startLocation=new Point(location.x-difference.x,location.y-difference.y,location.z-difference.z);
        Point endLocation=new Point(location.x,location.y,location.z);
        Point halfLocation= new Point((startLocation.x + endLocation.x) / 2, (startLocation.y + endLocation.y) / 2, (startLocation.z + endLocation.z) / 2);
        float startTime=0,endTime=1,halfTime=0.5f;
        Box collidedWall=new Box(element.getLocation(),element.getMeasurements());
        for(int i=0;i<10;i++) {
            if (Collisions.CheckSphereAABBCollsion(new Sphere(halfLocation, radius), collidedWall)) {
                endTime = halfTime;
                endLocation = halfLocation;
            } else {
                startTime = halfTime;
                startLocation = halfLocation;
            }
            halfTime = (startTime + endTime) / 2;
            halfLocation = new Point((startLocation.x + endLocation.x) / 2, (startLocation.y + endLocation.y) / 2, (startLocation.z + endLocation.z) / 2);
        }
        Point wallLocation = element.getLocation();
        BoxSize wallSize = element.getMeasurements();
        Vector normal=new Vector(0,0,0);
        Point min = new Point(wallLocation.x - wallSize.x / 2, wallLocation.y - wallSize.y / 2, wallLocation.z - wallSize.z / 2);
        Point max = new Point(wallLocation.x + wallSize.x / 2, wallLocation.y + wallSize.y / 2, wallLocation.z + wallSize.z / 2);

        if(min.x-halfLocation.x>0&&min.x-halfLocation.x<=radius+USER_EXPERIENCE)normal.x=-1;
        else if(halfLocation.x-max.x>0&&halfLocation.x-max.x<=radius+USER_EXPERIENCE)normal.x=1;

        if(min.y-halfLocation.y>0&&min.y-halfLocation.y<=radius+USER_EXPERIENCE)normal.y=-1;
        else if(halfLocation.y-max.y>0&&halfLocation.y-max.y<=radius+USER_EXPERIENCE)normal.y=1;

        if(min.z-halfLocation.z>0&&min.z-halfLocation.z<=radius+USER_EXPERIENCE)normal.z=-1;
        else if(halfLocation.z-max.z>0&&halfLocation.z-max.z<=radius+USER_EXPERIENCE) normal.z=1;

        if(normal.length()==1) {
            if (Math.abs(normal.x) == 1) velocity.x = -velocity.x;
            else if (Math.abs(normal.z) == 1) velocity.z = -velocity.z;
            else // normal.y!=0
                velocity.y=-velocity.y;
        }
        else {
            if (Math.abs(normal.length() - Math.sqrt(3)) < USER_EXPERIENCE) normal.y = 0;
            normal = normal.normalize();
            Vector normalVelocity = velocity.normalize();
            normalVelocity = new Vector(-normalVelocity.x, -normalVelocity.y, -normalVelocity.z);
            Vector newNormalVelocity = new Vector(0, 0, 0);

            //odtąd jest założenie zerowego y
            if (normal.y == 0) {
                Vector velocityWithOutY =new Vector( -velocity.x,0, -velocity.z);
                normalVelocity = velocityWithOutY.normalize();
                if (Math.abs(normalVelocity.z * normal.x - normal.z * normalVelocity.x) < 0.0001)
                    velocity = new Vector(-velocity.x, velocity.y, -velocity.z);
                else {
                    float alfa = (float) Math.acos(normal.dotProduct(normalVelocity));

                    newNormalVelocity.z =
                            ((float) (normal.x * Math.cos(2 * alfa)  - Math.cos(alfa) * normalVelocity.x)) /
                                    (normalVelocity.z * normal.x - normal.z * normalVelocity.x);
                    newNormalVelocity.x =
                            ((float) Math.cos(alfa) - newNormalVelocity.z * normal.z) / (normal.x);
                    // newNormalVelocity.x = normalVelocity.x;
                    float length = velocityWithOutY.length();
                    velocity = new Vector(length * newNormalVelocity.x,velocity.y, length * newNormalVelocity.z);
                }
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
            }
            else if(normal.x==0){
                Vector velocityWithOutX =new Vector(0, -velocity.y, -velocity.z);
                normalVelocity = velocityWithOutX.normalize();
                if (Math.abs(normalVelocity.z * normal.y - normal.z * normalVelocity.y) < 0.0001)
                    velocity = new Vector(velocity.x, -velocity.y, -velocity.z);
                else {
                    float alfa = (float) Math.acos(normal.dotProduct(normalVelocity));

                    newNormalVelocity.z =
                            ((float) (normal.y * Math.cos(2 * alfa)  - Math.cos(alfa) * normalVelocity.y)) /
                                    (normalVelocity.z * normal.y - normal.z * normalVelocity.y);
                    newNormalVelocity.y =
                            ((float) Math.cos(alfa) - newNormalVelocity.z * normal.z) / (normal.y);
                   // newNormalVelocity.x = normalVelocity.x;
                    float length = velocityWithOutX.length();
                    velocity = new Vector(velocity.x, length * newNormalVelocity.y, length * newNormalVelocity.z);
                }
            }
            else { //normal.z==0
                Vector velocityWithOutZ =new Vector(-velocity.x, -velocity.y,0);
                normalVelocity = velocityWithOutZ.normalize();
                if (Math.abs(normalVelocity.y * normal.x - normal.y * normalVelocity.x) < 0.0001)
                    velocity = new Vector(-velocity.x, -velocity.y, velocity.z);
                else {
                    float alfa = (float) Math.acos(normal.dotProduct(normalVelocity));

                    newNormalVelocity.y =
                            ((float) (normal.x * Math.cos(2 * alfa) - Math.cos(alfa) * normalVelocity.x)) /
                                    (normalVelocity.y * normal.x - normal.y * normalVelocity.x);
                    newNormalVelocity.x =
                            ((float) Math.cos(alfa) - newNormalVelocity.y * normal.y) / (normal.x);
                    // newNormalVelocity.z = normalVelocity.x;
                    float length = velocityWithOutZ.length();
                    velocity = new Vector(length * newNormalVelocity.x, length * newNormalVelocity.y, velocity.z);
                }


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
            }
        }

        location=new Point(halfLocation.x+velocity.x*(1-halfTime)*Updater.INTERVAL_TIME,
                halfLocation.y+velocity.y*(1-halfTime)*Updater.INTERVAL_TIME,
                halfLocation.z+velocity.z*(1-halfTime)*Updater.INTERVAL_TIME);

//        Point limit=new Point(difference.x>0?min.x:max.x,difference.y>0?min.y:max.y,difference.z>0?min.z:max.z);
//
//        float percentToX=(Math.abs(limit.x-lastLocation.x)-radius)/Math.abs(difference.x);
//        float percentToY=(Math.abs(limit.y-lastLocation.y)-radius)/Math.abs(difference.y);
//        float percentToZ=(Math.abs(limit.z-lastLocation.z)-radius)/Math.abs(difference.z);
//        float newX=-10000,newY=-10000,newZ=-10000;
//        Vector normal=new Vector(0,0,0);
//        if(percentToZ>0&&percentToZ<1)
//            newX=lastLocation.x+percentToZ*difference.x;
//        if(percentToX>0&&percentToX<1)
//            newZ=lastLocation.z+percentToX*difference.z;
//        if(newX>=min.x&&newX<=max.x)
//            normal.z=-Math.signum(difference.z);
//        else if(newZ>=min.z&&newZ<=max.z)
//            normal.x=-Math.signum(difference.x);
//        else {
//            normal.x = newX < min.x ? -1 : 1;
//            normal.z = newZ < min.z ? -1 : 1;
//            normal = normal.normalize();
//        }
//        Vector tangential=new Vector(-normal.z,0,normal.x);
//        float b=(velocity.x*tangential.z-velocity.z*tangential.x)/(normal.x*tangential.z-normal.z*tangential.x);
//        velocity=new Vector(velocity.x-2*b*normal.x,0,velocity.z-2*b*normal.z);

//        if (min.x <= location.x && location.x <= max.x && min.y <= location.y && location.y <= max.y &&
//                (max.z >= location.z - radius || min.z <= location.z + radius))
//            velocity.z = -velocity.z;
//        if (min.x <= location.x && location.x <= max.x && min.z <= location.z && location.z <= max.z &&
//                (max.y >= location.y - radius || min.y <= location.y + radius))
//            velocity.y = -velocity.y;
//        if (min.y <= location.y && location.y <= max.y && min.z <= location.z && location.z <= max.z &&
//                (max.x >= location.x - radius || min.x <= location.x + radius))
//            velocity.x = -velocity.x;
    }
}
