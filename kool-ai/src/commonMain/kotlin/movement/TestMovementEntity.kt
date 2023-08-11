package movement

import de.fabmax.kool.math.Vec3f
import de.fabmax.kool.physics.RigidActor
import de.fabmax.kool.physics.RigidBody
import de.fabmax.kool.physics.RigidDynamic

class TestMovementEntity(body: RigidActor):MovementEntity(body) {

    init {

    }

    override fun applyMovement() {
        (body as? RigidDynamic)?.linearVelocity=Vec3f(movementVelocity)
    }
}