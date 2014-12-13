package mini.paranormalgolf.Graphics;

import android.content.Context;

import mini.paranormalgolf.Graphics.ShaderPrograms.ColorShaderProgram;
import mini.paranormalgolf.Graphics.ShaderPrograms.LightColorShaderProgram;
import mini.paranormalgolf.Graphics.ShaderPrograms.TextureLightShaderProgram;
import mini.paranormalgolf.Graphics.ShaderPrograms.TextureShaderProgram;
import mini.paranormalgolf.Helpers.ResourceHelper;
import mini.paranormalgolf.Physics.Ball;
import mini.paranormalgolf.Physics.Floor;
import mini.paranormalgolf.Physics.FloorPart;
import mini.paranormalgolf.Primitives.Point;
import mini.paranormalgolf.R;

import static android.opengl.GLES20.GL_COLOR_BUFFER_BIT;
import static android.opengl.GLES20.GL_DEPTH_BUFFER_BIT;
import static android.opengl.GLES20.glClear;
import static android.opengl.GLES20.glViewport;
import static android.opengl.Matrix.multiplyMM;
import static android.opengl.Matrix.setIdentityM;
import static android.opengl.Matrix.setLookAtM;
import static android.opengl.Matrix.translateM;

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

    private Point lightPos =  new Point(0f, 3.0f, 1.5f);

    private final float fieldOfViewDegree = 45;
    private final float near = 1f;
    private final float far = 10f;

    private final Point cameraTranslation = new Point(0f, 1.5f, 1.5f);

    private ColorShaderProgram colorShaderProgram;
    private LightColorShaderProgram lightColorShaderProgram;
    private TextureShaderProgram textureShaderProgram;
    private TextureLightShaderProgram textureLightShaderProgram;

    int topFloorTexture;
    int sideFloorTexture;
    int bottomFloorTexture;

    public DrawManager(Context context){
        this.context = context;
        colorShaderProgram = new ColorShaderProgram(context);
        lightColorShaderProgram = new LightColorShaderProgram(context);
        textureShaderProgram = new TextureShaderProgram(context);
        textureLightShaderProgram = new TextureLightShaderProgram(context);

        topFloorTexture = ResourceHelper.loadTexture(context, R.drawable.top_floor_texture);
        sideFloorTexture = ResourceHelper.loadTexture(context, R.drawable.side_floor_texture);
        bottomFloorTexture = ResourceHelper.loadTexture(context, R.drawable.bottom_floor_texture);
    }

    public void surfaceChange(int width, int height){
        glViewport(0, 0, width, height);
        //ustawianie pozycji sceny
        MatrixHelper.perspectiveM(projectionMatrix, fieldOfViewDegree, (float) width / (float) height, near, far);

    }

    public void preDraw(Point ballLocation){
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
        setLookAtM(viewMatrix, 0, ballLocation.X + cameraTranslation.X, ballLocation.Y + cameraTranslation.Y, ballLocation.Z + cameraTranslation.Z, ballLocation.X, ballLocation.Y, ballLocation.Z, 0f, 1f, 0f);
        multiplyMM(viewProjectionMatrix, 0, projectionMatrix, 0, viewMatrix, 0);
    }


   public void drawBall(Ball ball){
        lightColorShaderProgram.useProgram();
        positionObjectInScene(ball.getLocation());
        lightColorShaderProgram.setUniforms(modelViewProjectionMatrix, modelViewMatrix, ball.rgba,lightPos);
        ball.bindData(lightColorShaderProgram);
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

    private void positionObjectInScene(Point location) {
        setIdentityM(modelMatrix, 0);
        translateM(modelMatrix, 0, location.X, location.Y, location.Z);
        multiplyMM(modelViewProjectionMatrix, 0, viewProjectionMatrix, 0, modelMatrix, 0);
        multiplyMM(modelViewMatrix, 0, viewMatrix, 0, modelMatrix, 0);
    }
}
