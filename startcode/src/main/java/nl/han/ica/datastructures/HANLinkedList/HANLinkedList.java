package nl.han.ica.datastructures.HANLinkedList;

import nl.han.ica.datastructures.IHANLinkedList;

public class HANLinkedList<T> implements IHANLinkedList<T> {
    private Node<T> head;

    @Override
    public void addFirst(T value) {
        Node<T> firstNode = new Node<T>(value);
        firstNode.setNextNode(this.head);
        this.head = firstNode;


    }

    @Override
    public void clear() {
        this.head = null;
    }

    @Override
    public void insert(int index, T value) {
        Node<T> previousNodeToInsert = this.head;

        if (index == 0) {
            this.addFirst(value);
        } else {
            for (int i = 0; i < index - 1; i++) {
                if (previousNodeToInsert.getNextNode() != null) {
                    previousNodeToInsert = previousNodeToInsert.getNextNode();
                }
            }
            Node<T> nodeToInsert = new Node<T>(value);
            nodeToInsert.setNextNode(previousNodeToInsert.getNextNode());
            previousNodeToInsert.setNextNode(nodeToInsert);
        }
    }

    @Override
    public void delete(int pos) {
        Node<T> previousNodeToDelete = this.head;
        if (pos == 0) {
            this.head = this.head.getNextNode();
        } else {
            for (int i = 0; i < pos - 1; i++) {
                if (previousNodeToDelete.getNextNode() != null) {
                    previousNodeToDelete = previousNodeToDelete.getNextNode();
                }
            }
            Node<T> nodeToDelete = previousNodeToDelete.getNextNode();
            if (nodeToDelete.getNextNode() != null) {
                previousNodeToDelete.setNextNode(nodeToDelete.getNextNode());
            } else {
                previousNodeToDelete.setNextNode(null);
            }
        }

    }

    @Override
    public T get(int pos) {
        Node<T> nodeToGet = this.head;
        for (int i = 0; i < pos; i++) {
            if(nodeToGet.getNextNode() != null){
                nodeToGet = nodeToGet.getNextNode();
            }
        }
        return nodeToGet.getData();
    }

    @Override
    public void removeFirst() {
        this.head = this.head.getNextNode();
    }

    @Override
    public T getFirst() {
        if(this.head != null){
            return this.head.getData();
        }else{
            return null;
        }
    }

    @Override
    public int getSize() {
        if(this.head == null){
            return 0;
        }

        int size = 1;
        Node<T> currentNode = this.head;

        while(currentNode.getNextNode() != null){
            currentNode = currentNode.getNextNode();
            size++;
        }
        return size;
    }
}
