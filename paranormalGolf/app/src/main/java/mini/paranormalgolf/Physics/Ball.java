package mini.paranormalgolf.Physics;

import mini.paranormalgolf.Primitives.Point;
import mini.paranormalgolf.Primitives.Vector;

/**
 * Created by Sławomir on 2014-12-03.
 */
public class Ball extends MovableElement {

    private float R;
    private float omega;
    private Vector axis;
    private float mass;
    private float area;
    private float Cd;
    private float density;

    public Ball(Vector _velocity, Point _location) {
        super(_velocity, _location);
        R=2;
        omega=0;
        axis=new Vector(0,0,1);
        mass=5;
        area=(float)Math.PI*R*R;
        Cd=1.2f;
        density=1.3f;
    }

    public void Update(float dt,Vector accelerometrData){
        //Update związany z poruszeniem się elementu

        //jeśli jest na powierzchni to liczymy następująco
        //TODO

        //jeśli jest w powietrzu, to liczymy następująco:
        //TODO
    }

    public boolean CheckCollision(MovableElement element){
        return false;
    }
  //  public boolean CheckCollision(Bonus element){
   //     return false;
   // }
    public boolean CheckCollision(Wall element) {
        return false;
    }

    public void ReactOnCollision(MovableElement element){

    }

 //   public void ReactOnCollision(Bonus element){
//
 //   }

    public void ReactOnCollision(Wall element){

    }
}
