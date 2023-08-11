package movement

import de.fabmax.kool.math.MutableVec3f
import kotlin.math.sqrt

class ArriveProgram(var seeker:MovementEntity, var target:MovementEntity):MovementProgram("Arrive") {

    var decelerationDistance=15f
    var stopDistance=1.5f

    override fun calculateMovement() {

        val distance=seeker.body.position.distance(target.body.position)

        var seekerSpeed=seeker.maxSpeed
        //if the seeker hasn't reached the maximum distance, keep moving
        if(distance>=stopDistance){
            val distanceVector=target.body.position.subtract(seeker.body.position, MutableVec3f())
            val direction=distanceVector.norm()

            val vel=direction.scale(seeker.maxAcceleration)

            //code to decelerate as soon as it reaches the decelerationDistance
            if (distance<=decelerationDistance) vel.scale((distance/decelerationDistance))

            seeker.movementVelocity.set(vel)
        }else{
            seeker.movementVelocity.set(0f,0f,0f)
        }

    }
}