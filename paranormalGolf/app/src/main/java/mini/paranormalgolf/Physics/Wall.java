package mini.paranormalgolf.Physics;

import mini.paranormalgolf.Graphics.ShaderPrograms.ShaderProgram;
import mini.paranormalgolf.Primitives.BoxSize;
import mini.paranormalgolf.Primitives.Point;

/**
 * Created by Sławomir on 2014-12-03.
 */
public class Wall extends Element {
    private BoxSize measurements;

    public Wall(BoxSize measure,Point location) {
        super(location);
        measurements = measure;
    }

    public void bindData(ShaderProgram shaderProgram){
        //TODO dodać bindowanie Z texture program
    }

}
