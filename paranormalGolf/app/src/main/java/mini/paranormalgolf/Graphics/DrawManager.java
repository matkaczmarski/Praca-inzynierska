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
import mini.paranormalgolf.Physics.HourGlass;
import mini.paranormalgolf.Physics.SkyBox;
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
 * Służy do generowania pojedynczej klatki obrazu: rysowania wszystkich obiektów, wyznaczanie mapy głębokości cieni, obroty kamery.
 */
public class DrawManager {

    /**
     * Szerokość ekranu.
     */
    private int displayWidth;
    /**
     * Wysokość ekranu.
     */
    private int displayHeight;
    /**
     * Szerokość mapy głębokości cieni.
     */
    private int depthMapWidth;
    /**
     * Wysokość mapy głębokości cieni.
     */
    private int depthMapHeight;


    /**
     * Macierz modeli stosowana do umiejscawiania modeli obiektów w odpowiedniej pozycji we współrzędnych świata.
     */
    private final float[] modelsMatrix = new float[16];
    /**
     * Macierz widoku kamery określająca położenie kamery oraz kierunek jej patrzenia.
     */
    private final float[] viewMatrix = new float[16];
    /**
     * Macierz projekcji kamery określająca bryłę zasięgu widoku.
     */
    private final float[] projectionMatrix = new float[16];
    /**
     * Iloczyn macierzy projekcji i macierzy widoku.
     */
    private final float[] viewProjectionMatrix = new float[16];
    /**
     * Iloczyn macierzy modelu oraz macierzy viewProjection.
     */
    private final float[] modelViewProjectionMatrix = new float[16];

    /**
     * Macierz do rotacji wektorów normalnych.
     */
    private final float[] normalsRotationMatrix = new float[16];

    /**
     * Macierz widoku światła określająca położenie jego źródła i nominalny kierunek świecenia.
     */
    private final float[] lightsViewMatrix = new float[16];
    /**
     * Macierz projekcji światła określający zasięg padania promieni.
     */
    private final float[] lightsProjectionMatrix = new float[16];
    /**
     * Iloczyn macierzy projekcji i widoku światła.
     */
    private final float[] lightsViewProjectionMatrix = new float[16];

    /**
     * Macierz określająca część tła, która w danym momencie jest wyświetlana.
     */
    private final float[] skyBoxViewProjectionMatrix = new float[16];


    /**
     *  Program do rysowania modeli obiektów przy użyciu określonego koloru.
     */
    private ColorShaderProgram colorShaderProgram;
    /**
     * Program do mapowania tekstur na modele obiektów (bez generowania ich cieni).
     */
    private TextureShaderProgram textureShaderProgram;
    /**
     * Program do rysowania przestrzennego tła.
     */
    private SkyBoxShaderProgram skyBoxShaderProgram;
    /**
     * Program do tworzenia mapy głębokośi cieni stosowanej do generowania cieni obiektów.
     */
    private DepthMapShaderProgram depthMapShaderProgram;
    /**
     * Program do mapowania tekstur na modele obiektów wraz z generowaniem ich cieni.
     */
    private ShadowingShaderProgram shadowingShaderProgram;

    /**
     * Identyfikator przechowujący informacje o buforze klatki wykorzystywanym do tworzenia mapy cieni.
     */
    private int[] frameBufferObjectId;
    /**
     * Identyfikator przechowujący informacje o buforze klatki wykorzystywanym do tworzenia mapy cieni.
     */
    private int[] depthTextureId;
    /**
     * Identyfikator przechowujący informacje o buforze klatki wykorzystywanym do tworzenia mapy cieni.
     */
    private int[] renderTextureId;

    /**
     * Informacja, czy opcja generowania cieni jest włączona (true) czy wyłączona (false).
     */
    private boolean withShadow;

    /**
     * Wartość kąta obrotu kamery wokół osi Y.
     */
    private float xRotation;
    /**
     * Wartość kąta obrotu wokół osi X.
     */
    private float yRotation;
    /**
     * Definiuje przestrzenne tło sceny.
     */
    private SkyBox skyBox;
    /**
     * Parametry światła.
     */
    private LightData lightData = new LightData(0.3f, 0.6f);

    /**
     * Zwraca wartość aktualnego kąta obrotu kamery wokół osi Y.
     * @return Wartość <em><b>xRotation</b></em>.
     */
    public float getxRotation(){
        return xRotation;
    }

    /**
     * Tworzy obiekt generujący pojedynczą klatkę.
     * @param context Bieżący kontekst pozwalający uzyskać dostęp do zasobów aplikacji.
     * @param withShadow Informacja czy podczas generowania klatki zastosować generowanie cieni.
     */
    public DrawManager(Context context, boolean withShadow){
        initialize(context);
        this.withShadow = withShadow;
        xRotation = INITIAL_ROTATION_X;
        yRotation = INITIAL_ROTATION_Y;
    }

    /**
     * Tworzy programy, obiekt tła sceny oraz pobiera tekstury z zasobów.
     * @param context Bieżący kontekst pozwalający uzyskać dostęp do zasobów aplikacji.
     */
    public void initialize(Context context){
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
        Finish.initTextures(context);
        Ball.initTextures(context);

        skyBox = new SkyBox(context);
    }

    /**
     * Zwalnia zasoby.
     */
    public void releaseResources(){
        try {
            glDeleteProgram(colorShaderProgram.getProgram());
            glDeleteProgram(textureShaderProgram.getProgram());
            glDeleteProgram(depthMapShaderProgram.getProgram());
            glDeleteProgram(skyBoxShaderProgram.getProgram());
            glDeleteProgram(shadowingShaderProgram.getProgram());

            int[] textureId = new int[]{Ball.getTexture(), Beam.getTexture(), CheckPoint.getTexture(), Diamond.getTexture(), HourGlass.getTexture(), Elevator.getTexture(), Floor.getStandardFloorTextureId(), Floor.getStickyFloorTextureId(), Wall.getTexture(), Finish.getTexture(), SkyBox.getTexture()};
            glDeleteTextures(textureId.length, textureId, 0);

            if (withShadow){
                glDeleteRenderbuffers(1, depthTextureId, 0);
                glDeleteTextures(1, renderTextureId, 0);
                glDeleteFramebuffers(1, frameBufferObjectId, 0);
            }
        }
        catch (Exception ex) {
            if(LoggerConfig.ON) {
                Log.d("RELEASING RESOURCES:", ex.getMessage());
            }
        }
    }

    /**
     * Wyznacza macierze projekcji oraz inicjuje mapę głębokości cieni.
     * @param width Szerokość ekranu.
     * @param height Wysokość ekranu.
     */
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

    /**
     * Inicjuje mapę głębokości cieni.
     */
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

    /**
     * Rysuje wszystkie obiekty na planszy oraz kulkę.
     * @param board Obiekt zawierający wszystkie elementy planszy.
     * @param ball Obiekt kulki.
     */
    public void drawBoard(Board board, Ball ball) {
        positionViewAndLightFrustums(ball.getLocation());
        if (withShadow) {
            renderSceneWithShadow(board, ball);
        } else {
            renderSceneWithoutShadow(board, ball);
        }
    }

    //////////////////////////// BEZ CIENI: ////////////////////////////

    /**
     * Rysuje wszystkie obiekty bez generowania ich cieni.
     * @param board Obiekt zawierający wszystkie elementy planszy.
     * @param ball Obiekt kulki.
     */
    private void renderSceneWithoutShadow(Board board, Ball ball){
        glViewport(0, 0, displayWidth, displayHeight);
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

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

    /**
     * Rysuje model kulki w odpowiednim miejscu na ekranie, bez generowania jego cienia.
     * @param ball Obiekt kulki.
     */
    private void drawBall(Ball ball) {
        textureShaderProgram.useProgram();
        positionBallInScene(ball);
        textureShaderProgram.setUniforms(modelViewProjectionMatrix, modelsMatrix, normalsRotationMatrix, lightData, Ball.getTexture(), ball.BALL_OPACITY);
        ball.bindAttributes(textureShaderProgram, ShaderProgram.ShaderProgramType.withoutShadowing);
        ball.draw();
    }

    /**
     * Rysuje model diamentu w odpowiednim miejscu na ekranie, bez generowania jego cienia.
     * @param diamond Obiekt diamentu.
     */
    private void drawDiamond(Diamond diamond) {
        textureShaderProgram.useProgram();
        positionBonusInScene(diamond);
        textureShaderProgram.setUniforms(modelViewProjectionMatrix, modelsMatrix, normalsRotationMatrix, lightData, Diamond.getTexture(), diamond.DIAMOND_OPACITY);
        diamond.bindAttributes(textureShaderProgram, ShaderProgram.ShaderProgramType.withoutShadowing);
        diamond.draw();
    }

    /**
     * Rysuje model podłogi w odpowiednim miejscu na ekranie, bez generowania jego cienia.
     * @param floor
     */
    private void drawFloor(Floor floor) {
        textureShaderProgram.useProgram();
        positionObjectInScene(floor.getLocation());
        textureShaderProgram.setUniforms(modelViewProjectionMatrix, modelsMatrix, normalsRotationMatrix, lightData, floor.getTexture(), floor.FLOOR_OPACITY);
        floor.bindAttributes(textureShaderProgram, ShaderProgram.ShaderProgramType.withoutShadowing);
        floor.draw();
    }

    /**
     * Rysuje model ściany w odpowiednim miejscu na ekranie, bez generowania jego cienia.
     * @param wall Obiekt ściany.
     */
    private void drawWall(Wall wall) {
        textureShaderProgram.useProgram();
        positionObjectInScene(wall.getLocation());
        textureShaderProgram.setUniforms(modelViewProjectionMatrix, modelsMatrix, normalsRotationMatrix, lightData, Wall.getTexture(), wall.WALL_OPACITY);
        wall.bindAttributes(textureShaderProgram, ShaderProgram.ShaderProgramType.withoutShadowing);
        wall.draw();
    }

    /**
     * Rysuje model belki w odpowiednim miejscu na ekranie, bez generowania jego cienia.
     * @param beam Obiekt belki.
     */
    private void drawBeam(Beam beam) {
        textureShaderProgram.useProgram();
        positionObjectInScene(beam.getLocation());
        textureShaderProgram.setUniforms(modelViewProjectionMatrix, modelsMatrix, normalsRotationMatrix, lightData, Beam.getTexture(), beam.BEAM_OPACITY);
        beam.bindAttributes(textureShaderProgram, ShaderProgram.ShaderProgramType.withoutShadowing);
        beam.draw();
    }

    /**
     * Rysuje model windy w odpowiednim miejscu na ekranie, bez generowania jego cienia.
     * @param elevator Obiekt windy.
     */
    private void drawElevator(Elevator elevator) {
        textureShaderProgram.useProgram();
        positionObjectInScene(elevator.getLocation());
        textureShaderProgram.setUniforms(modelViewProjectionMatrix, modelsMatrix, normalsRotationMatrix, lightData, Elevator.getTexture(), elevator.ELEVATOR_OPACITY);
        elevator.bindAttributes(textureShaderProgram, ShaderProgram.ShaderProgramType.withoutShadowing);
        elevator.draw();
    }

    /**
     * Rysuje model mety w odpowiednim miejscu na ekranie, bez generowania jego cienia.
     * @param finish Obiekt mety.
     */
    private void drawFinish(Finish finish) {
        textureShaderProgram.useProgram();
        positionObjectInScene(finish.getLocation());
        textureShaderProgram.setUniforms(modelViewProjectionMatrix, modelsMatrix, normalsRotationMatrix, lightData, Finish.getTexture(), finish.FINISH_OPACITY);
        finish.bindAttributes(textureShaderProgram, ShaderProgram.ShaderProgramType.withoutShadowing);
        finish.draw();

        colorShaderProgram.useProgram();
        positionObjectInScene(finish.getGlow().getLocation());
        colorShaderProgram.setUniforms(modelViewProjectionMatrix, modelsMatrix, normalsRotationMatrix, lightData, finish.isActive() ? finish.getGlow().ACTIVE_FINISH_COLOR : finish.getGlow().INACTIVE_FINISH_COLOR);
        finish.getGlow().bindAttributes(colorShaderProgram, ShaderProgram.ShaderProgramType.color);
        finish.getGlow().draw();
    }

    /**
     * Rysuje model punktu kontrolnego w odpowiednim miejscu na ekranie, bez generowania jego cienia.
     * @param checkPoint Obiekt punktu kontrolnego.
     */
    private void drawCheckPoint(CheckPoint checkPoint) {
        textureShaderProgram.useProgram();
        positionObjectInScene(checkPoint.getLocation());
        textureShaderProgram.setUniforms(modelViewProjectionMatrix, modelsMatrix, normalsRotationMatrix, lightData, CheckPoint.getTexture(), checkPoint.CHECKPOINT_OPACITY);
        checkPoint.bindAttributes(textureShaderProgram, ShaderProgram.ShaderProgramType.withoutShadowing);
        checkPoint.draw();

        if (!checkPoint.isVisited()) {
            colorShaderProgram.useProgram();
            positionObjectInScene(checkPoint.getGlow().getLocation());
            colorShaderProgram.setUniforms(modelViewProjectionMatrix, modelsMatrix, normalsRotationMatrix, lightData, checkPoint.getGlow().CHECKPOINT_GLOW_COLOR);
            checkPoint.getGlow().bindAttributes(colorShaderProgram, ShaderProgram.ShaderProgramType.color);
            checkPoint.getGlow().draw();
        }
    }

    /**
     * Rysuje model klepsydry w odpowiednim miejscu na ekranie, bez generowania jego cienia.
     * @param hourGlass Obiekt klepsydry.
     */
    private void drawHourglass(HourGlass hourGlass) {
        textureShaderProgram.useProgram();
        positionBonusInScene(hourGlass);
        textureShaderProgram.setUniforms(modelViewProjectionMatrix, modelsMatrix, normalsRotationMatrix, lightData, HourGlass.getTexture(), hourGlass.getWoodenParts().HOURGLASS_WOODEN_PART_OPACITY);
        hourGlass.getWoodenParts().bindAttributes(textureShaderProgram, ShaderProgram.ShaderProgramType.withoutShadowing);
        hourGlass.getWoodenParts().draw();

        colorShaderProgram.useProgram();
        colorShaderProgram.setUniforms(modelViewProjectionMatrix, modelsMatrix, normalsRotationMatrix, lightData, hourGlass.GLASS_COLOR);
        hourGlass.bindAttributes(colorShaderProgram, ShaderProgram.ShaderProgramType.color);
        hourGlass.draw();
    }

    //////////////////////////// Z CIENIAMI: ////////////////////////////

    /**
     * Rysuje wszystkie elementy razem z tworzonymi przez nie cieniami
     * @param board Obiekt zawierający wszystkie elementy planszy.
     * @param ball Obiekt kulki.
     */
    private void renderSceneWithShadow(Board board, Ball ball){
        glBindFramebuffer(GL_FRAMEBUFFER, frameBufferObjectId[0]);
        glViewport(0, 0, depthMapWidth, depthMapHeight);
        glClearColor(1.0f, 1.0f, 1.0f, 1.0f);
        glClear(GL_DEPTH_BUFFER_BIT | GL_COLOR_BUFFER_BIT);
        glCullFace(GL_FRONT);
        renderDepthMap(board, ball);
        glCullFace(GL_BACK);

        glBindFramebuffer(GL_FRAMEBUFFER, 0);
        glViewport(0, 0, displayWidth, displayHeight);
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

        renderBoardWithShadows(board, ball);
    }

    /**
     * Generuje mapę głębokości cieni.
     * @param board Obiekt zawierający wszystkie elementy planszy.
     * @param ball Obiekt kulki.
     */
    private void renderDepthMap(Board board, Ball ball){
        depthMapShaderProgram.useProgram();

        for (Floor floor : board.floors) {
            positionObjectInScene(floor.getLocation());
            depthMapShaderProgram.setUniforms(lightsViewProjectionMatrix, modelsMatrix);
            floor.bindAttributes(depthMapShaderProgram, ShaderProgram.ShaderProgramType.depthMap);
            floor.draw();
        }

        for (Wall wall : board.walls) {
            positionObjectInScene(wall.getLocation());
            depthMapShaderProgram.setUniforms(lightsViewProjectionMatrix, modelsMatrix);
            wall.bindAttributes(depthMapShaderProgram, ShaderProgram.ShaderProgramType.depthMap);
            wall.draw();
        }

        for (Beam beam : board.beams) {
            positionObjectInScene(beam.getLocation());
            depthMapShaderProgram.setUniforms(lightsViewProjectionMatrix, modelsMatrix);
            beam.bindAttributes(depthMapShaderProgram, ShaderProgram.ShaderProgramType.depthMap);
            beam.draw();
        }

        for (Elevator elevator : board.elevators) {
            positionObjectInScene(elevator.getLocation());
            depthMapShaderProgram.setUniforms(lightsViewProjectionMatrix, modelsMatrix);
            elevator.bindAttributes(depthMapShaderProgram, ShaderProgram.ShaderProgramType.depthMap);
            elevator.draw();
        }

        positionBallInScene(ball);
        depthMapShaderProgram.setUniforms(lightsViewProjectionMatrix, modelsMatrix);
        ball.bindAttributes(depthMapShaderProgram, ShaderProgram.ShaderProgramType.depthMap);
        ball.draw();

        for (Diamond diamond : board.diamonds) {
            positionBonusInScene(diamond);
            depthMapShaderProgram.setUniforms(lightsViewProjectionMatrix, modelsMatrix);
            diamond.bindAttributes(depthMapShaderProgram, ShaderProgram.ShaderProgramType.depthMap);
            diamond.draw();
        }

        for (HourGlass hourGlass : board.hourGlasses) {
            positionBonusInScene(hourGlass);
            depthMapShaderProgram.setUniforms(lightsViewProjectionMatrix, modelsMatrix);
            hourGlass.getWoodenParts().bindAttributes(depthMapShaderProgram, ShaderProgram.ShaderProgramType.depthMap);
            hourGlass.getWoodenParts().draw();
        }

        positionObjectInScene(board.finish.getLocation());
        depthMapShaderProgram.setUniforms(lightsViewProjectionMatrix, modelsMatrix);
        board.finish.bindAttributes(depthMapShaderProgram, ShaderProgram.ShaderProgramType.depthMap);
        board.finish.draw();

        for (CheckPoint checkPoint : board.checkpoints) {
            positionObjectInScene(checkPoint.getLocation());
            depthMapShaderProgram.setUniforms(lightsViewProjectionMatrix, modelsMatrix);
            checkPoint.bindAttributes(depthMapShaderProgram, ShaderProgram.ShaderProgramType.depthMap);
            checkPoint.draw();
        }
    }

    /**
     * Rysuje wszystkie obiekty uwzględniając wygenerowaną mapę głębokości cieni.
     * @param board Obiekt zawierający wszystkie elementy planszy.
     * @param ball Obiekt kulki.
     */
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

    /**
     * Rysuje model kulki w odpowiednim miejscu na ekranie, razem z jego cieniem.
     * @param ball Obiekt kulki.
     */
    private void drawBallWithShadow(Ball ball) {
        shadowingShaderProgram.useProgram();
        positionBallInScene(ball);
        shadowingShaderProgram.setUniforms(modelViewProjectionMatrix, modelsMatrix, normalsRotationMatrix, lightData, Ball.getTexture(), ball.BALL_OPACITY, lightsViewProjectionMatrix, renderTextureId[0]);
        ball.bindAttributes(shadowingShaderProgram, ShaderProgram.ShaderProgramType.withShadowing);
        ball.draw();
    }

    /**
     * Rysuje model diamentu w odpowiednim miejscu na ekranie, razem z jego cieniem.
     * @param diamond Obiekt diamentu.
     */
    private void drawDiamondWithShadow(Diamond diamond) {
        shadowingShaderProgram.useProgram();
        positionBonusInScene(diamond);
        shadowingShaderProgram.setUniforms(modelViewProjectionMatrix, modelsMatrix, normalsRotationMatrix, lightData, Diamond.getTexture(), diamond.DIAMOND_OPACITY, lightsViewProjectionMatrix, renderTextureId[0]);
        diamond.bindAttributes(shadowingShaderProgram, ShaderProgram.ShaderProgramType.withShadowing);
        diamond.draw();
    }

    /**
     * Rysuje model podłogi w odpowiednim miejscu na ekranie, razem z jego cieniem.
     * @param floor Obiekt podłogi.
     */
    private void drawFloorWithShadow(Floor floor) {
        shadowingShaderProgram.useProgram();
        positionObjectInScene(floor.getLocation());
        shadowingShaderProgram.setUniforms(modelViewProjectionMatrix, modelsMatrix, normalsRotationMatrix, lightData, floor.getTexture(), floor.FLOOR_OPACITY, lightsViewProjectionMatrix, renderTextureId[0]);
        floor.bindAttributes(shadowingShaderProgram, ShaderProgram.ShaderProgramType.withShadowing);
        floor.draw();
    }

    /**
     * Rysuje model ściany w odpowiednim miejscu na ekranie, razem z jego cieniem.
     * @param wall Obiekt podłogi.
     */
    private void drawWallWithShadow(Wall wall) {
        shadowingShaderProgram.useProgram();
        positionObjectInScene(wall.getLocation());
        shadowingShaderProgram.setUniforms(modelViewProjectionMatrix, modelsMatrix, normalsRotationMatrix, lightData, Wall.getTexture(), wall.WALL_OPACITY, lightsViewProjectionMatrix, renderTextureId[0]);
        wall.bindAttributes(shadowingShaderProgram, ShaderProgram.ShaderProgramType.withShadowing);
        wall.draw();
    }

    /**
     * Rysuje model belki w odpowiednim miejscu na ekranie, razem z jego cieniem.
     * @param beam Obiekt belki.
     */
    private void drawBeamWithShadow(Beam beam) {
        shadowingShaderProgram.useProgram();
        positionObjectInScene(beam.getLocation());
        shadowingShaderProgram.setUniforms(modelViewProjectionMatrix, modelsMatrix, normalsRotationMatrix, lightData, Beam.getTexture(), beam.BEAM_OPACITY, lightsViewProjectionMatrix, renderTextureId[0]);
        beam.bindAttributes(shadowingShaderProgram, ShaderProgram.ShaderProgramType.withShadowing);
        beam.draw();
    }

    /**
     * Rysuje model windy w odpowiednim miejscu na ekranie, razem z jego cieniem.
     * @param elevator Obiekt windy.
     */
    private void drawElevatorWithShadow(Elevator elevator) {
        shadowingShaderProgram.useProgram();
        positionObjectInScene(elevator.getLocation());
        shadowingShaderProgram.setUniforms(modelViewProjectionMatrix, modelsMatrix, normalsRotationMatrix, lightData, Elevator.getTexture(), elevator.ELEVATOR_OPACITY, lightsViewProjectionMatrix, renderTextureId[0]);
        elevator.bindAttributes(shadowingShaderProgram, ShaderProgram.ShaderProgramType.withShadowing);
        elevator.draw();
    }

    /**
     * Rysuje model mety w odpowiednim miejscu na ekranie, razem z jego cieniem.
     * @param finish Obiekt mety.
     */
    private void drawFinishWithShadow(Finish finish) {
        shadowingShaderProgram.useProgram();
        positionObjectInScene(finish.getLocation());
        shadowingShaderProgram.setUniforms(modelViewProjectionMatrix, modelsMatrix, normalsRotationMatrix, lightData, Finish.getTexture(), finish.FINISH_OPACITY, lightsViewProjectionMatrix, renderTextureId[0]);
        finish.bindAttributes(shadowingShaderProgram, ShaderProgram.ShaderProgramType.withShadowing);
        finish.draw();

        colorShaderProgram.useProgram();
        positionObjectInScene(finish.getGlow().getLocation());
        colorShaderProgram.setUniforms(modelViewProjectionMatrix, modelsMatrix, normalsRotationMatrix, lightData, finish.isActive() ? finish.getGlow().ACTIVE_FINISH_COLOR : finish.getGlow().INACTIVE_FINISH_COLOR);
        finish.getGlow().bindAttributes(colorShaderProgram, ShaderProgram.ShaderProgramType.color);
        finish.getGlow().draw();
    }

    /**
     * Rysuje model punktu kontrolnego w odpowiednim miejscu na ekranie, razem z jego cieniem.
     * @param checkPoint Obiekt punktu kontrolnego.
     */
    private void drawCheckPointWithShadow(CheckPoint checkPoint) {
        shadowingShaderProgram.useProgram();
        positionObjectInScene(checkPoint.getLocation());
        shadowingShaderProgram.setUniforms(modelViewProjectionMatrix, modelsMatrix, normalsRotationMatrix, lightData, CheckPoint.getTexture(), checkPoint.CHECKPOINT_OPACITY, lightsViewProjectionMatrix, renderTextureId[0]);
        checkPoint.bindAttributes(shadowingShaderProgram, ShaderProgram.ShaderProgramType.withShadowing);
        checkPoint.draw();

        if (!checkPoint.isVisited()) {
            colorShaderProgram.useProgram();
            positionObjectInScene(checkPoint.getGlow().getLocation());
            colorShaderProgram.setUniforms(modelViewProjectionMatrix, modelsMatrix, normalsRotationMatrix, lightData, checkPoint.getGlow().CHECKPOINT_GLOW_COLOR);
            checkPoint.getGlow().bindAttributes(colorShaderProgram, ShaderProgram.ShaderProgramType.color);
            checkPoint.getGlow().draw();
        }
    }

    /**
     * Rysuje model klepsydry w odpowiednim miejscu na ekranie, razem z jego cieniem.
     * @param hourGlass Obiekt klepsydry.
     */
    private void drawHourglassWithShadow(HourGlass hourGlass) {
        shadowingShaderProgram.useProgram();
        positionBonusInScene(hourGlass);
        shadowingShaderProgram.setUniforms(modelViewProjectionMatrix, modelsMatrix, normalsRotationMatrix, lightData, HourGlass.getTexture(), hourGlass.getWoodenParts().HOURGLASS_WOODEN_PART_OPACITY, lightsViewProjectionMatrix, renderTextureId[0]);
        hourGlass.getWoodenParts().bindAttributes(shadowingShaderProgram, ShaderProgram.ShaderProgramType.withShadowing);
        hourGlass.getWoodenParts().draw();

        colorShaderProgram.useProgram();
        colorShaderProgram.setUniforms(modelViewProjectionMatrix, modelsMatrix, normalsRotationMatrix, lightData, hourGlass.GLASS_COLOR);
        hourGlass.bindAttributes(colorShaderProgram, ShaderProgram.ShaderProgramType.color);
        hourGlass.draw();
    }


   ///////////////////////////

    /**
     * Rysuje model przestrzennego tła.
     */
    private void drawSkyBox() {
        glDepthFunc(GL_LEQUAL);
        skyBoxShaderProgram.useProgram();
        skyBoxShaderProgram.setUniforms(skyBoxViewProjectionMatrix, SkyBox.getTexture());
        skyBox.bindAttributes(skyBoxShaderProgram, ShaderProgram.ShaderProgramType.skyBox);
        skyBox.draw();
        glDepthFunc(GL_LESS);
    }

    ///////////////////////////

    /**
     * Obsługuje zdarzenie dotyku ekranu.
     * @param deltaX Wartość przesunięcia dotyku względem szerokości ekranu.
     * @param deltaY Wartość przesunięcia dotyku względem wysokości ekranu.
     */
    public void handleTouchDrag(float deltaX, float deltaY) {
        xRotation = (xRotation + deltaX / ROTATION_FACTOR) %360f;
        yRotation += deltaY / ROTATION_FACTOR;

        if (yRotation < -RIGHT_ANGLE) {
            yRotation = -RIGHT_ANGLE + RIGHT_ANGLE_BIAS;
        } else if (yRotation > RIGHT_ANGLE){
            yRotation = RIGHT_ANGLE - RIGHT_ANGLE_BIAS;
        }

        updateSkyBoxMVPMatrix();
    }

    /**
     * Odświeża macierz viewProjection dla obiektu przestrzennego tła.
     */
    private void updateSkyBoxMVPMatrix() {
        float[] skyBoxViewRotationMatrix = new float[16];
        setIdentityM(skyBoxViewRotationMatrix, 0);
        rotateM(skyBoxViewRotationMatrix, 0, -(yRotation + SKYBOX_ANGLE_SHIFT_X), 1f, 0f, 0f);
        rotateM(skyBoxViewRotationMatrix, 0, -(xRotation + SKYBOX_ANGLE_SHIFT_Y), 0f, 1f, 0f);
        multiplyMM(skyBoxViewProjectionMatrix, 0, projectionMatrix, 0, skyBoxViewRotationMatrix, 0);
    }

    /**
     * Wylicza macierz modelu dla aktualnej pozycji obiektu kulki, macierz modelViewProjection oraz macierz obrotu wekorów normalnych.
     * @param ball Obiekt kulki.
     */
    private void positionBallInScene(Ball ball) {
        float[] tmp1 = new float[16];
        float[] tmp2 = new float[16];

        setIdentityM(tmp1, 0);
        translateM(tmp1, 0, ball.getLocation().x, ball.getLocation().y, ball.getLocation().z);
        multiplyMM(modelsMatrix, 0, tmp1, 0, ball.getRotation(), 0);
        multiplyMM(modelViewProjectionMatrix, 0, viewProjectionMatrix, 0, modelsMatrix, 0);

        setIdentityM(tmp1, 0);
        multiplyMM(tmp2, 0, tmp1, 0, ball.getRotation(), 0);
        invertM(tmp1, 0, tmp2, 0);
        transposeM(normalsRotationMatrix, 0, tmp1, 0);
    }

    /**
     * Wylicza macierz modelu dla aktualnego położenia elementów bonusowych, macierz modelViewProjection oraz macierz obrotu wekorów normalnych.
     * @param bonus Obiekt bonusu.
     */
    private void positionBonusInScene(Bonus bonus) {
        setIdentityM(modelsMatrix, 0);
        translateM(modelsMatrix, 0, bonus.getLocation().x, bonus.getLocation().y, bonus.getLocation().z);
        rotateM(modelsMatrix, 0, bonus.rotate(), 0, 1, 0);
        multiplyMM(modelViewProjectionMatrix, 0, viewProjectionMatrix, 0, modelsMatrix, 0);

        float[] tmp1 = new float[16];
        float[] tmp2 = new float[16];
        setIdentityM(tmp1, 0);
        rotateM(tmp1, 0, bonus.rotate(), 0, 1, 0);
        invertM(tmp2, 0, tmp1, 0);
        transposeM(normalsRotationMatrix, 0, tmp2, 0);
    }

    /**
     * Wylicza macierz modelu oraz modelViewProjection dla nieobracających się elementów planszy.
     * @param location Aktualne położenie obiektu.
     */
    private void positionObjectInScene(Point location) {
        setIdentityM(modelsMatrix, 0);
        translateM(modelsMatrix, 0, location.x, location.y, location.z);
        multiplyMM(modelViewProjectionMatrix, 0, viewProjectionMatrix, 0, modelsMatrix, 0);
        setIdentityM(normalsRotationMatrix, 0); //bo gdy nie ma rotacji, to nie musimy nic robić z wektorami normalnymi
    }

    /**
     * Wylicza macierze widoku kamery oraz światła.
     * @param ballLocation
     */
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



    //STALE:

    //Dla orbit kamery i światła
    /**
     * Promień orbity, po której kamera jest obracana.
     */
    private final float CAMERA_ORBIT_RADIUS = 20f;
    /**
     * Promień orboty, po której żródło światła jest obracane.
     */
    private final float LIGHT_ORBIT_RADIUS = 30f;
    /**
     * Liczba stopni o jakie źródło światła jest przesunięte w stosunku do kamery w płaszczyźnie poziomej.
     */
    private final float LIGHT_SHIFT_DEGREE_X = 20f;
    /**
     * Liczba stopni o jakie źródło światła jest przesunięte w stosunku do kamery w płaszczyźnie pionowej.
     */
    private final float LIGHT_SHIFT_DEGREE_Y = -15f;
    /**
     * Przelicznik stosowany do zmiany kąta w stopniach na radiany.
     */
    private final float DEGREE_TO_RAD_CONVERSION = (float) Math.PI / 180;

    //Dla obrotów:
    /**
     * Stała określająca początkowy kąt obrótu kamery wokół osi Y.
     */
    private final float INITIAL_ROTATION_X = 180f;
    /**
     *  Stała określająca początkowy kąt obrótu kamery wokół osi X.
     */
    private final float INITIAL_ROTATION_Y = -45f;

    /**
     * Stała określająca wspołczynnik szybkości obrotu kamery przy dotyku ekranu.
     */
    private final float ROTATION_FACTOR = 16f;
    /**
     * Stała definiująca kąt prosty.
     */
    private final float RIGHT_ANGLE = 90f;
    /**
     * Stała określająca przesunięcie względem kąta prostego.
     */
    private final float RIGHT_ANGLE_BIAS = 0.01f;

    /**
     * Stała określająca przesunięcie kąta widoczności tła względem kąta obrotu kamery dla osi Y.
     */
    private final float SKYBOX_ANGLE_SHIFT_X = 15f;
    /**
     * Stała określająca przesunięcie kąta widoczności tła względem kąta obrotu kamery dla osi X.
     */
    private final float SKYBOX_ANGLE_SHIFT_Y = 180f;

    //Dla frustum widoku
    /**
     * Kąt zasięgu widoczności kamery.
     */
    private final float VIEW_FIELD_OF_VIEW_DEGREES = 45;
    /**
     * Odległość od bliższej rzutni kamery.
     */
    private final float VIEW_NEAR = 1f;
    /**
     * Odległość od dalszej rzutni kamery.
     */
    private final float VIEW_FAR = 100f;

    //Dla frustum światła
    /**
     * Kąt zasięgu padania promieni światła.
     */
    private final float LIGHT_FIELD_OF_VIEW_DEGREES = 45;
    /**
     * Odległość bliższej rzutni światła.
     */
    private final float LIGHT_NEAR = 2f;
    /**
     * Odległość dalszej rzutni światła.
     */
    private final float LIGHT_FAR = 100f;

    /**
     * Stosunek wymiarów mapy głębokości cieni i erkanu.
     */
    private final float DEPTH_MAP_PRECISION = 1.5f;
}
