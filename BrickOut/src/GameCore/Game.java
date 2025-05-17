package GameCore;
//ID: 322374240
//NAME: OFEK KAHARIZI

import Interfaces.Collidable;
import Interfaces.Sprite;
import Collision.Velocity;
import Geometry.Point;
import Geometry.Rectangle;
import Sprites.Ball;
import Sprites.Block;
import Sprites.Paddle;
import Sprites.SpriteCollection;
import biuoop.DrawSurface;
import biuoop.GUI;
import biuoop.Sleeper;

import java.awt.Color;

/**
 * The GameCore.Game class represents a simple game.
 * It manages the game environment, the sprites, and the animation loop.
 */
public class Game {
    private SpriteCollection sprites;
    private GameEnvironment environment;
    private GUI gui = new GUI("GameCore.Game", 800, 600);
    private final Counter ballCounter = new Counter();
    private final Counter blockCounter = new Counter();
    private final Counter scoreCounter = new Counter();

    /**
     * Adds a collidable object to the game environment.
     *
     * @param c The collidable object to add.
     */
    public void addCollidable(Collidable c) {
        environment.addCollidable(c);
    }
    /**
     * Removes a collidable object from the game environment.
     *
     * @param c The collidable object to remove.
     */
    public void removeCollidable(Collidable c) {
        environment.removeCollidable(c);
    }

    /**
     * Adds a sprite to the game.
     *
     * @param s The sprite to add.
     */
    public void addSprite(Sprite s) {
        sprites.addSprite(s);
    }
    /**
     * Removes a sprite from the game.
     *
     * @param s The sprite to remove.
     */
    public void removeSprite(Sprite s) {
        sprites.removeSprite(s);
    }

    /**
     * Initializes the game.
     * Creates and adds all the objects like balls, paddle, blocks, and walls to the game.
     */
    public void initialize() {
        sprites = new SpriteCollection();
        environment = new GameEnvironment();

        // Sprites.Paddle
        Rectangle paddleBounds = new Rectangle(new Point(150, 580), 100, 10);
        biuoop.KeyboardSensor keyboard = gui.getKeyboardSensor();
        Paddle paddle = new Paddle(paddleBounds, keyboard);
        paddle.addToGame(this);

        // Balls
        BallRemover ballRemover = new BallRemover(this, ballCounter);
        Ball ball1 = new Ball(new Point(300, 400), 5, Color.white, environment);
        Ball ball2 = new Ball(new Point(300, 350), 5, Color.white, environment);
        Ball ball3 = new Ball(new Point(300, 500), 5, Color.white, environment);
        ball1.setVelocity(Velocity.fromAngleAndSpeed(275, 5));
        ball2.setVelocity(Velocity.fromAngleAndSpeed(330, 5));
        ball3.setVelocity(Velocity.fromAngleAndSpeed(250, 5));
        ball1.addToGame(this);
        ballRemover.getCounter().increase(1);
        ball2.addToGame(this);
        ballRemover.getCounter().increase(1);
        ball3.addToGame(this);
        ballRemover.getCounter().increase(1);
        Block deathRegion = new Block(new Rectangle(new Point(0, 600), 800, 20));
        deathRegion.addToGame(this);
        deathRegion.addHitListener(ballRemover);

        // Walls
        Block leftWallBlock = new Block(new Rectangle(new Point(0, 0), 30, 600));
        Block rightWallBlock = new Block(new Rectangle(new Point(770, 0), 30, 600));
        Block topWallBlock = new Block(new Rectangle(new Point(0, 0), 800, 30));
        leftWallBlock.addToGame(this);
        rightWallBlock.addToGame(this);
        topWallBlock.addToGame(this);

        // Blocks
        ScoreTrackingListener scoreTrackingListener = new ScoreTrackingListener(scoreCounter);
        BlockRemover blockRemover = new BlockRemover(this, blockCounter);
        for (int i = 0; i <= 9; i++) {
            Block block = new Block(new Rectangle(new Point(45 + 70 * i, 30), 70, 25));
            block.addToGame(this);
            block.addHitListener(blockRemover);
            block.addHitListener(scoreTrackingListener);
            blockRemover.getCounter().increase(1);
        }
        for (int i = 0; i <= 7; i++) {
            Block block = new Block(new Rectangle(new Point(115 + 70 * i, 55), 70, 25));
            block.addToGame(this);
            block.addHitListener(blockRemover);
            block.addHitListener(scoreTrackingListener);
            blockRemover.getCounter().increase(1);
        }
        for (int i = 0; i <= 5; i++) {
            Block block = new Block(new Rectangle(new Point(185 + 70 * i, 80), 70, 25));
            block.addToGame(this);
            block.addHitListener(blockRemover);
            block.addHitListener(scoreTrackingListener);
            blockRemover.getCounter().increase(1);
        }
        for (int i = 0; i <= 3; i++) {
            Block block = new Block(new Rectangle(new Point(255 + 70 * i, 105), 70, 25));
            block.addToGame(this);
            block.addHitListener(blockRemover);
            block.addHitListener(scoreTrackingListener);
            blockRemover.getCounter().increase(1);
        }
        for (int i = 0; i <= 1; i++) {
            Block block = new Block(new Rectangle(new Point(325 + 70 * i, 130), 70, 25));
            block.addToGame(this);
            block.addHitListener(blockRemover);
            block.addHitListener(scoreTrackingListener);
            blockRemover.getCounter().increase(1);
        }
        ScoreIndicator score = new ScoreIndicator(scoreCounter);
        score.addToGame(this);
    }

    /**
     * Runs the game loop, updating and drawing the game at a constant frame rate.
     */
    public void run() {
        Sleeper sleeper = new Sleeper();  // Controls the frame rate
        int framesPerSecond = 60;
        int millisecondsPerFrame = 1000 / framesPerSecond;

        while (true) {
            if (blockCounter.getValue() == 0) {
                scoreCounter.increase(100);
                System.out.println(scoreCounter.getValue());
                gui.close();
            }
            if (ballCounter.getValue() == 0) {
                System.out.println(scoreCounter.getValue());
                gui.close();
            }
            long startTime = System.currentTimeMillis(); // Timing for each frame
            DrawSurface d = gui.getDrawSurface();

            // Background
            d.setColor(Color.BLACK);
            d.fillRectangle(0, 0, 800, 600);

            // Draw and update game elements
            this.environment.drawAllOn(d);
            this.sprites.drawAllOn(d);
            gui.show(d);
            this.sprites.notifyAllTimePassed();

            // Frame rate control
            long usedTime = System.currentTimeMillis() - startTime;
            long milliSecondLeftToSleep = millisecondsPerFrame - usedTime;
            if (milliSecondLeftToSleep > 0) {
                sleeper.sleepFor(milliSecondLeftToSleep);
            }
        }
    }
}
