package mini.paranormalgolf.Graphics;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.Matrix;
import android.util.FloatMath;
import android.util.Log;

import mini.paranormalgolf.Graphics.ShaderPrograms.ColorShaderProgram;
import mini.paranormalgolf.Graphics.ShaderPrograms.DepthMapShaderProgram;
import mini.paranormalgolf.Graphics.ShaderPrograms.ShaderProgram;
import mini.paranormalgolf.Graphics.ShaderPrograms.ShadowingShaderProgram;
import mini.paranormalgolf.Graphics.ShaderPrograms.SkyBoxShaderProgram;
import mini.paranormalgolf.Graphics.ShaderPrograms.TextureShaderProgram;
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
import static android.opengl.GLES20.GL_CLAMP_TO_EDGE;
import static android.opengl.GLES20.GL_COLOR_ATTACHMENT0;
import static android.opengl.GLES20.GL_COLOR_BUFFER_BIT;
import static android.opengl.GLES20.GL_DEPTH_ATTACHMENT;
import static android.opengl.GLES20.GL_DEPTH_BUFFER_BIT;
import static android.opengl.GLES20.GL_DEPTH_COMPONENT16;
import static android.opengl.GLES20.GL_FRAMEBUFFER;
import static android.opengl.GLES20.GL_FRAMEBUFFER_COMPLETE;
import static android.opengl.GLES20.GL_FRONT;
import static android.opengl.GLES20.GL_LEQUAL;
import static android.opengl.GLES20.GL_LESS;
import static android.opengl.GLES20.GL_NEAREST;
import static android.opengl.GLES20.GL_RENDERBUFFER;
import static android.opengl.GLES20.GL_RGBA;
import static android.opengl.GLES20.GL_TEXTURE_2D;
import static android.opengl.GLES20.GL_TEXTURE_MAG_FILTER;
import static android.opengl.GLES20.GL_TEXTURE_MIN_FILTER;
import static android.opengl.GLES20.GL_TEXTURE_WRAP_S;
import static android.opengl.GLES20.GL_TEXTURE_WRAP_T;
import static android.opengl.GLES20.GL_UNSIGNED_BYTE;
import static android.opengl.GLES20.glBindFramebuffer;
import static android.opengl.GLES20.glBindRenderbuffer;
import static android.opengl.GLES20.glBindTexture;
import static android.opengl.GLES20.glCheckFramebufferStatus;
import static android.opengl.GLES20.glClear;
import static android.opengl.GLES20.glClearColor;
import static android.opengl.GLES20.glCullFace;
import static android.opengl.GLES20.glDeleteFramebuffers;
import static android.opengl.GLES20.glDeleteProgram;
import static android.opengl.GLES20.glDeleteRenderbuffers;
import static android.opengl.GLES20.glDeleteTextures;
import static android.opengl.GLES20.glDepthFunc;
import static android.opengl.GLES20.glFramebufferRenderbuffer;
import static android.opengl.GLES20.glFramebufferTexture2D;
import static android.opengl.GLES20.glGenFramebuffers;
import static android.opengl.GLES20.glGenRenderbuffers;
import static android.opengl.GLES20.glGenTextures;
import static android.opengl.GLES20.glRenderbufferStorage;
import static android.opengl.GLES20.glTexImage2D;
import static android.opengl.GLES20.glTexParameteri;
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

    //Wymiary ekranu i depthMapy
    private int displayWidth;
    private int displayHeight;
    private int depthMapWidth;
    private int depthMapHeight;

    //Macierze:
    private final float[] modelMatrix = new float[16];
    private final float[] viewMatrix = new float[16];
    private final float[] projectionMatrix = new float[16];
    private final float[] viewProjectionMatrix = new float[16];
    private final float[] modelViewProjectionMatrix = new float[16];

    private final float[] normalsRotationMatrix = new float[16];

    private final float[] lightsViewMatrix = new float[16];
    private final float[] lightsProjectionMatrix = new float[16];
    private final float[] lightsViewProjectionMatrix = new float[16];

    private final float[] skyBoxViewProjectionMatrix = new float[16];

    //Programy
    private ColorShaderProgram colorShaderProgram;
    private TextureShaderProgram textureShaderProgram;
    private SkyBoxShaderProgram skyBoxShaderProgram;
    private DepthMapShaderProgram depthMapShaderProgram;
    private ShadowingShaderProgram shadowingShaderProgram;

    private int[] frameBufferObjectId;
    private int[] depthTextureId;
    private int[] renderTextureId;

    private boolean withShadow;

    private float xRotation;
    private float yRotation;
    private SkyBox skyBox;
    private LightData lightData = new LightData(0.2f, 0.6f);


    public float getxRotation(){
        return xRotation;
    }

    public float getyRotation(){
        return yRotation;
    }

    public DrawManager(Context context, boolean withShadow){
        initTextures(context);

        this.withShadow = withShadow;
        resetDrawManager();
    }

    public DrawManager(Context context, boolean withShadow, float xRotation, float yRotation){
        this(context, withShadow);
        this.xRotation = xRotation;
        this.yRotation = yRotation;
    }

    public void initTextures(Context context)
    {
        textureShaderProgram = new TextureShaderProgram(context);
        colorShaderProgram = new ColorShaderProgram(context);
        skyBoxShaderProgram = new SkyBoxShaderProgram(context);
        depthMapShaderProgram = new DepthMapShaderProgram(context);
        shadowingShaderProgram = new ShadowingShaderProgram(context);

        Floor.initTextures(context);
        Beam.initTextures(context);
        CheckPoint.initTextures(context);
        Elevator.initTextures(context);
        Diamond.initTextures(context);
        HourGlass.initTextures(context);
        Wall.initTextures(context);

        skyBox = new SkyBox(context);
    }

    public void resetDrawManager(){
        xRotation = INITIAL_ROTATION_X;
        yRotation = INITIAL_ROTATION_Y;
    }

    public void surfaceChange(int width, int height) {
        displayHeight = height;
        displayWidth = width;

        MatrixHelper.perspectiveM(projectionMatrix, VIEW_FIELD_OF_VIEW_DEGREES, (float) width / height, VIEW_NEAR, VIEW_FAR);
        updateSkyBoxMVPMatrix();

        if (withShadow) {
            depthMapWidth = Math.round(displayWidth * DEPTH_MAP_PRECISION);
            depthMapHeight = Math.round(displayHeight * DEPTH_MAP_PRECISION);
            MatrixHelper.perspectiveM(lightsProjectionMatrix, LIGHT_FIELD_OF_VIEW_DEGREES, (float) width / height, LIGHT_NEAR, LIGHT_FAR);
            generateShadowFBO();
        }
    }

    public void releaseResources(){
        try
        {
            //usuwanie shader programów
            glDeleteProgram(colorShaderProgram.getProgram());
            glDeleteProgram(textureShaderProgram.getProgram());
            glDeleteProgram(depthMapShaderProgram.getProgram());
            glDeleteProgram(skyBoxShaderProgram.getProgram());
            glDeleteProgram(shadowingShaderProgram.getProgram());

            //usuwanie fbo
            glDeleteFramebuffers(1, frameBufferObjectId, 0);
            glDeleteRenderbuffers(1, depthTextureId, 0);
            glDeleteTextures(1, renderTextureId, 0);

            //sposób usuwania tekstur:
            int[] textureId = new int[] {Beam.getBeamTexture(), CheckPoint.getCheckPointTexture(), Diamond.getDiamondTexture(), Elevator.getElevatorTexture(), Floor.getBottomFloorTextureNormal(), Floor.getBottomFloorTextureSticky(), Floor.getTopFloorTextureNormal(), Floor.getTopFloorTextureSticky(), Floor.getSideFloorTextureNormal(), Floor.getSideFloorTextureSticky(), Wall.getWallTexture()};
            glDeleteTextures(textureId.length, textureId, 0);
        }
        catch (Exception ex) {}
    }

    private void generateShadowFBO() {

        frameBufferObjectId = new int[1];
        depthTextureId = new int[1];
        renderTextureId = new int[1];

        // create a framebuffer object
        glGenFramebuffers(1, frameBufferObjectId, 0);

        // create render buffer and bind 16-bit depth buffer
        glGenRenderbuffers(1, depthTextureId, 0);
        glBindRenderbuffer(GL_RENDERBUFFER, depthTextureId[0]);
        glRenderbufferStorage(GL_RENDERBUFFER, GL_DEPTH_COMPONENT16, depthMapWidth, depthMapHeight);

        // Try to use a texture depth component
        glGenTextures(1, renderTextureId, 0);
        glBindTexture(GL_TEXTURE_2D, renderTextureId[0]);

        // GL_LINEAR does not make sense for depth texture. However, next tutorial shows usage of GL_LINEAR and PCF. Using GL_NEAREST
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);

        // Remove artifact on the edges of the shadowmap
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);

        glBindFramebuffer(GL_FRAMEBUFFER, frameBufferObjectId[0]);

        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, depthMapWidth, depthMapHeight, 0, GL_RGBA, GL_UNSIGNED_BYTE, null);

        // specify texture as color attachment
        glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, GL_TEXTURE_2D, renderTextureId[0], 0);

        // attach the texture to FBO depth attachment point
        // (not supported with gl_texture_2d)
        glFramebufferRenderbuffer(GL_FRAMEBUFFER, GL_DEPTH_ATTACHMENT, GL_RENDERBUFFER, depthTextureId[0]);


        // check FBO status
        int FBOstatus = glCheckFramebufferStatus(GLES20.GL_FRAMEBUFFER);
        if (FBOstatus != GL_FRAMEBUFFER_COMPLETE) {
            if (LoggerConfig.ON) {
                Log.e("FBO", "GL_FRAMEBUFFER_COMPLETE failed, CANNOT use FBO");
            }
            throw new RuntimeException("GL_FRAMEBUFFER_COMPLETE failed, CANNOT use FBO");
        }
    }

    public void drawBoard(Board board, Ball ball) {
        positionViewAndLightFrustums(ball.getLocation());
        if (withShadow) {
            renderSceneWithShadow(board, ball);
        } else {
            renderSceneWithoutShadow(board, ball);
        }
    }

    //////////////////////////// BEZ CIENI: ////////////////////////////

    private void renderSceneWithoutShadow(Board board, Ball ball){
        glViewport(0, 0, displayWidth, displayHeight);
        glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);

        drawSkyBox();
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

    private void drawBall(Ball ball) {
        textureShaderProgram.useProgram();
        positionBallInScene(ball);
        textureShaderProgram.setUniforms(modelViewProjectionMatrix, modelMatrix, normalsRotationMatrix, lightData, ball.getTexture(), ball.BALL_OPACITY);
        ball.bindData(textureShaderProgram, ShaderProgram.ShaderProgramType.withoutShadowing);
        ball.draw();
    }

    private void drawDiamond(Diamond diamond) {
        textureShaderProgram.useProgram();
        positionBonusInScene(diamond);
        textureShaderProgram.setUniforms(modelViewProjectionMatrix, modelMatrix, normalsRotationMatrix, lightData, diamond.getTexture(), diamond.DIAMOND_OPACITY);
        diamond.bindData(textureShaderProgram, ShaderProgram.ShaderProgramType.withoutShadowing);
        diamond.draw();
    }

    private void drawFloor(Floor floor) {
        textureShaderProgram.useProgram();
        positionObjectInScene(floor.getBottomPart().getLocation());
        textureShaderProgram.setUniforms(modelViewProjectionMatrix, modelMatrix, normalsRotationMatrix, lightData, floor.getBottomFloorTexture(), floor.FLOOR_OPACITY);
        floor.getBottomPart().bindData(textureShaderProgram, ShaderProgram.ShaderProgramType.withoutShadowing);
        floor.getBottomPart().draw();

        for (FloorPart floorPart : floor.getSideParts()) {
            positionObjectInScene(floorPart.getLocation());
            textureShaderProgram.setUniforms(modelViewProjectionMatrix, modelMatrix, normalsRotationMatrix, lightData, floor.getSideFloorTexture(), floor.FLOOR_OPACITY);
            floorPart.bindData(textureShaderProgram, ShaderProgram.ShaderProgramType.withoutShadowing);
            floorPart.draw();
        }

        positionObjectInScene(floor.getTopPart().getLocation());
        textureShaderProgram.setUniforms(modelViewProjectionMatrix, modelMatrix, normalsRotationMatrix, lightData, floor.getTopFloorTexture(), floor.FLOOR_OPACITY);
        floor.getTopPart().bindData(textureShaderProgram, ShaderProgram.ShaderProgramType.withoutShadowing);
        floor.getTopPart().draw();
    }

    private void drawWall(Wall wall) {
        textureShaderProgram.useProgram();
        positionObjectInScene(wall.getLocation());
        textureShaderProgram.setUniforms(modelViewProjectionMatrix, modelMatrix, normalsRotationMatrix, lightData, wall.getTexture(), wall.WALL_OPACITY);
        wall.bindData(textureShaderProgram, ShaderProgram.ShaderProgramType.withoutShadowing);
        wall.draw();
    }

    private void drawBeam(Beam beam) {
        textureShaderProgram.useProgram();
        positionObjectInScene(beam.getLocation());
        textureShaderProgram.setUniforms(modelViewProjectionMatrix, modelMatrix, normalsRotationMatrix, lightData, beam.getTexture(), beam.BEAM_OPACITY);
        beam.bindData(textureShaderProgram, ShaderProgram.ShaderProgramType.withoutShadowing);
        beam.draw();
    }

    private void drawElevator(Elevator elevator) {
        textureShaderProgram.useProgram();
        positionObjectInScene(elevator.getLocation());
        textureShaderProgram.setUniforms(modelViewProjectionMatrix, modelMatrix, normalsRotationMatrix, lightData, elevator.getTexture(), elevator.ELEVATOR_OPACITY);
        elevator.bindData(textureShaderProgram, ShaderProgram.ShaderProgramType.withoutShadowing);
        elevator.draw();
    }

    private void drawFinish(Finish finish) {
        textureShaderProgram.useProgram();
        positionObjectInScene(finish.getLocation());
        textureShaderProgram.setUniforms(modelViewProjectionMatrix, modelMatrix, normalsRotationMatrix, lightData, finish.getTexture(), finish.FINISH_OPACITY);
        finish.bindData(textureShaderProgram, ShaderProgram.ShaderProgramType.withoutShadowing);
        finish.draw();

        colorShaderProgram.useProgram();
        positionObjectInScene(finish.getGlow().getLocation());
        colorShaderProgram.setUniforms(modelViewProjectionMatrix, modelMatrix, normalsRotationMatrix, lightData, finish.getGlow().ifCanFinish() ? finish.getGlow().CAN_FINISH_COLOR : finish.getGlow().CANNOT_FINISH_COLOR);
        finish.getGlow().bindData(colorShaderProgram, ShaderProgram.ShaderProgramType.color);
        finish.getGlow().draw();
    }

    private void drawCheckPoint(CheckPoint checkPoint) {
        textureShaderProgram.useProgram();
        positionObjectInScene(checkPoint.getLocation());
        textureShaderProgram.setUniforms(modelViewProjectionMatrix, modelMatrix, normalsRotationMatrix, lightData, checkPoint.getTexture(), checkPoint.CHECKPOINT_OPACITY);
        checkPoint.bindData(textureShaderProgram, ShaderProgram.ShaderProgramType.withoutShadowing);
        checkPoint.draw();

        if (!checkPoint.isVisited()) {
            colorShaderProgram.useProgram();
            positionObjectInScene(checkPoint.getGlow().getLocation());
            colorShaderProgram.setUniforms(modelViewProjectionMatrix, modelMatrix, normalsRotationMatrix, lightData, checkPoint.getGlow().ACTIVE_COLOR);
            checkPoint.getGlow().bindData(colorShaderProgram, ShaderProgram.ShaderProgramType.color);
            checkPoint.getGlow().draw();
        }
    }

    private void drawHourglass(HourGlass hourGlass) {
        textureShaderProgram.useProgram();
        positionBonusInScene(hourGlass);
        textureShaderProgram.setUniforms(modelViewProjectionMatrix, modelMatrix, normalsRotationMatrix, lightData, hourGlass.getWoodenParts().getTexture(), hourGlass.getWoodenParts().HOURGLASS_WOODEN_PART_OPACITY);
        hourGlass.getWoodenParts().bindData(textureShaderProgram, ShaderProgram.ShaderProgramType.withoutShadowing);
        hourGlass.getWoodenParts().draw();

        colorShaderProgram.useProgram();
        colorShaderProgram.setUniforms(modelViewProjectionMatrix, modelMatrix, normalsRotationMatrix, lightData, hourGlass.GLASS_COLOR);
        hourGlass.bindData(colorShaderProgram, ShaderProgram.ShaderProgramType.color);
        hourGlass.draw();
    }

    //////////////////////////// Z CIENIAMI: ////////////////////////////

    private void renderSceneWithShadow(Board board, Ball ball){
        glBindFramebuffer(GLES20.GL_FRAMEBUFFER, frameBufferObjectId[0]);
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

    private void renderDepthMap(Board board, Ball ball){
        depthMapShaderProgram.useProgram();

        for (Floor floor : board.floors) {
            positionObjectInScene(floor.getBottomPart().getLocation());
            depthMapShaderProgram.setUniforms(lightsViewProjectionMatrix, modelMatrix);
            floor.getBottomPart().bindData(depthMapShaderProgram, ShaderProgram.ShaderProgramType.depthMap);
            floor.getBottomPart().draw();

            for (FloorPart floorPart : floor.getSideParts()) {
                positionObjectInScene(floorPart.getLocation());
                depthMapShaderProgram.setUniforms(lightsViewProjectionMatrix, modelMatrix);
                floorPart.bindData(depthMapShaderProgram, ShaderProgram.ShaderProgramType.depthMap);
                floorPart.draw();
            }

            positionObjectInScene(floor.getTopPart().getLocation());
            depthMapShaderProgram.setUniforms(lightsViewProjectionMatrix, modelMatrix);
            floor.getTopPart().bindData(depthMapShaderProgram, ShaderProgram.ShaderProgramType.depthMap);
            floor.getTopPart().draw();
        }

        for (Wall wall : board.walls) {
            positionObjectInScene(wall.getLocation());
            depthMapShaderProgram.setUniforms(lightsViewProjectionMatrix, modelMatrix);
            wall.bindData(depthMapShaderProgram, ShaderProgram.ShaderProgramType.depthMap);
            wall.draw();
        }

        for (Beam beam : board.beams) {
            positionObjectInScene(beam.getLocation());
            depthMapShaderProgram.setUniforms(lightsViewProjectionMatrix, modelMatrix);
            beam.bindData(depthMapShaderProgram, ShaderProgram.ShaderProgramType.depthMap);
            beam.draw();
        }

        for (Elevator elevator : board.elevators) {
            positionObjectInScene(elevator.getLocation());
            depthMapShaderProgram.setUniforms(lightsViewProjectionMatrix, modelMatrix);
            elevator.bindData(depthMapShaderProgram, ShaderProgram.ShaderProgramType.depthMap);
            elevator.draw();
        }

        positionBallInScene(ball);
        depthMapShaderProgram.setUniforms(lightsViewProjectionMatrix, modelMatrix);
        ball.bindData(depthMapShaderProgram, ShaderProgram.ShaderProgramType.depthMap);
        ball.draw();

        for (Diamond diamond : board.diamonds) {
            positionBonusInScene(diamond);
            depthMapShaderProgram.setUniforms(lightsViewProjectionMatrix, modelMatrix);
            diamond.bindData(depthMapShaderProgram, ShaderProgram.ShaderProgramType.depthMap);
            diamond.draw();
        }

        for (HourGlass hourGlass : board.hourGlasses) {
            positionBonusInScene(hourGlass);
            depthMapShaderProgram.setUniforms(lightsViewProjectionMatrix, modelMatrix);
            hourGlass.getWoodenParts().bindData(depthMapShaderProgram, ShaderProgram.ShaderProgramType.depthMap);
            hourGlass.getWoodenParts().draw();
        }

        positionObjectInScene(board.finish.getLocation());
        depthMapShaderProgram.setUniforms(lightsViewProjectionMatrix, modelMatrix);
        board.finish.bindData(depthMapShaderProgram, ShaderProgram.ShaderProgramType.depthMap);
        board.finish.draw();

        for (CheckPoint checkPoint : board.checkpoints) {
            positionObjectInScene(checkPoint.getLocation());
            depthMapShaderProgram.setUniforms(lightsViewProjectionMatrix, modelMatrix);
            checkPoint.bindData(depthMapShaderProgram, ShaderProgram.ShaderProgramType.depthMap);
            checkPoint.draw();
        }
    }

    private void renderBoardWithShadows(Board board, Ball ball){
        drawSkyBox();
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

    private void drawBallWithShadow(Ball ball) {
        shadowingShaderProgram.useProgram();
        positionBallInScene(ball);
        shadowingShaderProgram.setUniforms(modelViewProjectionMatrix, modelMatrix, normalsRotationMatrix, lightData, ball.getTexture(), ball.BALL_OPACITY, lightsViewProjectionMatrix, renderTextureId[0]);
        ball.bindData(shadowingShaderProgram, ShaderProgram.ShaderProgramType.withShadowing);
        ball.draw();
    }

    private void drawDiamondWithShadow(Diamond diamond) {
        shadowingShaderProgram.useProgram();
        positionBonusInScene(diamond);
        shadowingShaderProgram.setUniforms(modelViewProjectionMatrix, modelMatrix, normalsRotationMatrix, lightData, diamond.getTexture(), diamond.DIAMOND_OPACITY, lightsViewProjectionMatrix, renderTextureId[0]);
        diamond.bindData(shadowingShaderProgram, ShaderProgram.ShaderProgramType.withShadowing);
        diamond.draw();
    }

    private void drawFloorWithShadow(Floor floor) {
        shadowingShaderProgram.useProgram();
        positionObjectInScene(floor.getBottomPart().getLocation());
        shadowingShaderProgram.setUniforms(modelViewProjectionMatrix, modelMatrix, normalsRotationMatrix, lightData, floor.getBottomFloorTexture(), floor.FLOOR_OPACITY, lightsViewProjectionMatrix, renderTextureId[0]);
        floor.getBottomPart().bindData(shadowingShaderProgram, ShaderProgram.ShaderProgramType.withShadowing);
        floor.getBottomPart().draw();

        for (FloorPart floorPart : floor.getSideParts()) {
            positionObjectInScene(floorPart.getLocation());
            shadowingShaderProgram.setUniforms(modelViewProjectionMatrix, modelMatrix, normalsRotationMatrix, lightData, floor.getSideFloorTexture(), floor.FLOOR_OPACITY, lightsViewProjectionMatrix, renderTextureId[0]);
            floorPart.bindData(shadowingShaderProgram, ShaderProgram.ShaderProgramType.withShadowing);
            floorPart.draw();
        }

        positionObjectInScene(floor.getTopPart().getLocation());
        shadowingShaderProgram.setUniforms(modelViewProjectionMatrix, modelMatrix, normalsRotationMatrix, lightData, floor.getTopFloorTexture(), floor.FLOOR_OPACITY, lightsViewProjectionMatrix, renderTextureId[0]);
        floor.getTopPart().bindData(shadowingShaderProgram, ShaderProgram.ShaderProgramType.withShadowing);
        floor.getTopPart().draw();
    }

    private void drawWallWithShadow(Wall wall) {
        shadowingShaderProgram.useProgram();
        positionObjectInScene(wall.getLocation());
        shadowingShaderProgram.setUniforms(modelViewProjectionMatrix, modelMatrix, normalsRotationMatrix, lightData, wall.getTexture(), wall.WALL_OPACITY, lightsViewProjectionMatrix, renderTextureId[0]);
        wall.bindData(shadowingShaderProgram, ShaderProgram.ShaderProgramType.withShadowing);
        wall.draw();
    }

    private void drawBeamWithShadow(Beam beam) {
        shadowingShaderProgram.useProgram();
        positionObjectInScene(beam.getLocation());
        shadowingShaderProgram.setUniforms(modelViewProjectionMatrix, modelMatrix, normalsRotationMatrix, lightData, beam.getTexture(), beam.BEAM_OPACITY, lightsViewProjectionMatrix, renderTextureId[0]);
        beam.bindData(shadowingShaderProgram, ShaderProgram.ShaderProgramType.withShadowing);
        beam.draw();
    }

    private void drawElevatorWithShadow(Elevator elevator) {
        shadowingShaderProgram.useProgram();
        positionObjectInScene(elevator.getLocation());
        shadowingShaderProgram.setUniforms(modelViewProjectionMatrix, modelMatrix, normalsRotationMatrix, lightData, elevator.getTexture(), elevator.ELEVATOR_OPACITY, lightsViewProjectionMatrix, renderTextureId[0]);
        elevator.bindData(shadowingShaderProgram, ShaderProgram.ShaderProgramType.withShadowing);
        elevator.draw();
    }

    private void drawFinishWithShadow(Finish finish) {
        shadowingShaderProgram.useProgram();
        positionObjectInScene(finish.getLocation());
        shadowingShaderProgram.setUniforms(modelViewProjectionMatrix, modelMatrix, normalsRotationMatrix, lightData, finish.getTexture(), finish.FINISH_OPACITY, lightsViewProjectionMatrix, renderTextureId[0]);
        finish.bindData(shadowingShaderProgram, ShaderProgram.ShaderProgramType.withShadowing);
        finish.draw();

        colorShaderProgram.useProgram();
        positionObjectInScene(finish.getGlow().getLocation());
        colorShaderProgram.setUniforms(modelViewProjectionMatrix, modelMatrix, normalsRotationMatrix, lightData, finish.getGlow().ifCanFinish() ? finish.getGlow().CAN_FINISH_COLOR : finish.getGlow().CANNOT_FINISH_COLOR);
        finish.getGlow().bindData(colorShaderProgram, ShaderProgram.ShaderProgramType.color);
        finish.getGlow().draw();
    }

    private void drawCheckPointWithShadow(CheckPoint checkPoint) {
        shadowingShaderProgram.useProgram();
        positionObjectInScene(checkPoint.getLocation());
        shadowingShaderProgram.setUniforms(modelViewProjectionMatrix, modelMatrix, normalsRotationMatrix, lightData, checkPoint.getTexture(), checkPoint.CHECKPOINT_OPACITY, lightsViewProjectionMatrix, renderTextureId[0]);
        checkPoint.bindData(shadowingShaderProgram, ShaderProgram.ShaderProgramType.withShadowing);
        checkPoint.draw();

        if (!checkPoint.isVisited()) {
            colorShaderProgram.useProgram();
            positionObjectInScene(checkPoint.getGlow().getLocation());
            colorShaderProgram.setUniforms(modelViewProjectionMatrix, modelMatrix, normalsRotationMatrix, lightData, checkPoint.getGlow().ACTIVE_COLOR);
            checkPoint.getGlow().bindData( colorShaderProgram, ShaderProgram.ShaderProgramType.color);
            checkPoint.getGlow().draw();
        }
    }

    private void drawHourglassWithShadow(HourGlass hourGlass) {
        shadowingShaderProgram.useProgram();
        positionBonusInScene(hourGlass);
        shadowingShaderProgram.setUniforms(modelViewProjectionMatrix, modelMatrix, normalsRotationMatrix, lightData, hourGlass.getWoodenParts().getTexture(), hourGlass.getWoodenParts().HOURGLASS_WOODEN_PART_OPACITY, lightsViewProjectionMatrix, renderTextureId[0]);
        hourGlass.getWoodenParts().bindData(shadowingShaderProgram, ShaderProgram.ShaderProgramType.withShadowing);
        hourGlass.getWoodenParts().draw();

        colorShaderProgram.useProgram();
        colorShaderProgram.setUniforms(modelViewProjectionMatrix, modelMatrix, normalsRotationMatrix, lightData, hourGlass.GLASS_COLOR);
        hourGlass.bindData(colorShaderProgram, ShaderProgram.ShaderProgramType.color);
        hourGlass.draw();
    }


   ///////////////////////////

    private void drawSkyBox() {
        glDepthFunc(GL_LEQUAL);
        skyBoxShaderProgram.useProgram();
        skyBoxShaderProgram.setUniforms(skyBoxViewProjectionMatrix, skyBox.getTexture());
        skyBox.bindData(skyBoxShaderProgram, ShaderProgram.ShaderProgramType.skyBox);
        skyBox.draw();
        glDepthFunc(GL_LESS);
    }

    ///////////////////////////

    public void handleTouchDrag(float deltaX, float deltaY) {
        xRotation += deltaX / ROTATION_FACTOR;
        yRotation += deltaY / ROTATION_FACTOR;

        if (yRotation < -RIGHT_ANGLE) {
            yRotation = -RIGHT_ANGLE + RIGHT_ANGLE_BIAS;
//        } else if (yRotation > 0){
//            yRotation = 0f;
//        }
        } else if (yRotation > RIGHT_ANGLE){
            yRotation = RIGHT_ANGLE - RIGHT_ANGLE_BIAS;
        }

        updateSkyBoxMVPMatrix();
    }

    private void updateSkyBoxMVPMatrix() {
        float[] skyBoxViewRotationMatrix = new float[16];
        setIdentityM(skyBoxViewRotationMatrix, 0);
        rotateM(skyBoxViewRotationMatrix, 0, -(yRotation + SKYBOX_ANGLE_SHIFT_X), 1f, 0f, 0f);
        rotateM(skyBoxViewRotationMatrix, 0, -(xRotation + SKYBOX_ANGLE_SHIFT_Y), 0f, 1f, 0f);
        multiplyMM(skyBoxViewProjectionMatrix, 0, projectionMatrix, 0, skyBoxViewRotationMatrix, 0);
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

    private void positionObjectInScene(Point location) {
        setIdentityM(modelMatrix, 0);
        translateM(modelMatrix, 0, location.x, location.y, location.z);
        multiplyMM(modelViewProjectionMatrix, 0, viewProjectionMatrix, 0, modelMatrix, 0);
        setIdentityM(normalsRotationMatrix, 0); //bo gdy nie ma rotacji, to nie musimy nic robić z wektorami normalnymi
    }

    private void positionViewAndLightFrustums(Point ballLocation){
        float cameraX = ballLocation.x - CAMERA_ORBIT_RADIUS * FloatMath.sin(xRotation * DEGREE_TO_RAD_CONVERSION) * FloatMath.cos(yRotation * DEGREE_TO_RAD_CONVERSION);
        float cameraY = ballLocation.y - CAMERA_ORBIT_RADIUS * FloatMath.sin(yRotation * DEGREE_TO_RAD_CONVERSION);
        float cameraZ = ballLocation.z - CAMERA_ORBIT_RADIUS * FloatMath.cos(xRotation * DEGREE_TO_RAD_CONVERSION) * FloatMath.cos(yRotation * DEGREE_TO_RAD_CONVERSION);

        setLookAtM(viewMatrix, 0, cameraX, cameraY, cameraZ, ballLocation.x, ballLocation.y, ballLocation.z, 0f, 1f, 0f);
        multiplyMM(viewProjectionMatrix, 0, projectionMatrix, 0, viewMatrix, 0);

        float lightX = ballLocation.x - LIGHT_ORBIT_RADIUS * FloatMath.sin((xRotation + LIGHT_SHIFT_DEGREE_X) * DEGREE_TO_RAD_CONVERSION) * FloatMath.cos(yRotation * DEGREE_TO_RAD_CONVERSION);
        float lightY = ballLocation.y - LIGHT_ORBIT_RADIUS * FloatMath.sin((yRotation + LIGHT_SHIFT_DEGREE_Y) * DEGREE_TO_RAD_CONVERSION);
        float lightZ = ballLocation.z - LIGHT_ORBIT_RADIUS * FloatMath.cos((xRotation + LIGHT_SHIFT_DEGREE_X) * DEGREE_TO_RAD_CONVERSION) * FloatMath.cos(yRotation * DEGREE_TO_RAD_CONVERSION);
        lightData.position = new Point(lightX, lightY, lightZ);

        if(withShadow) {
            Matrix.setLookAtM(lightsViewMatrix, 0, lightData.position.x, lightData.position.y, lightData.position.z, ballLocation.x, ballLocation.y, ballLocation.z, 0f, 1f, 0f);
            multiplyMM(lightsViewProjectionMatrix, 0, lightsProjectionMatrix, 0, lightsViewMatrix, 0);
        }
    }



    //STAŁE:

    //Dla orbit kamery i światła
    private final float CAMERA_ORBIT_RADIUS = 20f;
    private final float LIGHT_ORBIT_RADIUS = 30f;
    private final float LIGHT_SHIFT_DEGREE_X = 20f;
    private final float LIGHT_SHIFT_DEGREE_Y = -15f;
    private final float DEGREE_TO_RAD_CONVERSION = (float) Math.PI / 180;

    //Dla obrotów:
    private final float INITIAL_ROTATION_X = 180f;
    private final float INITIAL_ROTATION_Y = -45f;

    private final float ROTATION_FACTOR = 16f;
    private final float RIGHT_ANGLE = 90f;
    private final float RIGHT_ANGLE_BIAS = 0.01f;

    private final float SKYBOX_ANGLE_SHIFT_X = 15f;
    private final float SKYBOX_ANGLE_SHIFT_Y = 180f;

    //Dla frustum widoku
    private final float VIEW_FIELD_OF_VIEW_DEGREES = 45;
    private final float VIEW_NEAR = 1f;
    private final float VIEW_FAR = 100f;

    //Dla frustum światła
    private final float LIGHT_FIELD_OF_VIEW_DEGREES = 45;
    private final float LIGHT_NEAR = 2f;
    private final float LIGHT_FAR = 100f;

    private final float DEPTH_MAP_PRECISION = 1.5f;
}
