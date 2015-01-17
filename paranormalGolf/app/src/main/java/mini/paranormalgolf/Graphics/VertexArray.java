package mini.paranormalgolf.Graphics;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import static android.opengl.GLES20.GL_FLOAT;
import static android.opengl.GLES20.glEnableVertexAttribArray;
import static android.opengl.GLES20.glVertexAttribPointer;

/**
 * Służy do przetrzymywania atrybutów wierzchołków modeli obiektów.
 */
public class VertexArray {

    /**
     * Stała określająca ilość bajtów w pojedynczej zmiennej typu <em>float</em>.
     */
    public static final int BYTES_PER_FLOAT = 4;

    /**
     * Bufor przechowujący atrybuty wierzchołków siatki trójkątów.
     */
    private final FloatBuffer floatBuffer;


    /**
     * Tworzy i inicjalizuje obiekt przechowujący atrybuty wierzchołków modeli obiektów.
     * @param vertexData Atrybuty wierzchołków modeli.
     */
    public VertexArray(float[] vertexData) {
        floatBuffer = ByteBuffer
                .allocateDirect(vertexData.length * BYTES_PER_FLOAT)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer()
                .put(vertexData);
    }

    /**
     * Wskazuje w buforze <b><em>floatBuffer</em></b> miejsce skojarzone z danym atrybutem.
     * @param dataOffset Wstępne przesunięcie wskaźnika.
     * @param attributeLocation Lokalizacja atrybutu.
     * @param componentCount Ilość komórek definiujących dany atrybut.
     * @param stride Krok określający ile komórek definiuje wszystkie atrybuty danego wierzchołka.
     */
    public void setVertexAttribPointer(int dataOffset, int attributeLocation, int componentCount, int stride) {
        floatBuffer.position(dataOffset);
        glVertexAttribPointer(attributeLocation, componentCount, GL_FLOAT, false, stride, floatBuffer);
        glEnableVertexAttribArray(attributeLocation);
        floatBuffer.position(0);
    }
}
