package mini.paranormalgolf.Graphics.ShaderPrograms;

import android.content.Context;

import mini.paranormalgolf.Graphics.LightData;
import mini.paranormalgolf.R;

import static android.opengl.GLES20.GL_TEXTURE0;
import static android.opengl.GLES20.GL_TEXTURE_2D;
import static android.opengl.GLES20.glActiveTexture;
import static android.opengl.GLES20.glBindTexture;
import static android.opengl.GLES20.glGetAttribLocation;
import static android.opengl.GLES20.glGetUniformLocation;
import static android.opengl.GLES20.glUniform1f;
import static android.opengl.GLES20.glUniform1i;
import static android.opengl.GLES20.glUniform3f;
import static android.opengl.GLES20.glUniformMatrix4fv;

/**
 * Opisuje program do mapowania tekstur na modelach bez generowania cieni obiektów.
 */
public class TextureShaderProgram extends ShaderProgram {

    /**
     * Określa lokalizację stałej wartości (uniform macierzy modelViewProjection) występującej w kodzie programu.
     */
    private final int uMVPMatrixLocation;
    /**
     * Określa lokalizację stałej wartości (uniform macierzy modelViewMatrix) występującej w kodzie programu.
     */
    private final int uMVMatrixLocation;
    /**
     * Określa lokalizację stałej wartości (uniform macierzy normalsRotation) występującej w kodzie programu.
     */
    private final int uItMVMatrixLocation;
    /**
     * Określa lokalizację stałej wartości (uniform położenia źródła światła) występującej w kodzie programu.
     */
    private final int uLightPosLocation;
    /**
     * Określa lokalizację stałej wartości (uniform  współczynnika światła otoczenia) występującej w kodzie programu.
     */
    private final int uLightAmbLocation;
    /**
     * Określa lokalizację stałej wartości (uniform  współczynnika światła rozproszonego) występującej w kodzie programu.
     */
    private final int uLightDiffLocation;
    /**
     * Określa lokalizację stałej wartości (uniform  tekstury) występującej w kodzie programu.
     */
    private final int uTextureUnitLocation;
    /**
     * Określa lokalizację stałej wartości (uniform stopnia przezroczystości obiektu) występującej w kodzie programu.
     */
    private final int uOpacityLocation;

    /**
     * Określa lokalizację atrybutu pozycji wierzchołków występującego w kodzie programu.
     */
    private final int aPositionLocation;
    /**
     * Określa lokalizację atrybutu wektorów normalnych występującego w kodzie programu.
     */
    private final int aNormalLocation;
    /**
     * Określa lokalizację atrybutu współrzędnych tekstur występującego w kodzie programu.
     */
    private final int aTextureCoordinatesLocation;

    /**
     * Tworzy obiekt programu do mapowania tekstur na modelach bez generowania cieni obiektów.
     * @param context Bieżący kontekst pozwalający uzyskać dostęp do zasobów aplikacji.
     */
    public TextureShaderProgram(Context context) {
        super(context, R.raw.texture_light_vertex_shader, R.raw.texture_light_fragment_shader);

        // Retrieve uniform locations for the shader program.
        uMVPMatrixLocation = glGetUniformLocation(program, U_MVPMATRIX);
        uMVMatrixLocation = glGetUniformLocation(program, U_MODELMATRIX);
        uItMVMatrixLocation = glGetUniformLocation(program, U_NORMALSROTATIONMATRIX);
        uLightPosLocation = glGetUniformLocation(program, U_LIGHTPOSITION);
        uLightAmbLocation = glGetUniformLocation(program, U_LIGHTAMBIENT);
        uLightDiffLocation = glGetUniformLocation(program, U_LIGHTDIFFUSION);
        uTextureUnitLocation = glGetUniformLocation(program, U_TEXTURE);
        uOpacityLocation = glGetUniformLocation(program, U_OPACITY);

        // Retrieve attribute locations for the shader program.
        aPositionLocation = glGetAttribLocation(program, A_POSITION);
        aNormalLocation = glGetAttribLocation(program, A_NORMAL);
        aTextureCoordinatesLocation = glGetAttribLocation(program, A_TEXTURE_COORDINATES);
    }

    /**
     * Przypisuje stałym wartościom (uniformom) występującym w kodzie programu odpowiednie wartości.
     * @param mvpMatrix Przypisywana wartość macierzy modelViewProjection.
     * @param mvMatrix Przypisywana wartość macierzy modelView.
     * @param itMvMatrix Przypisywana wartość normalsRotation.
     * @param light Przypisywana wartość parametrów światła.
     * @param textureId Przypisywana wartość identyfikatora tekstury.
     * @param opacity Przypisywana wartość stopnia przezroczystości.
     */
    public void setUniforms(float[] mvpMatrix, float[] mvMatrix, float[] itMvMatrix, LightData light, int textureId, float opacity) {
        glUniformMatrix4fv(uMVPMatrixLocation, 1, false, mvpMatrix, 0);
        glUniformMatrix4fv(uMVMatrixLocation, 1, false, mvMatrix, 0);
        glUniformMatrix4fv(uItMVMatrixLocation, 1, false, itMvMatrix, 0);
        glUniform3f(uLightPosLocation, light.position.x, light.position.y, light.position.z);
        glUniform1f(uLightAmbLocation, light.ambient);
        glUniform1f(uLightDiffLocation, light.diffusion);
        glUniform1f(uOpacityLocation, opacity);

        glActiveTexture(GL_TEXTURE0);
        glBindTexture(GL_TEXTURE_2D, textureId);
        glUniform1i(uTextureUnitLocation, 0);
    }

    /**
     * Zwraca lokalizację atrybutu pozycji wierzchołków.
     * @return Wartość <em><b>aPositionLocation</b></em>.
     */
    public int getPositionAttributeLocation() {
        return aPositionLocation;
    }

    /**
     * Zwraca lokalizację atrybutu wektorów normalnych.
     * @return Wartość <em><b>aNormalLocation</b></em>.
     */
    public int getNormalAttributeLocation() {
        return aNormalLocation;
    }


    /**
     * Zwraca lokalizację atrybutu współrzędnych tekstur.
     * @return Wartość <em><b>aTextureCoordinatesLocation</b></em>.
     */
    public int getTextureCoordinatesAttributeLocation() {
        return aTextureCoordinatesLocation;
    }
}
