package movement

import de.fabmax.kool.math.MutableVec3f
import de.fabmax.kool.math.Vec3f
import de.fabmax.kool.physics.RigidActor

open class MovementEntity(var body:RigidActor,var currentProgram: MovementProgram?=null) {
    var programsList= mutableMapOf<String, MovementProgram>()
    //position and speed variables
    var movementVelocity=MutableVec3f(0f,0f,0f)
    var maxSpeed=10f
    var maxAcceleration=20f

    init {
        currentProgram?.let {
            programsList.put(it.id, it)
        }
    }

    open fun applyMovement(){

    }

}