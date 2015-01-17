package mini.paranormalgolf.Graphics;

/**
 * Wspomaga tworzenie macierzy stosowanych do wyliczania położen obiektów na ekranie.
 */
public class MatrixHelper {

    /**
     * Tworzy macierz projekcji.
     * @param m Macierz wyjściowa.
     * @param fovDegree Kąt zasięgu widoczności.
     * @param aspect Stosunek szerokości oraz wysokości rzutni (ekranu).
     * @param near Odległość od rzutni bliższej.
     * @param far Odległość od rzutni dalszej.
     */
    public static void perspectiveM(float[] m, float fovDegree, float aspect, float near, float far) {
        final float angleInRadians = (float) (fovDegree * Math.PI / 180.0);
        final float a = (float) (1.0 / Math.tan(angleInRadians / 2.0));

        m[0] = a / aspect;
        m[1] = 0f;
        m[2] = 0f;
        m[3] = 0f;

        m[4] = 0f;
        m[5] = a;
        m[6] = 0f;
        m[7] = 0f;

        m[8] = 0f;
        m[9] = 0f;
        m[10] = -((far + near) / (far - near));
        m[11] = -1f;

        m[12] = 0f;
        m[13] = 0f;
        m[14] = -((2f * far * near) / (far - near));
        m[15] = 0f;
    }
}
