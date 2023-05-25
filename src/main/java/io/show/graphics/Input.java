package io.show.graphics;

import java.util.HashMap;
import java.util.Map;

import static org.lwjgl.glfw.GLFW.*;

public class Input {

    public enum KeyCode {
        SPACE, APOSTROPHE, COMMA, MINUS, PERIOD, SLASH,

        A0, A1, A2, A3, A4, A5, A6, A7, A8, A9,

        SEMICOLON, EQUAL,

        A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, V, W, X, Y, Z,

        LEFT_BRACKET, BACKSLASH, RIGHT_BRACKET, GRAVE_ACCENT, WORLD_1, WORLD_2,

        ESCAPE, ENTER, TAB, BACKSPACE, INSERT, DELETE,

        RIGHT, LEFT, DOWN, UP,

        PAGE_UP, PAGE_DOWN, HOME, END, CAPS_LOCK, SCROLL_LOCK, NUM_LOCK, PRINT_SCREEN, PAUSE,

        F1, F2, F3, F4, F5, F6, F7, F8, F9, F10, F11, F12, F13, F14, F15, F16, F17, F18, F19, F20, F21, F22, F23, F24, F25,

        KP_0, KP_1, KP_2, KP_3, KP_4, KP_5, KP_6, KP_7, KP_8, KP_9, KP_DECIMAL, KP_DIVIDE, KP_MULTIPLY, KP_SUBTRACT, KP_ADD, KP_ENTER, KP_EQUAL,

        LEFT_SHIFT, LEFT_CONTROL, LEFT_ALT, LEFT_SUPER, RIGHT_SHIFT, RIGHT_CONTROL, RIGHT_ALT, RIGHT_SUPER, MENU,

        UNKNOWN
    }

    private static class KeyState {
        boolean prev, now;
    }

    private static int getGLFWKeyCode(KeyCode keyCode) {
        return switch (keyCode) {

            case SPACE -> GLFW_KEY_SPACE;
            case APOSTROPHE -> GLFW_KEY_APOSTROPHE;
            case COMMA -> GLFW_KEY_COMMA;
            case MINUS -> GLFW_KEY_MINUS;
            case PERIOD -> GLFW_KEY_PERIOD;
            case SLASH -> GLFW_KEY_SLASH;
            case A0 -> GLFW_KEY_0;
            case A1 -> GLFW_KEY_1;
            case A2 -> GLFW_KEY_2;
            case A3 -> GLFW_KEY_3;
            case A4 -> GLFW_KEY_4;
            case A5 -> GLFW_KEY_5;
            case A6 -> GLFW_KEY_6;
            case A7 -> GLFW_KEY_7;
            case A8 -> GLFW_KEY_8;
            case A9 -> GLFW_KEY_9;
            case SEMICOLON -> GLFW_KEY_SEMICOLON;
            case EQUAL -> GLFW_KEY_EQUAL;
            case A -> GLFW_KEY_A;
            case B -> GLFW_KEY_B;
            case C -> GLFW_KEY_C;
            case D -> GLFW_KEY_D;
            case E -> GLFW_KEY_E;
            case F -> GLFW_KEY_F;
            case G -> GLFW_KEY_G;
            case H -> GLFW_KEY_H;
            case I -> GLFW_KEY_I;
            case J -> GLFW_KEY_J;
            case K -> GLFW_KEY_K;
            case L -> GLFW_KEY_L;
            case M -> GLFW_KEY_M;
            case N -> GLFW_KEY_N;
            case O -> GLFW_KEY_O;
            case P -> GLFW_KEY_P;
            case Q -> GLFW_KEY_Q;
            case R -> GLFW_KEY_R;
            case S -> GLFW_KEY_S;
            case T -> GLFW_KEY_T;
            case U -> GLFW_KEY_U;
            case V -> GLFW_KEY_V;
            case W -> GLFW_KEY_W;
            case X -> GLFW_KEY_X;
            case Y -> GLFW_KEY_Y;
            case Z -> GLFW_KEY_Z;
            case LEFT_BRACKET -> GLFW_KEY_LEFT_BRACKET;
            case BACKSLASH -> GLFW_KEY_BACKSLASH;
            case RIGHT_BRACKET -> GLFW_KEY_RIGHT_BRACKET;
            case GRAVE_ACCENT -> GLFW_KEY_GRAVE_ACCENT;
            case WORLD_1 -> GLFW_KEY_WORLD_1;
            case WORLD_2 -> GLFW_KEY_WORLD_2;
            case ESCAPE -> GLFW_KEY_ESCAPE;
            case ENTER -> GLFW_KEY_ENTER;
            case TAB -> GLFW_KEY_TAB;
            case BACKSPACE -> GLFW_KEY_BACKSPACE;
            case INSERT -> GLFW_KEY_INSERT;
            case DELETE -> GLFW_KEY_DELETE;
            case RIGHT -> GLFW_KEY_RIGHT;
            case LEFT -> GLFW_KEY_LEFT;
            case DOWN -> GLFW_KEY_DOWN;
            case UP -> GLFW_KEY_UP;
            case PAGE_UP -> GLFW_KEY_PAGE_UP;
            case PAGE_DOWN -> GLFW_KEY_PAGE_DOWN;
            case HOME -> GLFW_KEY_HOME;
            case END -> GLFW_KEY_END;
            case CAPS_LOCK -> GLFW_KEY_CAPS_LOCK;
            case SCROLL_LOCK -> GLFW_KEY_SCROLL_LOCK;
            case NUM_LOCK -> GLFW_KEY_NUM_LOCK;
            case PRINT_SCREEN -> GLFW_KEY_PRINT_SCREEN;
            case PAUSE -> GLFW_KEY_PAUSE;
            case F1 -> GLFW_KEY_F1;
            case F2 -> GLFW_KEY_F2;
            case F3 -> GLFW_KEY_F3;
            case F4 -> GLFW_KEY_F4;
            case F5 -> GLFW_KEY_F5;
            case F6 -> GLFW_KEY_F6;
            case F7 -> GLFW_KEY_F7;
            case F8 -> GLFW_KEY_F8;
            case F9 -> GLFW_KEY_F9;
            case F10 -> GLFW_KEY_F10;
            case F11 -> GLFW_KEY_F11;
            case F12 -> GLFW_KEY_F12;
            case F13 -> GLFW_KEY_F13;
            case F14 -> GLFW_KEY_F14;
            case F15 -> GLFW_KEY_F15;
            case F16 -> GLFW_KEY_F16;
            case F17 -> GLFW_KEY_F17;
            case F18 -> GLFW_KEY_F18;
            case F19 -> GLFW_KEY_F19;
            case F20 -> GLFW_KEY_F20;
            case F21 -> GLFW_KEY_F21;
            case F22 -> GLFW_KEY_F22;
            case F23 -> GLFW_KEY_F23;
            case F24 -> GLFW_KEY_F24;
            case F25 -> GLFW_KEY_F25;
            case KP_0 -> GLFW_KEY_KP_0;
            case KP_1 -> GLFW_KEY_KP_1;
            case KP_2 -> GLFW_KEY_KP_2;
            case KP_3 -> GLFW_KEY_KP_3;
            case KP_4 -> GLFW_KEY_KP_4;
            case KP_5 -> GLFW_KEY_KP_5;
            case KP_6 -> GLFW_KEY_KP_6;
            case KP_7 -> GLFW_KEY_KP_7;
            case KP_8 -> GLFW_KEY_KP_8;
            case KP_9 -> GLFW_KEY_KP_9;
            case KP_DECIMAL -> GLFW_KEY_KP_DECIMAL;
            case KP_DIVIDE -> GLFW_KEY_KP_DIVIDE;
            case KP_MULTIPLY -> GLFW_KEY_KP_MULTIPLY;
            case KP_SUBTRACT -> GLFW_KEY_KP_SUBTRACT;
            case KP_ADD -> GLFW_KEY_KP_ADD;
            case KP_ENTER -> GLFW_KEY_KP_ENTER;
            case KP_EQUAL -> GLFW_KEY_KP_EQUAL;
            case LEFT_SHIFT -> GLFW_KEY_LEFT_SHIFT;
            case LEFT_CONTROL -> GLFW_KEY_LEFT_CONTROL;
            case LEFT_ALT -> GLFW_KEY_LEFT_ALT;
            case LEFT_SUPER -> GLFW_KEY_LEFT_SUPER;
            case RIGHT_SHIFT -> GLFW_KEY_RIGHT_SHIFT;
            case RIGHT_CONTROL -> GLFW_KEY_RIGHT_CONTROL;
            case RIGHT_ALT -> GLFW_KEY_RIGHT_ALT;
            case RIGHT_SUPER -> GLFW_KEY_RIGHT_SUPER;
            case MENU -> GLFW_KEY_MENU;
            case UNKNOWN -> GLFW_KEY_UNKNOWN;
        };
    }

    public static KeyCode getKeyCode(int keyCode) {
        return switch (keyCode) {

            case GLFW_KEY_SPACE -> KeyCode.SPACE;
            case GLFW_KEY_APOSTROPHE -> KeyCode.APOSTROPHE;
            case GLFW_KEY_COMMA -> KeyCode.COMMA;
            case GLFW_KEY_MINUS -> KeyCode.MINUS;
            case GLFW_KEY_PERIOD -> KeyCode.PERIOD;
            case GLFW_KEY_SLASH -> KeyCode.SLASH;
            case GLFW_KEY_0 -> KeyCode.A0;
            case GLFW_KEY_1 -> KeyCode.A1;
            case GLFW_KEY_2 -> KeyCode.A2;
            case GLFW_KEY_3 -> KeyCode.A3;
            case GLFW_KEY_4 -> KeyCode.A4;
            case GLFW_KEY_5 -> KeyCode.A5;
            case GLFW_KEY_6 -> KeyCode.A6;
            case GLFW_KEY_7 -> KeyCode.A7;
            case GLFW_KEY_8 -> KeyCode.A8;
            case GLFW_KEY_9 -> KeyCode.A9;
            case GLFW_KEY_SEMICOLON -> KeyCode.SEMICOLON;
            case GLFW_KEY_EQUAL -> KeyCode.EQUAL;
            case GLFW_KEY_A -> KeyCode.A;
            case GLFW_KEY_B -> KeyCode.B;
            case GLFW_KEY_C -> KeyCode.C;
            case GLFW_KEY_D -> KeyCode.D;
            case GLFW_KEY_E -> KeyCode.E;
            case GLFW_KEY_F -> KeyCode.F;
            case GLFW_KEY_G -> KeyCode.G;
            case GLFW_KEY_H -> KeyCode.H;
            case GLFW_KEY_I -> KeyCode.I;
            case GLFW_KEY_J -> KeyCode.J;
            case GLFW_KEY_K -> KeyCode.K;
            case GLFW_KEY_L -> KeyCode.L;
            case GLFW_KEY_M -> KeyCode.M;
            case GLFW_KEY_N -> KeyCode.N;
            case GLFW_KEY_O -> KeyCode.O;
            case GLFW_KEY_P -> KeyCode.P;
            case GLFW_KEY_Q -> KeyCode.Q;
            case GLFW_KEY_R -> KeyCode.R;
            case GLFW_KEY_S -> KeyCode.S;
            case GLFW_KEY_T -> KeyCode.T;
            case GLFW_KEY_U -> KeyCode.U;
            case GLFW_KEY_V -> KeyCode.V;
            case GLFW_KEY_W -> KeyCode.W;
            case GLFW_KEY_X -> KeyCode.X;
            case GLFW_KEY_Y -> KeyCode.Y;
            case GLFW_KEY_Z -> KeyCode.Z;
            case GLFW_KEY_LEFT_BRACKET -> KeyCode.LEFT_BRACKET;
            case GLFW_KEY_BACKSLASH -> KeyCode.BACKSLASH;
            case GLFW_KEY_RIGHT_BRACKET -> KeyCode.RIGHT_BRACKET;
            case GLFW_KEY_GRAVE_ACCENT -> KeyCode.GRAVE_ACCENT;
            case GLFW_KEY_WORLD_1 -> KeyCode.WORLD_1;
            case GLFW_KEY_WORLD_2 -> KeyCode.WORLD_2;
            case GLFW_KEY_ESCAPE -> KeyCode.ESCAPE;
            case GLFW_KEY_ENTER -> KeyCode.ENTER;
            case GLFW_KEY_TAB -> KeyCode.TAB;
            case GLFW_KEY_BACKSPACE -> KeyCode.BACKSPACE;
            case GLFW_KEY_INSERT -> KeyCode.INSERT;
            case GLFW_KEY_DELETE -> KeyCode.DELETE;
            case GLFW_KEY_RIGHT -> KeyCode.RIGHT;
            case GLFW_KEY_LEFT -> KeyCode.LEFT;
            case GLFW_KEY_DOWN -> KeyCode.DOWN;
            case GLFW_KEY_UP -> KeyCode.UP;
            case GLFW_KEY_PAGE_UP -> KeyCode.PAGE_UP;
            case GLFW_KEY_PAGE_DOWN -> KeyCode.PAGE_DOWN;
            case GLFW_KEY_HOME -> KeyCode.HOME;
            case GLFW_KEY_END -> KeyCode.END;
            case GLFW_KEY_CAPS_LOCK -> KeyCode.CAPS_LOCK;
            case GLFW_KEY_SCROLL_LOCK -> KeyCode.SCROLL_LOCK;
            case GLFW_KEY_NUM_LOCK -> KeyCode.NUM_LOCK;
            case GLFW_KEY_PRINT_SCREEN -> KeyCode.PRINT_SCREEN;
            case GLFW_KEY_PAUSE -> KeyCode.PAUSE;
            case GLFW_KEY_F1 -> KeyCode.F1;
            case GLFW_KEY_F2 -> KeyCode.F2;
            case GLFW_KEY_F3 -> KeyCode.F3;
            case GLFW_KEY_F4 -> KeyCode.F4;
            case GLFW_KEY_F5 -> KeyCode.F5;
            case GLFW_KEY_F6 -> KeyCode.F6;
            case GLFW_KEY_F7 -> KeyCode.F7;
            case GLFW_KEY_F8 -> KeyCode.F8;
            case GLFW_KEY_F9 -> KeyCode.F9;
            case GLFW_KEY_F10 -> KeyCode.F10;
            case GLFW_KEY_F11 -> KeyCode.F11;
            case GLFW_KEY_F12 -> KeyCode.F12;
            case GLFW_KEY_F13 -> KeyCode.F13;
            case GLFW_KEY_F14 -> KeyCode.F14;
            case GLFW_KEY_F15 -> KeyCode.F15;
            case GLFW_KEY_F16 -> KeyCode.F16;
            case GLFW_KEY_F17 -> KeyCode.F17;
            case GLFW_KEY_F18 -> KeyCode.F18;
            case GLFW_KEY_F19 -> KeyCode.F19;
            case GLFW_KEY_F20 -> KeyCode.F20;
            case GLFW_KEY_F21 -> KeyCode.F21;
            case GLFW_KEY_F22 -> KeyCode.F22;
            case GLFW_KEY_F23 -> KeyCode.F23;
            case GLFW_KEY_F24 -> KeyCode.F24;
            case GLFW_KEY_F25 -> KeyCode.F25;
            case GLFW_KEY_KP_0 -> KeyCode.KP_0;
            case GLFW_KEY_KP_1 -> KeyCode.KP_1;
            case GLFW_KEY_KP_2 -> KeyCode.KP_2;
            case GLFW_KEY_KP_3 -> KeyCode.KP_3;
            case GLFW_KEY_KP_4 -> KeyCode.KP_4;
            case GLFW_KEY_KP_5 -> KeyCode.KP_5;
            case GLFW_KEY_KP_6 -> KeyCode.KP_6;
            case GLFW_KEY_KP_7 -> KeyCode.KP_7;
            case GLFW_KEY_KP_8 -> KeyCode.KP_8;
            case GLFW_KEY_KP_9 -> KeyCode.KP_9;
            case GLFW_KEY_KP_DECIMAL -> KeyCode.KP_DECIMAL;
            case GLFW_KEY_KP_DIVIDE -> KeyCode.KP_DIVIDE;
            case GLFW_KEY_KP_MULTIPLY -> KeyCode.KP_MULTIPLY;
            case GLFW_KEY_KP_SUBTRACT -> KeyCode.KP_SUBTRACT;
            case GLFW_KEY_KP_ADD -> KeyCode.KP_ADD;
            case GLFW_KEY_KP_ENTER -> KeyCode.KP_ENTER;
            case GLFW_KEY_KP_EQUAL -> KeyCode.KP_EQUAL;
            case GLFW_KEY_LEFT_SHIFT -> KeyCode.LEFT_SHIFT;
            case GLFW_KEY_LEFT_CONTROL -> KeyCode.LEFT_CONTROL;
            case GLFW_KEY_LEFT_ALT -> KeyCode.LEFT_ALT;
            case GLFW_KEY_LEFT_SUPER -> KeyCode.LEFT_SUPER;
            case GLFW_KEY_RIGHT_SHIFT -> KeyCode.RIGHT_SHIFT;
            case GLFW_KEY_RIGHT_CONTROL -> KeyCode.RIGHT_CONTROL;
            case GLFW_KEY_RIGHT_ALT -> KeyCode.RIGHT_ALT;
            case GLFW_KEY_RIGHT_SUPER -> KeyCode.RIGHT_SUPER;
            case GLFW_KEY_MENU -> KeyCode.MENU;
            default -> KeyCode.UNKNOWN;
        };
    }

    private static final Map<KeyCode, KeyState> __key_states = new HashMap<>();

    private Input() {
    }

    public static void loopOnce() {
        for (int i = 0; i < GLFW_KEY_LAST; i++) {
            KeyCode code = getKeyCode(i);
            if (code == KeyCode.UNKNOWN) continue;
            __key_states.putIfAbsent(code, new KeyState());
        }

        __key_states.forEach((code, state) -> {
            state.prev = state.now;
            state.now = Graphics.getInstance().getWindow().getKeyDown(getGLFWKeyCode(code));
        });
    }

    public static boolean getKey(KeyCode keyCode) {
        return __key_states.get(keyCode).now;
    }

    public static boolean getKeyPress(KeyCode keyCode) {
        return !__key_states.get(keyCode).prev && __key_states.get(keyCode).now;
    }

    public static boolean getKeyRelease(KeyCode keyCode) {
        return __key_states.get(keyCode).prev && !__key_states.get(keyCode).now;
    }

    public static boolean getKeyRepeat(KeyCode keyCode) {
        return __key_states.get(keyCode).prev && __key_states.get(keyCode).now;
    }
}
