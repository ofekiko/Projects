//ID: 322374240
//NAME: OFEK KAHARIZI
package Sprites;

import Interfaces.Sprite;
import biuoop.DrawSurface;

import java.util.ArrayList;
import java.util.List;

/**
 * A collection of all the sprites in the game.
 */
public class SpriteCollection {
    private List<Sprite> sprites = new ArrayList<>();

    /**
     * Adds a sprite to the collection.
     *
     * @param s The sprite to add.
     */
    public void addSprite(Sprite s) {
        sprites.add(s);
    }

    /**
     * removes a sprite from the collection.
     *
     * @param s The sprite to remove.
     */
    public void removeSprite(Sprite s) {
        sprites.remove(s);
    }

    /**
     * Tells all sprites to do their time-based updates.
     */
    public void notifyAllTimePassed() {
        List<Sprite> spriteList = new ArrayList<Sprite>(this.sprites);
        for (Sprite s : spriteList) {
            s.timePassed();
        }
    }

    /**
     * Tells all sprites to draw themselves on the given surface.
     *
     * @param d The surface to draw on.
     */
    public void drawAllOn(DrawSurface d) {
        for (Sprite s : sprites) {
            s.drawOn(d);
        }
    }
}
