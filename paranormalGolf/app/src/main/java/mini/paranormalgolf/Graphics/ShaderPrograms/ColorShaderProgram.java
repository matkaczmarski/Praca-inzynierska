package mini.paranormalgolf.Graphics.ShaderPrograms;

import android.content.Context;

import mini.paranormalgolf.Graphics.LightData;
import mini.paranormalgolf.R;

import static android.opengl.GLES20.glGetAttribLocation;
import static android.opengl.GLES20.glGetUniformLocation;
import static android.opengl.GLES20.glUniform1f;
import static android.opengl.GLES20.glUniform3f;
import static android.opengl.GLES20.glUniform4f;
import static android.opengl.GLES20.glUniformMatrix4fv;

/**
 * Opisuje program do rysowania obiektów za pomocą określonego koloru.
 */
public class ColorShaderProgram extends ShaderProgram {

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
     * Określa lokalizację stałej wartości (uniform tablicy RGB koloru) występującej w kodzie programu.
     */
    private final int uColorLocation;
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
     * Określa lokalizację atrybutu pozycji wierzchołków występującego w kodzie programu.
     */
    private final int aPositionLocation;
    /**
     * Określa lokalizację atrybutu wektorów normalnych występującego w kodzie programu.
     */
    private final int aNormalLocation;

    /**
     * Tworzy obiekt programu do rysowania obiektów za pomocą określonego koloru.
     * @param context Bieżący kontekst pozwalający uzyskać dostęp do zasobów aplikacji.
     */
    public ColorShaderProgram(Context context) {
        super(context, R.raw.light_vertex_shader, R.raw.light_fragmet_shader);

        uMVPMatrixLocation = glGetUniformLocation(program, U_MVPMATRIX);
        uMVMatrixLocation = glGetUniformLocation(program, U_MODELMATRIX);
        uItMVMatrixLocation = glGetUniformLocation(program, U_NORMALSROTATIONMATRIX);
        uColorLocation = glGetUniformLocation(program, U_COLOR);
        uLightPosLocation = glGetUniformLocation(program, U_LIGHTPOSITION);
        uLightAmbLocation = glGetUniformLocation(program, U_LIGHTAMBIENT);
        uLightDiffLocation = glGetUniformLocation(program, U_LIGHTDIFFUSION);

        aPositionLocation = glGetAttribLocation(program, A_POSITION);
        aNormalLocation = glGetAttribLocation(program, A_NORMAL);
    }

    /**
     * Przypisuje stałym wartościom (uniformom) występującym w kodzie programu odpowiednie wartości.
     * @param mvpMatrix Przypisywana wartość macierzy modelViewProjection.
     * @param mvMatrix Przypisywana wartość macierzy modelView.
     * @param itMvMatrix Przypisywana wartość normalsRotation.
     * @param light Przypisywana wartość parametrów światła.
     * @param rgba Przypisywana wartość tablicy RGBA opisującej kolor.
     */
    public void setUniforms(float[] mvpMatrix, float[] mvMatrix, float[] itMvMatrix, LightData light, float[] rgba) {
        glUniformMatrix4fv(uMVPMatrixLocation, 1, false, mvpMatrix, 0);
        glUniformMatrix4fv(uMVMatrixLocation, 1, false, mvMatrix, 0);
        glUniformMatrix4fv(uItMVMatrixLocation, 1, false, itMvMatrix, 0);
        glUniform4f(uColorLocation, rgba[0], rgba[1], rgba[2], rgba[3]);
        glUniform3f(uLightPosLocation, light.position.x, light.position.y, light.position.z);
        glUniform1f(uLightAmbLocation, light.ambient);
        glUniform1f(uLightDiffLocation, light.diffusion);
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


}
