package mini.paranormalgolf.Graphics;

import android.content.Context;

import mini.paranormalgolf.Graphics.ShaderPrograms.ColorShaderProgram;
import mini.paranormalgolf.Graphics.ShaderPrograms.SkyboxShaderProgram;
import mini.paranormalgolf.Graphics.ShaderPrograms.TextureShaderProgram;
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
    // private final float[] modelViewMatrix = new float[16];
    private final float[] modelViewProjectionMatrix = new float[16];
    private final float[] normalsRotationMatrix = new float[16];

    final LightData lightData = new LightData(new Point(0f, 10f, 0f), 0.5f, 0.6f);

    private final float fieldOfViewDegree = 45;
    private final float near = 1f;
    private final float far = 100f;

    private final Point cameraTranslation = new Point(0f, 15f, 15f);

    private ColorShaderProgram colorShaderProgram;
    private TextureShaderProgram textureShaderProgram;
    private SkyboxShaderProgram skyboxShaderProgram;
    private Skybox skybox;

    float[] skyboxModelViewProjectionMatrix = new float[16];
    private float xRotation, yRotation;


    public DrawManager(Context context) {
        textureShaderProgram = new TextureShaderProgram(context);
        colorShaderProgram = new ColorShaderProgram(context);
        skyboxShaderProgram = new SkyboxShaderProgram(context);
        skybox = new Skybox(context, new Point(0, 0, 0), Skybox.SkyboxTexture.dayClouds);
    }

    public void surfaceChange(int width, int height) {
        glViewport(0, 0, width, height);
        MatrixHelper.perspectiveM(projectionMatrix, fieldOfViewDegree, (float) width / (float) height, near, far);
        updateSkyboxMVPMatrix();
    }

    public void preDraw(Point ballLocation) {
        setLookAtM(viewMatrix, 0, ballLocation.x + cameraTranslation.x, ballLocation.y + cameraTranslation.y, ballLocation.z + cameraTranslation.z, ballLocation.x, ballLocation.y, ballLocation.z, 0f, 1f, 0f);
        multiplyMM(viewProjectionMatrix, 0, projectionMatrix, 0, viewMatrix, 0);
    }


    public void drawBall(Ball ball, float rotationAngle, Vector rotationAxis) {
        textureShaderProgram.useProgram();
        positionBallInScene(ball);
        textureShaderProgram.setUniforms(modelViewProjectionMatrix, modelMatrix, normalsRotationMatrix, lightData, ball.getTexture(), ball.BALL_OPACITY);
        ball.bindData(textureShaderProgram);
        ball.draw();
    }


    public void drawDiamond(Diamond diamond) {
        textureShaderProgram.useProgram();
        positionBonusInScene(diamond);
        textureShaderProgram.setUniforms(modelViewProjectionMatrix, modelMatrix, normalsRotationMatrix, lightData, diamond.getTexture(), diamond.DIAMOND_OPACITY);
        diamond.bindData(textureShaderProgram);
        diamond.draw();
    }


    public void drawFloor(Floor floor) {
        textureShaderProgram.useProgram();

        positionObjectInScene(floor.getBottomPart().getLocation());
        textureShaderProgram.setUniforms(modelViewProjectionMatrix, modelMatrix, normalsRotationMatrix, lightData, floor.getBottomFloorTexture(), floor.FLOOR_OPACITY);
        floor.getBottomPart().bindData(textureShaderProgram);
        floor.getBottomPart().draw();

        for (FloorPart floorPart : floor.getSideParts()) {
            positionObjectInScene(floorPart.getLocation());
            textureShaderProgram.setUniforms(modelViewProjectionMatrix, modelMatrix, normalsRotationMatrix, lightData, floor.getSideFloorTexture(), floor.FLOOR_OPACITY);
            floorPart.bindData(textureShaderProgram);
            floorPart.draw();
        }

        positionObjectInScene(floor.getTopPart().getLocation());
        textureShaderProgram.setUniforms(modelViewProjectionMatrix, modelMatrix, normalsRotationMatrix, lightData, floor.getTopFloorTexture(), floor.FLOOR_OPACITY);
        floor.getTopPart().bindData(textureShaderProgram);
        floor.getTopPart().draw();
    }

    public void drawWall(Wall wall) {
        textureShaderProgram.useProgram();
        positionObjectInScene(wall.getLocation());
        textureShaderProgram.setUniforms(modelViewProjectionMatrix, modelMatrix, normalsRotationMatrix, lightData, wall.getTexture(), wall.WALL_OPACITY);
        wall.bindData(textureShaderProgram);
        wall.draw();
    }

    public void drawBeam(Beam beam) {
        textureShaderProgram.useProgram();
        positionObjectInScene(beam.getLocation());
        textureShaderProgram.setUniforms(modelViewProjectionMatrix, modelMatrix, normalsRotationMatrix, lightData, beam.getTexture(), beam.BEAM_OPACITY);
        beam.bindData(textureShaderProgram);
        beam.draw();
    }

    public void drawElevator(Elevator elevator) {
        textureShaderProgram.useProgram();
        positionObjectInScene(elevator.getLocation());
        textureShaderProgram.setUniforms(modelViewProjectionMatrix, modelMatrix, normalsRotationMatrix, lightData, elevator.getTexture(), elevator.ELEVATOR_OPACITY);
        elevator.bindData(textureShaderProgram);
        elevator.draw();
    }

    public void drawFinish(Finish finish) {
        textureShaderProgram.useProgram();
        positionObjectInScene(finish.getLocation());
        textureShaderProgram.setUniforms(modelViewProjectionMatrix, modelMatrix, normalsRotationMatrix, lightData, finish.getTexture(), finish.FINISH_OPACITY);
        finish.bindData(textureShaderProgram);
        finish.draw();

        colorShaderProgram.useProgram();
        positionObjectInScene(finish.getGlow().getLocation());
        colorShaderProgram.setUniforms(modelViewProjectionMatrix, modelMatrix, normalsRotationMatrix, lightData, finish.getGlow().getIfCanFinish() ? finish.getGlow().CAN_FINISH_COLOR : finish.getGlow().CANNOT_FINISH_COLOR);
        finish.getGlow().bindData(colorShaderProgram);
        finish.getGlow().draw();
    }

    public void drawCheckPoint(CheckPoint checkPoint) {
        textureShaderProgram.useProgram();
        positionObjectInScene(checkPoint.getLocation());
        textureShaderProgram.setUniforms(modelViewProjectionMatrix, modelMatrix, normalsRotationMatrix, lightData, checkPoint.getTexture(), checkPoint.CHECKPOINT_OPACITY);
        checkPoint.bindData(textureShaderProgram);
        checkPoint.draw();

        if (!checkPoint.ifVisited()) {
            colorShaderProgram.useProgram();
            positionObjectInScene(checkPoint.getGlow().getLocation());
            colorShaderProgram.setUniforms(modelViewProjectionMatrix, modelMatrix, normalsRotationMatrix, lightData, checkPoint.getGlow().CAN_FINISH_COLOR);
            checkPoint.getGlow().bindData(colorShaderProgram);
            checkPoint.getGlow().draw();
        }
    }

    public void drawHourglass(HourGlass hourGlass) {

        textureShaderProgram.useProgram();
        positionBonusInScene(hourGlass);
        textureShaderProgram.setUniforms(modelViewProjectionMatrix, modelMatrix, normalsRotationMatrix, lightData, hourGlass.getWoodenParts().getTexture(), hourGlass.getWoodenParts().HOURGLASS_WOODEN_PART_OPACITY);
        hourGlass.getWoodenParts().bindData(textureShaderProgram);
        hourGlass.getWoodenParts().draw();

        colorShaderProgram.useProgram();
        positionBonusInScene(hourGlass);
        colorShaderProgram.setUniforms(modelViewProjectionMatrix, modelMatrix, normalsRotationMatrix, lightData, hourGlass.GLASS_COLOR);
        hourGlass.bindData(colorShaderProgram);
        hourGlass.draw();
    }


    public void handleTouchDrag(float deltaX, float deltaY) {
        xRotation += deltaX / 16f;
        yRotation += deltaY / 16f;

        if (yRotation < -90) {
            yRotation = -90;
        } else if (yRotation > 90) {
            yRotation = 90;
        }

        updateSkyboxMVPMatrix();
    }

    private void updateSkyboxMVPMatrix() {
        float[] tmp = new float[16];
        setIdentityM(tmp, 0);
        rotateM(tmp, 0, -yRotation, 1f, 0f, 0f);
        rotateM(tmp, 0, -xRotation, 0f, 1f, 0f);
        multiplyMM(skyboxModelViewProjectionMatrix, 0, projectionMatrix, 0, tmp, 0);
    }

    public void drawSkybox() {
        glDepthFunc(GL_LEQUAL);
        skyboxShaderProgram.useProgram();
        skyboxShaderProgram.setUniforms(skyboxModelViewProjectionMatrix, skybox.getTexture());
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

        setIdentityM(tmp1, 0);
        multiplyMM(tmp2, 0, tmp1, 0, ball.rotation, 0);
        invertM(tmp1, 0, tmp2, 0);
        transposeM(normalsRotationMatrix, 0, tmp1, 0);
    }

    private void positionBonusInScene(Bonus bonus) {
        setIdentityM(modelMatrix, 0);
        translateM(modelMatrix, 0, bonus.getLocation().x, bonus.getLocation().y, bonus.getLocation().z);
        rotateM(modelMatrix, 0, bonus.rotate(), 0, 1, 0);
        multiplyMM(modelViewProjectionMatrix, 0, viewProjectionMatrix, 0, modelMatrix, 0);

        float[] tmp1 = new float[16];
        float[] tmp2 = new float[16];
        setIdentityM(tmp1, 0);
        rotateM(tmp1, 0, bonus.rotate(), 0, 1, 0);
        invertM(tmp2, 0, tmp1, 0);
        transposeM(normalsRotationMatrix, 0, tmp2, 0);
    }


    private void positionObjectInScene(Point location) {
        setIdentityM(modelMatrix, 0);
        translateM(modelMatrix, 0, location.x, location.y, location.z);
        multiplyMM(modelViewProjectionMatrix, 0, viewProjectionMatrix, 0, modelMatrix, 0);
        setIdentityM(normalsRotationMatrix, 0); //bo gdy nie ma rotacji, to nie musimy nic robić z wektorami normalnymi
    }

}
