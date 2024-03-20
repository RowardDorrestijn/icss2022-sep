package nl.han.ica.datastructures.HANLinkedList;

import nl.han.ica.datastructures.IHANLinkedList;

public class HANLinkedList<T> implements IHANLinkedList<T> {
    private Node<T> head;

    @Override
    public void addFirst(T value) {
        Node<T> firstNode = new Node<T>(value);
        firstNode.setNextNode(head);
        this.head = firstNode;


    }

    @Override
    public void clear() {

    }

    @Override
    public void insert(int index, Object value) {
        while (nextNode.getNextNode() != null) {
            nextNode = nextNode.getNextNode();
        }
    }

    @Override
    public void delete(int pos) {
        Node<T> nodeToDelete = head;
        if (pos == 0) {
            this.head = head.getNextNode();
        } else {
            for (int i = 0; i < pos - 1; i++) {
                nodeToDelete = nodeToDelete.getNextNode();
            }
        }
    }

    @Override
    public T get(int pos) {
        return null;
    }

    @Override
    public void removeFirst() {

    }

    @Override
    public T getFirst() {
        return null;
    }

    @Override
    public int getSize() {
        return 0;
    }
}
