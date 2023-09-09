package movement

import de.fabmax.kool.math.MutableVec3f
import de.fabmax.kool.math.Vec3f
import de.fabmax.kool.math.sqrDistanceToEdge
import de.fabmax.kool.util.Time
import kotlin.math.sqrt
import kotlin.random.Random

/**
 * This program makes the owner moves in a semi-random direction, it does so by
 * rotating its orientation vector by a random amount between -maxRotation and maxRotation in degrees each frame
 * @param maxRotation the maximum amount of rotation that the direction vector can rotate
 * around a certain axes every frame
 * @param maxChangeDirectionRate the time in seconds that needs to pass for the direction vector to rotate again
 */
class RandomMovementProgram(var owner:MovementEntity, var maxRotation:Float=30f, var maxChangeDirectionRate:Float=0.05f):MovementProgram("Random") {
    //the direction that the movement entity should follow
    var directionVec=MutableVec3f()


    var changeDirectionRate=maxChangeDirectionRate

    var originPoint:Vec3f
    var maxDistanceFromOrigin=3f

    init {
        //set the direction vector to a random direction (for now)
        val lookAtPoint=MutableVec3f(randomTriangular(1f))
        val initialDirection=Vec3f(owner.body.position.subtract(lookAtPoint, MutableVec3f()))
        directionVec.set(initialDirection)
        //set initial point
        originPoint=owner.body.position
        println(originPoint)
    }
    override fun calculateMovement() {

        changeDirectionRate-=Time.deltaT
        if (changeDirectionRate<=0){
            //rotate the vector by a random amount
            val randomRotation=randomTriangular(maxRotation)
            directionVec.rotate(randomRotation, Vec3f.Y_AXIS)
            changeDirectionRate=maxChangeDirectionRate
        }

        val vel=directionVec.scale(1f)
        println("Distance from origin: ${originPoint.distance(owner.body.position)}") //why is it 0?
        //for the sake of this demo we don't care about y velocity
        if (maxDistanceFromOrigin>0 && owner.body.position.distance(originPoint)<maxDistanceFromOrigin){
            owner.movementVelocity.set(vel.x, 0f, vel.z)
        }

    }
    fun randomTriangular(max: Float): Float {
        return (Random.nextFloat() - Random.nextFloat()) * max
    }
}