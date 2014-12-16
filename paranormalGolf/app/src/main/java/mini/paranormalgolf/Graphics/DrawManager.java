package mini.paranormalgolf.Graphics;

import android.content.Context;

import mini.paranormalgolf.Graphics.ShaderPrograms.ColorShaderProgram;
import mini.paranormalgolf.Graphics.ShaderPrograms.LightColorShaderProgram;
import mini.paranormalgolf.Graphics.ShaderPrograms.SkyboxShaderProgram;
import mini.paranormalgolf.Graphics.ShaderPrograms.TextureLightShaderProgram;
import mini.paranormalgolf.Graphics.ShaderPrograms.TextureShaderProgram;
import mini.paranormalgolf.Helpers.ResourceHelper;
import mini.paranormalgolf.Physics.Ball;
import mini.paranormalgolf.Physics.Diamond;
import mini.paranormalgolf.Physics.Floor;
import mini.paranormalgolf.Physics.FloorPart;
import mini.paranormalgolf.Primitives.BoxSize;
import mini.paranormalgolf.Primitives.Point;
import mini.paranormalgolf.Primitives.Pyramid;
import mini.paranormalgolf.Primitives.Vector;
import mini.paranormalgolf.R;

import static android.opengl.GLES20.GL_BLEND;
import static android.opengl.GLES20.GL_COLOR_BUFFER_BIT;
import static android.opengl.GLES20.GL_CULL_FACE;
import static android.opengl.GLES20.GL_DEPTH_BUFFER_BIT;
import static android.opengl.GLES20.GL_DEPTH_TEST;
import static android.opengl.GLES20.GL_LEQUAL;
import static android.opengl.GLES20.GL_LESS;
import static android.opengl.GLES20.GL_ONE_MINUS_SRC_ALPHA;
import static android.opengl.GLES20.GL_SRC_ALPHA;
import static android.opengl.GLES20.glBlendFunc;
import static android.opengl.GLES20.glClear;
import static android.opengl.GLES20.glDepthFunc;
import static android.opengl.GLES20.glDisable;
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

    float[] tempMatrix = new float[16];

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
       textureLightShaderProgram.setUniforms(modelViewProjectionMatrix, modelViewMatrix,lightPos, ball.getTexture(), ball.BALL_OPACITY);
       ball.bindData(textureLightShaderProgram);
       ball.draw();
    }


    public void drawDiamond(Diamond diamond){
        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

        textureLightShaderProgram.useProgram();
        positionDiamondInScene(diamond);
        textureLightShaderProgram.setUniforms(modelViewProjectionMatrix, modelViewMatrix,lightPos, diamond.getTexture(), diamond.DIAMOND_OPACITY);
        diamond.bindData(textureLightShaderProgram);
        diamond.draw();

        glDisable(GL_BLEND);
    }


   public void drawFloor(Floor floor){
        textureLightShaderProgram.useProgram();

        positionObjectInScene(floor.getBottomPart().getLocation());
        textureLightShaderProgram.setUniforms(modelViewProjectionMatrix, modelViewMatrix,lightPos, floor.getBottomFloorTexture(), floor.FLOOR_OPACITY);
        floor.getBottomPart().bindData(textureLightShaderProgram);
        floor.getBottomPart().draw();

        for(FloorPart floorPart : floor.getSideParts()){
            positionObjectInScene(floorPart.getLocation());
            textureLightShaderProgram.setUniforms(modelViewProjectionMatrix, modelViewMatrix,lightPos, floor.getBottomFloorTexture(), floor.FLOOR_OPACITY);
            floorPart.bindData(textureLightShaderProgram);
            floorPart.draw();
        }

        positionObjectInScene(floor.getTopPart().getLocation());
        textureLightShaderProgram.setUniforms(modelViewProjectionMatrix, modelViewMatrix,lightPos, floor.getTopFloorTexture(), floor.FLOOR_OPACITY);
        floor.getTopPart().bindData(textureLightShaderProgram);
        floor.getTopPart().draw();
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
        setIdentityM(modelMatrix, 0);
        translateM(modelMatrix, 0, ball.getLocation().X, ball.getLocation().Y, ball.getLocation().Z);
        if(ball.pole.X>0)
            rotateM(modelMatrix,0,(float)(360*Math.acos(ball.pole.Y)/(2*Math.PI)),ball.pole.Z,0,-ball.pole.X);
        else
            rotateM(modelMatrix,0,(float)(360*Math.acos(ball.pole.Y)/(2*Math.PI)),ball.pole.Z,0,-ball.pole.X);
       // for(int i=0;i<ball.angles.size();i++){
       //     rotateM(modelMatrix, 0, angle, axis.X, axis.Y, axis.Z);
       // }
      //  rotateM(modelMatrix, 0, (float)(360*Math.acos(pole.Y)/(2*Math.PI)), pole.Z, 0, -pole.X);
        multiplyMM(modelViewProjectionMatrix, 0, viewProjectionMatrix, 0, modelMatrix, 0);
        multiplyMM(modelViewMatrix, 0, viewMatrix, 0, modelMatrix, 0);
    }

    private void positionDiamondInScene(Diamond diamond){
        setIdentityM(modelMatrix, 0);
        translateM(modelMatrix, 0, diamond.getLocation().X,  diamond.getLocation().Y,  diamond.getLocation().Z);
        rotateM(modelMatrix, 0, diamond.rotate(), 0, 1, 0);
        multiplyMM(modelViewProjectionMatrix, 0, viewProjectionMatrix, 0, modelMatrix, 0);
        multiplyMM(modelViewMatrix, 0, viewMatrix, 0, modelMatrix, 0);
        invertM(tempMatrix, 0, modelViewMatrix, 0);
        transposeM(modelViewMatrix, 0, tempMatrix, 0);
    }

    private void positionObjectInScene(Point location) {
        setIdentityM(modelMatrix, 0);
        translateM(modelMatrix, 0, location.X, location.Y, location.Z);
        multiplyMM(modelViewProjectionMatrix, 0, viewProjectionMatrix, 0, modelMatrix, 0);
        multiplyMM(modelViewMatrix, 0, viewMatrix, 0, modelMatrix, 0);
       // invertM(tempMatrix, 0, modelViewMatrix, 0);
       // transposeM(modelViewMatrix, 0, tempMatrix, 0);
    }

}
