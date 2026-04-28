package dataStructures;

public class MyQueue {
    private Node front;
    private Node rear;
    private int size;

    public Queue() {
        front = null;
        rear = null;
        size = 0;
    }

    public void enqueue(Arac arac) {
        Node newNode = new Node(arac);

        if (rear == null) {  
            front = rear = newNode;
        } else {
            rear.next = newNode;
            rear = newNode;
        }

        size++;
    }

    public Arac dequeue() {
        if (isEmpty()) {
            return null;
        }

        Arac temp = front.data;
        front = front.next;

        if (front == null) {
            rear = null;
        }

        size--;
        return temp;
    }

    
    public Arac peek() {
        if (isEmpty()) {
            return null;
        }
        return front.data;
    }

    public boolean isEmpty() {
        return front == null;
    }

    public int getSize() {
        return size;
    }
}

