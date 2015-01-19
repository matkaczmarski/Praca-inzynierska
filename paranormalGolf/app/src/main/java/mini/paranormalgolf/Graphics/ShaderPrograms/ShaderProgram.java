/**
 * Podprzestrzeń przestrzeni Graphics. Zawiera klasy opisujące programy urachamiane na karcie graficznej.
 */
package mini.paranormalgolf.Graphics.ShaderPrograms;

import android.content.Context;
import static android.opengl.GLES20.glUseProgram;
import mini.paranormalgolf.Helpers.ResourceHelper;

/**
 * Opisuje program uruchamiany na karcie graficznej.
 */
abstract public class ShaderProgram {

    /**
     * Typ wyliczeniowy opisujące występujące w aplikacji typy programów.
     */
    public enum ShaderProgramType {
        color,
        depthMap,
        withShadowing,
        withoutShadowing,
        skyBox
    }

    /**
     * Definiuje nazwę stałej wartości (uniform macierzy modelViewProjection) występującej w kodzie programów.
     */
    protected static final String U_MVPMATRIX = "u_MVPMatrix";
    /**
     * Definiuje nazwę stałej wartości (uniform macierzy modelView) występującej w kodzie programów.
     */
    protected static final String U_MODELMATRIX = "u_MMatrix";
    /**
     * Definiuje nazwę stałej wartości (uniform macierzy normalsRotation) występującej w kodzie programów.
     */
    protected static final String U_NORMALSROTATIONMATRIX = "u_NMatrix";
    /**
     * Definiuje nazwę stałej wartości (uniform tablicy RGB koloru) występującej w kodzie programów.
     */
    protected static final String U_COLOR = "u_Color";
    /**
     * Definiuje nazwę stałej wartości (uniform tekstury) występującej w kodzie programów.
     */
    protected static final String U_TEXTURE = "u_Texture";
    /**
     * Definiuje nazwę stałej wartości (uniform położenia źródła światła) występującej w kodzie programów.
     */
    protected static final String U_LIGHTPOSITION = "u_LightPosition";
    /**
     * Definiuje nazwę stałej wartości (uniform współczynnika światła otoczenia) występującej w kodzie programów.
     */
    protected static final String U_LIGHTAMBIENT = "u_LightsAmbient";
    /**
     * Definiuje nazwę stałej wartości (uniform współczynnika światła rozproszonego) występującej w kodzie programów.
     */
    protected static final String U_LIGHTDIFFUSION = "u_LightsDiffusion";
    /**
     * Definiuje nazwę stałej wartości (uniform stopnia przezroczystości obiektu) występującej w kodzie programów.
     */
    protected static final String U_OPACITY = "u_Opacity";
    /**
     * Definiuje nazwę stałej wartości (uniform macierzy lightsViewProjection) występującej w kodzie programów.
     */
    protected static final String U_SHADOWPMATRIX = "u_ShadowVPMatrix";
    /**
     * Definiuje nazwę stałej wartości (uniform mapy głębokości cieni) występującej w kodzie programów.
     */
    protected static final String U_SHADOWTEXTURE = "u_ShadowTexture";


    /**
     * Definiuje nazwę atrybutu pozycji wierzchołków występującego w kodzie programów.
     */
    protected static final String A_POSITION = "a_Position";
    /**
     * Definiuje nazwę atrybutu wektorów normalnych występującego w kodzie programów.
     */
    protected static final String A_NORMAL = "a_Normal";
    /**
     * Definiuje nazwę atrybutu współrzędnych tekstur dla wierzchołków występującego w kodzie programów.
     */
    protected static final String A_TEXTURE_COORDINATES = "a_TextureCoordinates";

    /**
     * Identyfikator OpenGL dla programu.
     */
    protected final int program;

    /**
     * Tworzy program na podstawie kodów programów dla wierzchołków oraz fragmentów.
     * @param context Bieżący kontekst pozwalający uzyskać dostęp do zasobów aplikacji.
     * @param vertexShaderResourceId Identyfikator pliku z kodem programu dla wierchołków.
     * @param fragmentShaderResourceId Identyfikator pliku z kodem programu dla fragmentów.
     */
    protected ShaderProgram(Context context, int vertexShaderResourceId, int fragmentShaderResourceId) {
        program = ShaderHelper.buildProgram(ResourceHelper.readTextFileFromResource(context, vertexShaderResourceId), ResourceHelper.readTextFileFromResource(context, fragmentShaderResourceId));
    }

    /**
     * Zwraca identyfikator OpenGL programu.
     * @return Wartość <em><b>program</b></em>.
     */
    public int getProgram() {
        return program;
    }

    /**
     * Zawiadamia środowisko OpenGL, aby zastosować program o identyfikatorze OpenGL <em><b>program</b></em>.
     */
    public void useProgram() {
        glUseProgram(program);
    }
}
