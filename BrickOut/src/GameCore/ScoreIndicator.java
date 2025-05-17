//ID: 322374240
//NAME: OFEK KAHARIZI
package GameCore;

import Interfaces.Sprite;
import biuoop.DrawSurface;

import java.awt.Color;

/**
 * The GameCore.ScoreIndicator class is responsible for displaying the current score
 * on the screen. It is a sprite that can be added to the game.
 */
public class ScoreIndicator implements Sprite {

    private Counter scoreCounter;

    /**
     * Constructor for GameCore.ScoreIndicator.
     *
     * @param scoreCounter the counter that keeps track of the current score
     */
    public ScoreIndicator(Counter scoreCounter) {
        this.scoreCounter = scoreCounter;
    }

    /**
     * Draws the score on the given DrawSurface.
     *
     * @param d the DrawSurface to draw on
     */
    @Override
    public void drawOn(DrawSurface d) {
        int score = this.scoreCounter.getValue();
        d.setColor(Color.BLACK);
        d.drawText(330, 25, "SCORE: " + score, 30);
    }

    /**
     * Notifies the sprite that time has passed.
     * This implementation does nothing as the GameCore.ScoreIndicator does not change over time.
     */
    @Override
    public void timePassed() {
        // No action needed
    }

    /**
     * Adds the GameCore.ScoreIndicator to the game as a sprite.
     *
     * @param game the game to which this sprite will be added
     */
    public void addToGame(Game game) {
        game.addSprite(this);
    }
}
