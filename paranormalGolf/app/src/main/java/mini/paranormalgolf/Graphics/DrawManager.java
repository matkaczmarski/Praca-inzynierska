package mini.paranormalgolf.Graphics;

import android.content.Context;

import mini.paranormalgolf.Graphics.ShaderPrograms.ColorShaderProgram;
import mini.paranormalgolf.Graphics.ShaderPrograms.LightColorShaderProgram;
import mini.paranormalgolf.Graphics.ShaderPrograms.SkyboxShaderProgram;
import mini.paranormalgolf.Graphics.ShaderPrograms.TextureLightShaderProgram;
import mini.paranormalgolf.Graphics.ShaderPrograms.TextureShaderProgram;
import mini.paranormalgolf.Helpers.ResourceHelper;
import mini.paranormalgolf.Physics.Ball;
import mini.paranormalgolf.Physics.Floor;
import mini.paranormalgolf.Physics.FloorPart;
import mini.paranormalgolf.Primitives.BoxSize;
import mini.paranormalgolf.Primitives.Point;
import mini.paranormalgolf.Primitives.Vector;
import mini.paranormalgolf.R;

import static android.opengl.GLES20.GL_COLOR_BUFFER_BIT;
import static android.opengl.GLES20.GL_CULL_FACE;
import static android.opengl.GLES20.GL_DEPTH_BUFFER_BIT;
import static android.opengl.GLES20.GL_DEPTH_TEST;
import static android.opengl.GLES20.GL_LEQUAL;
import static android.opengl.GLES20.GL_LESS;
import static android.opengl.GLES20.glClear;
import static android.opengl.GLES20.glDepthFunc;
import static android.opengl.GLES20.glEnable;
import static android.opengl.GLES20.glViewport;
import static android.opengl.Matrix.invertM;
import static android.opengl.Matrix.multiplyMM;
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

    private Context context;

    private Vector lightPos =  new Vector(0f, 3f, 0f);//.normalize();

    private final float fieldOfViewDegree = 45;
    private final float near = 1f;
    private final float far = 100f;

    private final Point cameraTranslation = new Point(0f, 15f, 15f);

//    private ColorShaderProgram colorShaderProgram;
//    private LightColorShaderProgram lightColorShaderProgram;
//    private TextureShaderProgram textureShaderProgram;
    private TextureLightShaderProgram textureLightShaderProgram;

    int topFloorTexture;
    int sideFloorTexture;
    int bottomFloorTexture;
    //int golfTexture;

    private SkyboxShaderProgram skyboxShaderProgram;
    private Skybox skybox;

    public DrawManager(Context context) {
        this.context = context;
//        colorShaderProgram = new ColorShaderProgram(context);
//        lightColorShaderProgram = new LightColorShaderProgram(context);
//        textureShaderProgram = new TextureShaderProgram(context);
        textureLightShaderProgram = new TextureLightShaderProgram(context);

        topFloorTexture = ResourceHelper.loadTexture(context, R.drawable.top_floor_texture);
        sideFloorTexture = ResourceHelper.loadTexture(context, R.drawable.side_floor_texture);
        bottomFloorTexture = ResourceHelper.loadTexture(context, R.drawable.bottom_floor_texture);
//        golfTexture = ResourceHelper.loadTexture(context, R.drawable.golf_texture);

        skyboxShaderProgram = new SkyboxShaderProgram(context);
        skybox = new Skybox(context, new Point(0,0,0), Skybox.SkyboxTexture.dayClouds);
    }

    public void surfaceChange(int width, int height){
        glViewport(0, 0, width, height);
        //ustawianie pozycji sceny
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
       positionBallInScene(ball.getLocation(), rotationAngle, rotationAxis);
       textureLightShaderProgram.setUniforms(modelViewProjectionMatrix, modelViewMatrix,lightPos, ball.getTexture());

       ball.bindData(textureLightShaderProgram);
       ball.draw();
    }

   public void drawFloor(Floor floor){
        textureLightShaderProgram.useProgram();

        positionObjectInScene(floor.bottomPart.getLocation());
        textureLightShaderProgram.setUniforms(modelViewProjectionMatrix, modelViewMatrix,lightPos, bottomFloorTexture);
        floor.bottomPart.bindData(textureLightShaderProgram);
        floor.bottomPart.draw();

        for(FloorPart floorPart : floor.sideParts){
            positionObjectInScene(floorPart.getLocation());
            textureLightShaderProgram.setUniforms(modelViewProjectionMatrix, modelViewMatrix,lightPos, bottomFloorTexture);
            floorPart.bindData(textureLightShaderProgram);
            floorPart.draw();
        }

        positionObjectInScene(floor.topPart.getLocation());
        textureLightShaderProgram.setUniforms(modelViewProjectionMatrix, modelViewMatrix,lightPos, topFloorTexture);
        floor.topPart.bindData(textureLightShaderProgram);
        floor.topPart.draw();
    }



    public void drawSkybox() {
        //setIdentityM(modelViewProjectionMatrix, 0);

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



    private void positionBallInScene(Point location, float angle,Vector axis){
        setIdentityM(modelMatrix, 0);
        translateM(modelMatrix, 0, location.X, location.Y, location.Z);
       // for(int i=0;i<ball.angles.size();i++){
            rotateM(modelMatrix, 0, angle, axis.X, axis.Y, axis.Z);
       // }
      //  rotateM(modelMatrix, 0, (float)(360*Math.acos(pole.Y)/(2*Math.PI)), pole.Z, 0, -pole.X);
        multiplyMM(modelViewProjectionMatrix, 0, viewProjectionMatrix, 0, modelMatrix, 0);
        multiplyMM(modelViewMatrix, 0, viewMatrix, 0, modelMatrix, 0);
    }

    private void positionObjectInScene(Point location) {
        setIdentityM(modelMatrix, 0);
        translateM(modelMatrix, 0, location.X, location.Y, location.Z);
        multiplyMM(modelViewProjectionMatrix, 0, viewProjectionMatrix, 0, modelMatrix, 0);
        multiplyMM(modelViewMatrix, 0, viewMatrix, 0, modelMatrix, 0);
    }
}
