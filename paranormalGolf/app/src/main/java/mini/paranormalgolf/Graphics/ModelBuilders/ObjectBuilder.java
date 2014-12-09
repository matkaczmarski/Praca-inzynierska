package mini.paranormalgolf.Graphics.ModelBuilders;

import android.util.FloatMath;

import java.util.ArrayList;
import java.util.List;

import mini.paranormalgolf.Graphics.GraphicsData;
import mini.paranormalgolf.Primitives.Sphere;
import mini.paranormalgolf.Primitives.Rectangle;

import static android.opengl.GLES20.GL_TRIANGLE_STRIP;
import static android.opengl.GLES20.glDrawArrays;

/**
 * Created by Mateusz on 2014-12-05.
 */
public class ObjectBuilder {

    private static final int FLOATS_PER_VERTEX = 3;

    public static interface DrawCommand {
        void draw();
    }

    public enum Axis{
        xAxis,
        yAxis,
        zAxis
    }

    private final float[] vertexData;
    private final List<DrawCommand> drawCommands;
    private int offset = 0;

    public ObjectBuilder(int sizeInVertices) {
        vertexData = new float[sizeInVertices * FLOATS_PER_VERTEX];
        drawCommands = new ArrayList<DrawCommand>();
    }

    //MOŻNA ZOPTYMALIZOWAć
    public void appendSphere(Sphere sphere, int numPoints){

        final int verticesCount = (numPoints + 1) * 2;

        for(int i=0; i<=numPoints; i++){

            final int startVertex = offset / FLOATS_PER_VERTEX;
            float iRadian = -1f * (float)Math.PI/2 + (((float)i/numPoints)  * (float)Math.PI);
            float iiRadian = -1f * (float)Math.PI/2 + (((float)(i+1)/numPoints)  * (float)Math.PI);

            for(int j=0; j<=numPoints; j++){

                float jRadian = ((float)j/numPoints) * 2f * (float)Math.PI;

                //theta - i
                // X = r * cos(theta) * cos(phi)
                // Y = r * cos(theta) * sin(phi)
                // Z = r * sin(theta)

                vertexData[offset++] = sphere.center.X + sphere.radius * FloatMath.cos(iRadian) * FloatMath.cos(jRadian);
                vertexData[offset++] = sphere.center.Y + sphere.radius * FloatMath.cos(iRadian) * FloatMath.sin(jRadian);
                vertexData[offset++] = sphere.center.Z + sphere.radius * FloatMath.sin(iRadian);

                vertexData[offset++] = sphere.center.X + sphere.radius * FloatMath.cos(iiRadian) * FloatMath.cos(jRadian);
                vertexData[offset++] = sphere.center.Y + sphere.radius * FloatMath.cos(iiRadian) * FloatMath.sin(jRadian);
                vertexData[offset++] = sphere.center.Z + sphere.radius * FloatMath.sin(iiRadian);

            }

            drawCommands.add(new DrawCommand() {
                @Override
                public void draw() {
                    glDrawArrays(GL_TRIANGLE_STRIP, startVertex, verticesCount);
                }
            });
        }
    }

    //Axis constantsAxis - oś, dla której prostokąt leży "płasko"
    public void appendRectangle(Rectangle rectangle, Axis constantAxis){

        final int startVertex = offset / FLOATS_PER_VERTEX;

        vertexData[offset++] = rectangle.center.X;
        vertexData[offset++] = rectangle.center.Y;
        vertexData[offset++] = rectangle.center.Z;

        switch (constantAxis)
        {
            case xAxis:
                vertexData[offset++] = rectangle.center.X;
                vertexData[offset++] = rectangle.center.Y - rectangle.a /2;
                vertexData[offset++] = rectangle.center.Z - rectangle.b /2;

                vertexData[offset++] = rectangle.center.X;
                vertexData[offset++] = rectangle.center.Y - rectangle.a /2;
                vertexData[offset++] = rectangle.center.Z + rectangle.b /2;

                vertexData[offset++] = rectangle.center.X;
                vertexData[offset++] = rectangle.center.Y + rectangle.a /2;
                vertexData[offset++] = rectangle.center.Z + rectangle.b /2;

                vertexData[offset++] = rectangle.center.X;
                vertexData[offset++] = rectangle.center.Y + rectangle.a /2;
                vertexData[offset++] = rectangle.center.Z - rectangle.b /2;

                vertexData[offset++] = rectangle.center.X;
                vertexData[offset++] = rectangle.center.Y - rectangle.a /2;
                vertexData[offset++] = rectangle.center.Z - rectangle.b /2;
                break;
            case yAxis:
                vertexData[offset++] = rectangle.center.X - rectangle.a /2;
                vertexData[offset++] = rectangle.center.Y;//- rectangle.b/2;
                vertexData[offset++] = rectangle.center.Z - rectangle.b /2;

                vertexData[offset++] = rectangle.center.X - rectangle.a /2;
                vertexData[offset++] = rectangle.center.Y;//+ rectangle.b/2;
                vertexData[offset++] = rectangle.center.Z + rectangle.b /2;

                vertexData[offset++] = rectangle.center.X + rectangle.a /2;
                vertexData[offset++] = rectangle.center.Y;// + rectangle.b/2;
                vertexData[offset++] = rectangle.center.Z + rectangle.b /2;

                vertexData[offset++] = rectangle.center.X + rectangle.a /2;
                vertexData[offset++] = rectangle.center.Y;//- rectangle.b/2;
                vertexData[offset++] = rectangle.center.Z - rectangle.b /2;

                vertexData[offset++] = rectangle.center.X - rectangle.a /2;
                vertexData[offset++] = rectangle.center.Y;//- rectangle.b/2;
                vertexData[offset++] = rectangle.center.Z - rectangle.b /2;
                break;
            case zAxis:
                vertexData[offset++] = rectangle.center.X - rectangle.a /2;
                vertexData[offset++] = rectangle.center.Y - rectangle.b /2;
                vertexData[offset++] = rectangle.center.Z;

                vertexData[offset++] = rectangle.center.X - rectangle.a /2;
                vertexData[offset++] = rectangle.center.Y + rectangle.b /2;
                vertexData[offset++] = rectangle.center.Z;

                vertexData[offset++] = rectangle.center.X + rectangle.a /2;
                vertexData[offset++] = rectangle.center.Y + rectangle.b /2;
                vertexData[offset++] = rectangle.center.Z;

                vertexData[offset++] = rectangle.center.X + rectangle.a /2;
                vertexData[offset++] = rectangle.center.Y - rectangle.b /2;
                vertexData[offset++] = rectangle.center.Z;

                vertexData[offset++] = rectangle.center.X - rectangle.a /2;
                vertexData[offset++] = rectangle.center.Y - rectangle.b /2;
                vertexData[offset++] = rectangle.center.Z;
                break;
        }


        drawCommands.add(new DrawCommand() {
            @Override
            public void draw() {
                glDrawArrays(GL_TRIANGLE_STRIP, startVertex, 6);
            }
        });

    }


    public GraphicsData build(){
        return new GraphicsData(vertexData, drawCommands);
    }

}
