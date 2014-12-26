package mini.paranormalgolf.Graphics;

import android.content.Context;

import mini.paranormalgolf.Graphics.ShaderPrograms.LightColorShaderProgram;
import mini.paranormalgolf.Graphics.ShaderPrograms.SkyboxShaderProgram;
import mini.paranormalgolf.Graphics.ShaderPrograms.TextureLightShaderProgram;
import mini.paranormalgolf.Physics.Ball;
import mini.paranormalgolf.Physics.Beam;
import mini.paranormalgolf.Physics.Bonus;
import mini.paranormalgolf.Physics.CheckPoint;
import mini.paranormalgolf.Physics.Diamond;
import mini.paranormalgolf.Physics.Elevator;
import mini.paranormalgolf.Physics.Finish;
import mini.paranormalgolf.Physics.Floor;
import mini.paranormalgolf.Physics.FloorPart;
import mini.paranormalgolf.Physics.HourGlass;
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
    private LightColorShaderProgram lightColorShaderProgram;
//    private TextureShaderProgram textureShaderProgram;
    private TextureLightShaderProgram textureLightShaderProgram;


    private SkyboxShaderProgram skyboxShaderProgram;
    private Skybox skybox;


    public DrawManager(Context context) {
        this.context = context;
        textureLightShaderProgram = new TextureLightShaderProgram(context);
        lightColorShaderProgram = new LightColorShaderProgram(context);
        skyboxShaderProgram = new SkyboxShaderProgram(context);
        skybox = new Skybox(context, new Point(0,0,0), Skybox.SkyboxTexture.dayClouds);
    }

    public void surfaceChange(int width, int height){
        glViewport(0, 0, width, height);
        MatrixHelper.perspectiveM(projectionMatrix, fieldOfViewDegree, (float) width / (float) height, near, far);
    }

    public void preDraw(Point ballLocation){
        setLookAtM(viewMatrix, 0, ballLocation.x + cameraTranslation.x, ballLocation.y + cameraTranslation.y, ballLocation.z + cameraTranslation.z, ballLocation.x, ballLocation.y, ballLocation.z, 0f, 1f, 0f);
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
        positionBonusInScene(diamond);
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
            textureLightShaderProgram.setUniforms(modelViewProjectionMatrix, modelViewMatrix, modelViewMatrix, lightPos, floor.getSideFloorTexture(), floor.FLOOR_OPACITY);
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

    public void drawBeam(Beam beam){
        textureLightShaderProgram.useProgram();
        positionObjectInScene(beam.getLocation());
        textureLightShaderProgram.setUniforms(modelViewProjectionMatrix, modelViewMatrix, modelViewMatrix, lightPos, beam.getTexture(), beam.BEAM_OPACITY);
        beam.bindData(textureLightShaderProgram);
        beam.draw();
    }

    public void drawElevator(Elevator elevator){
        textureLightShaderProgram.useProgram();
        positionObjectInScene(elevator.getLocation());
        textureLightShaderProgram.setUniforms(modelViewProjectionMatrix, modelViewMatrix, modelViewMatrix, lightPos, elevator.getTexture(), elevator.ELEVATOR_OPACITY);
        elevator.bindData(textureLightShaderProgram);
        elevator.draw();
    }

    public void drawFinish(Finish finish){
        textureLightShaderProgram.useProgram();
        positionObjectInScene(finish.getLocation());
        textureLightShaderProgram.setUniforms(modelViewProjectionMatrix, modelViewMatrix, modelViewMatrix, lightPos, finish.getTexture(), finish.FINISH_OPACITY);
        finish.bindData(textureLightShaderProgram);
        finish.draw();

        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

        lightColorShaderProgram.useProgram();
        positionObjectInScene(finish.getGlow().getLocation());
        lightColorShaderProgram.setUniforms(modelViewProjectionMatrix, modelViewMatrix, itModelViewMatrix, lightPos, finish.getGlow().getIfCanFinish()? finish.getGlow().CAN_FINISH_COLOR : finish.getGlow().CANNOT_FINISH_COLOR);
        finish.getGlow().bindData(lightColorShaderProgram);
        finish.getGlow().draw();

        glDisable(GL_BLEND);
    }

    public void drawCheckPoint(CheckPoint checkPoint){
        textureLightShaderProgram.useProgram();
        positionObjectInScene(checkPoint.getLocation());
        textureLightShaderProgram.setUniforms(modelViewProjectionMatrix, modelViewMatrix, modelViewMatrix, lightPos, checkPoint.getTexture(), checkPoint.CHECKPOINT_OPACITY);
        checkPoint.bindData(textureLightShaderProgram);
        checkPoint.draw();

        if(!checkPoint.ifVisited()){
            glEnable(GL_BLEND);
            glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

            lightColorShaderProgram.useProgram();
            positionObjectInScene(checkPoint.getGlow().getLocation());
            lightColorShaderProgram.setUniforms(modelViewProjectionMatrix, modelViewMatrix, itModelViewMatrix, lightPos, checkPoint.getGlow().getIfCanFinish()? checkPoint.getGlow().CAN_FINISH_COLOR : checkPoint.getGlow().CANNOT_FINISH_COLOR);
            checkPoint.getGlow().bindData(lightColorShaderProgram);
            checkPoint.getGlow().draw();

            glDisable(GL_BLEND);
        }
    }

    public void drawHourglass(HourGlass hourGlass){

        textureLightShaderProgram.useProgram();
        positionBonusInScene(hourGlass);
        textureLightShaderProgram.setUniforms(modelViewProjectionMatrix, modelViewMatrix, modelViewMatrix, lightPos, hourGlass.getWoodenParts().getTexture(), hourGlass.getWoodenParts().HOURGLASS_WOODEN_PART_OPACITY);
        hourGlass.getWoodenParts().bindData(textureLightShaderProgram);
        hourGlass.getWoodenParts().draw();

        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

        lightColorShaderProgram.useProgram();
        positionBonusInScene(hourGlass);
        lightColorShaderProgram.setUniforms(modelViewProjectionMatrix, modelViewMatrix, itModelViewMatrix, lightPos, hourGlass.GLASS_COLOR);
        hourGlass.bindData(lightColorShaderProgram);
        hourGlass.draw();

        glDisable(GL_BLEND);
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




    private void positionBallInScene(Ball ball) {
        float[] tmp1 = new float[16];
        float[] tmp2 = new float[16];

        setIdentityM(tmp1, 0);
        translateM(tmp1, 0, ball.getLocation().x, ball.getLocation().y, ball.getLocation().z);
        multiplyMM(modelMatrix, 0, tmp1, 0, ball.rotation, 0);
        multiplyMM(modelViewProjectionMatrix, 0, viewProjectionMatrix, 0, modelMatrix, 0);
        multiplyMM(modelViewMatrix, 0, viewMatrix, 0, modelMatrix, 0);
        invertM(tempMatrix, 0, modelViewMatrix, 0);
        transposeM(itModelViewMatrix, 0, tempMatrix, 0);
    }

    private void positionBonusInScene(Bonus bonus){
        setIdentityM(modelMatrix, 0);
        translateM(modelMatrix, 0, bonus.getLocation().x,  bonus.getLocation().y,  bonus.getLocation().z);
        rotateM(modelMatrix, 0, bonus.rotate(), 0, 1, 0);
        multiplyMM(modelViewProjectionMatrix, 0, viewProjectionMatrix, 0, modelMatrix, 0);
        multiplyMM(modelViewMatrix, 0, viewMatrix, 0, modelMatrix, 0);
        invertM(tempMatrix, 0, modelViewMatrix, 0);
        transposeM(itModelViewMatrix, 0, tempMatrix, 0);
    }



    private void positionObjectInScene(Point location) {
        setIdentityM(modelMatrix, 0);
        translateM(modelMatrix, 0, location.x, location.y, location.z);
        multiplyMM(modelViewProjectionMatrix, 0, viewProjectionMatrix, 0, modelMatrix, 0);
        multiplyMM(modelViewMatrix, 0, viewMatrix, 0, modelMatrix, 0);
        invertM(tempMatrix, 0, modelViewMatrix, 0);
        transposeM(itModelViewMatrix, 0, tempMatrix, 0);
    }

}
