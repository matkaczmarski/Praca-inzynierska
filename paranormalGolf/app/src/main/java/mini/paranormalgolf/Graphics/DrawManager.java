package mini.paranormalgolf.Graphics;

import android.content.Context;

import mini.paranormalgolf.Graphics.ShaderPrograms.SkyboxShaderProgram;
import mini.paranormalgolf.Graphics.ShaderPrograms.TextureLightShaderProgram;
import mini.paranormalgolf.Physics.Ball;
import mini.paranormalgolf.Physics.Diamond;
import mini.paranormalgolf.Physics.Floor;
import mini.paranormalgolf.Physics.FloorPart;
import mini.paranormalgolf.Physics.Wall;
import mini.paranormalgolf.Primitives.Point;
import mini.paranormalgolf.Primitives.Vector;

import static android.opengl.GLES20.GL_BLEND;
import static android.opengl.GLES20.GL_LEQUAL;
import static android.opengl.GLES20.GL_LESS;
import static android.opengl.GLES20.GL_ONE_MINUS_SRC_ALPHA;
import static android.opengl.GLES20.GL_SRC_ALPHA;
import static android.opengl.GLES20.glBlendFunc;
import static android.opengl.GLES20.glDepthFunc;
import static android.opengl.GLES20.glDisable;
import static android.opengl.GLES20.glEnable;
import static android.opengl.GLES20.glViewport;
import static android.opengl.Matrix.invertM;
import static android.opengl.Matrix.multiplyMM;
import static android.opengl.Matrix.multiplyMV;
import static android.opengl.Matrix.setIdentityM;
import static android.opengl.Matrix.setLookAtM;
import static android.opengl.Matrix.translateM;
import static android.opengl.Matrix.rotateM;
import static android.opengl.Matrix.transposeM;

/**
 * Created by Mateusz on 2014-12-13.
 */
public class DrawManager {

    private final float[] projectionMatrix = new float[16];
    private final float[] modelMatrix = new float[16];
    private final float[] viewMatrix = new float[16];
    private final float[] viewProjectionMatrix = new float[16];
    private final float[] modelViewMatrix = new float[16];
    private final float[] modelViewProjectionMatrix = new float[16];
    private final float[] itModelViewMatrix = new float[16];
    private final float[] tempMatrix = new float[16];

    private Context context;

    private Vector lightPos =  new Vector(0f, 10f, 0f);//.normalize();

    private final float fieldOfViewDegree = 45;
    private final float near = 1f;
    private final float far = 100f;

    private final Point cameraTranslation = new Point(0f, 15f, 15f);

//    private ColorShaderProgram colorShaderProgram;
//    private LightColorShaderProgram lightColorShaderProgram;
//    private TextureShaderProgram textureShaderProgram;
    private TextureLightShaderProgram textureLightShaderProgram;


    private SkyboxShaderProgram skyboxShaderProgram;
    private Skybox skybox;


    public DrawManager(Context context) {
        this.context = context;
        textureLightShaderProgram = new TextureLightShaderProgram(context);
        skyboxShaderProgram = new SkyboxShaderProgram(context);
        skybox = new Skybox(context, new Point(0,0,0), Skybox.SkyboxTexture.stars);
    }

    public void surfaceChange(int width, int height){
        glViewport(0, 0, width, height);
        MatrixHelper.perspectiveM(projectionMatrix, fieldOfViewDegree, (float) width / (float) height, near, far);
    }

    public void preDraw(Point ballLocation){
        setLookAtM(viewMatrix, 0, ballLocation.X + cameraTranslation.X, ballLocation.Y + cameraTranslation.Y, ballLocation.Z + cameraTranslation.Z, ballLocation.X, ballLocation.Y, ballLocation.Z, 0f, 1f, 0f);
        multiplyMM(viewProjectionMatrix, 0, projectionMatrix, 0, viewMatrix, 0);
    }


   public void drawBall(Ball ball, float rotationAngle, Vector rotationAxis){
//        lightColorShaderProgram.useProgram();
//        positionObjectInScene(ball.getLocation());
//        lightColorShaderProgram.setUniforms(modelViewProjectionMatrix, modelViewMatrix, ball.rgba,lightPos);
//        ball.bindData(lightColorShaderProgram);
//        ball.draw();
       textureLightShaderProgram.useProgram();
       positionBallInScene(ball);
       textureLightShaderProgram.setUniforms(modelViewProjectionMatrix, modelViewMatrix, itModelViewMatrix,  lightPos, ball.getTexture(), ball.BALL_OPACITY);
       ball.bindData(textureLightShaderProgram);
       ball.draw();
    }


    public void drawDiamond(Diamond diamond){
        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

        textureLightShaderProgram.useProgram();
        positionDiamondInScene(diamond);
        textureLightShaderProgram.setUniforms(modelViewProjectionMatrix, modelViewMatrix, itModelViewMatrix, lightPos, diamond.getTexture(), diamond.DIAMOND_OPACITY);
        diamond.bindData(textureLightShaderProgram);
        diamond.draw();

        glDisable(GL_BLEND);
    }


   public void drawFloor(Floor floor){
        textureLightShaderProgram.useProgram();

        positionObjectInScene(floor.getBottomPart().getLocation());
        textureLightShaderProgram.setUniforms(modelViewProjectionMatrix, modelViewMatrix, modelViewMatrix, lightPos, floor.getBottomFloorTexture(), floor.FLOOR_OPACITY);
        floor.getBottomPart().bindData(textureLightShaderProgram);
        floor.getBottomPart().draw();

        for(FloorPart floorPart : floor.getSideParts()){
            positionObjectInScene(floorPart.getLocation());
            textureLightShaderProgram.setUniforms(modelViewProjectionMatrix, modelViewMatrix, modelViewMatrix, lightPos, floor.getBottomFloorTexture(), floor.FLOOR_OPACITY);
            floorPart.bindData(textureLightShaderProgram);
            floorPart.draw();
        }

        positionObjectInScene(floor.getTopPart().getLocation());
        textureLightShaderProgram.setUniforms(modelViewProjectionMatrix, modelViewMatrix, modelViewMatrix, lightPos, floor.getTopFloorTexture(), floor.FLOOR_OPACITY);
        floor.getTopPart().bindData(textureLightShaderProgram);
        floor.getTopPart().draw();
    }

    public void drawWall(Wall wall){
        textureLightShaderProgram.useProgram();
        positionObjectInScene(wall.getLocation());
        textureLightShaderProgram.setUniforms(modelViewProjectionMatrix, modelViewMatrix, modelViewMatrix, lightPos, wall.getTexture(), wall.WALL_OPACITY);
        wall.bindData(textureLightShaderProgram);
        wall.draw();
    }



    public void drawSkybox() {
        setIdentityM(modelMatrix, 0);
        setIdentityM(viewMatrix, 0);
       // rotateM(viewMatrix, 0, 2f, 1f, 0f, 0f);
        multiplyMM(modelViewMatrix, 0, viewMatrix, 0, modelMatrix, 0);
        multiplyMM(modelViewProjectionMatrix, 0, projectionMatrix, 0, modelViewMatrix, 0);


        glDepthFunc(GL_LEQUAL);
        skyboxShaderProgram.useProgram();
        skyboxShaderProgram.setUniforms(modelViewProjectionMatrix, skybox.getTexture());
        skybox.bindData(skyboxShaderProgram);
        skybox.draw();
        glDepthFunc(GL_LESS);
    }




    private void positionBallInScene(Ball ball){
        float[] helpMatrix=new float[16];
        float[] result=new float[4];
        setIdentityM(helpMatrix,0);
        setIdentityM(modelMatrix, 0);
        translateM(modelMatrix, 0, ball.getLocation().X, ball.getLocation().Y, ball.getLocation().Z);
        //obrót-start
        if(ball.pole.Y!=1) {
            if (ball.pole.X != 0 && ball.pole.Z != 0) {
                // Vector axis=new Vector(-ball.pole.Z,0,ball.pole.X).normalize();
                if (ball.pole.X > 0) {
                    rotateM(modelMatrix, 0, (float) (360 * Math.acos(ball.pole.Y) / (2 * Math.PI)), -ball.pole.Z, 0, ball.pole.X);
                    rotateM(helpMatrix, 0, (float) (360 * Math.acos(ball.pole.Y) / (2 * Math.PI)), -ball.pole.Z, 0, ball.pole.X);
                    multiplyMV(result, 0, helpMatrix, 0, new float[]{1, 0, 0, 1}, 0);

                } else {
                    rotateM(modelMatrix, 0, (float) (360 * Math.acos(ball.pole.Y) / (2 * Math.PI)),ball.pole.Z, 0, -ball.pole.X);
                    rotateM(helpMatrix, 0, (float) (360 * Math.acos(ball.pole.Y) / (2 * Math.PI)), ball.pole.Z, 0, -ball.pole.X);
                    multiplyMV(result, 0, helpMatrix, 0, new float[]{1, 0, 0, 1}, 0);
                }
            } else {
                rotateM(modelMatrix, 0, 180, 0, 0, 1);
                rotateM(helpMatrix, 0, 180, 0, 0, 1);
                multiplyMV(result, 0, helpMatrix, 0, new float[]{1, 0, 0, 1}, 0);
            }
            float d = (float) Math.sqrt((result[0] - ball.onEquator.X) * (result[0] - ball.onEquator.X) + (result[1] - ball.onEquator.Y) * (result[1] - ball.onEquator.Y) +
                    (result[2] - ball.onEquator.Z) * (result[2] - ball.onEquator.Z));
            float alfa = (float) (360 * Math.acos((2 - d * d) / 2) / (2 * Math.PI));
            float sign = (-ball.onEquator.Z) * (result[0] - ball.onEquator.X) - (-ball.onEquator.X) * (result[2] - ball.onEquator.Z);
            if (sign > 0)
                rotateM(modelMatrix, 0, alfa, -ball.pole.X, -ball.pole.Y, -ball.pole.Z);
            else
                rotateM(modelMatrix, 0, alfa, ball.pole.X, ball.pole.Y, ball.pole.Z);
        }
      //  rotateM(modelMatrix, 0, (float)(360*Math.acos(pole.Y)/(2*Math.PI)), pole.Z, 0, -pole.X);
        multiplyMM(modelViewProjectionMatrix, 0, viewProjectionMatrix, 0, modelMatrix, 0);
        multiplyMM(modelViewMatrix, 0, viewMatrix, 0, modelMatrix, 0);
        invertM(tempMatrix, 0, modelViewMatrix, 0);
        transposeM(itModelViewMatrix, 0, tempMatrix, 0);
    }

    private Point rotatePoint(Point point,float angle,Vector axis){
        float fi;
        if (axis.X > 0 && axis.Z > 0)
            fi = (float) Math.atan(axis.X / axis.Z);
        else if (axis.Z < 0)
            fi = (float) Math.PI + (float) Math.atan(axis.X / axis.Z);
        else
            fi = (float) (2 * Math.PI) + (float) Math.atan(axis.X / axis.Z);

        Point newPole = new Point(0, point.Y, 0);
        newPole.X = (float) (point.X * Math.cos(fi) - point.Z * Math.sin(fi));
        newPole.Z = (float) (point.X * Math.sin(fi) + point.Z * Math.cos(fi));
        point = new Point(newPole.X, newPole.Y, newPole.Z);

        newPole = new Point(0, 0, point.Z);
        newPole.X = (float) (point.X * Math.cos(angle) - point.Y * Math.sin(angle));
        newPole.Y = (float) (point.X * Math.sin(angle) + point.Y * Math.cos(angle));
        point = new Point(newPole.X, newPole.Y, newPole.Z);

        newPole = new Point(0, point.Y, 0);
        newPole.X = (float) (point.X * Math.cos(fi) + point.Z * Math.sin(fi));
        newPole.Z = (float) (-point.X * Math.sin(fi) + point.Z * Math.cos(fi));
        point = new Point(newPole.X, newPole.Y, newPole.Z);
        return point;
    }

    private void positionDiamondInScene(Diamond diamond){
        setIdentityM(modelMatrix, 0);
        translateM(modelMatrix, 0, diamond.getLocation().X,  diamond.getLocation().Y,  diamond.getLocation().Z);
        rotateM(modelMatrix, 0, diamond.rotate(), 0, 1, 0);
        multiplyMM(modelViewProjectionMatrix, 0, viewProjectionMatrix, 0, modelMatrix, 0);
        multiplyMM(modelViewMatrix, 0, viewMatrix, 0, modelMatrix, 0);
        invertM(tempMatrix, 0, modelViewMatrix, 0);
        transposeM(itModelViewMatrix, 0, tempMatrix, 0);
    }



    private void positionObjectInScene(Point location) {
        setIdentityM(modelMatrix, 0);
        translateM(modelMatrix, 0, location.X, location.Y, location.Z);
        multiplyMM(modelViewProjectionMatrix, 0, viewProjectionMatrix, 0, modelMatrix, 0);
        multiplyMM(modelViewMatrix, 0, viewMatrix, 0, modelMatrix, 0);
        invertM(tempMatrix, 0, modelViewMatrix, 0);
        transposeM(itModelViewMatrix, 0, tempMatrix, 0);
    }

}
