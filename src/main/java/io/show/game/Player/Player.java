package io.show.game.Player;

import io.show.game.world.Constants;

public class Player {
    /*
    Player
        v
        A
    ----------- Position = 1 block
    //////////  Detailed pos = between blocks
     */
    private int m_XPosition = 0;
    private double m_XDetailedPos = 0;
    private int m_YPosition = 1800;
    private double m_YDetailedPos = 0;
    private double m_JumpStartspeed = 0;

    public void moveRight() {
        m_XDetailedPos += 0.05;
        if (m_XDetailedPos > 0.5) {
            m_XPosition++;
            m_XDetailedPos = -m_XDetailedPos;
        }
    }

    public void moveLeft() {
        m_XDetailedPos -= 0.05;
        if (m_XDetailedPos < -0.5) {
            m_XPosition--;
            m_XDetailedPos = -m_XDetailedPos;
        }
    }

    public void moveUp() {
        m_YDetailedPos += 0.05;
        if (m_YDetailedPos > 0.5) {
            m_YPosition++;
            m_YDetailedPos = -m_YDetailedPos;
        }
    }

    public void moveDown() {
        m_YDetailedPos -= 0.05;
        if (m_YDetailedPos < -0.5) {
            m_YPosition--;
            m_YDetailedPos = -m_YDetailedPos;
        }
    }

    public void fall() {
        m_YDetailedPos += Constants.GRAVITY * (1 / Constants.FPS) * (1 / Constants.FPS);
        m_YPosition += (int) Math.floor(m_YDetailedPos);
        m_YDetailedPos = m_YPosition - m_YPosition;
    }

    public void jump() {
        m_YDetailedPos += m_JumpStartspeed;
    }

}
