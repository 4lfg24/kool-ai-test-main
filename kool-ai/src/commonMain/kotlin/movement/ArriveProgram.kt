package movement

import de.fabmax.kool.math.MutableVec3f

/**
 * This program makes the owner move towards the target entity and slowly
 * decreases its speed the closer it gets to it, it then halts its movement
 * when it gets too close
 */
class ArriveProgram(var seeker:MovementEntity, var target:MovementEntity):MovementProgram("Arrive") {

    var decelerationDistance=30f
    var stopDistance=1.5f

    /**
     * @param decelerationDistance the distance that the seeker must reach from the target after which it will start to decelerate
     * @param stopDistance how far from the target the seeker should be to stop its movement, must be inferior
     * to decelerationDistance
     */
    constructor(seeker:MovementEntity, target:MovementEntity, decelerationDistance:Float, stopDistance:Float) : this(seeker, target) {
        this.decelerationDistance=decelerationDistance
        this.stopDistance=stopDistance
    }
    override fun calculateMovement() {

        val distance=seeker.body.position.distance(target.body.position)

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