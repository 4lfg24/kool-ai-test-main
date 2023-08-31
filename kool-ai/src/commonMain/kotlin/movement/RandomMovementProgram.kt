package movement

import de.fabmax.kool.math.MutableVec3f
import de.fabmax.kool.math.Vec3f
import de.fabmax.kool.util.Time
import kotlin.math.sqrt
import kotlin.random.Random

class RandomMovementProgram(var owner:MovementEntity):MovementProgram("Random") {
    //the direction that the movement entity should follow
    var directionVec=MutableVec3f()

    /**how often the direction should change in seconds*/
    var maxChangeDirectionRate=0.01f

    var changeDirectionRate=maxChangeDirectionRate

    init {
        //set the direction vector to a random direction (for now)
        val lookAtPoint=MutableVec3f(randomTriangular(1f))
        val initialDirection=Vec3f(owner.body.position.subtract(lookAtPoint, MutableVec3f()))
        directionVec.set(initialDirection)
    }
    override fun calculateMovement() {

        changeDirectionRate-=Time.deltaT
        if (changeDirectionRate<=0){
            //rotate the vector by a random amount
            val randomRotation=randomTriangular(40f)
            directionVec.rotate(randomRotation, Vec3f.Y_AXIS)
        }

        val vel=directionVec.scale(1f)

        //for the sake of this demo we don't care about y velocity
        owner.movementVelocity.set(vel.x, 0f, vel.z)
    }
    fun randomTriangular(max: Float): Float {
        return (Random.nextFloat() - Random.nextFloat()) * max
    }
}