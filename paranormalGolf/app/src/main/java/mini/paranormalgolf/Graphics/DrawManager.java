package mini.paranormalgolf.Graphics;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.Matrix;
import android.util.Log;

import mini.paranormalgolf.Graphics.ShaderPrograms.ColorShaderProgram;
import mini.paranormalgolf.Graphics.ShaderPrograms.DepthMapShaderProgram;
import mini.paranormalgolf.Graphics.ShaderPrograms.SkyboxShaderProgram;
import mini.paranormalgolf.Graphics.ShaderPrograms.TextureShaderProgram;
import mini.paranormalgolf.Graphics.ShaderPrograms.TmpShaderProgram;
import mini.paranormalgolf.LoggerConfig;
import mini.paranormalgolf.Physics.Ball;
import mini.paranormalgolf.Physics.Beam;
import mini.paranormalgolf.Physics.Board;
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

import static android.opengl.GLES20.GL_BACK;
import static android.opengl.GLES20.GL_COLOR_BUFFER_BIT;
import static android.opengl.GLES20.GL_DEPTH_BUFFER_BIT;
import static android.opengl.GLES20.GL_FRONT;
import static android.opengl.GLES20.GL_LEQUAL;
import static android.opengl.GLES20.GL_LESS;
import static android.opengl.GLES20.glBindFramebuffer;
import static android.opengl.GLES20.glClear;
import static android.opengl.GLES20.glClearColor;
import static android.opengl.GLES20.glCullFace;
import static android.opengl.GLES20.glDepthFunc;
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

    final LightData lightData = new LightData(new Point(1f, 25f, 1f), 0.5f, 0.6f);

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

    DepthMapShaderProgram depthMapShaderProgram;
    TmpShaderProgram tmpShaderProgram;

    private int displayWidth;
    private int displayHeight;
    private int depthMapWidth;
    private int depthMapHeight;

    int[] fboId;
    int[] depthTextureId;
    int[] renderTextureId;

    private final float[] lightsViewProjectionMatrix = new float[16];
    private final float[] mLightMvpMatrix_dynamicShapes = new float[16];
    private final float[] lightsProjectionMatrix = new float[16];
    private final float[] lightsViewMatrix = new float[16];


    private final boolean withShadow;


    public DrawManager(Context context, boolean withShadow) {
        textureShaderProgram = new TextureShaderProgram(context);
        colorShaderProgram = new ColorShaderProgram(context);
        skyboxShaderProgram = new SkyboxShaderProgram(context);
        skybox = new Skybox(context, new Point(0, 0, 0), Skybox.SkyboxTexture.dayClouds);

        depthMapShaderProgram = new DepthMapShaderProgram(context);
        tmpShaderProgram = new TmpShaderProgram(context);

        this.withShadow = withShadow;
    }

    public void surfaceChange(int width, int height) {
        displayHeight = height;
        displayWidth = width;

        MatrixHelper.perspectiveM(projectionMatrix, fieldOfViewDegree, (float) width / (float) height, near, far);
        updateSkyboxMVPMatrix();

        if(withShadow) {
            float ratio = 1.5f;
            depthMapWidth = Math.round(displayWidth * ratio);
            depthMapHeight = Math.round(displayHeight * ratio);
            MatrixHelper.perspectiveM(lightsProjectionMatrix, 120f, (float) width / (float) height, near, far);
            generateShadowFBO();
        }
    }

    public void generateShadowFBO() {

        fboId = new int[1];
        depthTextureId = new int[1];
        renderTextureId = new int[1];

        // create a framebuffer object
        GLES20.glGenFramebuffers(1, fboId, 0);

        // create render buffer and bind 16-bit depth buffer
        GLES20.glGenRenderbuffers(1, depthTextureId, 0);
        GLES20.glBindRenderbuffer(GLES20.GL_RENDERBUFFER, depthTextureId[0]);
        GLES20.glRenderbufferStorage(GLES20.GL_RENDERBUFFER, GLES20.GL_DEPTH_COMPONENT16, depthMapWidth, depthMapHeight);

        // Try to use a texture depth component
        GLES20.glGenTextures(1, renderTextureId, 0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, renderTextureId[0]);

        // GL_LINEAR does not make sense for depth texture. However, next tutorial shows usage of GL_LINEAR and PCF. Using GL_NEAREST
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_NEAREST);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_NEAREST);

        // Remove artifact on the edges of the shadowmap
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);

        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, fboId[0]);

        GLES20.glTexImage2D(GLES20.GL_TEXTURE_2D, 0, GLES20.GL_RGBA, depthMapWidth, depthMapHeight, 0, GLES20.GL_RGBA, GLES20.GL_UNSIGNED_BYTE, null);

        // specify texture as color attachment
        GLES20.glFramebufferTexture2D(GLES20.GL_FRAMEBUFFER, GLES20.GL_COLOR_ATTACHMENT0, GLES20.GL_TEXTURE_2D, renderTextureId[0], 0);

        // attach the texture to FBO depth attachment point
        // (not supported with gl_texture_2d)
        GLES20.glFramebufferRenderbuffer(GLES20.GL_FRAMEBUFFER, GLES20.GL_DEPTH_ATTACHMENT, GLES20.GL_RENDERBUFFER, depthTextureId[0]);


        // check FBO status
        int FBOstatus = GLES20.glCheckFramebufferStatus(GLES20.GL_FRAMEBUFFER);
        if (FBOstatus != GLES20.GL_FRAMEBUFFER_COMPLETE) {
            if(LoggerConfig.ON) {
                Log.e("FBO", "GL_FRAMEBUFFER_COMPLETE failed, CANNOT use FBO");
            }
            throw new RuntimeException("GL_FRAMEBUFFER_COMPLETE failed, CANNOT use FBO");
        }
    }

    public void preDraw(Point ballLocation) {

        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

        setLookAtM(viewMatrix, 0, ballLocation.x + cameraTranslation.x, ballLocation.y + cameraTranslation.y, ballLocation.z + cameraTranslation.z, ballLocation.x, ballLocation.y, ballLocation.z, 0f, 1f, 0f);
        multiplyMM(viewProjectionMatrix, 0, projectionMatrix, 0, viewMatrix, 0);

        if(withShadow) {
            Matrix.setLookAtM(lightsViewMatrix, 0,
                    lightData.position.x, lightData.position.y, lightData.position.z,
                    0f, 0f, 0f,
                    0f, 1f, 0f);
            multiplyMM(lightsViewProjectionMatrix, 0, lightsProjectionMatrix, 0, lightsViewMatrix, 0);
        }
    }


    public void drawBoard(Board board, Ball ball) {
        if (withShadow) {
            renderSceneWithShadow(board, ball);
        } else {
            renderSceneWithoutShadow(board, ball);
        }
    }

    private void renderSceneWithShadow(Board board, Ball ball){
        glBindFramebuffer(GLES20.GL_FRAMEBUFFER, fboId[0]);

        glViewport(0, 0, depthMapWidth, depthMapHeight);

        glClearColor(1.0f, 1.0f, 1.0f, 1.0f);
        glClear(GL_DEPTH_BUFFER_BIT | GL_COLOR_BUFFER_BIT);
        glCullFace(GL_FRONT);

        renderDepthMap(board, ball);

        glCullFace(GL_BACK);

        glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0);
        glViewport(0, 0, displayWidth, displayHeight);
        glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);


        renderBoardWithShadows(board, ball);
    }

    private void renderSceneWithoutShadow(Board board, Ball ball){
        glViewport(0, 0, displayWidth, displayHeight);
        glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);

        drawSkybox();

        for (Floor floor : board.floors) {
            drawFloor(floor);
        }

        for (Wall wall : board.walls) {
            drawWall(wall);
        }

        for (Beam beam : board.beams) {
            drawBeam(beam);
        }
        for (Elevator elevator : board.elevators) {
            drawElevator(elevator);
        }

        drawBall(ball);

        for (Diamond diamond : board.diamonds) {
            drawDiamond(diamond);
        }

        for (HourGlass hourGlass : board.hourGlasses) {
            drawHourglass(hourGlass);
        }

        for (CheckPoint checkPoint : board.checkpoints) {
            drawCheckPoint(checkPoint);
        }
        drawFinish(board.finish);
    }

    private void renderDepthMap(Board board, Ball ball){
        depthMapShaderProgram.useProgram();

        for (Floor floor : board.floors) {
            positionObjectInScene(floor.getBottomPart().getLocation());
            depthMapShaderProgram.setUniforms(lightsViewProjectionMatrix, modelMatrix);
            floor.getBottomPart().bindDepthMapData(depthMapShaderProgram);
            floor.getBottomPart().draw();

            for (FloorPart floorPart : floor.getSideParts()) {
                positionObjectInScene(floorPart.getLocation());
                depthMapShaderProgram.setUniforms(lightsViewProjectionMatrix, modelMatrix);
                floorPart.bindDepthMapData(depthMapShaderProgram);
                floorPart.draw();
            }

            positionObjectInScene(floor.getTopPart().getLocation());
            depthMapShaderProgram.setUniforms(lightsViewProjectionMatrix, modelMatrix);
            floor.getTopPart().bindDepthMapData(depthMapShaderProgram);
            floor.getTopPart().draw();
        }

        for (Wall wall : board.walls) {
            positionObjectInScene(wall.getLocation());
            depthMapShaderProgram.setUniforms(lightsViewProjectionMatrix, modelMatrix);
            wall.bindDepthMapData(depthMapShaderProgram);
            wall.draw();
        }

        for (Beam beam : board.beams) {
            positionObjectInScene(beam.getLocation());
            depthMapShaderProgram.setUniforms(lightsViewProjectionMatrix, modelMatrix);
            beam.bindDepthMapData(depthMapShaderProgram);
            beam.draw();
        }

        for (Elevator elevator : board.elevators) {
            positionObjectInScene(elevator.getLocation());
            depthMapShaderProgram.setUniforms(lightsViewProjectionMatrix, modelMatrix);
            elevator.bindDepthMapData(depthMapShaderProgram);
            elevator.draw();
        }

        positionBallInScene(ball);
        depthMapShaderProgram.setUniforms(lightsViewProjectionMatrix, modelMatrix);
        ball.bindDepthMapData(depthMapShaderProgram);
        ball.draw();

        for (Diamond diamond : board.diamonds) {
            positionBonusInScene(diamond);
            depthMapShaderProgram.setUniforms(lightsViewProjectionMatrix, modelMatrix);
            diamond.bindDepthMapData(depthMapShaderProgram);
            diamond.draw();
        }

        for (HourGlass hourGlass : board.hourGlasses) {
            positionBonusInScene(hourGlass);
            depthMapShaderProgram.setUniforms(lightsViewProjectionMatrix, modelMatrix);
            hourGlass.getWoodenParts().bindDepthMapData(depthMapShaderProgram);
            hourGlass.getWoodenParts().draw();
        }

        positionObjectInScene(board.finish.getLocation());
        depthMapShaderProgram.setUniforms(lightsViewProjectionMatrix, modelMatrix);
        board.finish.bindDepthMapData(depthMapShaderProgram);
        board.finish.draw();

        for (CheckPoint checkPoint : board.checkpoints) {
            positionObjectInScene(checkPoint.getLocation());
            depthMapShaderProgram.setUniforms(lightsViewProjectionMatrix, modelMatrix);
            checkPoint.bindDepthMapData(depthMapShaderProgram);
            checkPoint.draw();
        }
    }

    private void renderBoardWithShadows(Board board, Ball ball){
        drawSkybox();
        for (Floor floor : board.floors) {
            drawFloorWithShadow(floor);
        }

        for (Wall wall : board.walls) {
            drawWallWithShadow(wall);
        }

        for (Beam beam : board.beams) {
            drawBeamWithShadow(beam);
        }
        for (Elevator elevator : board.elevators) {
            drawElevatorWithShadow(elevator);
        }

        drawBallWithShadow(ball);

        for (Diamond diamond : board.diamonds) {
            drawDiamondWithShadow(diamond);
        }

        for (HourGlass hourGlass : board.hourGlasses) {
            drawHourglassWithShadow(hourGlass);
        }

        for (CheckPoint checkPoint : board.checkpoints) {
            drawCheckPointWithShadow(checkPoint);
        }
        drawFinishWithShadow(board.finish);
    }

    private void drawBall(Ball ball) {
        textureShaderProgram.useProgram();
        positionBallInScene(ball);
        textureShaderProgram.setUniforms(modelViewProjectionMatrix, modelMatrix, normalsRotationMatrix, lightData, ball.getTexture(), ball.BALL_OPACITY);
        ball.bindData(textureShaderProgram);
        ball.draw();
    }

    private void drawDiamond(Diamond diamond) {
        textureShaderProgram.useProgram();
        positionBonusInScene(diamond);
        textureShaderProgram.setUniforms(modelViewProjectionMatrix, modelMatrix, normalsRotationMatrix, lightData, diamond.getTexture(), diamond.DIAMOND_OPACITY);
        diamond.bindData(textureShaderProgram);
        diamond.draw();
    }

    private void drawFloor(Floor floor) {
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

    private void drawWall(Wall wall) {
        textureShaderProgram.useProgram();
        positionObjectInScene(wall.getLocation());
        textureShaderProgram.setUniforms(modelViewProjectionMatrix, modelMatrix, normalsRotationMatrix, lightData, wall.getTexture(), wall.WALL_OPACITY);
        wall.bindData(textureShaderProgram);
        wall.draw();
    }

    private void drawBeam(Beam beam) {
        textureShaderProgram.useProgram();
        positionObjectInScene(beam.getLocation());
        textureShaderProgram.setUniforms(modelViewProjectionMatrix, modelMatrix, normalsRotationMatrix, lightData, beam.getTexture(), beam.BEAM_OPACITY);
        beam.bindData(textureShaderProgram);
        beam.draw();
    }

    private void drawElevator(Elevator elevator) {
        textureShaderProgram.useProgram();
        positionObjectInScene(elevator.getLocation());
        textureShaderProgram.setUniforms(modelViewProjectionMatrix, modelMatrix, normalsRotationMatrix, lightData, elevator.getTexture(), elevator.ELEVATOR_OPACITY);
        elevator.bindData(textureShaderProgram);
        elevator.draw();
    }

    private void drawFinish(Finish finish) {
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

    private void drawCheckPoint(CheckPoint checkPoint) {
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

    private void drawHourglass(HourGlass hourGlass) {
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

    ////////////////////////////////////////

    private void drawBallWithShadow(Ball ball) {
        tmpShaderProgram.useProgram();
        positionBallInScene(ball);
        tmpShaderProgram.setUniforms(modelViewProjectionMatrix, modelMatrix, normalsRotationMatrix, lightData, ball.getTexture(), ball.BALL_OPACITY, lightsViewProjectionMatrix, renderTextureId[0]);
        ball.bindShadowData(tmpShaderProgram);
        ball.draw();
    }

    private void drawDiamondWithShadow(Diamond diamond) {
        tmpShaderProgram.useProgram();
        positionBonusInScene(diamond);
        tmpShaderProgram.setUniforms(modelViewProjectionMatrix, modelMatrix, normalsRotationMatrix, lightData, diamond.getTexture(), diamond.DIAMOND_OPACITY, lightsViewProjectionMatrix, renderTextureId[0]);
        diamond.bindShadowData(tmpShaderProgram);
        diamond.draw();
    }

    private void drawFloorWithShadow(Floor floor) {
        tmpShaderProgram.useProgram();
        positionObjectInScene(floor.getBottomPart().getLocation());
        tmpShaderProgram.setUniforms(modelViewProjectionMatrix, modelMatrix, normalsRotationMatrix, lightData, floor.getBottomFloorTexture(), floor.FLOOR_OPACITY, lightsViewProjectionMatrix, renderTextureId[0]);
        floor.getBottomPart().bindShadowData(tmpShaderProgram);
        floor.getBottomPart().draw();

        for (FloorPart floorPart : floor.getSideParts()) {
            positionObjectInScene(floorPart.getLocation());
            tmpShaderProgram.setUniforms(modelViewProjectionMatrix, modelMatrix, normalsRotationMatrix, lightData, floor.getSideFloorTexture(), floor.FLOOR_OPACITY, lightsViewProjectionMatrix, renderTextureId[0]);
            floorPart.bindShadowData(tmpShaderProgram);
            floorPart.draw();
        }

        positionObjectInScene(floor.getTopPart().getLocation());
        tmpShaderProgram.setUniforms(modelViewProjectionMatrix, modelMatrix, normalsRotationMatrix, lightData, floor.getTopFloorTexture(), floor.FLOOR_OPACITY, lightsViewProjectionMatrix, renderTextureId[0]);
        floor.getTopPart().bindShadowData(tmpShaderProgram);
        floor.getTopPart().draw();
    }

    private void drawWallWithShadow(Wall wall) {
        tmpShaderProgram.useProgram();
        positionObjectInScene(wall.getLocation());
        tmpShaderProgram.setUniforms(modelViewProjectionMatrix, modelMatrix, normalsRotationMatrix, lightData, wall.getTexture(), wall.WALL_OPACITY, lightsViewProjectionMatrix, renderTextureId[0]);
        wall.bindShadowData(tmpShaderProgram);
        wall.draw();
    }

    private void drawBeamWithShadow(Beam beam) {
        tmpShaderProgram.useProgram();
        positionObjectInScene(beam.getLocation());
        tmpShaderProgram.setUniforms(modelViewProjectionMatrix, modelMatrix, normalsRotationMatrix, lightData, beam.getTexture(), beam.BEAM_OPACITY, lightsViewProjectionMatrix, renderTextureId[0]);
        beam.bindShadowData(tmpShaderProgram);
        beam.draw();
    }

    private void drawElevatorWithShadow(Elevator elevator) {
        tmpShaderProgram.useProgram();
        positionObjectInScene(elevator.getLocation());
        tmpShaderProgram.setUniforms(modelViewProjectionMatrix, modelMatrix, normalsRotationMatrix, lightData, elevator.getTexture(), elevator.ELEVATOR_OPACITY, lightsViewProjectionMatrix, renderTextureId[0]);
        elevator.bindShadowData(tmpShaderProgram);
        elevator.draw();
    }

    private void drawFinishWithShadow(Finish finish) {
        tmpShaderProgram.useProgram();
        positionObjectInScene(finish.getLocation());
        tmpShaderProgram.setUniforms(modelViewProjectionMatrix, modelMatrix, normalsRotationMatrix, lightData, finish.getTexture(), finish.FINISH_OPACITY, lightsViewProjectionMatrix, renderTextureId[0]);
        finish.bindShadowData(tmpShaderProgram);
        finish.draw();

        colorShaderProgram.useProgram();
        positionObjectInScene(finish.getGlow().getLocation());
        colorShaderProgram.setUniforms(modelViewProjectionMatrix, modelMatrix, normalsRotationMatrix, lightData, finish.getGlow().getIfCanFinish() ? finish.getGlow().CAN_FINISH_COLOR : finish.getGlow().CANNOT_FINISH_COLOR);
        finish.getGlow().bindData(colorShaderProgram);
        finish.getGlow().draw();
    }

    private void drawCheckPointWithShadow(CheckPoint checkPoint) {
        tmpShaderProgram.useProgram();
        positionObjectInScene(checkPoint.getLocation());
        tmpShaderProgram.setUniforms(modelViewProjectionMatrix, modelMatrix, normalsRotationMatrix, lightData, checkPoint.getTexture(), checkPoint.CHECKPOINT_OPACITY, lightsViewProjectionMatrix, renderTextureId[0]);
        checkPoint.bindShadowData(tmpShaderProgram);
        checkPoint.draw();

        if (!checkPoint.ifVisited()) {
            colorShaderProgram.useProgram();
            positionObjectInScene(checkPoint.getGlow().getLocation());
            colorShaderProgram.setUniforms(modelViewProjectionMatrix, modelMatrix, normalsRotationMatrix, lightData, checkPoint.getGlow().CAN_FINISH_COLOR);
            checkPoint.getGlow().bindData(colorShaderProgram);
            checkPoint.getGlow().draw();
        }
    }

    private void drawHourglassWithShadow(HourGlass hourGlass) {
        tmpShaderProgram.useProgram();
        positionBonusInScene(hourGlass);
        tmpShaderProgram.setUniforms(modelViewProjectionMatrix, modelMatrix, normalsRotationMatrix, lightData, hourGlass.getWoodenParts().getTexture(), hourGlass.getWoodenParts().HOURGLASS_WOODEN_PART_OPACITY, lightsViewProjectionMatrix, renderTextureId[0]);
        hourGlass.getWoodenParts().bindShadowData(tmpShaderProgram);
        hourGlass.getWoodenParts().draw();

        colorShaderProgram.useProgram();
        positionBonusInScene(hourGlass);
        colorShaderProgram.setUniforms(modelViewProjectionMatrix, modelMatrix, normalsRotationMatrix, lightData, hourGlass.GLASS_COLOR);
        hourGlass.bindData(colorShaderProgram);
        hourGlass.draw();
    }


    //////////////////

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

    private void drawSkybox() {
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
        multiplyMM(modelMatrix, 0, tmp1, 0, ball.getRotation(), 0);
        multiplyMM(modelViewProjectionMatrix, 0, viewProjectionMatrix, 0, modelMatrix, 0);

        setIdentityM(tmp1, 0);
        multiplyMM(tmp2, 0, tmp1, 0, ball.getRotation(), 0);
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

    //float[] modelViewMatrix = new float[16];


    private void positionObjectInScene(Point location) {
        setIdentityM(modelMatrix, 0);
        translateM(modelMatrix, 0, location.x, location.y, location.z);
        multiplyMM(modelViewProjectionMatrix, 0, viewProjectionMatrix, 0, modelMatrix, 0);
        setIdentityM(normalsRotationMatrix, 0); //bo gdy nie ma rotacji, to nie musimy nic robić z wektorami normalnymi

        //setIdentityM(modelViewMatrix, 0);
       // multiplyMM(modelViewMatrix, 0, viewMatrix, 0,  modelMatrix, 0);
        //multiplyMM(mLightMvpMatrix_dynamicShapes, 0, lightsViewProjectionMatrix, 0, viewMatrix, 0);

    }

}
