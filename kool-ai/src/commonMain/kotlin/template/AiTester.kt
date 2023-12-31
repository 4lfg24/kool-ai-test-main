package template

import de.fabmax.kool.KoolContext
import de.fabmax.kool.input.KeyboardInput
import de.fabmax.kool.input.UniversalKeyCode
import de.fabmax.kool.math.MutableVec3d
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
import movement.*

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
    //path for the follow path program
    var path= mutableListOf(Vec3f(0f, 10f, 25f), Vec3f(30f, 10f, 0f), Vec3f(-20f, 10f, -30f))
    fun aiTest(ctx: KoolContext) {
        //load world
        ctx.scenes += scene {
            //load physics world
            loadPhysicsWorld()
            //create two bodies for testing
            loadEntities()
            //draw path points
            loadPathPoints()
            //draw surround path points
            //loadSurroundPoints()
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
                    size(400.dp, 450.dp)
                    align(AlignmentX.End, AlignmentY.Top)
                    background(RoundRectBackground(colors.background, 16.dp))
                }
                Row {
                    Text("Press S to activate Seek Program") {
                        modifier
                            .alignX(AlignmentX.Start)
                            .margin(15.dp)
                    }
                }
                Row {
                    Text("Press F to activate Flee Program") {
                        modifier
                            .alignX(AlignmentX.Start)
                            .margin(15.dp)
                    }
                }
                Row {
                    Text("Press A to activate Arrive Program") {
                        modifier
                            .alignX(AlignmentX.Start)
                            .margin(15.dp)
                    }
                }
                Row {
                    Text("Press P to activate follow path Program"){
                        modifier
                            .alignX(AlignmentX.Start)
                            .margin(15.dp)
                    }
                }
                Row {
                    Text("Press M to activate follow match speed Program"){
                        modifier
                            .alignX(AlignmentX.Start)
                            .margin(15.dp)
                    }
                }
                Row {
                    Text("Press R to activate random movement") {
                        modifier
                            .alignX(AlignmentX.Start)
                            .margin(15.dp)
                    }
                }
                Row {
                    Text("press X to activate surround program") {
                        modifier
                            .alignX(AlignmentX.Start)
                            .margin(15.dp)
                    }
                }
                Row {
                    Text("Use the arrow keys to move the character") {
                        modifier
                            .alignX(AlignmentX.Start)
                            .margin(15.dp)
                    }
                }
                Row {
                    Text("(Yellow spheres represents path points)") {
                        modifier
                            .alignX(AlignmentX.Start)
                            .margin(15.dp)
                    }
                }
            }
        }
    }

    private fun Scene.loadPathPoints() {
        //creating first path point mesh
        colorMesh {
            generate {
                color = MdColor.YELLOW
                icoSphere {
                    radius=1f
                    center.set(path[0])
                }
            }
            shader = KslPbrShader {
                color { vertexColor() }
            }
        }
        //creating second path point mesh
        colorMesh {
            generate {
                color = MdColor.YELLOW
                icoSphere {
                    radius=1f
                    center.set(path[1])
                }
            }
            shader = KslPbrShader {
                color { vertexColor() }
            }
        }
        //creating third path point mesh
        colorMesh {
            generate {
                color = MdColor.YELLOW
                icoSphere {
                    radius=1f
                    center.set(path[2])
                }
            }
            shader = KslPbrShader {
                color { vertexColor() }
            }
        }
    }

    private fun Scene.loadSurroundPoints(){
        val points=(entity2.programsList["Surround"] as SurroundTargetProgram).pathPoints
        //creating the pathpoints meshes
        for (point in (entity2.programsList["Surround"] as SurroundTargetProgram).pathPoints){
            colorMesh {
                generate {
                    color=MdColor.DEEP_ORANGE
                    icoSphere {
                        radius=1.5f
                        center.set(point.position)
                    }
                }
                shader = KslPbrShader {
                    color { vertexColor() }
                }
                onUpdate {
                    this.transform.setTranslate(point.position)
                }
            }
        }
        /*colorMesh {
            generate {
                color=MdColor.DEEP_ORANGE
                icoSphere {
                    radius=1.5f
                    center.set(points[0].position)
                }
            }
            onUpdate {

            }
        }

         */
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
        val body1 = RigidDynamic().apply {
            attachShape(Shape(geom, material))
            position = Vec3f(-3f, 10f, -10f)
        }

        val body2 = RigidDynamic().apply {
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
        val followPath=FollowPathProgram(path, entity2, true)
        val matchSpeed=MatchSpeedProgram(entity2, entity1)
        val randomMovement=RandomMovementProgram(entity2)
        val surround=SurroundTargetProgram(entity2, entity1, Vec3f.Z_AXIS)

        entity2.programsList[flee.id] = flee
        entity2.programsList[arrive.id]=arrive
        entity2.programsList[seek.id]=seek
        entity2.programsList[followPath.id]=followPath
        entity2.programsList[matchSpeed.id]=matchSpeed
        entity2.programsList[randomMovement.id]=randomMovement
        entity2.programsList[surround.id]=surround

        entity2.currentProgram =surround
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
        KeyboardInput.addKeyListener(UniversalKeyCode('m'), "Activate match speed", {true}){
            if (it.isPressed){
                entity2.currentProgram= entity2.programsList["Match Speed"]
            }
        }
        KeyboardInput.addKeyListener(UniversalKeyCode('p'), "Activate follow path", {true}){
            if (it.isPressed){
                entity2.currentProgram= entity2.programsList["Follow Path"]
            }
        }
        KeyboardInput.addKeyListener(UniversalKeyCode('r'), "Activate random movement", {true}){
            if (it.isPressed){
                entity2.currentProgram= entity2.programsList["Random"]
            }
        }
        KeyboardInput.addKeyListener(UniversalKeyCode('x'), "Activate surround", {true}){
            if (it.isPressed){
                entity2.currentProgram= entity2.programsList["Surround"]
            }
        }

    }
}