package mini.paranormalgolf.Graphics.ModelBuilders;

import android.util.FloatMath;

import java.util.ArrayList;
import java.util.List;

import mini.paranormalgolf.Graphics.GraphicsData;
import mini.paranormalgolf.Primitives.Point;
import mini.paranormalgolf.Primitives.Sphere;
import mini.paranormalgolf.Primitives.Rectangle;
import mini.paranormalgolf.Primitives.Vector;

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

    public ObjectBuilder(int sizeInVertices, boolean ifTextured) {
        vertexData = new float[sizeInVertices * (FLOATS_PER_VERTEX + (ifTextured ? 2 : 0))];
        drawCommands = new ArrayList<DrawCommand>();
    }

    //MOŻNA ZOPTYMALIZOWAć
//    public void appendSphere(Sphere sphere, int numPoints){
//
//        final int verticesCount = (numPoints + 1) * 2;
//
//        for(int i=0; i<=numPoints; i++){
//
//            final int startVertex = offset / FLOATS_PER_VERTEX;
//            float iRadian = -1f * (float)Math.PI/2 + (((float)i/numPoints)  * (float)Math.PI);
//            float iiRadian = -1f * (float)Math.PI/2 + (((float)(i+1)/numPoints)  * (float)Math.PI);
//
//            for(int j=0; j<=numPoints; j++){
//
//                float jRadian = ((float)j/numPoints) * 2f * (float)Math.PI;
//
//                //theta - i
//                // X = r * cos(theta) * cos(phi)
//                // Y = r * cos(theta) * sin(phi)
//                // Z = r * sin(theta)
//
//                vertexData[offset++] = sphere.center.X + sphere.radius * FloatMath.cos(iRadian) * FloatMath.cos(jRadian);
//                vertexData[offset++] = sphere.center.Y + sphere.radius * FloatMath.cos(iRadian) * FloatMath.sin(jRadian);
//                vertexData[offset++] = sphere.center.Z + sphere.radius * FloatMath.sin(iRadian);
//
//                vertexData[offset++] = sphere.center.X + sphere.radius * FloatMath.cos(iiRadian) * FloatMath.cos(jRadian);
//                vertexData[offset++] = sphere.center.Y + sphere.radius * FloatMath.cos(iiRadian) * FloatMath.sin(jRadian);
//                vertexData[offset++] = sphere.center.Z + sphere.radius * FloatMath.sin(iiRadian);
//
//            }
//
//            drawCommands.add(new DrawCommand() {
//                @Override
//                public void draw() {
//                    glDrawArrays(GL_TRIANGLE_STRIP, startVertex, verticesCount);
//                }
//            });
//        }
//    }

    public void appendSphere(Sphere sphere, int numPoints){

        final int verticesCount = (numPoints + 1) * 2;

        for(int i=0; i<=numPoints; i++){

            final int startVertex = offset / (2 * FLOATS_PER_VERTEX);
            float iRadian = -1f * (float)Math.PI/2 + (((float)i/numPoints)  * (float)Math.PI);
            float iiRadian = -1f * (float)Math.PI/2 + (((float)(i+1)/numPoints)  * (float)Math.PI);

            for(int j=0; j<=numPoints; j++){

                float jRadian = ((float)j/numPoints) * 2f * (float)Math.PI;

                // X = r * cos(theta) * cos(phi)
                // Y = r * cos(theta) * sin(phi)
                // Z = r * sin(theta)

                vertexData[offset++] = sphere.center.X + sphere.radius * FloatMath.cos(iRadian) * FloatMath.cos(jRadian);
                vertexData[offset++] = sphere.center.Y + sphere.radius * FloatMath.cos(iRadian) * FloatMath.sin(jRadian);
                vertexData[offset++] = sphere.center.Z + sphere.radius * FloatMath.sin(iRadian);

                Vector v1 = new Vector(vertexData[offset - 3] - sphere.center.X, vertexData[offset - 2] - sphere.center.Y, vertexData[offset - 1] - sphere.center.Z ).normalize();
                vertexData[offset++] = v1.X;
                vertexData[offset++] = v1.Y;
                vertexData[offset++] = v1.Z;

                vertexData[offset++] = sphere.center.X + sphere.radius * FloatMath.cos(iiRadian) * FloatMath.cos(jRadian);
                vertexData[offset++] = sphere.center.Y + sphere.radius * FloatMath.cos(iiRadian) * FloatMath.sin(jRadian);
                vertexData[offset++] = sphere.center.Z + sphere.radius * FloatMath.sin(iiRadian);

                v1 = new Vector(vertexData[offset - 3] - sphere.center.X, vertexData[offset - 2] - sphere.center.Y, vertexData[offset - 1] - sphere.center.Z ).normalize();
                vertexData[offset++] = v1.X;
                vertexData[offset++] = v1.Y;
                vertexData[offset++] = v1.Z;

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
    public void appendRectangle(Rectangle rectangle, Axis constantAxis, Point cuboidCenter){

        //final int startVertex = offset / (2 * FLOATS_PER_VERTEX);
        final int startVertex = offset /  (FLOATS_PER_VERTEX + 2);

        float ratio = rectangle.a > rectangle.b ? rectangle.a/rectangle.b : rectangle.b / rectangle.a;


        vertexData[offset++] = rectangle.center.X - rectangle.a /2;
        vertexData[offset++] = rectangle.center.Y;//- rectangle.b/2;
        vertexData[offset++] = rectangle.center.Z - rectangle.b /2;

                vertexData[offset++] = 0;
                vertexData[offset++] = 0;

        vertexData[offset++] = rectangle.center.X - rectangle.a /2;
        vertexData[offset++] = rectangle.center.Y;//+ rectangle.b/2;
        vertexData[offset++] = rectangle.center.Z + rectangle.b /2;

                vertexData[offset++] = 0;
                vertexData[offset++] = rectangle.a > rectangle.b ? 1 : ratio;

        vertexData[offset++] = rectangle.center.X + rectangle.a /2;
        vertexData[offset++] = rectangle.center.Y;// + rectangle.b/2;
        vertexData[offset++] = rectangle.center.Z - rectangle.b /2;

                vertexData[offset++] = rectangle.a > rectangle.b ? ratio : 1;
                vertexData[offset++] = 0;

        vertexData[offset++] = rectangle.center.X + rectangle.a /2;
        vertexData[offset++] = rectangle.center.Y;//- rectangle.b/2;
        vertexData[offset++] = rectangle.center.Z + rectangle.b /2;

                vertexData[offset++] = rectangle.a > rectangle.b ? ratio : 1;
                vertexData[offset++] = rectangle.a > rectangle.b ? 1 : ratio;


        /////////////////////////////////////////////////////////////////////


        /*
        vertexData[offset++] = rectangle.center.X;
        vertexData[offset++] = rectangle.center.Y;
        vertexData[offset++] = rectangle.center.Z;

//        Vector v1 = new Vector(vertexData[offset - 3] - cuboidCenter.X, vertexData[offset - 2] - cuboidCenter.Y, vertexData[offset - 1] - cuboidCenter.Z ).normalize();
//        vertexData[offset++] = v1.X;
//        vertexData[offset++] = v1.Y;
//        vertexData[offset++] = v1.Z;

        switch (constantAxis)
        {
            case xAxis:
                vertexData[offset++] = rectangle.center.X;
                vertexData[offset++] = rectangle.center.Y - rectangle.a /2;
                vertexData[offset++] = rectangle.center.Z - rectangle.b /2;

//                v1 = new Vector(vertexData[offset - 3] - cuboidCenter.X, vertexData[offset - 2] - cuboidCenter.Y, vertexData[offset - 1] - cuboidCenter.Z ).normalize();
//                vertexData[offset++] = v1.X;
//                vertexData[offset++] = v1.Y;
//                vertexData[offset++] = v1.Z;

                vertexData[offset++] = rectangle.center.X;
                vertexData[offset++] = rectangle.center.Y - rectangle.a /2;
                vertexData[offset++] = rectangle.center.Z + rectangle.b /2;

//                v1 = new Vector(vertexData[offset - 3] - cuboidCenter.X, vertexData[offset - 2] - cuboidCenter.Y, vertexData[offset - 1] - cuboidCenter.Z ).normalize();
//                vertexData[offset++] = v1.X;
//                vertexData[offset++] = v1.Y;
//                vertexData[offset++] = v1.Z;

                vertexData[offset++] = rectangle.center.X;
                vertexData[offset++] = rectangle.center.Y + rectangle.a /2;
                vertexData[offset++] = rectangle.center.Z + rectangle.b /2;

//                v1 = new Vector(vertexData[offset - 3] - cuboidCenter.X, vertexData[offset - 2] - cuboidCenter.Y, vertexData[offset - 1] - cuboidCenter.Z ).normalize();
//                vertexData[offset++] = v1.X;
//                vertexData[offset++] = v1.Y;
//                vertexData[offset++] = v1.Z;

                vertexData[offset++] = rectangle.center.X;
                vertexData[offset++] = rectangle.center.Y + rectangle.a /2;
                vertexData[offset++] = rectangle.center.Z - rectangle.b /2;

//                v1 = new Vector(vertexData[offset - 3] - cuboidCenter.X, vertexData[offset - 2] - cuboidCenter.Y, vertexData[offset - 1] - cuboidCenter.Z ).normalize();
//                vertexData[offset++] = v1.X;
//                vertexData[offset++] = v1.Y;
//                vertexData[offset++] = v1.Z;

                vertexData[offset++] = rectangle.center.X;
                vertexData[offset++] = rectangle.center.Y - rectangle.a /2;
                vertexData[offset++] = rectangle.center.Z - rectangle.b /2;

//                v1 = new Vector(vertexData[offset - 3] - cuboidCenter.X, vertexData[offset - 2] - cuboidCenter.Y, vertexData[offset - 1] - cuboidCenter.Z ).normalize();
//                vertexData[offset++] = v1.X;
//                vertexData[offset++] = v1.Y;
//                vertexData[offset++] = v1.Z;

                break;
            case yAxis:
                vertexData[offset++] = rectangle.center.X + rectangle.a /2;
                vertexData[offset++] = rectangle.center.Y;//- rectangle.b/2;
                vertexData[offset++] = rectangle.center.Z + rectangle.b /2;

//                v1 = new Vector(vertexData[offset - 3] - cuboidCenter.X, vertexData[offset - 2] - cuboidCenter.Y, vertexData[offset - 1] - cuboidCenter.Z ).normalize();
//                vertexData[offset++] = v1.X;
//                vertexData[offset++] = v1.Y;
//                vertexData[offset++] = v1.Z;

                vertexData[offset++] = rectangle.center.X - rectangle.a /2;
                vertexData[offset++] = rectangle.center.Y;//+ rectangle.b/2;
                vertexData[offset++] = rectangle.center.Z + rectangle.b /2;

//                v1 = new Vector(vertexData[offset - 3] - cuboidCenter.X, vertexData[offset - 2] - cuboidCenter.Y, vertexData[offset - 1] - cuboidCenter.Z ).normalize();
//                vertexData[offset++] = v1.X;
//                vertexData[offset++] = v1.Y;
//                vertexData[offset++] = v1.Z;

                vertexData[offset++] = rectangle.center.X - rectangle.a /2;
                vertexData[offset++] = rectangle.center.Y;// + rectangle.b/2;
                vertexData[offset++] = rectangle.center.Z - rectangle.b /2;

//                v1 = new Vector(vertexData[offset - 3] - cuboidCenter.X, vertexData[offset - 2] - cuboidCenter.Y, vertexData[offset - 1] - cuboidCenter.Z ).normalize();
//                vertexData[offset++] = v1.X;
//                vertexData[offset++] = v1.Y;
//                vertexData[offset++] = v1.Z;

                vertexData[offset++] = rectangle.center.X + rectangle.a /2;
                vertexData[offset++] = rectangle.center.Y;//- rectangle.b/2;
                vertexData[offset++] = rectangle.center.Z - rectangle.b /2;

//                v1 = new Vector(vertexData[offset - 3] - cuboidCenter.X, vertexData[offset - 2] - cuboidCenter.Y, vertexData[offset - 1] - cuboidCenter.Z ).normalize();
//                vertexData[offset++] = v1.X;
//                vertexData[offset++] = v1.Y;
//                vertexData[offset++] = v1.Z;

                vertexData[offset++] = rectangle.center.X + rectangle.a /2;
                vertexData[offset++] = rectangle.center.Y;//- rectangle.b/2;
                vertexData[offset++] = rectangle.center.Z + rectangle.b /2;

//                v1 = new Vector(vertexData[offset - 3] - cuboidCenter.X, vertexData[offset - 2] - cuboidCenter.Y, vertexData[offset - 1] - cuboidCenter.Z ).normalize();
//                vertexData[offset++] = v1.X;
//                vertexData[offset++] = v1.Y;
//                vertexData[offset++] = v1.Z;
                break;
            case zAxis:
                vertexData[offset++] = rectangle.center.X - rectangle.a /2;
                vertexData[offset++] = rectangle.center.Y - rectangle.b /2;
                vertexData[offset++] = rectangle.center.Z;

//                v1 = new Vector(vertexData[offset - 3] - cuboidCenter.X, vertexData[offset - 2] - cuboidCenter.Y, vertexData[offset - 1] - cuboidCenter.Z ).normalize();
//                vertexData[offset++] = v1.X;
//                vertexData[offset++] = v1.Y;
//                vertexData[offset++] = v1.Z;

                vertexData[offset++] = rectangle.center.X - rectangle.a /2;
                vertexData[offset++] = rectangle.center.Y + rectangle.b /2;
                vertexData[offset++] = rectangle.center.Z;

//                v1 = new Vector(vertexData[offset - 3] - cuboidCenter.X, vertexData[offset - 2] - cuboidCenter.Y, vertexData[offset - 1] - cuboidCenter.Z ).normalize();
//                vertexData[offset++] = v1.X;
//                vertexData[offset++] = v1.Y;
//                vertexData[offset++] = v1.Z;

                vertexData[offset++] = rectangle.center.X + rectangle.a /2;
                vertexData[offset++] = rectangle.center.Y + rectangle.b /2;
                vertexData[offset++] = rectangle.center.Z;

//                v1 = new Vector(vertexData[offset - 3] - cuboidCenter.X, vertexData[offset - 2] - cuboidCenter.Y, vertexData[offset - 1] - cuboidCenter.Z ).normalize();
//                vertexData[offset++] = v1.X;
//                vertexData[offset++] = v1.Y;
//                vertexData[offset++] = v1.Z;

                vertexData[offset++] = rectangle.center.X + rectangle.a /2;
                vertexData[offset++] = rectangle.center.Y - rectangle.b /2;
                vertexData[offset++] = rectangle.center.Z;

//                v1 = new Vector(vertexData[offset - 3] - cuboidCenter.X, vertexData[offset - 2] - cuboidCenter.Y, vertexData[offset - 1] - cuboidCenter.Z ).normalize();
//                vertexData[offset++] = v1.X;
//                vertexData[offset++] = v1.Y;
//                vertexData[offset++] = v1.Z;

                vertexData[offset++] = rectangle.center.X - rectangle.a /2;
                vertexData[offset++] = rectangle.center.Y - rectangle.b /2;
                vertexData[offset++] = rectangle.center.Z;

//                v1 = new Vector(vertexData[offset - 3] - cuboidCenter.X, vertexData[offset - 2] - cuboidCenter.Y, vertexData[offset - 1] - cuboidCenter.Z ).normalize();
//                vertexData[offset++] = v1.X;
//                vertexData[offset++] = v1.Y;
//                vertexData[offset++] = v1.Z;
                break;
        }
        */


        drawCommands.add(new DrawCommand() {
            @Override
            public void draw() {
                glDrawArrays(GL_TRIANGLE_STRIP, startVertex, 4);
            }
        });

    }


    public GraphicsData build(){
        return new GraphicsData(vertexData, drawCommands);
    }

}
