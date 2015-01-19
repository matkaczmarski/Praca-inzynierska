package mini.paranormalgolf.Graphics.ModelBuilders;

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
 * Generowanie podstawowych modeli obiektów. Zawiera metody rozszerzające dany model o podstawowe kształty.
 */
public class ObjectBuilder {

    /**
     * Typ wyliczeniowy określający sposób rysowania poszczególnych modeli.
     */
    public enum DrawType{
        texturing,
        coloring,
        skyBox
    }

    /**
     * Stała określająca ilość liczb zmiennoprzecinkowych typu <em>float</em> potrzebnych do zdefiniowania atrybutów położenia (3 floats) oraz wektorów normlnych (3 floats) dla danego wierzchołka.
     */
    private static final int FLOATS_PER_VERTEX_WITHOUT_TEXTURE = 6;
    /**
     * Stała określająca ilość liczb zmiennoprzecinkowych typu <em>float</em> potrzebnych do zdefiniowania atrybutów położenia (3 floats), wektorów normlnych (3 floats) oraz współrzędnych tekstury (2 floats) dla danego wierzchołka.
     */
    private static final int FLOATS_PER_VERTEX_WITH_TEXTURE = 8;
    /**
     * Stała opisująca ilość liczb zmniennoprzecinkowych typu <em>float</em> potrzebnych do zdefiniowania obiektu tła przestrzennego.
     */
    private static final int FLOATS_PER_CUBE_IN_SKYBOX = 24;
    /**
     * Stała opisująca ilość indeksów potrzebnych do zdefiniowania obiektu tła przestrzennego.
     */
    private static final int INDICES_COUNT_IN_SKYBOX = 36;

    /**
     * Określa regułę rysowania części lub całości modelu.
     */
    public static interface DrawCommand {
        void draw();
    }

    /**
     * Typ wyliczeniowy określający osie globalnego układu współrzędnych.
     */
    public enum Axis{
        xAxis,
        yAxis,
        zAxis
    }

    /**
     * Tablica atrybutów wszyskich wierzchołków siatki trójkątów.
     */
    private final float[] vertexData;
    /**
     * Lista reguł rysowania siatki trójkątów.
     */
    private final List<DrawCommand> drawCommands;
    /**
     * Określa aktualny index dla tablicy <em><b>vertexData</b></em>.
     */
    private int offset;
    /**
     * Informuje, czy model będzie miał nakładaną teksturę.
     */
    private  boolean isTextured;

    /**
     * Na podstawie liczby wierzchołków i sposobu rysowania, tworzy tablicę <em><b>vertexData</b></em> o odpowiednim rozmiarze służącą do przechowywania atrybutów dla wszystkich wierzchołków modelu.
     * @param sizeInVertices Liczba wierzchołków w końcowym modelu.
     * @param drawType Sposób rysowania modelu.
     */
    public ObjectBuilder(int sizeInVertices, DrawType drawType) {
        vertexData = new float[getArraySize(sizeInVertices, drawType)];
        drawCommands = new ArrayList<DrawCommand>();
        offset = 0;
    }

    /**
     * Wyznacza rozmiar tablicy <em><b>vertexData</b></em>.
     * @param vertices Liczba wierzchołków w modelu.
     * @param drawType Sposób rysowania modelu.
     * @return Rozmiar tablicy.
     */
    private int getArraySize(int vertices, DrawType drawType){
        switch (drawType){
            case texturing:
                isTextured = true;
                return vertices * FLOATS_PER_VERTEX_WITH_TEXTURE;
            case coloring:
                isTextured = false;
                return vertices * FLOATS_PER_VERTEX_WITHOUT_TEXTURE;
            case skyBox:
                isTextured = false;
                return FLOATS_PER_CUBE_IN_SKYBOX;
        }
        return 0;
    }

    /**
     * Dodaje do tablicy <em><b>vertexData</b></em> atrybuty wierzchołków przestrzennego tła a do listy <em><b>drawCommands</b></em> odpowiednie reguły rysowania.
     */
    public void appendSkyBox(){
        vertexData[offset++] = -1; vertexData[offset++] = 1; vertexData[offset++] = 1;
        vertexData[offset++] = 1; vertexData[offset++] = 1; vertexData[offset++] = 1;
        vertexData[offset++] = -1; vertexData[offset++] = -1; vertexData[offset++] = 1;
        vertexData[offset++] = 1; vertexData[offset++] = -1; vertexData[offset++] = 1;
        vertexData[offset++] = -1; vertexData[offset++] = 1; vertexData[offset++] = -1;
        vertexData[offset++] = 1; vertexData[offset++] = 1; vertexData[offset++] = -1;
        vertexData[offset++] = -1; vertexData[offset++] = -1; vertexData[offset++] = -1;
        vertexData[offset++] = 1; vertexData[offset++] = -1; vertexData[offset++] = -1;

        final ByteBuffer indexArray =  ByteBuffer.allocateDirect(INDICES_COUNT_IN_SKYBOX).put(new byte[]{
                        // Front
                        1, 3, 0, 0, 3, 2,
                        // Back
                        4, 6, 5, 5, 6, 7,
                        // Left
                        0, 2, 4, 4, 2, 6,
                        // Right
                        5, 7, 1, 1, 7, 3,
                        // Top
                        5, 1, 4, 4, 1, 0,
                        // Bottom
                        6, 2, 7, 7, 2, 3
                });
        indexArray.position(0);

        drawCommands.add(new ObjectBuilder.DrawCommand() {
            @Override
            public void draw() {
                glDrawElements(GL_TRIANGLES, INDICES_COUNT_IN_SKYBOX, GL_UNSIGNED_BYTE, indexArray);
            }
        });
    }

    /**
     * Dodaje do tablicy <em><b>vertexData</b></em> atrybuty wierzchołków obiektu kulki a do listy <em><b>drawCommands</b></em> odpowiednie reguły rysowania.
     * @param sphere Wymiary kuli.
     * @param numPoints Rozdzielczość siatki trójkątów.
     */
    public void appendSphere(Sphere sphere, int numPoints){

        final int verticesCount = (numPoints + 1) * 2;

        for(int i=0; i<numPoints; i++){

            final int startVertex = offset / ( isTextured ? FLOATS_PER_VERTEX_WITH_TEXTURE : FLOATS_PER_VERTEX_WITHOUT_TEXTURE);
            float textureY1 = (float)i/numPoints;
            float textureY2 = (float)(i+1)/numPoints;
            float iRadian = -1f * (float)Math.PI/2 + (textureY1  * (float)Math.PI);
            float iiRadian = -1f * (float)Math.PI/2 + (textureY2 * (float)Math.PI);

            for(int j=numPoints; j>=0; j--){

                float textureX = (float) j/numPoints;
                float jRadian = textureX * 2f * (float)Math.PI;

                vertexData[offset++] = sphere.center.x + sphere.radius * FloatMath.cos(iRadian) * FloatMath.cos(jRadian);
                vertexData[offset++] = sphere.center.y + sphere.radius * FloatMath.cos(iRadian) * FloatMath.sin(jRadian);
                vertexData[offset++] = sphere.center.z + sphere.radius * FloatMath.sin(iRadian);

                Vector vNormal = new Vector(vertexData[offset - 3] - sphere.center.x, vertexData[offset - 2] - sphere.center.y, vertexData[offset - 1] - sphere.center.z).normalize();
                vertexData[offset++] = vNormal.x;
                vertexData[offset++] = vNormal.y;
                vertexData[offset++] = vNormal.z;

                if(isTextured) {
                    vertexData[offset++] = textureX;
                    vertexData[offset++] = textureY1;
                }

                vertexData[offset++] = sphere.center.x + sphere.radius * FloatMath.cos(iiRadian) * FloatMath.cos(jRadian);
                vertexData[offset++] = sphere.center.y + sphere.radius * FloatMath.cos(iiRadian) * FloatMath.sin(jRadian);
                vertexData[offset++] = sphere.center.z + sphere.radius * FloatMath.sin(iiRadian);

                vNormal = new Vector(vertexData[offset - 3] - sphere.center.x, vertexData[offset - 2] - sphere.center.y, vertexData[offset - 1] - sphere.center.z).normalize();
                vertexData[offset++] = vNormal.x;
                vertexData[offset++] = vNormal.y;
                vertexData[offset++] = vNormal.z;

                if(isTextured) {
                    vertexData[offset++] = textureX;
                    vertexData[offset++] = textureY2;
                }

            }

            drawCommands.add(new DrawCommand() {
                @Override
                public void draw() {
                    glDrawArrays(GL_TRIANGLE_STRIP, startVertex, verticesCount);
                }
            });
        }
    }

    /**
     * Dodaje do tablicy <em><b>vertexData</b></em> atrybuty wierzchołków prostokąta a do listy <em><b>drawCommands</b></em> odpowiednie reguły rysowania.
     * @param rectangle Wymiary prostokąta.
     * @param constantAxis Oś względem której prostokąt jest prostopadły.
     * @param normalVectorDirection Określa kierunek wektora normalnego.
     * @param textureUnit Wielkość kwadratowego kafelka tekstury.
     */
    public void appendRectangle(Rectangle rectangle, Axis constantAxis, float normalVectorDirection, float textureUnit) {

        final int startVertex = offset / ( isTextured ? FLOATS_PER_VERTEX_WITH_TEXTURE : FLOATS_PER_VERTEX_WITHOUT_TEXTURE);
        float aTextureUnits = rectangle.a / textureUnit;
        float bTextureUnits = rectangle.b / textureUnit;

        switch (constantAxis){
            case xAxis:

                if(normalVectorDirection > 0) {
                    vertexData[offset++] = rectangle.center.x;
                    vertexData[offset++] = rectangle.center.y + rectangle.a / 2;
                    vertexData[offset++] = rectangle.center.z + rectangle.b / 2;

                    vertexData[offset++] = normalVectorDirection;
                    vertexData[offset++] = 0;
                    vertexData[offset++] = 0;

                    if(isTextured) {
                        vertexData[offset++] = 0;
                        vertexData[offset++] = 0;
                    }

                    vertexData[offset++] = rectangle.center.x;
                    vertexData[offset++] = rectangle.center.y - rectangle.a / 2;
                    vertexData[offset++] = rectangle.center.z + rectangle.b / 2;

                    vertexData[offset++] = normalVectorDirection;
                    vertexData[offset++] = 0;
                    vertexData[offset++] = 0;

                    if(isTextured) {
                        vertexData[offset++] = 0;
                        vertexData[offset++] = aTextureUnits;
                    }

                    vertexData[offset++] = rectangle.center.x;
                    vertexData[offset++] = rectangle.center.y + rectangle.a / 2;
                    vertexData[offset++] = rectangle.center.z - rectangle.b / 2;

                    vertexData[offset++] = normalVectorDirection;
                    vertexData[offset++] = 0;
                    vertexData[offset++] = 0;

                    if(isTextured) {
                        vertexData[offset++] = bTextureUnits;
                        vertexData[offset++] = 0;
                    }

                    vertexData[offset++] = rectangle.center.x;
                    vertexData[offset++] = rectangle.center.y - rectangle.a / 2;
                    vertexData[offset++] = rectangle.center.z - rectangle.b / 2;

                    vertexData[offset++] = normalVectorDirection;
                    vertexData[offset++] = 0;
                    vertexData[offset++] = 0;

                    if(isTextured) {
                        vertexData[offset++] = bTextureUnits;
                        vertexData[offset++] = aTextureUnits;
                    }
                }
                else{
                    vertexData[offset++] = rectangle.center.x;
                    vertexData[offset++] = rectangle.center.y + rectangle.a / 2;
                    vertexData[offset++] = rectangle.center.z + rectangle.b / 2;

                    vertexData[offset++] = normalVectorDirection;
                    vertexData[offset++] = 0;
                    vertexData[offset++] = 0;

                    if(isTextured) {
                        vertexData[offset++] = 0;
                        vertexData[offset++] = 0;
                    }

                    vertexData[offset++] = rectangle.center.x;
                    vertexData[offset++] = rectangle.center.y + rectangle.a / 2;
                    vertexData[offset++] = rectangle.center.z - rectangle.b / 2;

                    vertexData[offset++] = normalVectorDirection;
                    vertexData[offset++] = 0;
                    vertexData[offset++] = 0;

                    if(isTextured) {
                        vertexData[offset++] = bTextureUnits;
                        vertexData[offset++] = 0;
                    }

                    vertexData[offset++] = rectangle.center.x;
                    vertexData[offset++] = rectangle.center.y - rectangle.a / 2;
                    vertexData[offset++] = rectangle.center.z + rectangle.b / 2;

                    vertexData[offset++] = normalVectorDirection;
                    vertexData[offset++] = 0;
                    vertexData[offset++] = 0;

                    if(isTextured) {
                        vertexData[offset++] = 0;
                        vertexData[offset++] = aTextureUnits;
                    }

                    vertexData[offset++] = rectangle.center.x;
                    vertexData[offset++] = rectangle.center.y - rectangle.a / 2;
                    vertexData[offset++] = rectangle.center.z - rectangle.b / 2;

                    vertexData[offset++] = normalVectorDirection;
                    vertexData[offset++] = 0;
                    vertexData[offset++] = 0;

                    if(isTextured) {
                        vertexData[offset++] = bTextureUnits;
                        vertexData[offset++] = aTextureUnits;
                    }
                }
                break;
            case yAxis:
                if(normalVectorDirection < 0){
                    vertexData[offset++] = rectangle.center.x + rectangle.a / 2;
                    vertexData[offset++] = rectangle.center.y;
                    vertexData[offset++] = rectangle.center.z - rectangle.b / 2;

                    vertexData[offset++] = 0;
                    vertexData[offset++] = normalVectorDirection;
                    vertexData[offset++] = 0;

                    if(isTextured) {
                        vertexData[offset++] = aTextureUnits;
                        vertexData[offset++] = 0;
                    }

                    vertexData[offset++] = rectangle.center.x + rectangle.a / 2;
                    vertexData[offset++] = rectangle.center.y;
                    vertexData[offset++] = rectangle.center.z + rectangle.b / 2;

                    vertexData[offset++] = 0;
                    vertexData[offset++] = normalVectorDirection;
                    vertexData[offset++] = 0;

                    if(isTextured) {
                        vertexData[offset++] = aTextureUnits;
                        vertexData[offset++] = bTextureUnits;
                    }

                    vertexData[offset++] = rectangle.center.x - rectangle.a / 2;
                    vertexData[offset++] = rectangle.center.y;
                    vertexData[offset++] = rectangle.center.z - rectangle.b / 2;

                    vertexData[offset++] = 0;
                    vertexData[offset++] = normalVectorDirection;
                    vertexData[offset++] = 0;

                    if(isTextured) {
                        vertexData[offset++] = 0;
                        vertexData[offset++] = 0;
                    }

                    vertexData[offset++] = rectangle.center.x - rectangle.a / 2;
                    vertexData[offset++] = rectangle.center.y;
                    vertexData[offset++] = rectangle.center.z + rectangle.b / 2;

                    vertexData[offset++] = 0;
                    vertexData[offset++] = normalVectorDirection;
                    vertexData[offset++] = 0;

                    if(isTextured) {
                        vertexData[offset++] = 0;
                        vertexData[offset++] = bTextureUnits;
                    }
                }
                else{
                    vertexData[offset++] = rectangle.center.x + rectangle.a / 2;
                    vertexData[offset++] = rectangle.center.y;
                    vertexData[offset++] = rectangle.center.z - rectangle.b / 2;

                    vertexData[offset++] = 0;
                    vertexData[offset++] = normalVectorDirection;
                    vertexData[offset++] = 0;

                    if(isTextured) {
                        vertexData[offset++] = aTextureUnits;
                        vertexData[offset++] = 0;
                    }

                    vertexData[offset++] = rectangle.center.x - rectangle.a / 2;
                    vertexData[offset++] = rectangle.center.y;
                    vertexData[offset++] = rectangle.center.z - rectangle.b / 2;

                    vertexData[offset++] = 0;
                    vertexData[offset++] = normalVectorDirection;
                    vertexData[offset++] = 0;

                    if(isTextured) {
                        vertexData[offset++] = 0;
                        vertexData[offset++] = 0;
                    }

                    vertexData[offset++] = rectangle.center.x + rectangle.a / 2;
                    vertexData[offset++] = rectangle.center.y;
                    vertexData[offset++] = rectangle.center.z + rectangle.b / 2;

                    vertexData[offset++] = 0;
                    vertexData[offset++] = normalVectorDirection;
                    vertexData[offset++] = 0;

                    if(isTextured) {
                        vertexData[offset++] = aTextureUnits;
                        vertexData[offset++] = bTextureUnits;
                    }

                    vertexData[offset++] = rectangle.center.x - rectangle.a / 2;
                    vertexData[offset++] = rectangle.center.y;
                    vertexData[offset++] = rectangle.center.z + rectangle.b / 2;

                    vertexData[offset++] = 0;
                    vertexData[offset++] = normalVectorDirection;
                    vertexData[offset++] = 0;

                    if(isTextured) {
                        vertexData[offset++] = 0;
                        vertexData[offset++] = bTextureUnits;
                    }
                }


                break;
            case zAxis:
                if(normalVectorDirection < 0){
                    vertexData[offset++] = rectangle.center.x + rectangle.a / 2;
                    vertexData[offset++] = rectangle.center.y + rectangle.b / 2;
                    vertexData[offset++] = rectangle.center.z;

                    vertexData[offset++] = 0;
                    vertexData[offset++] = 0;
                    vertexData[offset++] = normalVectorDirection;

                    if(isTextured) {
                        vertexData[offset++] = aTextureUnits;
                        vertexData[offset++] = 0;
                    }

                    vertexData[offset++] = rectangle.center.x + rectangle.a / 2;
                    vertexData[offset++] = rectangle.center.y - rectangle.b / 2;
                    vertexData[offset++] = rectangle.center.z;

                    vertexData[offset++] = 0;
                    vertexData[offset++] = 0;
                    vertexData[offset++] = normalVectorDirection;

                    if(isTextured) {
                        vertexData[offset++] = aTextureUnits;
                        vertexData[offset++] = bTextureUnits;
                    }

                    vertexData[offset++] = rectangle.center.x - rectangle.a / 2;
                    vertexData[offset++] = rectangle.center.y + rectangle.b / 2;
                    vertexData[offset++] = rectangle.center.z;

                    vertexData[offset++] = 0;
                    vertexData[offset++] = 0;
                    vertexData[offset++] = normalVectorDirection;

                    if(isTextured) {
                        vertexData[offset++] = 0;
                        vertexData[offset++] = 0;
                    }

                    vertexData[offset++] = rectangle.center.x - rectangle.a / 2;
                    vertexData[offset++] = rectangle.center.y - rectangle.b / 2;
                    vertexData[offset++] = rectangle.center.z;

                    vertexData[offset++] = 0;
                    vertexData[offset++] = 0;
                    vertexData[offset++] = normalVectorDirection;

                    if(isTextured) {
                        vertexData[offset++] = 0;
                        vertexData[offset++] = bTextureUnits;
                        break;
                    }
                }
                else{
                    vertexData[offset++] = rectangle.center.x + rectangle.a / 2;
                    vertexData[offset++] = rectangle.center.y + rectangle.b / 2;
                    vertexData[offset++] = rectangle.center.z;

                    vertexData[offset++] = 0;
                    vertexData[offset++] = 0;
                    vertexData[offset++] = normalVectorDirection;

                    if(isTextured) {
                        vertexData[offset++] = aTextureUnits;
                        vertexData[offset++] = 0;
                    }

                    vertexData[offset++] = rectangle.center.x - rectangle.a / 2;
                    vertexData[offset++] = rectangle.center.y + rectangle.b / 2;
                    vertexData[offset++] = rectangle.center.z;

                    vertexData[offset++] = 0;
                    vertexData[offset++] = 0;
                    vertexData[offset++] = normalVectorDirection;

                    if(isTextured) {
                        vertexData[offset++] = 0;
                        vertexData[offset++] = 0;
                    }

                    vertexData[offset++] = rectangle.center.x + rectangle.a / 2;
                    vertexData[offset++] = rectangle.center.y - rectangle.b / 2;
                    vertexData[offset++] = rectangle.center.z;

                    vertexData[offset++] = 0;
                    vertexData[offset++] = 0;
                    vertexData[offset++] = normalVectorDirection;

                    if(isTextured) {
                        vertexData[offset++] = aTextureUnits;
                        vertexData[offset++] = bTextureUnits;
                    }

                    vertexData[offset++] = rectangle.center.x - rectangle.a / 2;
                    vertexData[offset++] = rectangle.center.y - rectangle.b / 2;
                    vertexData[offset++] = rectangle.center.z;

                    vertexData[offset++] = 0;
                    vertexData[offset++] = 0;
                    vertexData[offset++] = normalVectorDirection;

                    if(isTextured) {
                        vertexData[offset++] = 0;
                        vertexData[offset++] = bTextureUnits;
                        break;
                    }
                }

        }

        drawCommands.add(new DrawCommand() {
            @Override
            public void draw() {
                glDrawArrays(GL_TRIANGLE_STRIP, startVertex, ObjectGenerator.VERTEX_PER_RECTANGLE);
            }
        });

    }

    /**
     * Dodaje do tablicy <em><b>vertexData</b></em> atrybuty wierzchołków ostrosłupa (bez podstawy) a do listy <em><b>drawCommands</b></em> odpowiednie reguły rysowania.
     * @param pyramid Wymiary ostrosłupa definiującego jedną z części diamentu.
     * @param direction Kierunek wektora wysokości ostrosłupa.
     */
    public void appendPyramidWithoutBase(Pyramid pyramid, float direction) {

        final int indicesCount = pyramid.baseVerticesCount * 3;
        int vertexStar = offset / ( isTextured ? FLOATS_PER_VERTEX_WITH_TEXTURE : FLOATS_PER_VERTEX_WITHOUT_TEXTURE);

        for (int i = 0; i < pyramid.baseVerticesCount; i++) {
            float ratio = direction > 0 ? ((float)( pyramid.baseVerticesCount - i) / pyramid.baseVerticesCount) : ( (float)i / pyramid.baseVerticesCount);
            float alpha =  ratio * 2f * (float) Math.PI;

            vertexData[offset++] =  pyramid.radius * FloatMath.cos(alpha);
            vertexData[offset++] = 0;
            vertexData[offset++] = pyramid.radius * FloatMath.sin(alpha);

            Vector vNormal = new Vector(vertexData[offset - 3], vertexData[offset - 2], vertexData[offset - 1]).normalize();
            vertexData[offset++] = vNormal.x;
            vertexData[offset++] = vNormal.y;
            vertexData[offset++] = vNormal.z;

            if(isTextured) {
                vertexData[offset++] = i % 2; // co drugi trójkąt ma teksturowanie od 0
                vertexData[offset++] = 1f;
            }
        }

        vertexData[offset++] = 0;
        vertexData[offset++] = (direction * pyramid.height);
        vertexData[offset++] = 0;

        vertexData[offset++] = 0;
        vertexData[offset++] = direction;
        vertexData[offset++] = 0;

        if(isTextured) {
            vertexData[offset++] = 0.5f;
            vertexData[offset++] = 0f;
        }

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

    /**
     *Dodaje do tablicy <em><b>vertexData</b></em> atrybuty wierzchołków bocznej powierzchni ściętego stożka, a do listy <em><b>drawCommands</b></em> odpowiednie reguły rysowania.
     * @param bottomCenter Położenie dolnej podstawy ściętego stożka.
     * @param bottomRadius Wartość promienia dolnej podstawy ściętego stożka.
     * @param topCenter Położenie górnej podstawy ściętego stożka.
     * @param topRadius Wartość promienia górnej podstawy ściętego stożka.
     * @param numPoints Rozdzielczość siatki trójkątów.
     * @param textureUnit Wielkość kwadratowego kafelka tekstury.
     */
    public void appendCylindersCurvedSurface( Point bottomCenter, float bottomRadius, Point topCenter, float topRadius, int numPoints, float textureUnit) {
        final int startVertex = offset / ( isTextured ? FLOATS_PER_VERTEX_WITH_TEXTURE : FLOATS_PER_VERTEX_WITHOUT_TEXTURE);
        final int numVertices = (numPoints + 1) * 2;

        float height = Math.abs(new Vector(bottomCenter.Subtract(topCenter)).length());
        float basePerimeter = 2f * (float) Math.PI * ((bottomRadius + topRadius)/2); // uśredniłem promienie obu podstaw

        float aTextureUnits = basePerimeter / textureUnit;
        float bTextureUnits = height / textureUnit;

        for (int i = numPoints; i >= 0; i--) {
            float angleInRadians = ((float) i / numPoints) * ((float) Math.PI * 2f);

            vertexData[offset++] = topCenter.x + topRadius * FloatMath.cos(angleInRadians);
            vertexData[offset++] = topCenter.y;
            vertexData[offset++] = topCenter.z + topRadius * FloatMath.sin(angleInRadians);

            Vector vNormal = new Vector(vertexData[offset - 3] - topCenter.x, vertexData[offset - 2] - topCenter.y, vertexData[offset - 1] - topCenter.z).normalize();
            vertexData[offset++] = vNormal.x;
            vertexData[offset++] = vNormal.y;
            vertexData[offset++] = vNormal.z;

            if(isTextured){
                vertexData[offset++] = ((float)i / numPoints) * aTextureUnits;
                vertexData[offset++] = 0f;
            }


            vertexData[offset++] = bottomCenter.x + bottomRadius * FloatMath.cos(angleInRadians);
            vertexData[offset++] = bottomCenter.y;
            vertexData[offset++] = bottomCenter.z + bottomRadius * FloatMath.sin(angleInRadians);

            vNormal = new Vector(vertexData[offset - 3] - bottomCenter.x, vertexData[offset - 2] - bottomCenter.y, vertexData[offset - 1] - bottomCenter.z).normalize();
            vertexData[offset++] = vNormal.x;
            vertexData[offset++] = vNormal.y;
            vertexData[offset++] = vNormal.z;

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

    /**
     * Dodaje do tablicy <em><b>vertexData</b></em> atrybuty wierzchołków koła, a do listy <em><b>drawCommands</b></em> odpowiednie reguły rysowania.
     * @param center Środek koła.
     * @param radius Promień koła.
     * @param direction Kierunek wektora normalnego.
     * @param numPoints Rozdzielczość siatki trójkątów.
     */
    public void appendCircle(Point center, float radius, float direction, int numPoints) {
        final int startVertex = offset / ( isTextured ? FLOATS_PER_VERTEX_WITH_TEXTURE : FLOATS_PER_VERTEX_WITHOUT_TEXTURE);
        final int numVertices = 1 + (numPoints + 1);

        final boolean upper = direction > 0;

        vertexData[offset++] = center.x;
        vertexData[offset++] = center.y;
        vertexData[offset++] = center.z;

        vertexData[offset++] = 0f;
        vertexData[offset++] = direction;
        vertexData[offset++] = 0f;

        if(isTextured) {
            vertexData[offset++] = 0.5f;
            vertexData[offset++] = 0.5f;
        }

        for (int i = 0; i <= numPoints; i++) {
            float ratio = upper ? (float)(numPoints - i)/numPoints : (float)i/numPoints;
            float alpha = ratio * ((float) Math.PI * 2f);

            vertexData[offset++] = center.x + radius * FloatMath.cos(alpha);
            vertexData[offset++] = center.y;
            vertexData[offset++] = center.z + radius * FloatMath.sin(alpha);

            Vector vNormal = new Vector(vertexData[offset - 3] - center.x, vertexData[offset - 2] - center.y, vertexData[offset - 1] - center.z).normalize();
            vertexData[offset++] = vNormal.x;
            vertexData[offset++] = vNormal.y;
            vertexData[offset++] = vNormal.z;

            if(isTextured) {
                vertexData[offset++] = FloatMath.cos(alpha) / 2 + 0.5f;
                vertexData[offset++] = FloatMath.sin(alpha) / 2 + 0.5f;
            }
        }

        drawCommands.add(new DrawCommand() {
            @Override
            public void draw() {
                glDrawArrays(GL_TRIANGLE_FAN, startVertex, numVertices);
            }
        });
    }

    /**
     * Zwraca dane modelu powstałego poprzez zastosowanie sekwencji metod rozszerzających.
     * @return Obiekt zawierający <em><b>vertexData</b></em> oraz <em><b>drawCommands</b></em>.
     */
    public GraphicsData build(){
        return new GraphicsData(vertexData, drawCommands);
    }

}
