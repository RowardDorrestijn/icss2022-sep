package nl.han.ica.datastructures;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Stack;

public class HANStack<T> implements IHANStack<T>{
    private LinkedList<T> stack = new LinkedList<T>();

    @Override
    public void push(T value) {
        stack.add(value);
    }

    @Override
    public T pop() {
        T value = this.peek();
        stack.removeLast();
        return value;
    }

    @Override
    public T peek() {
        return stack.getLast();
    }
}
