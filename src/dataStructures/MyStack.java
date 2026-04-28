package dataStructures;

public class MyStack {
 

    private Node top;
    private int size;
    private int capacity;

    public Stack(int capacity) {
        this.capacity = capacity;
        this.size = 0;
        this.top = null;
    }

    public boolean push(Arac arac) {

        if (isFull()) {
            return false; 
        }

        Node newNode = new Node(arac);
        newNode.next = top;
        top = newNode;

        size++;
        return true;
    }

    public Arac pop() {

        if (isEmpty()) {
            return null;
        }

        Arac temp = top.data;
        top = top.next;

        size--;
        return temp;
    }

    public Arac peek() {
        if (isEmpty()) {
            return null;
        }
        return top.data;
    }

    public boolean isEmpty() {
        return top == null;
    }

    public boolean isFull() {
        return size == capacity;
    }

    public int getSize() {
        return size;
    }

    public int getCapacity() {
        return capacity;
    }
}


