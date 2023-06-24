package io.show.graphics;

import io.show.graphics.internal.Animation;
import org.joml.Vector2f;

public class Player {

    public static final Animation ANIM_ATTACK = new Animation(4, 0);
    public static final Animation ANIM_ATTACK_2 = new Animation(6, 1);
    public static final Animation ANIM_ATTACK_2_NO_MOVEMENT = new Animation(6, 2);
    public static final Animation ANIM_ATTACK_COMBO_2_HIT = new Animation(10, 3);
    public static final Animation ANIM_ATTACK_COMBO_NO_MOVEMENT = new Animation(10, 4);
    public static final Animation ANIM_ATTACK_NO_MOVEMENT = new Animation(4, 5);
    public static final Animation ANIM_CROUCH = new Animation(1, 6);
    public static final Animation ANIM_CROUCH_ATTACK = new Animation(4, 7);
    public static final Animation ANIM_CROUCH_FULL = new Animation(3, 8);
    public static final Animation ANIM_CROUCH_TRANSITION = new Animation(1, 9);
    public static final Animation ANIM_CROUCH_WALK = new Animation(8, 10);
    public static final Animation ANIM_DASH = new Animation(2, 11);
    public static final Animation ANIM_DEATH = new Animation(10, 12);
    public static final Animation ANIM_DEATH_NO_MOVEMENT = new Animation(10, 13);
    public static final Animation ANIM_FALL = new Animation(3, 14);
    public static final Animation ANIM_HIT = new Animation(1, 15);
    public static final Animation ANIM_IDLE = new Animation(10, 16);
    public static final Animation ANIM_JUMP = new Animation(3, 17);
    public static final Animation ANIM_JUMP_FALL_IN_BETWEEN = new Animation(2, 18);
    public static final Animation ANIM_ROLL = new Animation(12, 19);
    public static final Animation ANIM_RUN = new Animation(10, 20);
    public static final Animation ANIM_SLIDE = new Animation(2, 21);
    public static final Animation ANIM_SLIDE_FULL = new Animation(4, 22);
    public static final Animation ANIM_SLIDE_TRANSITION_END = new Animation(1, 23);
    public static final Animation ANIM_SLIDE_TRANSITION_START = new Animation(1, 24);
    public static final Animation ANIM_TURN_AROUND = new Animation(3, 25);
    public static final Animation ANIM_WALL_CLIMB = new Animation(7, 26);
    public static final Animation ANIM_WALL_CLIMB_NO_MOVEMENT = new Animation(7, 27);
    public static final Animation ANIM_WALL_HANG = new Animation(1, 28);
    public static final Animation ANIM_WALL_SLIDE = new Animation(3, 29);

    public static Animation animationFromIndex(int index) {
        return switch (index) {
            case 0 -> ANIM_ATTACK;
            case 1 -> ANIM_ATTACK_2;
            case 2 -> ANIM_ATTACK_2_NO_MOVEMENT;
            case 3 -> ANIM_ATTACK_COMBO_2_HIT;
            case 4 -> ANIM_ATTACK_COMBO_NO_MOVEMENT;
            case 5 -> ANIM_ATTACK_NO_MOVEMENT;
            case 6 -> ANIM_CROUCH;
            case 7 -> ANIM_CROUCH_ATTACK;
            case 8 -> ANIM_CROUCH_FULL;
            case 9 -> ANIM_CROUCH_TRANSITION;
            case 10 -> ANIM_CROUCH_WALK;
            case 11 -> ANIM_DASH;
            case 12 -> ANIM_DEATH;
            case 13 -> ANIM_DEATH_NO_MOVEMENT;
            case 14 -> ANIM_FALL;
            case 15 -> ANIM_HIT;
            case 16 -> ANIM_IDLE;
            case 17 -> ANIM_JUMP;
            case 18 -> ANIM_JUMP_FALL_IN_BETWEEN;
            case 19 -> ANIM_ROLL;
            case 20 -> ANIM_RUN;
            case 21 -> ANIM_SLIDE;
            case 22 -> ANIM_SLIDE_FULL;
            case 23 -> ANIM_SLIDE_TRANSITION_END;
            case 24 -> ANIM_SLIDE_TRANSITION_START;
            case 25 -> ANIM_TURN_AROUND;
            case 26 -> ANIM_WALL_CLIMB;
            case 27 -> ANIM_WALL_CLIMB_NO_MOVEMENT;
            case 28 -> ANIM_WALL_HANG;
            case 29 -> ANIM_WALL_SLIDE;
            default -> throw new IllegalStateException("Undefined animation index " + index);
        };
    }

    private Animation m_CurrentAnimation = ANIM_RUN;
    private int m_CurrentFrame = 0;

    private Vector2f m_Position = new Vector2f();
    private int m_Layer = 0;
    private boolean m_LookingLeft = false;

    public Player() {
    }

    public Animation getCurrentAnimation() {
        return m_CurrentAnimation;
    }

    public int getCurrentFrame() {
        return m_CurrentFrame;
    }

    public void setCurrentAnimation(Animation animation) {
        m_CurrentAnimation = animation;
        m_CurrentFrame = 0;
    }

    public void setCurrentFrame(int frame) {
        m_CurrentFrame = frame;
    }

    public Vector2f getPosition() {
        return m_Position;
    }

    public int getLayer() {
        return m_Layer;
    }

    public boolean isLookingLeft() {
        return m_LookingLeft;
    }

    public void setLookingLeft(boolean lookingLeft) {
        m_LookingLeft = lookingLeft;
    }

    public void setLayer(int layer) {
        m_Layer = layer;
    }

    public void nextFrame() {
        m_CurrentFrame++;
        if (m_CurrentFrame >= m_CurrentAnimation.frames()) m_CurrentFrame = 0;
    }
}
