package mini.paranormalgolf.Physics;

import mini.paranormalgolf.Primitives.Box;
import mini.paranormalgolf.Primitives.BoxSize;
import mini.paranormalgolf.Primitives.Circle;
import mini.paranormalgolf.Primitives.Cylinder;
import mini.paranormalgolf.Primitives.Point;
import mini.paranormalgolf.Primitives.Sphere;
import mini.paranormalgolf.Primitives.Vector;

/**
 * Created by Sławomir on 2014-12-29.
 */
public final class Collisions {

    public final static float USER_EXPERIENCE = 4e-4f;

    public static boolean CheckSphereAABBCollsion(Sphere sphere, Box box) {

        Point boxCenter = box.center;
        BoxSize boxSize = box.size;
        Point sphereCenter = sphere.center;
        float sphereRadius = sphere.radius;
        Point min = new Point(boxCenter.x - boxSize.x / 2, boxCenter.y - boxSize.y / 2, boxCenter.z - boxSize.z / 2);
        Point max = new Point(boxCenter.x + boxSize.x / 2, boxCenter.y + boxSize.y / 2, boxCenter.z + boxSize.z / 2);
        float d = 0;
        if (sphereCenter.x < min.x) {
            d += (sphereCenter.x - min.x) * (sphereCenter.x - min.x);
        } else if (sphereCenter.x > max.x) {
            d += (sphereCenter.x - max.x) * (sphereCenter.x - max.x);
        }

        if (sphereCenter.y < min.y) {
            d += (sphereCenter.y - min.y) * (sphereCenter.y - min.y);
        } else if (sphereCenter.y > max.y) {
            d += (sphereCenter.y - max.y) * (sphereCenter.y - max.y);
        }

        if (sphereCenter.z < min.z) {
            d += (sphereCenter.z - min.z) * (sphereCenter.z - min.z);
        } else if (sphereCenter.z > max.z) {
            d += (sphereCenter.z - max.z) * (sphereCenter.z - max.z);
        }
        return d <= sphereRadius * sphereRadius;// + USER_EXPERIENCE;
    }

    public static void ResponseBallAABBCollisions(Ball ball, Box collidedBox) {

        //finding collision place and time
        Point startLocation = new Point(ball.getLocation().x - ball.getLastMove().x, ball.getLocation().y - ball.getLastMove().y, ball.getLocation().z - ball.getLastMove().z);
        Point endLocation = ball.getLocation();
        Point halfLocation = new Point((startLocation.x + endLocation.x) / 2, (startLocation.y + endLocation.y) / 2, (startLocation.z + endLocation.z) / 2);
        float startTime = 0, endTime = 1, halfTime = 0.5f;
        for (int i = 0; i < 10; i++) {
            if (Collisions.CheckSphereAABBCollsion(new Sphere(halfLocation, ball.getRadius()), collidedBox)) {
                endTime = halfTime;
                endLocation = halfLocation;
            } else {
                startTime = halfTime;
                startLocation = halfLocation;
            }
            halfTime = (startTime + endTime) / 2;
            halfLocation = new Point((startLocation.x + endLocation.x) / 2, (startLocation.y + endLocation.y) / 2, (startLocation.z + endLocation.z) / 2);
        }

        //finding normal vector in place of collision
        Vector normal = new Vector(0, 0, 0);
        Point min = new Point(collidedBox.center.x - collidedBox.size.x / 2, collidedBox.center.y - collidedBox.size.y / 2, collidedBox.center.z - collidedBox.size.z / 2);
        Point max = new Point(collidedBox.center.x + collidedBox.size.x / 2, collidedBox.center.y + collidedBox.size.y / 2, collidedBox.center.z + collidedBox.size.z / 2);

        if (min.x - halfLocation.x > 0 && min.x - halfLocation.x <= ball.getRadius() + Collisions.USER_EXPERIENCE)
            normal.x = -1;
        else if (halfLocation.x - max.x > 0 && halfLocation.x - max.x <= ball.getRadius() + Collisions.USER_EXPERIENCE)
            normal.x = 1;

        if (min.y - halfLocation.y > 0 && min.y - halfLocation.y <= ball.getRadius() + Collisions.USER_EXPERIENCE)
            normal.y = -1;
        else if (halfLocation.y - max.y > 0 && halfLocation.y - max.y <= ball.getRadius() + Collisions.USER_EXPERIENCE)
            normal.y = 1;

        if (min.z - halfLocation.z > 0 && min.z - halfLocation.z <= ball.getRadius() + Collisions.USER_EXPERIENCE)
            normal.z = -1;
        else if (halfLocation.z - max.z > 0 && halfLocation.z - max.z <= ball.getRadius() + Collisions.USER_EXPERIENCE)
            normal.z = 1;

        //counting and setting new location and velocity after collision
        Vector velocity = ball.getVelocity();
        Vector normalVelocity;
        Vector newNormalVelocity = new Vector(0, 0, 0);

        if (normal.length() == 1) {
            if (Math.abs(normal.x) == 1) velocity.x = -velocity.x;
            else if (Math.abs(normal.z) == 1) velocity.z = -velocity.z;
            else // normal.y!=0
            {
                if (normal.y > 0) {
                    halfLocation.y = collidedBox.center.y + collidedBox.size.y / 2 + ball.getRadius();
                    if (Math.abs(velocity.y) > 2)
                        velocity.y = -0.1f * velocity.y;
                    else
                        velocity.y = 0;
                }
            }
        } else {
            if (normal.absSum() == 3) normal.y = 0;
            normal = normal.normalize();

            if (normal.y == 0) {
                normalVelocity = new Vector(-velocity.x, 0, -velocity.z).normalize();
                if (Math.abs(normalVelocity.z * normal.x - normal.z * normalVelocity.x) < Collisions.USER_EXPERIENCE)
                    velocity = new Vector(-velocity.x, velocity.y, -velocity.z);
                else {
                    float alfa = (float) Math.acos(normal.dotProduct(normalVelocity));

                    newNormalVelocity.z =
                            ((float) (normal.x * Math.cos(2 * alfa) - Math.cos(alfa) * normalVelocity.x)) /
                                    (normalVelocity.z * normal.x - normal.z * normalVelocity.x);
                    newNormalVelocity.x =
                            ((float) Math.cos(alfa) - newNormalVelocity.z * normal.z) / (normal.x);
                    float length = (float) Math.sqrt(velocity.x * velocity.x + velocity.z * velocity.z);
                    velocity = new Vector(length * newNormalVelocity.x, velocity.y, length * newNormalVelocity.z);
                }
            } else if (normal.x == 0) {
                normalVelocity = new Vector(0, -velocity.y, -velocity.z).normalize();
                if (Math.abs(normalVelocity.z * normal.y - normal.z * normalVelocity.y) < Collisions.USER_EXPERIENCE)
                    velocity = new Vector(velocity.x, -velocity.y, -velocity.z);
                else {
                    float alfa = (float) Math.acos(normal.dotProduct(normalVelocity));

                    newNormalVelocity.z =
                            ((float) (normal.y * Math.cos(2 * alfa) - Math.cos(alfa) * normalVelocity.y)) /
                                    (normalVelocity.z * normal.y - normal.z * normalVelocity.y);
                    newNormalVelocity.y =
                            ((float) Math.cos(alfa) - newNormalVelocity.z * normal.z) / (normal.y);
                    // newNormalVelocity.x = normalVelocity.x;
                    float length = (float) Math.sqrt(velocity.y * velocity.y + velocity.z * velocity.z);
                    velocity = new Vector(velocity.x, length * newNormalVelocity.y, length * newNormalVelocity.z);
                }
            } else { //normal.z==0
                normalVelocity = new Vector(-velocity.x, -velocity.y, 0).normalize();
                if (Math.abs(normalVelocity.y * normal.x - normal.y * normalVelocity.x) < Collisions.USER_EXPERIENCE)
                    velocity = new Vector(-velocity.x, -velocity.y, velocity.z);
                else {
                    float alfa = (float) Math.acos(normal.dotProduct(normalVelocity));

                    newNormalVelocity.y =
                            ((float) (normal.x * Math.cos(2 * alfa) - Math.cos(alfa) * normalVelocity.x)) /
                                    (normalVelocity.y * normal.x - normal.y * normalVelocity.x);
                    newNormalVelocity.x =
                            ((float) Math.cos(alfa) - newNormalVelocity.y * normal.y) / (normal.x);
                    // newNormalVelocity.z = normalVelocity.x;
                    float length = (float) Math.sqrt(velocity.x * velocity.x + velocity.y * velocity.y);
                    velocity = new Vector(length * newNormalVelocity.x, length * newNormalVelocity.y, velocity.z);
                }
            }
        }

        ball.setLocation(new Point(halfLocation.x + velocity.x * (1 - halfTime) * Updater.INTERVAL_TIME,
                halfLocation.y + velocity.y * (1 - halfTime) * Updater.INTERVAL_TIME,
                halfLocation.z + velocity.z * (1 - halfTime) * Updater.INTERVAL_TIME));
        ball.setVelocity(velocity);
        if (CheckSphereAABBCollsion(new Sphere(ball.getLocation(), ball.getRadius()), collidedBox)) {
            Point newBallLocation = ball.getLocation();
//            if(element.getLastMove().x!=0||element.getLastMove().z!=0) {
//
//                Vector distance = element.getLastMove().x!=0?new Vector(element.getLastMove().x, 0, normal.z * element.getLastMove().x / normal.x):
//                        new Vector(normal.x * element.getLastMove().z / normal.z,0,element.getLastMove().z);
//                newBallLocation.x += distance.x;
//                newBallLocation.z += distance.z;
//                for (int i = 0; i < 10; i++) {
//                    distance.x /= 2;
//                    distance.z /= 2;
//                    newBallLocation.x -= distance.x;
//                    newBallLocation.z -= distance.z;
//                    if (CheckSphereAABBCollsion(new Sphere(newBallLocation, ball.getRadius()), new Box(element.getLocation(), boxSize))) {
//                        newBallLocation.x += distance.x;
//                        newBallLocation.z += distance.z;
//                    }
//                }
//
//            }
//            else{
            Vector distance = new Vector(0, 0, 0);
            if (min.x - newBallLocation.x > 0 && min.x - newBallLocation.x <= ball.getRadius() + Collisions.USER_EXPERIENCE)
                distance.x = newBallLocation.x - min.x;
            else if (newBallLocation.x - max.x > 0 && newBallLocation.x - max.x <= ball.getRadius() + Collisions.USER_EXPERIENCE)
                distance.x = newBallLocation.x - max.x;
            if (min.z - newBallLocation.z > 0 && min.z - newBallLocation.z <= ball.getRadius() + Collisions.USER_EXPERIENCE)
                distance.z = newBallLocation.z - min.z;
            else if (newBallLocation.z - max.z > 0 && newBallLocation.z - max.z <= ball.getRadius() + Collisions.USER_EXPERIENCE)
                distance.z = newBallLocation.z - max.z;
            distance.x = Math.signum(normal.x) * ball.getRadius();
            distance.z = Math.signum(normal.z) * ball.getRadius();
            newBallLocation.x += distance.x;
            newBallLocation.z += distance.z;
            for (int i = 0; i < 10; i++) {
                distance.x /= 2;
                distance.z /= 2;
                newBallLocation.x -= distance.x;
                newBallLocation.z -= distance.z;
                if (CheckSphereAABBCollsion(new Sphere(newBallLocation, ball.getRadius()), collidedBox)) {
                    newBallLocation.x += distance.x;
                    newBallLocation.z += distance.z;
                }
            }
            //}
            ball.setLocation(newBallLocation);
        }
    }

    public static boolean CheckSphereCylinderCollsion(Sphere sphere, Cylinder cylinder) {
        Point cylinderCenter = cylinder.center;
        float cylinderRadius = cylinder.radius;
        float cylinderMinY = cylinderCenter.y - cylinder.height/ 2;
        float cylinderMaxY = cylinderCenter.y + cylinder.height / 2;
        Point sphereCenter = sphere.center;
        float sphereRadius = sphere.radius;
        float d = 0;
        if (sphereCenter.y < cylinderMinY) {
            d += (sphereCenter.y - cylinderMinY) * (sphereCenter.y - cylinderMinY);
        } else if (sphereCenter.y > cylinderMaxY) {
            d += (sphereCenter.y - cylinderMaxY) * (sphereCenter.y - cylinderMaxY);
        }
        d += (sphereCenter.x - cylinderCenter.x) * (sphereCenter.x - cylinderCenter.x) + (sphereCenter.z - cylinderCenter.z) * (sphereCenter.z - cylinderCenter.z);
        return d < (sphereRadius + cylinderRadius) * (sphereRadius + cylinderRadius) + USER_EXPERIENCE;
    }

    public static boolean CheckSphereCircleCollision(Sphere sphere,Circle circle) {
        return (sphere.center.x - circle.center.x) * (sphere.center.x - circle.center.x) + (sphere.center.z - circle.center.z) * (sphere.center.z - circle.center.z) <= circle.radius * circle.radius
                && (sphere.center.y - circle.center.y - sphere.radius) < USER_EXPERIENCE;
    }

    public static void ResponseBallMovingAABBCollisions(Ball ball, MovableElement element) {
        BoxSize boxSize;
        if (element.getClass() == Elevator.class)
            boxSize = ((Elevator) element).getMeasurements();
        else
            boxSize = ((Beam) element).getMeasurements();

        //finding collision place and time
        Point startBallLocation = new Point(ball.getLocation().x - ball.getLastMove().x, ball.getLocation().y - ball.getLastMove().y, ball.getLocation().z - ball.getLastMove().z);
        Point endBallLocation = ball.getLocation();
        Point halfBallLocation = new Point((startBallLocation.x + endBallLocation.x) / 2, (startBallLocation.y + endBallLocation.y) / 2, (startBallLocation.z + endBallLocation.z) / 2);

        Point startMovableElementLocation = new Point(element.getLocation().x - element.getLastMove().x, element.getLocation().y - element.getLastMove().y, element.getLocation().z - element.getLastMove().z);
        Point endMovableElementLocation = element.getLocation();
        Point halfMovableElementLocation = new Point((startMovableElementLocation.x + endMovableElementLocation.x) / 2, (startMovableElementLocation.y + endMovableElementLocation.y) / 2, (startMovableElementLocation.z + endMovableElementLocation.z) / 2);

        float startTime = 0, endTime = 1, halfTime = 0.5f;
        for (int i = 0; i < 10; i++) {
            if (Collisions.CheckSphereAABBCollsion(new Sphere(halfBallLocation, ball.getRadius()), new Box(halfMovableElementLocation, boxSize))) {
                endTime = halfTime;
                endBallLocation = halfBallLocation;
                endMovableElementLocation = halfMovableElementLocation;
            } else {
                startTime = halfTime;
                startBallLocation = halfBallLocation;
                startMovableElementLocation = halfMovableElementLocation;
            }
            halfTime = (startTime + endTime) / 2;
            halfBallLocation = new Point((startBallLocation.x + endBallLocation.x) / 2, (startBallLocation.y + endBallLocation.y) / 2, (startBallLocation.z + endBallLocation.z) / 2);
            halfMovableElementLocation = new Point((startMovableElementLocation.x + endMovableElementLocation.x) / 2, (startMovableElementLocation.y + endMovableElementLocation.y) / 2, (startMovableElementLocation.z + endMovableElementLocation.z) / 2);
        }
        halfBallLocation = startBallLocation;
        halfMovableElementLocation = startMovableElementLocation;
        halfTime=startTime;

        //finding normal vector in place of collision
        Box collidedBox = new Box(halfMovableElementLocation, boxSize);
        Vector normal = new Vector(0, 0, 0);
        Point min = new Point(collidedBox.center.x - collidedBox.size.x / 2, collidedBox.center.y - collidedBox.size.y / 2, collidedBox.center.z - collidedBox.size.z / 2);
        Point max = new Point(collidedBox.center.x + collidedBox.size.x / 2, collidedBox.center.y + collidedBox.size.y / 2, collidedBox.center.z + collidedBox.size.z / 2);

        if (min.x - halfBallLocation.x > 0 && min.x - halfBallLocation.x <= ball.getRadius() + Collisions.USER_EXPERIENCE)
            normal.x = halfBallLocation.x-min.x;
        else if (halfBallLocation.x - max.x > 0 && halfBallLocation.x - max.x <= ball.getRadius() + Collisions.USER_EXPERIENCE)
            normal.x = halfBallLocation.x-max.x;

        if (min.y - halfBallLocation.y > 0 && min.y - halfBallLocation.y <= ball.getRadius() + Collisions.USER_EXPERIENCE)
            normal.y = halfBallLocation.y-min.y;
        else if (halfBallLocation.y - max.y > 0 && halfBallLocation.y - max.y <= ball.getRadius() + Collisions.USER_EXPERIENCE)
            normal.y = halfBallLocation.y-max.y;

        if (min.z - halfBallLocation.z > 0 && min.z - halfBallLocation.z <= ball.getRadius() + Collisions.USER_EXPERIENCE)
            normal.z = halfBallLocation.z-min.z;
        else if (halfBallLocation.z - max.z > 0 && halfBallLocation.z - max.z <= ball.getRadius() + Collisions.USER_EXPERIENCE)
            normal.z = halfBallLocation.z-max.z;

        //counting and setting new location and velocity after collision
        Vector velocity = new Vector(ball.getVelocity().x,ball.getVelocity().y,ball.getVelocity().z);
        Vector normalVelocity;
        Vector newNormalVelocity = new Vector(0, 0, 0);

        if ((normal.x==0&&normal.y==0)||(normal.x==0&&normal.z==0)||(normal.z==0&&normal.y==0)) {
            if (normal.x!=0) {
                if (element.getLastMove().x == 0)
                    velocity.x = -velocity.x;
                else if (element.getLastMove().x > 0 && element.getLastMove().x > ball.getLastMove().x)
                    velocity.x=Math.abs(element.getVelocity().x)+(velocity.x<0?-velocity.x:0);
                else if(element.getLastMove().x < 0 && element.getLastMove().x < ball.getLastMove().x)
                    velocity.x = -Math.abs(element.getVelocity().x)+(velocity.x>0?-velocity.x:0);
                else
                    velocity.x = -velocity.x;
            } else if (normal.z!=0) {
                if (element.getLastMove().z == 0)
                    velocity.z = -velocity.z;
                else if (element.getLastMove().z > 0 && element.getLastMove().z > ball.getLastMove().z)
                    velocity.z=Math.abs(element.getVelocity().z)+(velocity.z<0?-velocity.z:0);
                else if(element.getLastMove().z < 0 && element.getLastMove().z < ball.getLastMove().z)
                    velocity.z = -Math.abs(element.getVelocity().z)+(velocity.z>0?-velocity.z:0);
                else
                    velocity.z = -velocity.z;
            } else // normal.y!=0
            //jeszcze do dopracowania ta sytuacja
            {
                if (normal.y >0) {
                    halfBallLocation.y = element.getLocation().y + boxSize.y / 2 + ball.getRadius();
                    velocity.y = 0;
                }
                velocity.y = -velocity.y;
            }
        } else {
            if (normal.x!=0&&normal.y!=0&&normal.z!=0) normal.y = 0;
            normal = normal.normalize();

            if (normal.y == 0) {
//                if (element.getLastMove().x > 0 && element.getLastMove().x > ball.getLastMove().x) {
//                    float tmp = Math.abs(element.getVelocity().x) + (velocity.x < 0 ? -velocity.x : 0);
//                    velocity = new Vector(tmp, velocity.y, normal.z * tmp / normal.x);
//                }
//                else if(element.getLastMove().x < 0 && element.getLastMove().x < ball.getLastMove().x) {
//                    float tmp = -Math.abs(element.getVelocity().x) + (velocity.x > 0 ? -velocity.x : 0);
//                    velocity = new Vector(tmp, velocity.y, normal.z * tmp / normal.x);
//                }
//                else {
                    Vector velocityBeforeReversing=new Vector(velocity.x,0,velocity.z).normalize();
                    if(velocityBeforeReversing.dotProduct(normal)<0) {
                        normalVelocity = new Vector(-velocity.x, 0, -velocity.z).normalize();
                        if (Math.abs(normalVelocity.z * normal.x - normal.z * normalVelocity.x) < Collisions.USER_EXPERIENCE)
                            velocity = new Vector(-velocity.x, velocity.y, -velocity.z);
                        else {
                            float alfa = (float) Math.acos(normal.dotProduct(normalVelocity));

                            newNormalVelocity.z =
                                    ((float) (normal.x * Math.cos(2 * alfa) - Math.cos(alfa) * normalVelocity.x)) /
                                            (normalVelocity.z * normal.x - normal.z * normalVelocity.x);
                            newNormalVelocity.x =
                                    ((float) Math.cos(alfa) - newNormalVelocity.z * normal.z) / (normal.x);
                            float length = (float) Math.sqrt(velocity.x * velocity.x + velocity.z * velocity.z);
                            velocity = new Vector(length * newNormalVelocity.x, velocity.y, length * newNormalVelocity.z);
                        }
                    }
              //  }
            } else if (normal.x == 0) {
                normalVelocity = new Vector(0, -velocity.y, -velocity.z).normalize();
                if (Math.abs(normalVelocity.z * normal.y - normal.z * normalVelocity.y) < Collisions.USER_EXPERIENCE)
                    velocity = new Vector(velocity.x, -velocity.y, -velocity.z);
                else {
                    float alfa = (float) Math.acos(normal.dotProduct(normalVelocity));

                    newNormalVelocity.z =
                            ((float) (normal.y * Math.cos(2 * alfa) - Math.cos(alfa) * normalVelocity.y)) /
                                    (normalVelocity.z * normal.y - normal.z * normalVelocity.y);
                    newNormalVelocity.y =
                            ((float) Math.cos(alfa) - newNormalVelocity.z * normal.z) / (normal.y);
                    // newNormalVelocity.x = normalVelocity.x;
                    float length = (float) Math.sqrt(velocity.y * velocity.y + velocity.z * velocity.z);
                    velocity = new Vector(velocity.x, length * newNormalVelocity.y, length * newNormalVelocity.z);
                }
            } else { //normal.z==0
                normalVelocity = new Vector(-velocity.x, -velocity.y, 0).normalize();
                if (Math.abs(normalVelocity.y * normal.x - normal.y * normalVelocity.x) < Collisions.USER_EXPERIENCE)
                    velocity = new Vector(-velocity.x, -velocity.y, velocity.z);
                else {
                    float alfa = (float) Math.acos(normal.dotProduct(normalVelocity));

                    newNormalVelocity.y =
                            ((float) (normal.x * Math.cos(2 * alfa) - Math.cos(alfa) * normalVelocity.x)) /
                                    (normalVelocity.y * normal.x - normal.y * normalVelocity.x);
                    newNormalVelocity.x =
                            ((float) Math.cos(alfa) - newNormalVelocity.y * normal.y) / (normal.x);
                    // newNormalVelocity.z = normalVelocity.x;
                    float length = (float) Math.sqrt(velocity.x * velocity.x + velocity.y * velocity.y);
                    velocity = new Vector(length * newNormalVelocity.x, length * newNormalVelocity.y, velocity.z);
                }
            }
//            if (element.getLastMove().x != 0) {
//                if(Math.signum(element.getLastMove().x)==1)
//                    velocity.x=Math.max(velocity.x,Math.abs(element.getVelocity().x));
//                else
//                    velocity.x=-Math.max(velocity.x,Math.abs(element.getVelocity().x));
//              //  velocity.x += Math.max(Math.signum(element.getLastMove().x) * Math.abs(element.getVelocity().x),velocity.x);
//            }
//            if (element.getLastMove().z != 0) {
//                velocity.z += Math.signum(element.getLastMove().z) * Math.abs(element.getVelocity().z);
//            }
        }
        if (element.getClass() == Elevator.class)
            halfBallLocation.y += element.getLastMove().y * (1 - halfTime);

        ball.setLocation(new Point(halfBallLocation.x + velocity.x * (1 - halfTime) * Updater.INTERVAL_TIME,
                halfBallLocation.y + velocity.y * (1 - halfTime) * Updater.INTERVAL_TIME,
                halfBallLocation.z + velocity.z * (1 - halfTime) * Updater.INTERVAL_TIME));
        ball.setVelocity(velocity);
        if (CheckSphereAABBCollsion(new Sphere(ball.getLocation(), ball.getRadius()), new Box(element.getLocation(), boxSize))) {
            Point newBallLocation = ball.getLocation();
            if(element.getLastMove().x!=0||element.getLastMove().z!=0) {

                Vector distance = element.getLastMove().x!=0?new Vector(element.getLastMove().x, 0, normal.z * element.getLastMove().x / normal.x):
                                                            new Vector(normal.x * element.getLastMove().z / normal.z,0,element.getLastMove().z);
                newBallLocation.x += distance.x;
                newBallLocation.z += distance.z;
                for (int i = 0; i < 10; i++) {
                    distance.x /= 2;
                    distance.z /= 2;
                    newBallLocation.x -= distance.x;
                    newBallLocation.z -= distance.z;
                    if (CheckSphereAABBCollsion(new Sphere(newBallLocation, ball.getRadius()), new Box(element.getLocation(), boxSize))) {
                        newBallLocation.x += distance.x;
                        newBallLocation.z += distance.z;
                    }
                }

            }
            else{
                Vector distance=new Vector(0,0,0);
                if (min.x - newBallLocation.x > 0 && min.x - newBallLocation.x <= ball.getRadius() + Collisions.USER_EXPERIENCE)
                    distance.x = newBallLocation.x-min.x;
                else if (newBallLocation.x - max.x > 0 && newBallLocation.x - max.x <= ball.getRadius() + Collisions.USER_EXPERIENCE)
                    distance.x = newBallLocation.x-max.x;
                if (min.z - newBallLocation.z > 0 && min.z - newBallLocation.z <= ball.getRadius() + Collisions.USER_EXPERIENCE)
                    distance.z = newBallLocation.z-min.z;
                else if (newBallLocation.z - max.z > 0 && newBallLocation.z - max.z <= ball.getRadius() + Collisions.USER_EXPERIENCE)
                    distance.z = newBallLocation.z-max.z;
                distance.x=Math.signum(normal.x)*ball.getRadius();
                distance.z=Math.signum(normal.z)*ball.getRadius();
                newBallLocation.x += distance.x;
                newBallLocation.z += distance.z;
                for (int i = 0; i < 10; i++) {
                    distance.x /= 2;
                    distance.z /= 2;
                    newBallLocation.x -= distance.x;
                    newBallLocation.z -= distance.z;
                    if (CheckSphereAABBCollsion(new Sphere(newBallLocation, ball.getRadius()), new Box(element.getLocation(), boxSize))) {
                        newBallLocation.x += distance.x;
                        newBallLocation.z += distance.z;
                    }
                }
            }
            ball.setLocation(newBallLocation);
//            if (element.getLastMove().x != 0)
//                velocity.x = Math.signum(element.getLastMove().x) * Math.abs(element.getVelocity().x);
//            else if (element.getLastMove().z != 0)
//                velocity.z = Math.signum(element.getLastMove().z) * Math.abs(element.getVelocity().z);
//            else
//                velocity.y = Math.signum(element.getLastMove().y) * Math.abs(element.getVelocity().y);
//            ball.setLocation(new Point(halfBallLocation.x + velocity.x * (1 - halfTime) * Updater.INTERVAL_TIME,
//                    halfBallLocation.y + velocity.y * (1 - halfTime) * Updater.INTERVAL_TIME,
//                    halfBallLocation.z + velocity.z * (1 - halfTime) * Updater.INTERVAL_TIME));
//            ball.setVelocity(velocity);
        }
    }
}
