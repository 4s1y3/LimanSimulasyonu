package dataStructures;

public class MyQueue<T> {

    private Node<T> front;
    private Node<T> rear;
    private int size;

    public MyQueue() {
        front = null;
        rear = null;
        size = 0;
    }

   
    public void enqueue(T data) {

        Node<T> newNode = new Node<>(data);

        if (isEmpty()) {
            front = rear = newNode;
        } else {
            rear.next = newNode;
            rear = newNode;
        }

        size++;
    }

   
    public T dequeue() {

        if (isEmpty()) {
            return null;
        }

        T temp = front.data;
        front = front.next;

        if (front == null) {
            rear = null;
        }

        size--;

        return temp;
    }

    
    public T peek() {

        if (isEmpty()) {
            return null;
        }

        return front.data;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    public int size() {
        return size;
    }
}
