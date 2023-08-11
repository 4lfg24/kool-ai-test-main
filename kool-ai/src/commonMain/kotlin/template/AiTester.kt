package template

import de.fabmax.kool.KoolContext
import de.fabmax.kool.input.KeyboardInput
import de.fabmax.kool.input.UniversalKeyCode
import de.fabmax.kool.math.MutableVec3f
import de.fabmax.kool.math.Vec3f
import de.fabmax.kool.modules.ksl.KslPbrShader
import de.fabmax.kool.modules.ui2.*
import de.fabmax.kool.physics.*
import de.fabmax.kool.physics.geometry.SphereGeometry
import de.fabmax.kool.scene.Scene
import de.fabmax.kool.scene.colorMesh
import de.fabmax.kool.scene.defaultOrbitCamera
import de.fabmax.kool.scene.scene
import de.fabmax.kool.util.Color
import de.fabmax.kool.util.MdColor
import de.fabmax.kool.util.Time
import de.fabmax.kool.util.debugOverlay
import movement.ArriveProgram
import movement.FleeProgram
import movement.SeekProgram
import movement.TestMovementEntity

/**
 * Main application entry. This demo creates a small example scene, which you probably want to replace by your actual
 * game / application content.
 */

class AiTester {
    lateinit var world: PhysicsWorld
    var stepper = ConstantPhysicsStepperSync()
    lateinit var entity1: TestMovementEntity
    lateinit var entity2: TestMovementEntity
    var entity1Vel = MutableVec3f()

    fun aiTest(ctx: KoolContext) {
        //load world

        ctx.scenes += scene {
            //load physics world
            loadPhysicsWorld()
            //create two bodies for testing
            loadEntities()
            mainRenderPass.clearColor= Color.CYAN
            camera.apply {
                position.set(0f, 100f, 10f)
                clipNear = 0.5f
                clipFar = 500f
                lookAt.set(Vec3f(0f, 0f, 0f))
            }

            // set up a single light source
            lighting.singleLight {
                setDirectional(Vec3f(-1f, -1f, -1f))
                setColor(Color.WHITE, 5f)
            }
            onUpdate {
                entity1.currentProgram?.calculateMovement()
                entity1.movementVelocity.set(entity1Vel)
                entity1.applyMovement()
                entity2.currentProgram?.calculateMovement()
                entity2.applyMovement()
            }
        }


        // add the debugOverlay. provides an fps counter and some additional debug info
        ctx.scenes += UiScene {
        setupUiScene()
            Panel {
                with(modifier){
                    size(400.dp, 300.dp)
                    align(AlignmentX.End, AlignmentY.Top)
                    background(RoundRectBackground(colors.background, 16.dp))
                }
                Row {
                    Text("Press S to activate Seek Program") {
                        modifier
                            .alignX(AlignmentX.Start)
                            .margin(30.dp)
                    }
                }
                Row {
                    Text("Press F to activate Flee Program") {
                        modifier
                            .alignX(AlignmentX.Start)
                            .margin(30.dp)
                    }
                }
                Row {
                    Text("Press A to activate Arrive Program") {
                        modifier
                            .alignX(AlignmentX.Start)
                            .margin(30.dp)
                    }
                }
                Row {
                    Text("Use the arrow keys to move the character") {
                        modifier
                            .alignX(AlignmentX.Start)
                            .margin(30.dp)
                    }
                }
            }
        }
    }

    fun Scene.loadPhysicsWorld() {
        world = PhysicsWorld(this).apply {
            simStepper = stepper
            gravity = Vec3f(0f, 0f, 0f)
        }
        world.addDefaultGroundPlane()
    }

    fun Scene.loadEntities() {
        var material = Material(0.5f, 0.5f)
        val geom = SphereGeometry(2f)
        var body1 = RigidDynamic().apply {
            attachShape(Shape(geom, material))
            position = Vec3f(-20f, 10f, 3f)
        }
        var body2 = RigidDynamic().apply {
            attachShape(Shape(geom, material))
            position = Vec3f(10f, 10f, -3f)
            isTrigger = true
        }
        colorMesh {
            generate {
                color = MdColor.GREEN
                geom.generateMesh(this)
            }
            shader = KslPbrShader {
                color { vertexColor() }
            }
            onUpdate {
                transform.set(body1.transform)
            }
        }
        colorMesh {
            generate {
                color = MdColor.GREEN
                geom.generateMesh(this)
            }
            shader = KslPbrShader {
                color { vertexColor() }
            }
            onUpdate {
                transform.set(body2.transform)
            }
        }
        world.addActor(body1)
        world.addActor(body2)

        entity1 = TestMovementEntity(body1)
        entity2 = TestMovementEntity(body2)

        //entity1.currentProgram=ArriveProgram(entity1, entity2)
        //keyboard input for testing
        KeyboardInput.addKeyListener(KeyboardInput.KEY_CURSOR_UP, "move up", filter = { it.isPressed }) {
            entity1Vel.z = -30f
        }
        KeyboardInput.addKeyListener(KeyboardInput.KEY_CURSOR_DOWN, "move down", filter = { it.isPressed }) {
            entity1Vel.z = 30f
        }
        KeyboardInput.addKeyListener(KeyboardInput.KEY_CURSOR_RIGHT, "move right", filter = { it.isPressed }) {
            entity1Vel.x = 30f
        }
        KeyboardInput.addKeyListener(KeyboardInput.KEY_CURSOR_LEFT, "move left", filter = { it.isPressed }) {
            entity1Vel.x = -30f
        }


        val seek=SeekProgram(entity2, entity1)
        val flee=FleeProgram(entity2, entity1)
        val arrive=ArriveProgram(entity2, entity1)
        entity2.programsList[flee.id] = flee
        entity2.programsList[arrive.id]=arrive
        entity2.programsList[seek.id]=seek

        entity2.currentProgram = seek
        //code to switch to the different programs
        KeyboardInput.addKeyListener(UniversalKeyCode('s'), "Activate seek", {true}){
            if (it.isPressed){
                entity2.currentProgram= entity2.programsList["Seek"]
            }
        }
        KeyboardInput.addKeyListener(UniversalKeyCode('f'), "Activate flee", {true}){
            if (it.isPressed){
                entity2.currentProgram= entity2.programsList["Flee"]
            }
        }
        KeyboardInput.addKeyListener(UniversalKeyCode('a'), "Activate seek", {true}){
            if (it.isPressed){
                entity2.currentProgram= entity2.programsList["Arrive"]
            }
        }
    }
}