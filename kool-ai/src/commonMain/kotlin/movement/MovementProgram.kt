package movement

abstract class MovementProgram(var id:String = "") {

    var currentAcceleration=0f

    abstract fun calculateMovement()
}