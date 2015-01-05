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
import mini.paranormalgolf.Primitives.Vector;

import static android.opengl.GLES20.GL_BACK;
import static android.opengl.GLES20.GL_BLEND;
import static android.opengl.GLES20.GL_COLOR_BUFFER_BIT;
import static android.opengl.GLES20.GL_CULL_FACE;
import static android.opengl.GLES20.GL_DEPTH_BUFFER_BIT;
import static android.opengl.GLES20.GL_FRONT;
import static android.opengl.GLES20.GL_FRONT_AND_BACK;
import static android.opengl.GLES20.GL_LEQUAL;
import static android.opengl.GLES20.GL_LESS;
import static android.opengl.GLES20.GL_ONE_MINUS_SRC_ALPHA;
import static android.opengl.GLES20.GL_SRC_ALPHA;
import static android.opengl.GLES20.glBindFramebuffer;
import static android.opengl.GLES20.glBlendFunc;
import static android.opengl.GLES20.glClear;
import static android.opengl.GLES20.glClearColor;
import static android.opengl.GLES20.glCullFace;
import static android.opengl.GLES20.glDepthFunc;
import static android.opengl.GLES20.glDisable;
import static android.opengl.GLES20.glEnable;
import static android.opengl.GLES20.glViewport;
import static android.opengl.Matrix.invertM;
import static android.opengl.Matrix.multiplyMM;
import static android.opengl.Matrix.orthoM;
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


    public DrawManager(Context context) {
        textureShaderProgram = new TextureShaderProgram(context);
        colorShaderProgram = new ColorShaderProgram(context);
        skyboxShaderProgram = new SkyboxShaderProgram(context);
        skybox = new Skybox(context, new Point(0, 0, 0), Skybox.SkyboxTexture.dayClouds);

        depthMapShaderProgram = new DepthMapShaderProgram(context);
        tmpShaderProgram = new TmpShaderProgram(context);
    }

    public void surfaceChange(int width, int height) {
        glViewport(0, 0, width, height);
        MatrixHelper.perspectiveM(projectionMatrix, fieldOfViewDegree, (float) width / (float) height, near, far);
        MatrixHelper.perspectiveM(mLightProjectionMatrix, 120f, (float) width / (float) height, near, far);

        updateSkyboxMVPMatrix();

        mDisplayHeight = height;
        mDisplayWidth = width;
        float ratio = 1.5f;
        mShadowMapWidth = Math.round(mDisplayWidth * ratio);
        mShadowMapHeight = Math.round(mDisplayHeight * ratio);

        generateShadowFBO();
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
        GLES20.glRenderbufferStorage(GLES20.GL_RENDERBUFFER, GLES20.GL_DEPTH_COMPONENT16, mShadowMapWidth, mShadowMapHeight);

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

        GLES20.glTexImage2D(GLES20.GL_TEXTURE_2D, 0, GLES20.GL_RGBA, mShadowMapWidth, mShadowMapHeight, 0, GLES20.GL_RGBA, GLES20.GL_UNSIGNED_BYTE, null);

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
        setLookAtM(viewMatrix, 0, ballLocation.x + cameraTranslation.x, ballLocation.y + cameraTranslation.y, ballLocation.z + cameraTranslation.z, ballLocation.x, ballLocation.y, ballLocation.z, 0f, 1f, 0f);
        multiplyMM(viewProjectionMatrix, 0, projectionMatrix, 0, viewMatrix, 0);

        Matrix.setLookAtM(mLightViewMatrix, 0,
                lightData.position.x, lightData.position.y, lightData.position.z,
                0f,0f,0f,
                0f,1f,0f);
        multiplyMM(mLightMvpMatrix_staticShapes, 0, mLightProjectionMatrix, 0, mLightViewMatrix, 0);
    }



    DepthMapShaderProgram depthMapShaderProgram;
    TmpShaderProgram tmpShaderProgram;

    private int mDisplayWidth;
    private int mDisplayHeight;
    private int mShadowMapWidth;
    private int mShadowMapHeight;

    int[] fboId;
    int[] depthTextureId;
    int[] renderTextureId;

    private final float[] mLightMvpMatrix_staticShapes = new float[16];
    private final float[] mLightMvpMatrix_dynamicShapes = new float[16];
    private final float[] mLightProjectionMatrix = new float[16];
    private final float[] mLightViewMatrix = new float[16];

    public void drawShadow(Board board, Ball ball) {

        glBindFramebuffer(GLES20.GL_FRAMEBUFFER, fboId[0]);

        glViewport(0, 0, mShadowMapWidth, mShadowMapHeight);

        glClearColor(1.0f, 1.0f, 1.0f, 1.0f);
        glClear(GL_DEPTH_BUFFER_BIT | GL_COLOR_BUFFER_BIT);
        glCullFace(GL_FRONT);

        renderDepthMap(board, ball);


        glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0);
        glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);
        glViewport(0, 0, mDisplayWidth, mDisplayHeight);

        glCullFace(GL_BACK);

        renderScene(board, ball);
    }

    private void renderDepthMap(Board board, Ball ball){
        depthMapShaderProgram.useProgram();

        for (Floor floor : board.floors) {
            positionObjectInScene(floor.getBottomPart().getLocation());
            depthMapShaderProgram.setUniforms(mLightMvpMatrix_staticShapes, modelMatrix);
            floor.getBottomPart().bindDepthMapData(depthMapShaderProgram);
            floor.getBottomPart().draw();

            for (FloorPart floorPart : floor.getSideParts()) {
                positionObjectInScene(floorPart.getLocation());
                depthMapShaderProgram.setUniforms(mLightMvpMatrix_staticShapes, modelMatrix);
                floorPart.bindDepthMapData(depthMapShaderProgram);
                floorPart.draw();
            }

            positionObjectInScene(floor.getTopPart().getLocation());
            depthMapShaderProgram.setUniforms(mLightMvpMatrix_staticShapes, modelMatrix);
            floor.getTopPart().bindDepthMapData(depthMapShaderProgram);
            floor.getTopPart().draw();
        }

        for (Wall wall : board.walls) {
            positionObjectInScene(wall.getLocation());
            depthMapShaderProgram.setUniforms(mLightMvpMatrix_staticShapes, modelMatrix);
            wall.bindDepthMapData(depthMapShaderProgram);
            wall.draw();
        }

        for (Beam beam : board.beams) {
            positionObjectInScene(beam.getLocation());
            depthMapShaderProgram.setUniforms(mLightMvpMatrix_staticShapes, modelMatrix);
            beam.bindDepthMapData(depthMapShaderProgram);
            beam.draw();
        }

        for (Elevator elevator : board.elevators) {
            positionObjectInScene(elevator.getLocation());
            depthMapShaderProgram.setUniforms(mLightMvpMatrix_staticShapes, modelMatrix);
            elevator.bindDepthMapData(depthMapShaderProgram);
            elevator.draw();
        }

        positionBallInScene(ball);
        depthMapShaderProgram.setUniforms(mLightMvpMatrix_staticShapes, modelMatrix);
        ball.bindDepthMapData(depthMapShaderProgram);
        ball.draw();

        for (Diamond diamond : board.diamonds) {
            positionBonusInScene(diamond);
            depthMapShaderProgram.setUniforms(mLightMvpMatrix_staticShapes, modelMatrix);
            diamond.bindDepthMapData(depthMapShaderProgram);
            diamond.draw();
        }

        for (HourGlass hourGlass : board.hourGlasses) {
            positionBonusInScene(hourGlass);
            depthMapShaderProgram.setUniforms(mLightMvpMatrix_staticShapes, modelMatrix);
            hourGlass.getWoodenParts().bindDepthMapData(depthMapShaderProgram);
            hourGlass.getWoodenParts().draw();
        }

        positionObjectInScene(board.finish.getLocation());
        depthMapShaderProgram.setUniforms(mLightMvpMatrix_staticShapes, modelMatrix);
        board.finish.bindDepthMapData(depthMapShaderProgram);
        board.finish.draw();

        for (CheckPoint checkPoint : board.checkpoints) {
            positionObjectInScene(checkPoint.getLocation());
            depthMapShaderProgram.setUniforms(mLightMvpMatrix_staticShapes, modelMatrix);
            checkPoint.bindDepthMapData(depthMapShaderProgram);
            checkPoint.draw();
        }
    }

    private void renderScene(Board board, Ball ball){
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
        tmpShaderProgram.useProgram();
        positionBallInScene(ball);
        tmpShaderProgram.setUniforms(modelViewProjectionMatrix, modelMatrix, normalsRotationMatrix, lightData, ball.getTexture(), ball.BALL_OPACITY, mLightMvpMatrix_staticShapes, renderTextureId[0]);
        ball.bindData(tmpShaderProgram);
        ball.draw();
    }

    private void drawDiamond(Diamond diamond) {
        tmpShaderProgram.useProgram();
        positionBonusInScene(diamond);
        tmpShaderProgram.setUniforms(modelViewProjectionMatrix, modelMatrix, normalsRotationMatrix, lightData, diamond.getTexture(), diamond.DIAMOND_OPACITY, mLightMvpMatrix_staticShapes, renderTextureId[0]);
        diamond.bindData(tmpShaderProgram);
        diamond.draw();
    }

    private void drawFloor(Floor floor) {
        tmpShaderProgram.useProgram();
        positionObjectInScene(floor.getBottomPart().getLocation());
        tmpShaderProgram.setUniforms(modelViewProjectionMatrix, modelMatrix, normalsRotationMatrix, lightData, floor.getBottomFloorTexture(), floor.FLOOR_OPACITY, mLightMvpMatrix_staticShapes, renderTextureId[0]);
        floor.getBottomPart().bindData(tmpShaderProgram);
        floor.getBottomPart().draw();

        for (FloorPart floorPart : floor.getSideParts()) {
            positionObjectInScene(floorPart.getLocation());
            tmpShaderProgram.setUniforms(modelViewProjectionMatrix, modelMatrix, normalsRotationMatrix, lightData, floor.getSideFloorTexture(), floor.FLOOR_OPACITY, mLightMvpMatrix_staticShapes, renderTextureId[0]);
            floorPart.bindData(tmpShaderProgram);
            floorPart.draw();
        }

        positionObjectInScene(floor.getTopPart().getLocation());
        tmpShaderProgram.setUniforms(modelViewProjectionMatrix, modelMatrix, normalsRotationMatrix, lightData, floor.getTopFloorTexture(), floor.FLOOR_OPACITY, mLightMvpMatrix_staticShapes, renderTextureId[0]);
        floor.getTopPart().bindData(tmpShaderProgram);
        floor.getTopPart().draw();
    }

    private void drawWall(Wall wall) {
        tmpShaderProgram.useProgram();
        positionObjectInScene(wall.getLocation());
        tmpShaderProgram.setUniforms(modelViewProjectionMatrix, modelMatrix, normalsRotationMatrix, lightData, wall.getTexture(), wall.WALL_OPACITY, mLightMvpMatrix_staticShapes, renderTextureId[0]);
        wall.bindData(tmpShaderProgram);
        wall.draw();
    }

    private void drawBeam(Beam beam) {
        tmpShaderProgram.useProgram();
        positionObjectInScene(beam.getLocation());
        tmpShaderProgram.setUniforms(modelViewProjectionMatrix, modelMatrix, normalsRotationMatrix, lightData, beam.getTexture(), beam.BEAM_OPACITY, mLightMvpMatrix_staticShapes, renderTextureId[0]);
        beam.bindData(tmpShaderProgram);
        beam.draw();
    }

    private void drawElevator(Elevator elevator) {
        tmpShaderProgram.useProgram();
        positionObjectInScene(elevator.getLocation());
        tmpShaderProgram.setUniforms(modelViewProjectionMatrix, modelMatrix, normalsRotationMatrix, lightData, elevator.getTexture(), elevator.ELEVATOR_OPACITY, mLightMvpMatrix_staticShapes, renderTextureId[0]);
        elevator.bindData(tmpShaderProgram);
        elevator.draw();
    }

    private void drawFinish(Finish finish) {
        tmpShaderProgram.useProgram();
        positionObjectInScene(finish.getLocation());
        tmpShaderProgram.setUniforms(modelViewProjectionMatrix, modelMatrix, normalsRotationMatrix, lightData, finish.getTexture(), finish.FINISH_OPACITY, mLightMvpMatrix_staticShapes, renderTextureId[0]);
        finish.bindData(tmpShaderProgram);
        finish.draw();

        colorShaderProgram.useProgram();
        positionObjectInScene(finish.getGlow().getLocation());
        colorShaderProgram.setUniforms(modelViewProjectionMatrix, modelMatrix, normalsRotationMatrix, lightData, finish.getGlow().getIfCanFinish() ? finish.getGlow().CAN_FINISH_COLOR : finish.getGlow().CANNOT_FINISH_COLOR);
        finish.getGlow().bindData(colorShaderProgram);
        finish.getGlow().draw();
    }

    private void drawCheckPoint(CheckPoint checkPoint) {
        tmpShaderProgram.useProgram();
        positionObjectInScene(checkPoint.getLocation());
        tmpShaderProgram.setUniforms(modelViewProjectionMatrix, modelMatrix, normalsRotationMatrix, lightData, checkPoint.getTexture(), checkPoint.CHECKPOINT_OPACITY, mLightMvpMatrix_staticShapes, renderTextureId[0]);
        checkPoint.bindData(tmpShaderProgram);
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
        tmpShaderProgram.useProgram();
        positionBonusInScene(hourGlass);
        tmpShaderProgram.setUniforms(modelViewProjectionMatrix, modelMatrix, normalsRotationMatrix, lightData, hourGlass.getWoodenParts().getTexture(), hourGlass.getWoodenParts().HOURGLASS_WOODEN_PART_OPACITY, mLightMvpMatrix_staticShapes, renderTextureId[0]);
        hourGlass.getWoodenParts().bindData(tmpShaderProgram);
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
        //multiplyMM(mLightMvpMatrix_dynamicShapes, 0, mLightMvpMatrix_staticShapes, 0, viewMatrix, 0);

    }

}
