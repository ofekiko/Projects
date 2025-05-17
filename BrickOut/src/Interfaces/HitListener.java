//ID: 322374240
//NAME: OFEK KAHARIZI
package Interfaces;

import Sprites.Ball;
import Sprites.Block;

/**
 * The Interfaces.HitListener interface is used to handle events when a block is hit.
 */
public interface HitListener {

    /**
     * This method is called whenever the beingHit object is hit.
     *
     * @param beingHit the block that is being hit
     * @param hitter the ball that caused the hit
     */
    void hitEvent(Block beingHit, Ball hitter);
}
