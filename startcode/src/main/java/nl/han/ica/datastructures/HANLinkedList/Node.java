package nl.han.ica.datastructures.HANLinkedList;

public class Node<T> {
    private Node<T> nextNode;
    private T data;

    public Node(T data){
        this.data = data;
        this.nextNode = null;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public Node<T> getNextNode() {
        return nextNode;
    }

    public void setNextNode(Node<T> nextNode) {
        this.nextNode = nextNode;
    }
}
