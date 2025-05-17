//ID: 322374240
//NAME: OFEK KAHARIZI
package GameCore;

import Interfaces.HitListener;
import Sprites.Ball;
import Sprites.Block;


/**
 * The GameCore.BlockRemover class is responsible for removing blocks from the game
 * when they are hit. It also keeps track of the number of remaining blocks.
 */
public class BlockRemover implements HitListener {

    private Game game;
    private Counter remainingBlocks;

    /**
     * Constructor for GameCore.BlockRemover.
     *
     * @param game            the game from which blocks will be removed
     * @param remainingBlocks the counter that tracks the number of remaining blocks
     */
    public BlockRemover(Game game, Counter remainingBlocks) {
        this.game = game;
        this.remainingBlocks = remainingBlocks;
    }

    /**
     * Gets the counter of remaining blocks.
     *
     * @return the counter of remaining blocks
     */
    public Counter getCounter() {
        return remainingBlocks;
    }

    /**
     * Handles the hit event. When a block is hit, its color is transferred to the ball,
     * the block is removed from the game, and the remaining blocks counter is decreased by 1.
     *
     * @param beingHit the block that was hit
     * @param hitter   the ball that caused the hit
     */
    public void hitEvent(Block beingHit, Ball hitter) {
        hitter.setColor(beingHit.getColor());
        remainingBlocks.decrease(1);
        beingHit.removeHitListener(this);
        beingHit.removeFromGame(game);
    }
}
