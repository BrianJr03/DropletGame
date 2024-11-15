package jr.brian

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.audio.Music
import com.badlogic.gdx.audio.Sound
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.utils.Array
import com.badlogic.gdx.utils.ScreenUtils
import com.badlogic.gdx.utils.viewport.FitViewport

class Game {
    init {
        with(MUSIC) {
            isLooping = true
            volume = .5f
            play()
        }
    }

    fun resize(
        width: Int,
        height: Int,
        centerCamera: Boolean = false
    ) {
        VIEWPORT.update(width, height, centerCamera)
    }

    fun handleInput() {
        handleTouchInput()
        handleKeyInput()
    }

    private fun handleTouchInput() {
        if (Gdx.input.isTouched) {
            TOUCH_POS.set(
                Gdx.input.x.toFloat(),
                Gdx.input.y.toFloat()
            )
            VIEWPORT.unproject(TOUCH_POS)
            BUCKET_SPRITE.setCenterX(TOUCH_POS.x)
        }
    }

    private fun handleKeyInput() {
        val speed = 4f
        val delta = Gdx.graphics.deltaTime

        val rightPressed = Gdx.input.isKeyPressed(Input.Keys.RIGHT)
        val leftPressed = Gdx.input.isKeyPressed(Input.Keys.LEFT)

        if (rightPressed) {
            BUCKET_SPRITE.translateX(speed * delta)
        } else if (leftPressed) {
            BUCKET_SPRITE.translateX(-speed * delta)
        }
    }

    fun executeDrawLogic() {
        ScreenUtils.clear(Color.BLACK)
        VIEWPORT.apply()
        SPRITE_BATCH.setProjectionMatrix(VIEWPORT.camera.combined)
        SPRITE_BATCH.begin()
        draw()
        SPRITE_BATCH.end()
    }

    private fun draw() {
        val worldWidth = VIEWPORT.worldWidth
        val worldHeight = VIEWPORT.worldHeight
        SPRITE_BATCH.draw(
            BACKGROUND_TEXTURE,
            0f,
            0f,
            worldWidth,
            worldHeight
        )
        BUCKET_SPRITE.draw(SPRITE_BATCH)
        for (dropSprite in DROP_SPRITES) {
            dropSprite.draw(SPRITE_BATCH)
        }
    }

    fun executeGameLogic() {
        executeBucketLogic()
        executeDropletLogic()
    }

    private fun executeBucketLogic() {
        val worldWidth = VIEWPORT.worldWidth
        BUCKET_SPRITE.setSize(
            BUCKET_WIDTH,
            BUCKET_HEIGHT
        )
        BUCKET_SPRITE.x = MathUtils.clamp(
            BUCKET_SPRITE.x,
            0f,
            worldWidth - BUCKET_WIDTH
        )
        BUCKET_RECTANGLE.set(
            BUCKET_SPRITE.x,
            BUCKET_SPRITE.y,
            BUCKET_WIDTH,
            BUCKET_HEIGHT
        )
    }

    private fun createDroplet() {
        val dropWidth = 1f
        val dropHeight = 1f
        val worldWidth = VIEWPORT.worldWidth
        val worldHeight = VIEWPORT.worldHeight
        val dropSprite = Sprite(DROP_TEXTURE)
        dropSprite.setSize(dropWidth, dropHeight)
        dropSprite.x = MathUtils.random(
            0f,
            worldWidth - dropWidth
        )
        dropSprite.y = worldHeight
        DROP_SPRITES.add(dropSprite)
    }

    private fun executeDropletLogic() {
        val delta = Gdx.graphics.deltaTime
        for (i in DROP_SPRITES.size - 1 downTo 0) {
            val dropSprite = DROP_SPRITES[i]
            val dropWidth = dropSprite.width
            val dropHeight = dropSprite.height
            dropSprite.translateY(-2f * delta)
            DROP_RECTANGLE.set(
                dropSprite.x,
                dropSprite.y,
                dropWidth, dropHeight
            )
            if (dropSprite.y < -dropHeight) {
                DROP_SPRITES.removeIndex(i)
            } else if (BUCKET_RECTANGLE.overlaps(DROP_RECTANGLE)
            ) {
                DROP_SPRITES.removeIndex(i)
                DROP_SOUND.play()
            }
        }
        DROP_TIMER += delta
        if (DROP_TIMER > 1f) {
            DROP_TIMER = 0f
            createDroplet()
        }
    }

    companion object {
       private var DROP_TIMER = 0f

        private const val BUCKET_WIDTH = 1f
        private const val BUCKET_HEIGHT = 1f

        private val DROP_TEXTURE = Texture("drop.png")
        private val BUCKET_TEXTURE = Texture("bucket.png")
        private val BACKGROUND_TEXTURE = Texture("background.png")

        private val TOUCH_POS = Vector2()

        private val DROP_RECTANGLE = Rectangle()
        private val BUCKET_RECTANGLE = Rectangle()


        private val SPRITE_BATCH = SpriteBatch()
        private val DROP_SPRITES = Array<Sprite>()
        private val BUCKET_SPRITE = Sprite(BUCKET_TEXTURE)

        private val VIEWPORT = FitViewport(8f, 5f)

        private val MUSIC: Music = Gdx.audio.newMusic(Gdx.files.internal("music.mp3"))
        private val DROP_SOUND: Sound = Gdx.audio.newSound(Gdx.files.internal("drop.mp3"))
    }
}
