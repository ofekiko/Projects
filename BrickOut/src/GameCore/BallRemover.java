//ID: 322374240
//NAME: OFEK KAHARIZI
package GameCore;

import Interfaces.HitListener;
import Sprites.Ball;
import Sprites.Block;

/**
 * The GameCore.BallRemover class is in charge of removing balls from the game
 * when a hit event occurs. It also keeps track of the number of remaining balls.
 */
public class BallRemover implements HitListener {

    private Game game;
    private Counter remainingBalls;

    /**
     * Constructor for GameCore.BallRemover.
     *
     * @param game           the game from which balls will be removed
     * @param remainingBalls the counter that tracks the number of remaining balls
     */
    public BallRemover(Game game, Counter remainingBalls) {
        this.game = game;
        this.remainingBalls = remainingBalls;
    }

    /**
     * Gets the counter of remaining balls.
     *
     * @return the counter of remaining balls
     */
    public Counter getCounter() {
        return remainingBalls;
    }

    /**
     * Handles the hit event. When a ball hits a block, the ball is removed
     * from the game, and the remaining balls counter is decreased by 1.
     *
     * @param beingHit the block that was hit (not used here, but part of the event)
     * @param hitter   the ball that caused the hit
     */
    public void hitEvent(Block beingHit, Ball hitter) {
        remainingBalls.decrease(1);
        hitter.removeFromGame(game);
    }
}
