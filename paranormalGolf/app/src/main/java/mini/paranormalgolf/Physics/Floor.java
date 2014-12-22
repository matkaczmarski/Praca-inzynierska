package mini.paranormalgolf.Physics;

import android.content.Context;

import java.util.Arrays;
import java.util.List;

import mini.paranormalgolf.Graphics.ModelBuilders.ObjectBuilder;
import mini.paranormalgolf.Graphics.ShaderPrograms.ShaderProgram;
import mini.paranormalgolf.Graphics.ShaderPrograms.TextureShaderProgram;
import mini.paranormalgolf.Helpers.ResourceHelper;
import mini.paranormalgolf.Primitives.BoxSize;
import mini.paranormalgolf.Primitives.Point;
import mini.paranormalgolf.Primitives.Rectangle;
import mini.paranormalgolf.R;

/**
 * Created by Sławomir on 2014-12-03.
 */
public class Floor extends Element {

    public final float FLOOR_OPACITY = 1f;
    protected final int STRIDE = (POSITION_COMPONENT_COUNT + TEXTURE_COMPONENT_COUNT) * 4;

    public BoxSize measurements;
    public float mu;

    private FloorPart topPart;
    private FloorPart bottomPart;
    private List<FloorPart> sideParts;

    private int topFloorTexture;
    private int sideFloorTexture;
    private int bottomFloorTexture;

    public  FloorPart getTopPart(){return topPart;}
    public FloorPart getBottomPart(){return  bottomPart;}
    public List<FloorPart> getSideParts(){return sideParts;}
    public int getTopFloorTexture(){return topFloorTexture;}
    public  int getSideFloorTexture() {return  sideFloorTexture;}
    public  int getBottomFloorTexture() {return  bottomFloorTexture;}


    public Floor(BoxSize measures, float mu, Point location, Context context) {
        super(location);
        this.measurements = measures;
        this.mu = mu;
        createFloor(measures, location);

        topFloorTexture = ResourceHelper.loadTexture(context, R.drawable.top_floor_texture);
        sideFloorTexture = ResourceHelper.loadTexture(context, R.drawable.side_floor_texture);
        bottomFloorTexture = ResourceHelper.loadTexture(context, R.drawable.bottom_floor_texture);

    }

    private void createFloor(BoxSize measures, Point location) {
        topPart = new FloorPart(new Rectangle(new Point(location.X, location.Y + measures.y / 2, location.Z), measures.x, measures.z), ObjectBuilder.Axis.yAxis, 1);
        bottomPart = new FloorPart(new Rectangle(new Point(location.X, location.Y - measures.y / 2, location.Z), measures.x, measures.z), ObjectBuilder.Axis.yAxis, -1);

        FloorPart sidePart1 = new FloorPart(new Rectangle(new Point(location.X + measures.x / 2, location.Y, location.Z), measures.y, measures.z), ObjectBuilder.Axis.xAxis, 1);
        FloorPart sidePart2 = new FloorPart(new Rectangle(new Point(location.X - measures.x / 2, location.Y, location.Z), measures.y, measures.z), ObjectBuilder.Axis.xAxis, -1);
        FloorPart sidePart3 = new FloorPart(new Rectangle(new Point(location.X, location.Y, location.Z + measures.z / 2), measures.x, measures.y), ObjectBuilder.Axis.zAxis, 1);
        FloorPart sidePart4 = new FloorPart(new Rectangle(new Point(location.X, location.Y, location.Z - measures.z / 2), measures.x, measures.y), ObjectBuilder.Axis.zAxis, -1);

        sideParts = Arrays.asList(sidePart1, sidePart2, sidePart3, sidePart4);
    }

    public void bindData(ShaderProgram colorProgram) {
    }

}
