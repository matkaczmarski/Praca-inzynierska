package mini.paranormalgolf.Graphics.ModelBuilders;

import android.opengl.Matrix;
import android.util.FloatMath;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import mini.paranormalgolf.Graphics.GraphicsData;
import mini.paranormalgolf.Primitives.Point;
import mini.paranormalgolf.Primitives.Pyramid;
import mini.paranormalgolf.Primitives.Sphere;
import mini.paranormalgolf.Primitives.Rectangle;
import mini.paranormalgolf.Primitives.Vector;

import static android.opengl.GLES20.GL_TRIANGLES;
import static android.opengl.GLES20.GL_TRIANGLE_FAN;
import static android.opengl.GLES20.GL_TRIANGLE_STRIP;
import static android.opengl.GLES20.GL_UNSIGNED_BYTE;
import static android.opengl.GLES20.glDrawArrays;
import static android.opengl.GLES20.glDrawElements;

/**
 * Created by Mateusz on 2014-12-05.
 */
public class ObjectBuilder {

    private static final int FLOATS_PER_VERTEX_WITHOUT_TEXTURE = 6; //3 na pozycję oraz 3 na wektor normalny
    private static final int FLOATS_PER_VERTEX_WITH_TETURE = 8; //3 na pozycje, 3 na wektor normalny oraz 2 na pozycje tekstury

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
    private int offset;
    private  boolean isTextured;

    public ObjectBuilder(int sizeInVertices, boolean ifTextured) {
        vertexData = new float[sizeInVertices * (ifTextured ? FLOATS_PER_VERTEX_WITH_TETURE : FLOATS_PER_VERTEX_WITHOUT_TEXTURE)];
        drawCommands = new ArrayList<DrawCommand>();
        offset = 0;
        isTextured = ifTextured;
    }

    public void appendSphere(Sphere sphere, int numPoints){

        final int verticesCount = (numPoints + 1) * 2;

        for(int i=0; i<=numPoints; i++){

            final int startVertex = offset / ( isTextured ? FLOATS_PER_VERTEX_WITH_TETURE : FLOATS_PER_VERTEX_WITHOUT_TEXTURE);
            float textureY1 = (float)i/numPoints;
            float textureY2 = (float)(i+1)/numPoints;
            float iRadian = -1f * (float)Math.PI/2 + (textureY1  * (float)Math.PI);
            float iiRadian = -1f * (float)Math.PI/2 + (textureY2 * (float)Math.PI);

            for(int j=0; j<=numPoints; j++){

                float textureX = (float)j/numPoints;
                float jRadian = textureX * 2f * (float)Math.PI;

                vertexData[offset++] = sphere.center.X + sphere.radius * FloatMath.cos(iRadian) * FloatMath.cos(jRadian);
                vertexData[offset++] = sphere.center.Y + sphere.radius * FloatMath.cos(iRadian) * FloatMath.sin(jRadian);
                vertexData[offset++] = sphere.center.Z + sphere.radius * FloatMath.sin(iRadian);

                Vector vNormal = new Vector(vertexData[offset - 3] - sphere.center.X, vertexData[offset - 2] - sphere.center.Y, vertexData[offset - 1] - sphere.center.Z ).normalize();
                vertexData[offset++] = vNormal.X;
                vertexData[offset++] = vNormal.Y;
                vertexData[offset++] = vNormal.Z;

                vertexData[offset++] = textureX;
                vertexData[offset++] = textureY1;

                vertexData[offset++] = sphere.center.X + sphere.radius * FloatMath.cos(iiRadian) * FloatMath.cos(jRadian);
                vertexData[offset++] = sphere.center.Y + sphere.radius * FloatMath.cos(iiRadian) * FloatMath.sin(jRadian);
                vertexData[offset++] = sphere.center.Z + sphere.radius * FloatMath.sin(iiRadian);

                vNormal = new Vector(vertexData[offset - 3] - sphere.center.X, vertexData[offset - 2] - sphere.center.Y, vertexData[offset - 1] - sphere.center.Z ).normalize();
                vertexData[offset++] = vNormal.X;
                vertexData[offset++] = vNormal.Y;
                vertexData[offset++] = vNormal.Z;

                vertexData[offset++] = textureX;
                vertexData[offset++] = textureY2;

            }

            drawCommands.add(new DrawCommand() {
                @Override
                public void draw() {
                    glDrawArrays(GL_TRIANGLE_STRIP, startVertex, verticesCount);
                }
            });
        }
    }

    public void appendRectangle(Rectangle rectangle, Axis constantAxis, float normalVectorDirection, float textureUnit) {

        final int startVertex = offset / ( isTextured ? FLOATS_PER_VERTEX_WITH_TETURE : FLOATS_PER_VERTEX_WITHOUT_TEXTURE);
        float aTextureUnits = rectangle.a / textureUnit;
        float bTextureUnits = rectangle.b / textureUnit;

        switch (constantAxis){
            case xAxis:
                vertexData[offset++] = rectangle.center.X;
                vertexData[offset++] = rectangle.center.Y - rectangle.a / 2;
                vertexData[offset++] = rectangle.center.Z - rectangle.b / 2;

                vertexData[offset++] = normalVectorDirection;
                vertexData[offset++] = 0;
                vertexData[offset++] = 0;

                vertexData[offset++] = 0;
                vertexData[offset++] = 0;

                vertexData[offset++] = rectangle.center.X;
                vertexData[offset++] = rectangle.center.Y - rectangle.a / 2;
                vertexData[offset++] = rectangle.center.Z + rectangle.b / 2;

                vertexData[offset++] = normalVectorDirection;
                vertexData[offset++] = 0;
                vertexData[offset++] = 0;

                vertexData[offset++] = bTextureUnits;
                vertexData[offset++] = 0;

                vertexData[offset++] = rectangle.center.X;
                vertexData[offset++] = rectangle.center.Y + rectangle.a / 2;
                vertexData[offset++] = rectangle.center.Z - rectangle.b / 2;

                vertexData[offset++] = normalVectorDirection;
                vertexData[offset++] = 0;
                vertexData[offset++] = 0;

                vertexData[offset++] = 0;
                vertexData[offset++] = aTextureUnits;

                vertexData[offset++] = rectangle.center.X;
                vertexData[offset++] = rectangle.center.Y + rectangle.a / 2;
                vertexData[offset++] = rectangle.center.Z + rectangle.b / 2;

                vertexData[offset++] = normalVectorDirection;
                vertexData[offset++] = 0;
                vertexData[offset++] = 0;

                vertexData[offset++] = bTextureUnits;
                vertexData[offset++] = aTextureUnits;

                break;
            case yAxis:
                vertexData[offset++] = rectangle.center.X - rectangle.a / 2;
                vertexData[offset++] = rectangle.center.Y;
                vertexData[offset++] = rectangle.center.Z - rectangle.b / 2;

                vertexData[offset++] = 0;
                vertexData[offset++] = normalVectorDirection;
                vertexData[offset++] = 0;

                vertexData[offset++] = 0;
                vertexData[offset++] = 0;

                vertexData[offset++] = rectangle.center.X - rectangle.a / 2;
                vertexData[offset++] = rectangle.center.Y;
                vertexData[offset++] = rectangle.center.Z + rectangle.b / 2;

                vertexData[offset++] = 0;
                vertexData[offset++] = normalVectorDirection;
                vertexData[offset++] = 0;

                vertexData[offset++] = 0;
                vertexData[offset++] = bTextureUnits;

                vertexData[offset++] = rectangle.center.X + rectangle.a / 2;
                vertexData[offset++] = rectangle.center.Y;
                vertexData[offset++] = rectangle.center.Z - rectangle.b / 2;

                vertexData[offset++] = 0;
                vertexData[offset++] = normalVectorDirection;
                vertexData[offset++] = 0;

                vertexData[offset++] = aTextureUnits;
                vertexData[offset++] = 0;

                vertexData[offset++] = rectangle.center.X + rectangle.a / 2;
                vertexData[offset++] = rectangle.center.Y;
                vertexData[offset++] = rectangle.center.Z + rectangle.b / 2;

                vertexData[offset++] = 0;
                vertexData[offset++] = normalVectorDirection;
                vertexData[offset++] = 0;

                vertexData[offset++] = aTextureUnits;
                vertexData[offset++] = bTextureUnits;

                break;
            case zAxis:
                vertexData[offset++] = rectangle.center.X - rectangle.a / 2;
                vertexData[offset++] = rectangle.center.Y - rectangle.b / 2;
                vertexData[offset++] = rectangle.center.Z;

                vertexData[offset++] = 0;
                vertexData[offset++] = 0;
                vertexData[offset++] = normalVectorDirection;

                vertexData[offset++] = 0;
                vertexData[offset++] = 0;

                vertexData[offset++] = rectangle.center.X - rectangle.a / 2;
                vertexData[offset++] = rectangle.center.Y + rectangle.b / 2;
                vertexData[offset++] = rectangle.center.Z;

                vertexData[offset++] = 0;
                vertexData[offset++] = 0;
                vertexData[offset++] = normalVectorDirection;

                vertexData[offset++] = 0;
                vertexData[offset++] = bTextureUnits;

                vertexData[offset++] = rectangle.center.X + rectangle.a / 2;
                vertexData[offset++] = rectangle.center.Y - rectangle.b / 2;
                vertexData[offset++] = rectangle.center.Z;

                vertexData[offset++] = 0;
                vertexData[offset++] = 0;
                vertexData[offset++] = normalVectorDirection;

                vertexData[offset++] = aTextureUnits;
                vertexData[offset++] = 0;

                vertexData[offset++] = rectangle.center.X + rectangle.a / 2;
                vertexData[offset++] = rectangle.center.Y + rectangle.b / 2;
                vertexData[offset++] = rectangle.center.Z;

                vertexData[offset++] = 0;
                vertexData[offset++] = 0;
                vertexData[offset++] = normalVectorDirection;

                vertexData[offset++] = aTextureUnits;
                vertexData[offset++] = bTextureUnits;
                break;
        }

        drawCommands.add(new DrawCommand() {
            @Override
            public void draw() {
                glDrawArrays(GL_TRIANGLE_STRIP, startVertex, ObjectGenerator.VERTEX_PER_RECTANGLE);
            }
        });

    }

    public void appendPyramidWithoutBase(Point location, Pyramid pyramid, float direction) {

        final int indicesCount = pyramid.baseVerticesCount * 3;
        int vertexStar = offset / ( isTextured ? FLOATS_PER_VERTEX_WITH_TETURE : FLOATS_PER_VERTEX_WITHOUT_TEXTURE);

        for (int i = 0; i < pyramid.baseVerticesCount; i++) {
            float alpha = ((float) i / pyramid.baseVerticesCount) * 2f * (float) Math.PI;

            vertexData[offset++] = location.X + pyramid.radius * FloatMath.cos(alpha);
            vertexData[offset++] = location.Y;
            vertexData[offset++] = location.Z + pyramid.radius * FloatMath.sin(alpha);

            Vector vNormal = new Vector(vertexData[offset - 3] - location.X, vertexData[offset - 2] - location.Y, vertexData[offset - 1] - location.Z).normalize();
            vertexData[offset++] = vNormal.X;
            vertexData[offset++] = vNormal.Y;
            vertexData[offset++] = vNormal.Z;

            vertexData[offset++] = i % 2; // co drugi trójkąt ma teksturowanie od 0
            vertexData[offset++] = 1f;
        }

        vertexData[offset++] = location.X;
        vertexData[offset++] = location.Y + (direction * pyramid.height);
        vertexData[offset++] = location.Z;

        vertexData[offset++] = 0;
        vertexData[offset++] = direction;
        vertexData[offset++] = 0;

        vertexData[offset++] = 0.5f;
        vertexData[offset++] = 0f;

        byte[] indices = new byte[indicesCount];
        int indicesOffset = 0;
        for (int i = 0; i < pyramid.baseVerticesCount; i++) {
            indices[indicesOffset++] = (byte)(vertexStar + pyramid.baseVerticesCount);
            indices[indicesOffset++] = (byte)( vertexStar + i);
            indices[indicesOffset++] = (byte)(vertexStar + ((i+1) % pyramid.baseVerticesCount));
        }

        final ByteBuffer indexArray = ByteBuffer.allocateDirect(indicesCount).put(indices);
        indexArray.position(0);

        drawCommands.add(new DrawCommand() {
            @Override
            public void draw() {
                glDrawElements(GL_TRIANGLES, indicesCount, GL_UNSIGNED_BYTE, indexArray);
            }
        });

    }

    public void appendCylindersCurvedSurface( Point bottomCenter, float bottomRadius, Point topCenter, float topRadius, int numPoints, float textureUnit) {
        final int startVertex = offset / ( isTextured ? FLOATS_PER_VERTEX_WITH_TETURE : FLOATS_PER_VERTEX_WITHOUT_TEXTURE);
        final int numVertices = (numPoints + 1) * 2;

        float height = Math.abs(new Vector(bottomCenter.Substract(topCenter)).length());
        float basePerimeter = 2f * (float) Math.PI * ((bottomRadius + topRadius)/2); // uśredniłem promienie obu podstaw

        float aTextureUnits = basePerimeter / textureUnit;
        float bTextureUnits = height / textureUnit;

        for (int i = 0; i <= numPoints; i++) {
            float angleInRadians = ((float) i / numPoints) * ((float) Math.PI * 2f);

            vertexData[offset++] = topCenter.X + topRadius * FloatMath.cos(angleInRadians);
            vertexData[offset++] = topCenter.Y;
            vertexData[offset++] = topCenter.Z + topRadius * FloatMath.sin(angleInRadians);

            Vector vNormal = new Vector(vertexData[offset - 3] - topCenter.X, vertexData[offset - 2] - topCenter.Y, vertexData[offset - 1] - topCenter.Z).normalize();
            vertexData[offset++] = vNormal.X;
            vertexData[offset++] = vNormal.Y;
            vertexData[offset++] = vNormal.Z;

            if(isTextured){
                vertexData[offset++] = ((float)i / numPoints) * aTextureUnits;
                vertexData[offset++] = 0f;
            }


            vertexData[offset++] = bottomCenter.X + bottomRadius * FloatMath.cos(angleInRadians);
            vertexData[offset++] = bottomCenter.Y;
            vertexData[offset++] = bottomCenter.Z + bottomRadius * FloatMath.sin(angleInRadians);

            vNormal = new Vector(vertexData[offset - 3] - bottomCenter.X, vertexData[offset - 2] - bottomCenter.Y, vertexData[offset - 1] - bottomCenter.Z).normalize();
            vertexData[offset++] = vNormal.X;
            vertexData[offset++] = vNormal.Y;
            vertexData[offset++] = vNormal.Z;

            if(isTextured){
                vertexData[offset++] = ((float)i / numPoints) * aTextureUnits;
                vertexData[offset++] = bTextureUnits;
            }

        }

        drawCommands.add(new DrawCommand() {
            @Override
            public void draw() {
                glDrawArrays(GL_TRIANGLE_STRIP, startVertex, numVertices);
            }
        });
    }

    public void appendCircle(Point center, float radius, float direction, int numPoints) {
        final int startVertex = offset / ( isTextured ? FLOATS_PER_VERTEX_WITH_TETURE : FLOATS_PER_VERTEX_WITHOUT_TEXTURE);
        final int numVertices = 1 + (numPoints + 1);

        vertexData[offset++] = center.X;
        vertexData[offset++] = center.Y;
        vertexData[offset++] = center.Z;

        vertexData[offset++] = 0f;
        vertexData[offset++] = direction;
        vertexData[offset++] = 0f;

        vertexData[offset++] = 0.5f;
        vertexData[offset++] = 0.5f;

        for (int i = 0; i <= numPoints; i++) {
            float alpha = ((float) i / numPoints) * ((float) Math.PI * 2f);

            vertexData[offset++] = center.X + radius * FloatMath.cos(alpha);
            vertexData[offset++] = center.Y;
            vertexData[offset++] = center.Z + radius * FloatMath.sin(alpha);

            Vector vNormal = new Vector(vertexData[offset - 3] - center.X, vertexData[offset - 2] - center.Y, vertexData[offset - 1] - center.Z).normalize();
            vertexData[offset++] = vNormal.X;
            vertexData[offset++] = vNormal.Y;
            vertexData[offset++] = vNormal.Z;

            vertexData[offset++] = FloatMath.cos(alpha)/2 + 0.5f;
            vertexData[offset++] = FloatMath.sin(alpha)/2 + 0.5f;
        }

        drawCommands.add(new DrawCommand() {
            @Override
            public void draw() {
                glDrawArrays(GL_TRIANGLE_FAN, startVertex, numVertices);
            }
        });
    }

    public GraphicsData build(){
        return new GraphicsData(vertexData, drawCommands);
    }

}
