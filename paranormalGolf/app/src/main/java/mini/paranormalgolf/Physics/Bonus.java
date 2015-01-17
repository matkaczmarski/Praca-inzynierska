package mini.paranormalgolf.Physics;

import mini.paranormalgolf.Primitives.Point;

/**
 * Klasa abstrakcyjna reprezentująca bonus w grze. Dla uatrakcyjnienia wyglądu elementy
 * te jednocześnie poruszają się wzdłuż osi OY a także obracają wzdłuż osi OY
 */
public abstract class Bonus extends Element {

    /**
     * Wartość prędkości kątowej elementu podczas animacji ruchu bonusu
     */
    protected float rotationSpeed = 1f;

    /**
     * Wartość prędkości postępowej wzdłuż osi OY podczas animacji ruchu bonusu
     */
    protected float upDownSpeed = 0.002f;

    /**
     * Minimalna wartość współrzędnej y środka bonusu podczas animacji ruchu
     */
    protected float yShiftFrom;

    /**
     * Maksymalna wartość współrzędnej y środka bonusu podczas animacji ruchu
     */
    protected float yShiftTo;

    /**
     * Aktualna wartość kąta obrotu bonusu wzdłuż osi OY względem położenia początkowego bonusu
     */
    private float currentRotationAngle;

    /**
     * Wartość bonusu związanego z elementem
     */
    private int value;


    /**
     * Odświeża i zwraca współrzędne środka bonusu w globalnym układzie współrzędnych
     * @return Aktualne współrzędne środka bonusu w globalnym układzie współrzędnych
     */
    @Override
    public Point getLocation(){return lift();}

    /**
     * Zwraca wartość bonusu związanego z elementem
     * @return Wartość bonusu związanego z elementem
     */
    public int getValue()
    {
        return value;
    }

    /**
     * Tworzy obiekt typu bonus
     * @param location Współrzędne środka bonusu w globalnym układzie współrzędnych
     * @param value Wartość bonusu związanego z zebraniem elementu
     * @param yShift Wartość o jaką wzdłuż osi OY można podnosić element podczas animacji ruchu
     */
    public Bonus(Point location, int value, float yShift) {
        super(location);
        this.value = value;
        this.yShiftFrom = location.y;
        this.yShiftTo = location.y + yShift;
        currentRotationAngle = 0f;
    }

    /**
     * Odświeża i zwraca wartość kąta obrotu bonusu wzdłuż osi OY względem położenia początkowego bonusu
     * @return Aktualna wartość kąta obrotu bonusu wzdłuż osi OY względem położenia początkowego bonusu
     */
    public float rotate(){
        currentRotationAngle = (currentRotationAngle + rotationSpeed) % 360f;
        return  currentRotationAngle;
    }

    /**
     * Odświeża współrzędne środka bonusu i wartość prędkości postępowej wzdłuż osi OY
     * w globalnym układzie współrzędnych
     * @return Aktualne współrzędne środka bonusu w globalnym układzie współrzędnych
     */
    private Point lift(){
        if(location.y < yShiftFrom || location.y >= yShiftTo)
            upDownSpeed *= (-1f);
        location = new Point(location.x, location.y + upDownSpeed, location.z);
        return location;
    }
}
