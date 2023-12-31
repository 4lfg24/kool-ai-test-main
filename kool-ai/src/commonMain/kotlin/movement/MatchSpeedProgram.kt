package movement

import de.fabmax.kool.math.MutableVec3f
import de.fabmax.kool.physics.RigidActor
import de.fabmax.kool.physics.RigidBody

/**
 * This program makes the owner reach the same velocity as the target,
 * the velocity change is instantaneous
 */
class MatchSpeedProgram(var owner:MovementEntity, var target:MovementEntity):MovementProgram("Match Speed") {

    //always set it to bigger than 1
    var timeToTarget=2f
    override fun calculateMovement() {
        val targetVel=target.movementVelocity
        //owner.movementVelocity.set(targetVel.subtract(owner.movementVelocity.scale(1f/timeToTarget), MutableVec3f()))
        owner.movementVelocity.set(targetVel)
    }
}