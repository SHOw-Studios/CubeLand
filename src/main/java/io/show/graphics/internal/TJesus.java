package io.show.graphics.internal;

/**
 * This class is responsible for all kind of random stuff like debugging and more...
 *
 * @author Felix Schreiber
 */
public class TJesus {

    public static class Container<T> {

        private T m_Value;

        public Container(T value) {
            m_Value = value;
        }

        public T getValue() {
            return m_Value;
        }

        public void setValue(T value) {
            m_Value = value;
        }
    }
}
