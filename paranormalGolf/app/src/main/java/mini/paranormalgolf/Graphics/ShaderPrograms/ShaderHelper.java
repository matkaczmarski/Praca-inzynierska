package mini.paranormalgolf.Graphics.ShaderPrograms;

import android.util.Log;

import static android.opengl.GLES20.GL_COMPILE_STATUS;
import static android.opengl.GLES20.GL_FRAGMENT_SHADER;
import static android.opengl.GLES20.GL_LINK_STATUS;
import static android.opengl.GLES20.GL_VALIDATE_STATUS;
import static android.opengl.GLES20.GL_VERTEX_SHADER;
import static android.opengl.GLES20.glAttachShader;
import static android.opengl.GLES20.glCompileShader;
import static android.opengl.GLES20.glCreateProgram;
import static android.opengl.GLES20.glCreateShader;
import static android.opengl.GLES20.glDeleteProgram;
import static android.opengl.GLES20.glDeleteShader;
import static android.opengl.GLES20.glDetachShader;
import static android.opengl.GLES20.glGetProgramInfoLog;
import static android.opengl.GLES20.glGetProgramiv;
import static android.opengl.GLES20.glGetShaderInfoLog;
import static android.opengl.GLES20.glGetShaderiv;
import static android.opengl.GLES20.glLinkProgram;
import static android.opengl.GLES20.glShaderSource;
import static android.opengl.GLES20.glValidateProgram;
import mini.paranormalgolf.LoggerConfig;

/**
 * Zawiera metody wspomagające proces tworzenia programów na podstawie kodów programów dla wierzchołków oraz fragmentów.
 */
public class ShaderHelper {
    /**
     * Stała wykorzystywana podczas pisania w Logach.
     */
    private static final String TAG = "ShaderHelper";

    /**
     * Tworzy program na podstawie kodów programów dla wierzchołków oraz fragmentów.
     * @param vertexShaderSource Kod programu dla wierzchołków.
     * @param fragmentShaderSource Kod programu dla fragmentów.
     * @return Identyfikator OpenGL dla utworzonego programu.
     */
    public static int buildProgram(String vertexShaderSource, String fragmentShaderSource) {

        int vertexShader = compileShader(GL_VERTEX_SHADER, vertexShaderSource);
        int fragmentShader = compileShader(GL_FRAGMENT_SHADER, fragmentShaderSource);
        int program = linkProgram(vertexShader, fragmentShader);

        if (LoggerConfig.ON) {
            validateProgram(program);
        }
        return program;
    }

    /**
     * Tworzy i kompiluje obiekt typu <em>Shader</em> na podstawie kodu programu dla wierzchołków lub fragmentów.
     * @param type Typ kodu programu.
     * @param shaderCode Kod programu.
     * @return Identyfikator OpenGL dla utworzonego obiektu typu <em>Shader</em>.
     */
    private static int compileShader(int type, String shaderCode) {
        final int shaderObjectId = glCreateShader(type);
        if (shaderObjectId == 0) {
            if (LoggerConfig.ON) {
                Log.w(TAG, "Could not create new shader.");
            }
            return 0;
        }

        glShaderSource(shaderObjectId, shaderCode);
        glCompileShader(shaderObjectId);
        final int[] compileStatus = new int[1];
        glGetShaderiv(shaderObjectId, GL_COMPILE_STATUS, compileStatus, 0);
        if (LoggerConfig.ON) {
            Log.v(TAG, "Results of compiling source:" + "\n" + shaderCode + "\n:" + glGetShaderInfoLog(shaderObjectId));
        }

        if (compileStatus[0] == 0) {
            glDeleteShader(shaderObjectId);
            if (LoggerConfig.ON) {
                Log.w(TAG, "Compilation of shader failed.");
            }
            return 0;
        }
        return shaderObjectId;
    }

    /**
     * Łączy dwa obiekty typu <em>Shader</em> odpowiadające programom dla wierzchołków i fragmentów w pojedynczy program.
     * @param vertexShaderId Identyfikator OpenGL dla obiektu typu <em>Shader</em> dla wierzchołków.
     * @param fragmentShaderId Identyfikator OpenGL dla obiektu typu <em>Shader</em> dla fragmentów.
     * @return Identyfikator OpenGL dla utworzonego programu.
     */
    private static int linkProgram(int vertexShaderId, int fragmentShaderId) {
        final int programObjectId = glCreateProgram();
        if (programObjectId == 0) {
            if (LoggerConfig.ON) {
                Log.w(TAG, "Could not create new program");
            }

            return 0;
        }

        glAttachShader(programObjectId, vertexShaderId);
        glAttachShader(programObjectId, fragmentShaderId);
        glLinkProgram(programObjectId);

        final int[] linkStatus = new int[1];
        glGetProgramiv(programObjectId, GL_LINK_STATUS, linkStatus, 0);
        if (LoggerConfig.ON) {
            Log.v( TAG, "Results of linking program:\n" + glGetProgramInfoLog(programObjectId));
        }

        glDetachShader(programObjectId, vertexShaderId);
        glDetachShader(programObjectId, fragmentShaderId);
        glDeleteShader(vertexShaderId);
        glDeleteShader(fragmentShaderId);

        if (linkStatus[0] == 0) {
            glDeleteProgram(programObjectId);
            if (LoggerConfig.ON) {
                Log.w(TAG, "Linking of program failed.");
            }
            return 0;
        }
        return programObjectId;
    }

    /**
     * Sprawdza poprawność utworzonego programu.
     * @param programObjectId Identyfikator OpenGL dla programu.
     * @return Wartość <b>true</b> gdy program prawidłowo został utworzony, <b>false</b> w przeciwnym przypadku.
     */
    private static boolean validateProgram(int programObjectId) {
        glValidateProgram(programObjectId);
        final int[] validateStatus = new int[1];
        glGetProgramiv(programObjectId, GL_VALIDATE_STATUS,
                validateStatus, 0);
        Log.v(TAG, "Results of validating program: " + validateStatus[0] + "\nLog:" + glGetProgramInfoLog(programObjectId));
        return validateStatus[0] != 0;
    }

}