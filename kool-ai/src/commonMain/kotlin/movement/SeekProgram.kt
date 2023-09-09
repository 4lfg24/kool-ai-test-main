package movement

import de.fabmax.kool.math.MutableVec3f
import de.fabmax.kool.math.Vec3f
import kotlin.math.abs
import kotlin.math.sign
import kotlin.math.sqrt

/** This program makes the seeker move towards the target
 * as fast as it's max acceleration allows it
 *
 */
class SeekProgram(var seeker:MovementEntity, var target:MovementEntity):MovementProgram("Seek") {


    override fun calculateMovement() {

        val distance=target.body.position.subtract(seeker.body.position, MutableVec3f())
        val direction=distance.norm()

        val vel=direction.scale(seeker.maxAcceleration)
        seeker.movementVelocity.add(vel)
        //seeker.movementVelocity.set(Vec3f(velX, velY, velZ))
        //it works... don't ask me how though

    }

}

