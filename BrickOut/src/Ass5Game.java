//ID: 322374240
//NAME: OFEK KAHARIZI

import GameCore.Game;

/**
 * The main entry point of the game application.
 */
public class Ass5Game {
    /**
     * The main method of the application. It initializes a new game and starts running it.
     *
     * @param args command-line arguments (not used)
     */
    public static void main(String[] args) {
        Game game = new Game();
        game.initialize();
        game.run();
    }
}