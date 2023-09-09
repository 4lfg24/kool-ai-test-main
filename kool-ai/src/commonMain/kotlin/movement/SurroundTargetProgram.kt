package movement

import de.fabmax.kool.math.MutableVec3f
import de.fabmax.kool.math.MutableVec4f
import de.fabmax.kool.math.Vec3f
import de.fabmax.kool.math.toDeg
import kotlin.math.cos
import kotlin.math.sign
import kotlin.math.sin

/**
 * This program makes the owner rotate around the target around a certain axes
 * @param axis the axis around which the owner should rotate, for now this can
 * be either: Vec3f.Y_AXIS, Vec3f.X_AXIS or Vec3f.Z_AXIS
 *
 */
class SurroundTargetProgram(var seeker:MovementEntity, var target:MovementEntity, val axis:Vec3f= Vec3f.Y_AXIS):MovementProgram("Surround") {

    var distanceFromTarget=10f

    var numberOfPoints=1000
    var pathPoints= mutableListOf<PathPoint>()
    //index of the point that is being pursued
    var currentPointIndex=0
    var currentPoint:PathPoint
    var pointOffset=0.5f
    //if the seeker should rotate around the target
    //clockwise or counterclockwise
    var clockwise=false
    init {

        //ACTUAL WAY OF DOING THIS
        //set path points in a shape by giving the number of sides/vertices
        val angleDelta=360f/numberOfPoints

        for (i in 0 until numberOfPoints){
            var posX=0f
            var posY=0f
            var posZ=0f

            //nvm, the more points you add the better it works apparently
            if (axis== Vec3f.Y_AXIS) {
                posX = target.body.position.x + distanceFromTarget * cos(angleDelta * i)
                posZ = target.body.position.z + distanceFromTarget * sin(angleDelta * i)
                posY=target.body.position.y
            }
            if (axis==Vec3f.Z_AXIS){
                posX=target.body.position.x + distanceFromTarget * cos(angleDelta * i)
                posY=target.body.position.y + distanceFromTarget * sin(angleDelta * i)
                posZ=target.body.position.z
            }
            if (axis==Vec3f.X_AXIS){
                posY=target.body.position.y + distanceFromTarget * sin(angleDelta * i)
                posZ = target.body.position.z + distanceFromTarget * cos(angleDelta * i)
                posX=target.body.position.x
            }


            pathPoints.add(PathPoint(Vec3f(posX, posY, posZ)))
            /*var position=vecZ.rotate(angleDelta, Vec3f.Y_AXIS)
            position.scale(distanceFromTarget)

            pathPoints.add(PathPoint(target.body.position.add(position,MutableVec3f())))

             */

        }
        currentPoint=pathPoints[0]
    }

    override fun calculateMovement() {
        updatePathPointsPositions()
        val distance=currentPoint.position.subtract(seeker.body.position, MutableVec3f())
        val direction=distance.norm()

        val vel=direction.scale(seeker.maxAcceleration)
        //we want the seeker to reach the points at a constant time,
        //but because the target might be moving we need to add its speed
        //to the seeker's one
        vel.add(target.movementVelocity)
        seeker.movementVelocity.set(vel) //works

        val distanceFloat=currentPoint.position.distance(seeker.body.position)
        //here put the logic for clockwise and counterclockwise
        if (distanceFloat<=pointOffset){
            if (clockwise) {
                //if at the last point
                if (currentPointIndex == pathPoints.size - 1) currentPointIndex = 0
                else currentPointIndex++
            }else{
                //if at the first point
                if (currentPointIndex==0) currentPointIndex=pathPoints.size-1
                else currentPointIndex--
            }
        }
        currentPoint=pathPoints[currentPointIndex]
    }

    private fun updatePathPointsPositions() {
        //set the points position to their original one, but
        //taking into consideration the new player's position
        for (point in pathPoints){
            //point.add(target.body.position, point)
            point.updatePosition()
        }
    }

    inner class PathPoint(initialPosition:Vec3f){

        var position=MutableVec3f()
        var distanceVector=MutableVec3f()
        init {
            target.body.position.subtract(initialPosition, distanceVector)
            position.set(initialPosition)
        }

        fun updatePosition(){
            //set the point to be always at the same distance from the target
            position.set(target.body.position.subtract(distanceVector, MutableVec3f()))
        }
    }
}