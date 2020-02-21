package com.productions.gizzmoo.pokemonpuzzleleague

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import com.productions.gizzmoo.pokemonpuzzleleague.PanningLoop.Companion.MAX_FPS
import com.productions.gizzmoo.pokemonpuzzleleague.PanningLoop.Companion.NUM_OF_SECS_TO_PAN_VIEW
import com.productions.gizzmoo.pokemonpuzzleleague.puzzlegame.puzzleacademygame.PuzzleAcademySelectionActivity
import com.productions.gizzmoo.pokemonpuzzleleague.puzzlegame.marathongame.MarathonActivity
import com.productions.gizzmoo.pokemonpuzzleleague.puzzlegame.spaservicegame.SpaServiceGameActivity
import com.productions.gizzmoo.pokemonpuzzleleague.settings.SettingsActivity

class MenuActivity : AppCompatActivity(), PanningLoopListener {

    private val currentFrameKey = "CURRENT_FRAME_KEY"
    private val directionKey = "DIRECTION_KEY"

    private lateinit var panningImage: ImageViewPanning
    private var panningLoop: PanningLoop? = null
    private var currentFrame: Int = 0
    private var direction: Direction = Direction.None

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)
        val marathonButton = findViewById<Button>(R.id.marathonButton)
        marathonButton.setOnClickListener {
            val intent = Intent(this@MenuActivity, MarathonActivity::class.java)
            startActivity(intent)
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
        }

        val puzzleAcademyButton = findViewById<Button>(R.id.puzzleAcademy)
        puzzleAcademyButton.setOnClickListener {
            val intent = Intent(this@MenuActivity, PuzzleAcademySelectionActivity::class.java)
            startActivity(intent)
            overridePendingTransition(R.anim.slide_out_left, R.anim.slide_in_right)
        }

        val spaServiceButton = findViewById<Button>(R.id.spaService)
        spaServiceButton.setOnClickListener {
            val intent = Intent(this@MenuActivity, SpaServiceGameActivity::class.java)
            startActivity(intent)
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
        }

        panningImage = findViewById(R.id.backgroundPanning)

        if (savedInstanceState != null) {
            currentFrame = savedInstanceState.getInt(currentFrameKey)
            direction = savedInstanceState.getSerializable(directionKey) as Direction
        } else {
            currentFrame = NUM_OF_SECS_TO_PAN_VIEW * MAX_FPS / 2
            direction = Direction.Right
        }

        panningImage.framesNeededForView = NUM_OF_SECS_TO_PAN_VIEW * MAX_FPS
        panningImage.setCurrentFrame(currentFrame)
        panningImage.direction = direction
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean =
        when (item.itemId) {
            R.id.action_setting -> {
                val intent = Intent(this@MenuActivity, SettingsActivity::class.java)
                startActivity(intent)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }

    override fun onStart() {
        super.onStart()
        panningLoop = PanningLoop()
        panningLoop?.listener = this
        panningLoop?.execute()
    }

    override fun onStop() {
        super.onStop()
        panningLoop?.cancel(true)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt(currentFrameKey, panningImage.currentFrame)
        outState.putSerializable(directionKey, panningImage.direction)
    }

    override fun updateImageView() {
        panningImage.moveFrame()
        panningImage.invalidate()
    }
}
