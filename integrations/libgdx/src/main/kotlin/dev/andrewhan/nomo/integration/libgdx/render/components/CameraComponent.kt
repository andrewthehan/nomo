package dev.andrewhan.nomo.integration.libgdx.render.components

import com.badlogic.gdx.graphics.Camera
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.utils.viewport.FitViewport
import dev.andrewhan.nomo.core.Component
import dev.andrewhan.nomo.sdk.components.Exclusive
import dev.andrewhan.nomo.sdk.components.Pendant
import dev.andrewhan.nomo.sdk.engines.NomoEngine
import dev.andrewhan.nomo.sdk.stores.getComponents
import dev.andrewhan.nomo.sdk.util.Location
import dev.andrewhan.nomo.sdk.util.Size

class CameraComponent(
  var viewportOrigin: Location,
  var viewportSize: Size,
  var worldCenter: Vector2 = Vector2(0f, 0f),
  private val worldScale: Float = 1f
) : Component, Pendant, Exclusive {
  private val viewport by lazy { FitViewport(0f, 0f) }

  fun contains(location: Location): Boolean {
    return viewportOrigin.x <= location.x &&
      location.x <= viewportOrigin.x + viewportSize.width &&
      viewportOrigin.y <= location.y &&
      location.y <= viewportOrigin.y + viewportSize.height
  }

  fun toWorld(location: Location): Vector2 {
    return Vector2(
      (location.x + viewportOrigin.x - viewportSize.width / 2) * worldScale + worldCenter.x,
      (location.y + viewportOrigin.y - viewportSize.height / 2) * worldScale + worldCenter.y
    )
  }

  fun use(block: (camera: Camera) -> Unit) {
    synchronized(this) {
      viewport.camera.position.set(worldCenter.x, worldCenter.y, 0f)
      viewport.setWorldSize(viewportSize.width * worldScale, viewportSize.height * worldScale)
      viewport.setScreenBounds(
        viewportOrigin.x,
        viewportOrigin.y,
        viewportSize.width,
        viewportSize.height
      )
      viewport.apply()
      block(viewport.camera)
    }
  }

  override fun toString(): String =
    "${this::class.simpleName}(viewportOrigin=$viewportOrigin,viewportSize=$viewportSize,worldCenter=$worldCenter,worldScale=$worldScale)"
}

fun NomoEngine.getRelevantCamera(location: Location): CameraComponent? {
  return getComponents<CameraComponent>().singleOrNull { it.contains(location) }
}
