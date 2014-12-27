package mini.paranormalgolf.Graphics;

import mini.paranormalgolf.Primitives.Point;

/**
 * Created by Mateusz on 2014-12-27.
 */
public class LightData{
    public Point position;
    public float ambient;
    public float diffusion;

    public LightData(Point position, float ambient, float diffusion){
        this.position = position;
        this.ambient = ambient;
        this.diffusion = diffusion;
    }
}
