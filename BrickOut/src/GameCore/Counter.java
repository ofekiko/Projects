//ID: 322374240
//NAME: OFEK KAHARIZI
package GameCore;

/**
 * The Counter class is used to keep track of a count value.
 * It allows increasing, decreasing, and retrieving the current count.
 */
public class Counter {
    private int value;

    /**
     * Constructor for Counter. Initializes the count to 0.
     */
    public Counter() {
        value = 0;
    }

    /**
     * Adds a number to the current count.
     *
     * @param number the number to add to the count
     */
    void increase(int number) {
        value += number;
    }

    /**
     * Subtracts a number from the current count.
     *
     * @param number the number to subtract from the count
     */
    void decrease(int number) {
        value -= number;
    }

    /**
     * Returns the current count value.
     *
     * @return the current count
     */
    int getValue() {
        return value;
    }
}
