package mini.paranormalgolf.Physics;

import mini.paranormalgolf.Primitives.Point;

/**
 * Created by Sławomir on 2014-12-03.
 */
public abstract class Element {

    protected Point location;
    protected float[] vertexData;
    //protected List<DrawCommand> drawCommands;
    protected Element(Point _location){
     location=_location;
    }
}
