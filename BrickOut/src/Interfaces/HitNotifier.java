//ID: 322374240
//NAME: OFEK KAHARIZI
package Interfaces;

/**
 * The Interfaces.HitNotifier interface is used to manage hit event listeners.
 * Objects that can notify listeners of hit events should implement this interface.
 */
public interface HitNotifier {

    /**
     * Adds a Interfaces.HitListener to the list of listeners for hit events.
     *
     * @param hl the Interfaces.HitListener to add
     */
    void addHitListener(HitListener hl);

    /**
     * Removes a Interfaces.HitListener from the list of listeners for hit events.
     *
     * @param hl the Interfaces.HitListener to remove
     */
    void removeHitListener(HitListener hl);
}
