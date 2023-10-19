package io.show.game.physics;

import io.show.graphics.Graphics;
import io.show.graphics.Input;
import org.joml.Vector2f;

public class PlayerPhysics {
    private Vector2f speed = new Vector2f();
    private Vector2f playerPos;
    private Graphics g;
    private float deltaT;

    public PlayerPhysics(Graphics Graphicsclass) {
        g = Graphicsclass;
        playerPos = g.getPlayerPosition();
    }

    public void setSpeed(Vector2f newSpeed) {
        speed = newSpeed;
    }

    public void setXSpeed(float newSpeed) {
        speed = new Vector2f(newSpeed, speed.y);
    }

    public void setYSpeed(float newSpeed) {
        speed = new Vector2f(speed.x, newSpeed);
    }

    public void addSpeed(Vector2f additionalSpeed) {
        float newx = speed.x + additionalSpeed.x;
        float newy = speed.y + additionalSpeed.y;
        speed = new Vector2f(newx, newy);
    }

    public void addXSpeed(float additionalSpeed) {
        float newx = speed.x + additionalSpeed;
        speed = new Vector2f(newx, speed.y);
    }

    public void addYSpeed(float additionalSpeed) {
        float newy = speed.y + additionalSpeed;
        speed = new Vector2f(speed.x, newy);
    }

    private void move() {
        if (speed.x > 50) speed.x = 50f;
        if (speed.y > 50) speed.y = 50f;
        if (speed.x < -50) speed.x = -50f;
        if (speed.y < -50) speed.y = -50f;

        float newx = playerPos.x + Input.getDeltaTime() * speed.x;
        float newy = playerPos.y + Input.getDeltaTime() * speed.y;
        playerPos = new Vector2f(newx, newy);
    }

    public void update() {
        playerPos = g.getPlayerPosition();
        move();
        g.setPlayerPosition(playerPos);
    }
}
