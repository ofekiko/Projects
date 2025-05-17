//ID: 322374240
//NAME: OFEK KAHARIZI
package Interfaces;

import biuoop.DrawSurface;

/**
 * Represents a sprite that can be drawn to a screen and updated over time.
 */
public interface Sprite {

    /**
     * Draws the sprite on the given draw surface.
     *
     * @param d the draw surface on which the sprite will be drawn
     */
    void drawOn(DrawSurface d);

    /**
     * Notifies the sprite that time has passed, allowing it to update its state.
     */
    void timePassed();
}
