package mini.paranormalgolf.Graphics;

import mini.paranormalgolf.Primitives.Point;

/**
 * Created by Mateusz on 2014-12-27.
 */
public class LightData{
    public Point position;
    public float ambient;
    public float diffusion;

    public LightData( float ambient, float diffusion){
        this.position = new Point(0f,0f,0f);
        this.ambient = ambient;
        this.diffusion = diffusion;
    }
}
