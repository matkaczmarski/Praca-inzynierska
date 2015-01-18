package mini.paranormalgolf.Physics;


import android.content.Context;

import mini.paranormalgolf.Graphics.GraphicsData;
import mini.paranormalgolf.Graphics.ModelBuilders.ObjectGenerator;
import mini.paranormalgolf.Helpers.ResourceHelper;
import mini.paranormalgolf.Primitives.Box;
import mini.paranormalgolf.Primitives.Circle;
import mini.paranormalgolf.Primitives.Cylinder;
import mini.paranormalgolf.Primitives.Point;
import mini.paranormalgolf.Primitives.Sphere;
import mini.paranormalgolf.Primitives.Vector;
import mini.paranormalgolf.Graphics.VertexArray;
import mini.paranormalgolf.R;

import static android.opengl.Matrix.multiplyMM;
import static android.opengl.Matrix.setIdentityM;
import static android.opengl.Matrix.setRotateM;

/**
 * Reprezentuje kulkę w grze.
 */
public class Ball extends MovableElement {

    /**
     * Stała opisująca stopień przezroczystości kulki.
     */
    public final float BALL_OPACITY = 1f;

    /**
     * Stała określająca rozdzielczość (gęstość) siatki trójkątów w modelu kulki.
     */
    private final int MESH_DIMENSION = 25;


    /**
     * Domyślna wartość promienia kulki.
     */
    public static final float DEFAULT_RADIUS = 1.0f;

    /**
     * Współczynnik dla oporu powietrza.
     */
    private final float CD =0.4f;

    /**
     * Wartość gęstości powietrza.
     */
    private final float DENSITY =1.225f;

    /**
     * Długość promienia kulki.
     */
    private final float radius;

    /**
     * Masa kulki.
     */
    private final float mass;

    /**
     * Powierzchnia czołowa kulki.
     */
    private final float area;

    /**
     * Aktualny stan obrotu kulki, zapisany w postaci macierzy obrotu.
     */
    private float[] rotation;

    /**
     * Typ wyliczeniowy określający możliwe rodzaje teksturowania kulki.
     */
    public enum BallTexture{
        homeWorld,
        redAndWhite,
        noise,
        beach,
        lava,
        sun,
        jelly,
        marble,
        frozen,
        tiger,
        orangeSkin,
        amethystAlcove,
        drizzledPaint,
        dyedStonework,
        eyeOfTheSunGod,
        girlsBestFriend,
        jupiter,
        liquidCrystal,
        methaneLakes,
        spottedBianco,
        toxicByproduct,
        verdeJaspe
    }


    /**
     * Opisuje typ tekstury kulki.
     */
    private static BallTexture ballTextureType;

    /**
     * Statyczna wartość identyfikatora OpenGL tekstury kulki.
     */
    private static int ballTextureId;

    /**
     * Zwraca długość promienia kulki.
     * @return Wartość <b><em>radius</em></b>.
     */
    public float getRadius(){
        return this.radius;
    }

    /**
     * Zwraca stan obrotu kulki zapisany w postaci macierzy obrotu.
     * @return Obiekt <b><em>rotation</em></b>.
     */
    public float[] getRotation() {return this.rotation;}

    /**
     * Zwraca wartość identyfikatora OpenGL aktualnej tekstury kulki.
     * @return Wartość <b><em>ballTextureId</em></b>.
     */
    public static int getTexture(){return ballTextureId;}

    /**
     * Tworzy obiekt typu kulka.
     * @param location Współrzędne środka kulki w globalnym układzie współrzędnych.
     * @param radius Długość promienia kulki.
     * @param velocity Wektor prędkości początkowej.
     * @param ballTexture Typ tekstury kulki.
     */
    public Ball(Point location, float radius, Vector velocity, BallTexture ballTexture) {
        super(velocity, location);

        this.ballTextureType = ballTexture;

        this.radius = radius;
        mass=5;
        area=(float)Math.PI*radius*radius;

        rotation=new float[16];
        setIdentityM(rotation,0);

        GraphicsData generatedData = ObjectGenerator.createBallModel(radius, MESH_DIMENSION);
        vertexData = new VertexArray(generatedData.vertexData);
        drawCommands = generatedData.drawCommands;
    }

    /**
     * Odświeża położenie kulki.
     * @param dt Czas (w sekundach), który upłynął między 2 ostatnimi klatkami.
     * @param accelerometrData Wektor przyspieszenia grawitacyjnego.
     * @param mu Wartość współczynnika tarcia dla powierzchni, na której znajduje się kulka.
     */
    public void Update(float dt,Vector accelerometrData,float mu) {
        //Update związany z poruszeniem się elementu
        int j;
        int numEqns = 6;
        float[] dq1,dq2,dq3,dq4;

        float[] q = new float[6];
        {
            q[1] = location.x;
            q[0] = velocity.x;
            q[3] = location.y;
            q[2] = velocity.y;
            q[5] = location.z;
            q[4] = velocity.z;
        }

        dq1 = SolveEquation(q, q, dt, 0.0f, accelerometrData, mu);
        dq2 = SolveEquation(q, dq1, dt, 0.5f, accelerometrData, mu);
        dq3 = SolveEquation(q, dq2, dt, 0.5f, accelerometrData, mu);
        dq4 = SolveEquation(q, dq3, dt, 1.0f, accelerometrData, mu);
        for (j = 0; j < numEqns; ++j) {
            q[j] = q[j] + (dq1[j] + 2.0f * dq2[j] + 2.0f * dq3[j] + dq4[j]) / 6.0f;
        }

        lastMove =new Vector(q[1] - location.x,q[3] - location.y,q[5] - location.z);

        if (lastMove.x != 0 || lastMove.z != 0) {
            float[] tmp=new float[16];
            float[] tmp2=new float[16];

            Vector axis = new Vector(lastMove.z, 0, -lastMove.x).normalize();
            float angle = (float) (360*Math.sqrt(lastMove.x * lastMove.x + lastMove.z * lastMove.z) /(2*Math.PI*radius));

            setRotateM(tmp,0,angle,axis.x,axis.y,axis.z);
            multiplyMM(tmp2,0,tmp,0,rotation,0);
            System.arraycopy(tmp2,0,rotation,0,16);
        }
        {
            location.x = q[1];
            velocity.x = q[0];
            location.y = q[3];
            velocity.y = q[2];
            location.z = q[5];
            velocity.z = q[4];
        }
    }

    /**
     * Pomocnicza rozwiązująca równanie różniczkowe odświeżające położenie i prędkość kulki metodą Rungego-Kutty
     * rzędu 4.
     * @param q Tablica zawierająca wartości prędkości(q[0],q[2],q[4]) oraz współrzędne położenia (q[1],q[3],q[5]).
     * @param deltaQ Druga tablica zawierająca wartości prędkości(deltaQ[0],deltaQ[2],deltaQ[4])
     *               oraz współrzędne położenia (deltaQ[1],deltaQ[3],deltaQ[5]).
     * @param dt Czas (w sekundach), który upłynął między 2 ostatnimi klatkami.
     * @param qScale Wartość skalująca wartości z tablicy deltaQ.
     * @param accelerometrData Wektor przyspieszenia grawitacyjnego.
     * @param mu Wartość współczynnika tarcia dla powierzchni, na której znajduje się kulka.
     * @return Nowe wartości prędkości (q[0],q[2],q[4]) i współrzędne położenia (q[1],q[3],q[5]).
     */
    private float[] SolveEquation(float[] q, float deltaQ[], float dt, float qScale, Vector accelerometrData,float mu) {


        float dQ[] = new float[6];
        float newQ[] = new float[6];
        for (int i = 0; i < 6; ++i) {
            newQ[i] = q[i] + qScale * deltaQ[i];
        }
        Vector localVelocity = new Vector(newQ[0], newQ[2], newQ[4]);
        Vector acceleration;
        if (mu < 0)
            acceleration = CountAccelerationForFlying(accelerometrData, localVelocity);
        else acceleration = CountAccelerationForRolling(accelerometrData, mu, localVelocity);

        dQ[0] = dt * (acceleration.x);
        dQ[1] = dt * localVelocity.x;
        dQ[2] = dt * (acceleration.y);
        dQ[3] = dt * localVelocity.y;
        dQ[4] = dt * (acceleration.z);
        dQ[5] = dt * localVelocity.z;
        return dQ;
    }

    /**
     * Oblicza wektor przyspieszenia dla kulki znajdującej się na windzie lub podłodze.
     * @param accData Wektor przyspieszenia grawitacyjnego.
     * @param mu Wartość współczynnika tarcia dla powierzchni windy lub podłogi.
     * @param actualVelocity Wektor aktulanej prędkości kulki.
     * @return Wektor przyspieszenia dla kulki znajdującej się na windzie lub podłodze.
     */
    private Vector CountAccelerationForRolling(Vector accData,float mu,Vector actualVelocity) {
        float v = (float) (Math.sqrt(actualVelocity.x * actualVelocity.x + actualVelocity.y * actualVelocity.y + actualVelocity.z * actualVelocity.z) + 1e-8);
        float Fd = 0.5f * DENSITY * area * CD * v * v;
        float Fr = mu * Math.abs(accData.y) * mass;
        Vector acceleration = new Vector(
                accData.x - (Fd + Fr) * actualVelocity.x / (mass * v),
                0,
                accData.z - (Fd + Fr) * actualVelocity.z / (mass * v));
        return acceleration;
    }

    /**
     * Oblicza wektor przyspieszenia dla kulki znajdującej się w powietrzu.
     * @param accData Wektor przyspieszenia grawitacyjnego.
     * @param actualVelocity Wektor aktulanej prędkości kulki.
     * @return Wektor przyspieszenia dla kulki znajdującej się w powietrzu.
     */
    private Vector CountAccelerationForFlying(Vector accData,Vector actualVelocity) {
        float v = (float) (Math.sqrt(actualVelocity.x * actualVelocity.x + actualVelocity.y * actualVelocity.y + actualVelocity.z * actualVelocity.z) + 1e-8);
        float Fd = 0.5f * DENSITY * area * CD * v * v;

        Vector acceleration = new Vector(
                accData.x - (Fd) * actualVelocity.x / (mass * v),
                accData.y - (Fd) * actualVelocity.y / (mass * v),
                accData.z - (Fd) * actualVelocity.z / (mass * v));
        return acceleration;
    }


    /**
     * Sprawdza, czy występuje kolizja pomiędzy kulą a ścianą.
     * @param element Ściana, z którą kolizja jest sprawdzana.
     * @return Informacja, czy kulka i ściana kolidują ze sobą.
     */
    public boolean CheckCollision(Wall element){
        return Collisions.CheckSphereAABBCollision(new Sphere(location, radius), new Box(element.getLocation(), element.getMeasurements()));
    }

    /**
     * Sprawdza, czy występuje kolizja pomiędzy kulą a podłogą.
     * @param element Podłoga, z którą kolizja jest sprawdzana.
     * @return Informacja, czy kulka i podłoga kolidują ze sobą.
     */
    public boolean CheckCollision(Floor element){
        return Collisions.CheckSphereAABBCollision(new Sphere(location, radius), new Box(element.getLocation(), element.getMeasurements()));
    }

    /**
     * Sprawdza, czy występuje kolizja pomiędzy kulą a diamentem.
     * @param diamond Diament, z którym kolizja jest sprawdzana.
     * @return Informacja, czy kulka i diament kolidują ze sobą.
     */
    public boolean CheckCollision(Diamond diamond) {
        return Collisions.CheckSphereCylinderCollision(new Sphere(location, radius), new Cylinder(diamond.location, diamond.getPyramid().radius, 2 * diamond.getPyramid().height));
    }

    /**
     * Sprawdza, czy występuje kolizja pomiędzy kulą a klepsydrą.
     * @param hourGlass Klepsydra, z którą kolizja jest sprawdzana.
     * @return Informacja, czy kulka i klepsydra kolidują ze sobą.
     */
    public boolean CheckCollision(HourGlass hourGlass) {
        return Collisions.CheckSphereCylinderCollision(new Sphere(location, radius), new Cylinder(hourGlass.location, hourGlass.getLowerCone().bottomRadius, 2 * hourGlass.getLowerCone().height));
    }

    /**
     * Sprawdza, czy kulka znajduje się w elemencie mety.
     * @param finish Punkt mety, z którym kolizja jest sprawdzana.
     * @return Informacja, czy kulka znajduje się w elemencie mety.
     */
    public boolean CheckCollision(Finish finish) {
        if (finish == null) return false;
        return Collisions.CheckSphereCircleCollision(new Sphere(location, radius),
        new Circle(finish.getLocation(),finish.conicalFrustum.bottomRadius));
   //     return Collisions.CheckSphereCylinderCollision(, new Cylinder(finish.getLocation(),
   //             Math.min(finish.conicalFrustum.bottomRadius, finish.conicalFrustum.topRadius), finish.conicalFrustum.height));
    }

    /**
     * Sprawdza, czy kulka znajduje się w elemencie punktu kontrolnego.
     * @param checkPoint Punkt kontrolny, z którym kolizja jest sprawdzana.
     * @return Informacja, czy kulka znajduje się w elemencie punktu kontrolnego.
     */
    public boolean CheckCollision(CheckPoint checkPoint) {
        if (checkPoint == null) return false;
        return Collisions.CheckSphereCircleCollision(new Sphere(location, radius),
                new Circle(checkPoint.getLocation(),checkPoint.conicalFrustum.bottomRadius));
        //    return Collisions.CheckSphereCylinderCollision(new Sphere(location, radius), new Cylinder(checkPoint.getLocation(),
        //           Math.min(checkPoint.conicalFrustum.bottomRadius, checkPoint.conicalFrustum.topRadius), checkPoint.conicalFrustum.height));
    }

    /**
     * Sprawdza, czy występuje kolizja pomiędzy kulą a belką.
     * @param element Belka, z którą kolizja jest sprawdzana.
     * @return Informacja, czy kulka i belka kolidują ze sobą.
     */
    public boolean CheckCollision(Beam element){
        return Collisions.CheckSphereAABBCollision(new Sphere(location, radius), new Box(element.getLocation(), element.getMeasurements()));
    }

    /**
     * Sprawdza, czy występuje kolizja pomiędzy kulą a windą.
     * @param element Winda, z którą kolizja jest sprawdzana.
     * @return Informacja, czy kulka i winda kolidują ze sobą.
     */
    public boolean CheckCollision(Elevator element){
        return Collisions.CheckSphereAABBCollision(new Sphere(location, radius), new Box(element.getLocation(), element.getMeasurements()));
    }

    /**
     * Rozwiązuje sytuację kolizji kulki ze ścianą.
     * @param element Ściana, z którą kolizja jest rozwiązywana.
     */
    public void ReactOnCollision(Wall element)  {
        Collisions.ResponseBallAABBCollisions(this,new Box(element.getLocation(),element.getMeasurements()));
    }

    /**
     * Rozwiązuje sytuację kolizji kulki z podłogą.
     * @param element Podłoga, z którą kolizja jest rozwiązywana.
     */
    public void ReactOnCollision(Floor element)   {
        Collisions.ResponseBallAABBCollisions(this,new Box(element.getLocation(),element.getMeasurements()));
    }

    /**
     * Rozwiązuje sytuację kolizji kulki z belką.
     * @param beam Belka, z którą kolizja jest rozwiązywana.
     */
    public void ReactOnCollision(Beam beam) {
        Collisions.ResponseBallMovingAABBCollisions(this, beam);
    }

    /**
     * Rozwiązuje sytuację kolizji kulki z windą.
     * @param elevator Winda, z którą kolizja jest rozwiązywana.
     */
    public void ReactOnCollision(Elevator elevator) {
        Collisions.ResponseBallMovingAABBCollisions(this, elevator);
    }

    /**
     * Zwraca identyfikator OpenGL dla danego typu tekstury kulki.
     * @param ballTextureType Typ tekstury kulki.
     * @param context Bieżący kontekst pozwalający uzyskać dostęp do zasobów aplikacji.
     * @return Identyfikator OpenGL dla danej tekstury.
     */
    private static int loadTexture(BallTexture ballTextureType, Context context){
        switch (ballTextureType){
            case redAndWhite:
                return ResourceHelper.loadTexture(context, R.drawable.ball_texture_red_white_dots);
            case noise:
                return ResourceHelper.loadTexture(context, R.drawable.ball_texture_noise);
            case beach:
                return ResourceHelper.loadTexture(context, R.drawable.ball_texture_beachball);
            case lava:
                return ResourceHelper.loadTexture(context, R.drawable.ball_texture_lava);
            case sun:
                return ResourceHelper.loadTexture(context, R.drawable.ball_texture_sun);
            case jelly:
                return ResourceHelper.loadTexture(context, R.drawable.ball_texture_jelly);
            case marble:
                return ResourceHelper.loadTexture(context, R.drawable.ball_texture_marble);
            case frozen:
                return ResourceHelper.loadTexture(context, R.drawable.ball_texture_frozen);
            case tiger:
                return ResourceHelper.loadTexture(context, R.drawable.ball_texture_tiger);
            case orangeSkin:
                return ResourceHelper.loadTexture(context, R.drawable.ball_texture_orange_skin);
            case amethystAlcove:
                return ResourceHelper.loadTexture(context, R.drawable.ball_texture_amethyst_alcove);
            case drizzledPaint:
                return ResourceHelper.loadTexture(context, R.drawable.ball_texture_drizzled_paint);
            case eyeOfTheSunGod:
                return ResourceHelper.loadTexture(context, R.drawable.ball_texture_eye_of_the_sun_god);
            case girlsBestFriend:
                return ResourceHelper.loadTexture(context, R.drawable.ball_texture_girls_best_friend);
            case homeWorld:
                return ResourceHelper.loadTexture(context, R.drawable.ball_texture_home_world);
            case jupiter:
                return ResourceHelper.loadTexture(context, R.drawable.ball_texture_jupiter);
            case liquidCrystal:
                return ResourceHelper.loadTexture(context, R.drawable.ball_texture_liquid_crystal);
            case methaneLakes:
                return ResourceHelper.loadTexture(context, R.drawable.ball_texture_methane_lakes);
            case spottedBianco:
                return ResourceHelper.loadTexture(context, R.drawable.ball_texture_spotted_bianco);
            case toxicByproduct:
                return ResourceHelper.loadTexture(context, R.drawable.ball_texture_toxic_byproduct);
            case verdeJaspe:
                return ResourceHelper.loadTexture(context, R.drawable.ball_texture_verde_jaspe);
            case dyedStonework:
                return ResourceHelper.loadTexture(context, R.drawable.ball_texture_dyed_stonework);

        }
        return -1;
    }

    /**
     * Inicjuje wartość identyfikatora OpenGL tekstury kulki.
     * @param context Bieżący kontekst pozwalający uzyskać dostęp do zasobów aplikacji.
     */
    public static void initTextures(Context context){
        ballTextureId = loadTexture(ballTextureType, context);
    }
}
