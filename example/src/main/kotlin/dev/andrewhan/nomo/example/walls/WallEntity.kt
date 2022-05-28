package dev.andrewhan.nomo.example.walls

import com.badlogic.gdx.physics.box2d.BodyDef
import dev.andrewhan.nomo.boot.player.components.PlayerComponent
import dev.andrewhan.nomo.example.GameBounds
import dev.andrewhan.nomo.example.player.PlayerStatsComponent
import dev.andrewhan.nomo.integration.libgdx.physics.SafeWorld
import dev.andrewhan.nomo.integration.libgdx.physics.components.BodyComponent
import dev.andrewhan.nomo.integration.libgdx.physics.components.DestroySelfOnCollisionComponent
import dev.andrewhan.nomo.sdk.engines.NomoEngine
import dev.andrewhan.nomo.sdk.engines.key
import dev.andrewhan.nomo.sdk.entities.entity
import dev.andrewhan.nomo.sdk.stores.getComponent
import dev.andrewhan.nomo.sdk.stores.getEntity
import dev.andrewhan.nomo.sdk.util.Size
import ktx.box2d.box

fun NomoEngine.newWalls(world: SafeWorld) {
  val gameBounds = getInstance(key<Size>(GameBounds::class))
  val width = gameBounds.width
  val height = gameBounds.height

  "top wall" bind
    BodyComponent(world) {
      type = BodyDef.BodyType.StaticBody
      position.set(0f, height / 2)
      box(width = width, height = 0.1f) {
        friction = 0f
        restitution = 1f
      }
    }

  "bottom wall" bind
    BodyComponent(world) {
      type = BodyDef.BodyType.StaticBody
      position.set(0f, -height / 2)
      box(width = width, height = 0.1f) {
        friction = 0f
        restitution = 1f
      }
    }

  "right wall" bind
    BodyComponent(world) {
      type = BodyDef.BodyType.StaticBody
      position.set(width / 2, 0f)
      box(width = 0.1f, height = height) {
        friction = 0f
        restitution = 1f
      }
    }

  newLeftWall(world)
}

fun NomoEngine.newLeftWall(world: SafeWorld) {
  val gameBounds = getInstance(key<Size>(GameBounds::class))
  val width = gameBounds.width
  val height = gameBounds.height

  val player = getEntity(PlayerComponent)
  val stats = getComponent<PlayerStatsComponent>(player)

  val segmentHeight = height / stats.segments
  repeat(stats.segments) {
    entity(
      BodyComponent(world) {
        type = BodyDef.BodyType.StaticBody
        position.set(
          -width / 2,
          (-height / 2) + (it * height / stats.segments) + (segmentHeight / 2)
        )
        box(width = 0.1f, height = height / stats.segments) {
          friction = 0f
          restitution = 1f
        }
      },
      DestroySelfOnCollisionComponent
    )
  }
}
