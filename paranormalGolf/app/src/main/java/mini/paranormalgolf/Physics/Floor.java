package mini.paranormalgolf.Physics;

import android.content.Context;

import java.util.Arrays;
import java.util.List;

import mini.paranormalgolf.Graphics.ModelBuilders.ObjectBuilder;
import mini.paranormalgolf.Graphics.ShaderPrograms.ShaderProgram;
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

    public BoxSize getMeasurements() {
        return measures;
    }

    public void setMeasures(BoxSize measures) {
        this.measures = measures;
    }

    public BoxSize measures;
    public float mu;

    private FloorPart topPart;
    private FloorPart bottomPart;
    private List<FloorPart> sideParts;

    private final int topFloorTexture;
    private final  int sideFloorTexture;
    private final int bottomFloorTexture;

    public FloorPart getTopPart() {
        return topPart;
    }

    public FloorPart getBottomPart() {
        return bottomPart;
    }

    public List<FloorPart> getSideParts() {
        return sideParts;
    }

    public int getTopFloorTexture() {
        return topFloorTexture;
    }

    public int getSideFloorTexture() {
        return sideFloorTexture;
    }

    public int getBottomFloorTexture() {
        return bottomFloorTexture;
    }


    public float getFloorTop() {
        return this.location.y + this.measures.y / 2;
    }

    public Floor(BoxSize measures, float mu, Point location, Context context) {
        super(location);
        this.measures = measures;
        this.mu = mu;
        createFloor(measures, location);

        //ZMIENCIE TO, numer od 0 do 9
        topFloorTexture = ResourceHelper.loadTexture(context, R.drawable.new_floor_texture2);




//        topFloorTexture = ResourceHelper.loadTexture(context, R.drawable.floor_texture_top);
        sideFloorTexture = ResourceHelper.loadTexture(context, R.drawable.floor_texture_bottom);
        //sideFloorTexture = ResourceHelper.loadTexture(context, R.drawable.floor_texture_side);
        bottomFloorTexture = ResourceHelper.loadTexture(context, R.drawable.floor_texture_bottom);

    }

    private void createFloor(BoxSize measures, Point location) {
        topPart = new FloorPart(new Rectangle(new Point(location.x, location.y + measures.y / 2, location.z), measures.x, measures.z), ObjectBuilder.Axis.yAxis, 1);
        bottomPart = new FloorPart(new Rectangle(new Point(location.x, location.y - measures.y / 2, location.z), measures.x, measures.z), ObjectBuilder.Axis.yAxis, -1);

        FloorPart rightSidePart = new FloorPart(new Rectangle(new Point(location.x + measures.x / 2, location.y, location.z), measures.y, measures.z), ObjectBuilder.Axis.xAxis, 1);
        FloorPart leftSidePart = new FloorPart(new Rectangle(new Point(location.x - measures.x / 2, location.y, location.z), measures.y, measures.z), ObjectBuilder.Axis.xAxis, -1);
        FloorPart frontSidePart = new FloorPart(new Rectangle(new Point(location.x, location.y, location.z + measures.z / 2), measures.x, measures.y), ObjectBuilder.Axis.zAxis, 1);
        FloorPart backSidePart = new FloorPart(new Rectangle(new Point(location.x, location.y, location.z - measures.z / 2), measures.x, measures.y), ObjectBuilder.Axis.zAxis, -1);

        sideParts = Arrays.asList(rightSidePart, leftSidePart, frontSidePart, backSidePart);
    }

    public void bindShadowData(ShaderProgram shaderProgram) {
    }

    public void bindData(ShaderProgram shaderProgram) {
    }

}
