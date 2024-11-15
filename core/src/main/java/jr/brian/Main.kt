package jr.brian

import com.badlogic.gdx.ApplicationListener

class Main : ApplicationListener {
    private lateinit var game: Game

    override fun create() {
        game = Game()
    }

    override fun resize(width: Int, height: Int) {
        game.resize(width, height, true)
    }

    override fun render() {
        with(game) {
            handleInput()
            executeDrawLogic()
            executeGameLogic()
        }
    }

    override fun pause() {

    }

    override fun resume() {

    }

    override fun dispose() {

    }
}
