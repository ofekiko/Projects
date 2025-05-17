//ID: 322374240
//NAME: OFEK KAHARIZI
package GameCore;

import Interfaces.HitListener;
import Sprites.Ball;
import Sprites.Block;

/**
 * Listener that tracks the game score.
 */
public class ScoreTrackingListener implements HitListener {
    private final Counter currentScore;

    /**
     * Creates a new score tracking listener.
     *
     * @param scoreCounter the counter used to keep track of the current score
     */
    public ScoreTrackingListener(Counter scoreCounter) {
        this.currentScore = scoreCounter;
    }

    /**
     * Handles the event when a block is hit by increasing the score by 5 points.
     *
     * @param beingHit the block that was hit
     * @param hitter the ball that hit the block
     */
    @Override
    public void hitEvent(Block beingHit, Ball hitter) {
        this.currentScore.increase(5);
    }
}