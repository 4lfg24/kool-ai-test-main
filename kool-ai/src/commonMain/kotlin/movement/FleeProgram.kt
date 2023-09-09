package movement

import de.fabmax.kool.math.MutableVec3f

/**
 * This program does the opposite of the SeekProgram, it moves the fleer
 * away from the pursuer as fast as its maxAcceleration allows it
 */
class FleeProgram(var fleer:MovementEntity, var pursuer:MovementEntity):MovementProgram("Flee") {

    //the distance after which the fleer will start decelerating
    var decelerationDistance=80f
    var stopDistance=100f

    override fun calculateMovement() {
        val distance=fleer.body.position.distance(pursuer.body.position)

        if (distance<=stopDistance) {
            val distanceVector = fleer.body.position.subtract(pursuer.body.position, MutableVec3f())
            val direction = distanceVector.norm()

            val vel = direction.scale(fleer.maxAcceleration)

            //code to decelerate as soon as it reaches the decelerationDistance
            if (distance >= decelerationDistance) vel.scale((distance/decelerationDistance))

            fleer.movementVelocity.set(vel.scale(1f))
        }else{
            fleer.movementVelocity.set(0f,0f,0f)
        }
    }
}