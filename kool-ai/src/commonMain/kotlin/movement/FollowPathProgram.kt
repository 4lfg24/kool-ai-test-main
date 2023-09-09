package movement

import de.fabmax.kool.math.MutableVec3f
import de.fabmax.kool.math.Vec3f

/**
 * Given a certain path (consisting of two or more points), this program
 * will make the owner move from point to point, starting from the first one
 *
 * @param openPath if set to true the owner will traverse the path backwards
 * once it reaches the last point
 */
class FollowPathProgram(var path:MutableList<Vec3f>, var owner:MovementEntity, var openPath:Boolean = false):MovementProgram("Follow Path") {

    //the distance after which the point is considered reached
    var pathOffset=5f
    var pathIndex=0
    //first things first, get the point of the path that
    //the entity should move towards
    var currentPoint=path[0]
    var traverseBackwards=false

    override fun calculateMovement() {

        //then get the distance and direction from the owner
        //to the path point
        val distance=currentPoint.subtract(owner.body.position, MutableVec3f())
        val direction=distance.norm()

        val vel=direction.scale(owner.maxAcceleration)
        owner.movementVelocity.set(vel)

        //if it gets close enough to the current point to reach
        //go to the next one

        //println("Distance: ${distance.length()}") //why does it always stay around 4?
        var distanceFloat=currentPoint.distance(owner.body.position)
        if (distanceFloat<=pathOffset){
            if (pathIndex==0) traverseBackwards=false
            if (pathIndex==path.size-1){
                if (!openPath){
                    pathIndex=0
                }else{
                    traverseBackwards= !traverseBackwards
                    if(traverseBackwards) pathIndex=path.size-2
                    else pathIndex=1
                }

            }else {
                if(traverseBackwards) pathIndex--
                else pathIndex++
            }
        }
        currentPoint=path[pathIndex]
        //works, just need to do some adjustments
    }
}