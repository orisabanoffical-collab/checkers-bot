package org.example.geometrydash;

import javax.swing.JPanel;
import javax.swing.Timer;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

public class GeometryDashPanel extends JPanel {
    private static final int WIDTH = 900;
    private static final int HEIGHT = 500;
    private static final int GROUND_Y = 400;

    private static final int PLAYER_SIZE = 36;
    private static final int GRAVITY = 2;
    private static final int JUMP_VELOCITY = -18;
    private static final int MAX_FALL_SPEED = 20;
    private static final int COYOTE_FRAMES = 6;
    private static final int JUMP_BUFFER_FRAMES = 6;

    private final Timer timer;
    private final Random random = new Random();

    private int playerX = 140;
    private int playerY = GROUND_Y - PLAYER_SIZE;
    private int velocityY = 0;
    private boolean onGround = true;
    private int coyoteFramesLeft = COYOTE_FRAMES;
    private int jumpBufferFramesLeft = 0;
    private boolean jumpHeld = false;

    private int tick = 0;
    private int score = 0;
    private int speed = 7;
    private boolean gameOver = false;

    private final List<Obstacle> obstacles = new ArrayList<>();
    private final List<Platform> platforms = new ArrayList<>();

    public GeometryDashPanel() {
        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        setBackground(new Color(20, 25, 45));
        setFocusable(true);

        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_SPACE || e.getKeyCode() == KeyEvent.VK_UP) {
                    jumpHeld = true;
                    if (gameOver) {
                        reset();
                    } else {
                        jumpBufferFramesLeft = JUMP_BUFFER_FRAMES;
                    }
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_SPACE || e.getKeyCode() == KeyEvent.VK_UP) {
                    jumpHeld = false;
                    if (velocityY < -8) {
                        velocityY = -8;
                    }
                }
            }
        });

        timer = new Timer(16, e -> updateGame());
        timer.start();
    }

    private void jump() {
        if (onGround || coyoteFramesLeft > 0) {
            velocityY = JUMP_VELOCITY;
            onGround = false;
            coyoteFramesLeft = 0;
            jumpBufferFramesLeft = 0;
        }
    }

    private void updateGame() {
        if (!gameOver) {
            tick++;
            score++;
            if (tick % 240 == 0 && speed < 14) {
                speed++;
            }

            if (tick % 70 == 0) {
                int height = 20 + random.nextInt(55);
                int width = 25 + random.nextInt(30);
                obstacles.add(new Obstacle(WIDTH + 20, GROUND_Y - height, width, height));
            }

            if (tick % 210 == 0) {
                int width = 80 + random.nextInt(80);
                int y = 250 + random.nextInt(90);
                platforms.add(new Platform(WIDTH + 40, y, width, 14));
            }

            if (jumpBufferFramesLeft > 0) {
                jumpBufferFramesLeft--;
                if (onGround || coyoteFramesLeft > 0) {
                    jump();
                }
            }

            velocityY += GRAVITY;
            if (!jumpHeld && velocityY < 0) {
                velocityY += 1;
            }
            if (velocityY > MAX_FALL_SPEED) {
                velocityY = MAX_FALL_SPEED;
            }
            resolveVerticalMovement();

            if (onGround) {
                coyoteFramesLeft = COYOTE_FRAMES;
            } else if (coyoteFramesLeft > 0) {
                coyoteFramesLeft--;
            }

            updatePlatforms();
            updateObstacles();
        }

        repaint();
    }

    private void updatePlatforms() {
        Iterator<Platform> it = platforms.iterator();
        while (it.hasNext()) {
            Platform platform = it.next();
            platform.x -= speed;
            if (platform.x + platform.w < 0) {
                it.remove();
            }
        }
    }

    private void updateObstacles() {
        Iterator<Obstacle> it = obstacles.iterator();
        while (it.hasNext()) {
            Obstacle ob = it.next();

            for (int i = 0; i < speed; i++) {
                ob.x -= 1;
                if (collides(ob)) {
                    gameOver = true;
                    return;
                }
            }

            if (ob.x + ob.w < 0) {
                it.remove();
            }
        }
    }

    private void resolveVerticalMovement() {
        onGround = false;
        int steps = Math.max(1, Math.abs(velocityY));
        int direction = Integer.compare(velocityY, 0);

        for (int i = 0; i < steps; i++) {
            if (direction == 0) {
                break;
            }

            int nextY = playerY + direction;

            if (direction > 0) {
                Platform landing = platformBelow(nextY + PLAYER_SIZE);
                if (landing != null) {
                    playerY = landing.y - PLAYER_SIZE;
                    velocityY = 0;
                    onGround = true;
                    return;
                }
            }

            if (direction < 0) {
                Platform hit = platformAbove(nextY);
                if (hit != null) {
                    playerY = hit.y + hit.h;
                    velocityY = 0;
                    return;
                }
            }

            playerY = nextY;

            if (playerY >= GROUND_Y - PLAYER_SIZE) {
                playerY = GROUND_Y - PLAYER_SIZE;
                velocityY = 0;
                onGround = true;
                return;
            }
        }
    }

    private Platform platformBelow(int nextBottom) {
        Rectangle playerRect = new Rectangle(playerX, playerY, PLAYER_SIZE, PLAYER_SIZE);
        for (Platform platform : platforms) {
            boolean horizontalOverlap = playerRect.x < platform.x + platform.w
                    && playerRect.x + playerRect.width > platform.x;
            boolean crossesTop = playerY + PLAYER_SIZE <= platform.y && nextBottom >= platform.y;
            if (horizontalOverlap && crossesTop) {
                return platform;
            }
        }
        return null;
    }

    private Platform platformAbove(int nextTop) {
        Rectangle playerRect = new Rectangle(playerX, playerY, PLAYER_SIZE, PLAYER_SIZE);
        for (Platform platform : platforms) {
            boolean horizontalOverlap = playerRect.x < platform.x + platform.w
                    && playerRect.x + playerRect.width > platform.x;
            boolean crossesBottom = playerY >= platform.y + platform.h && nextTop <= platform.y + platform.h;
            if (horizontalOverlap && crossesBottom) {
                return platform;
            }
        }
        return null;
    }

    private boolean collides(Obstacle ob) {
        return playerX < ob.x + ob.w
                && playerX + PLAYER_SIZE > ob.x
                && playerY < ob.y + ob.h
                && playerY + PLAYER_SIZE > ob.y;
    }

    private void reset() {
        obstacles.clear();
        platforms.clear();
        playerY = GROUND_Y - PLAYER_SIZE;
        velocityY = 0;
        onGround = true;
        coyoteFramesLeft = COYOTE_FRAMES;
        jumpBufferFramesLeft = 0;
        jumpHeld = false;
        tick = 0;
        score = 0;
        speed = 7;
        gameOver = false;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        g2.setColor(new Color(38, 45, 75));
        g2.fillRect(0, GROUND_Y, WIDTH, HEIGHT - GROUND_Y);

        g2.setColor(new Color(0, 240, 255));
        g2.fillRect(playerX, playerY, PLAYER_SIZE, PLAYER_SIZE);

        g2.setColor(new Color(255, 80, 80));
        for (Obstacle ob : obstacles) {
            g2.fillRect(ob.x, ob.y, ob.w, ob.h);
        }

        g2.setColor(new Color(120, 220, 255));
        for (Platform platform : platforms) {
            g2.fillRoundRect(platform.x, platform.y, platform.w, platform.h, 8, 8);
        }

        g2.setColor(Color.WHITE);
        g2.setFont(new Font(Font.MONOSPACED, Font.BOLD, 20));
        g2.drawString("Score: " + score, 20, 35);
        g2.drawString("Speed: " + speed, 20, 65);
        g2.drawString("SPACE/UP = Jump", 20, 95);

        if (gameOver) {
            g2.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 42));
            g2.drawString("GAME OVER", WIDTH / 2 - 140, HEIGHT / 2 - 20);
            g2.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 22));
            g2.drawString("Press SPACE to restart", WIDTH / 2 - 130, HEIGHT / 2 + 25);
        }
    }

    private static class Obstacle {
        int x;
        final int y;
        final int w;
        final int h;

        Obstacle(int x, int y, int w, int h) {
            this.x = x;
            this.y = y;
            this.w = w;
            this.h = h;
        }
    }

    private static class Platform {
        int x;
        final int y;
        final int w;
        final int h;

        Platform(int x, int y, int w, int h) {
            this.x = x;
            this.y = y;
            this.w = w;
            this.h = h;
        }
    }
}
