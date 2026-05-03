package dataStructures;

public class MyStack<T> {

    private Node<T> top;
    private int size;
    private int capacity;

    public MyStack(int capacity) {
        this.capacity = capacity;
        this.size = 0;
        this.top = null;
    }

  
    public boolean push(T data) {

        if (isFull()) {
            return false;
        }

        Node<T> newNode = new Node<>(data);

        newNode.next = top;
        top = newNode;

        size++;
        return true;
    }

    
    public T pop() {

        if (isEmpty()) {
            return null;
        }

        T temp = top.data;
        top = top.next;

        size--;

        return temp;
    }

    
    public T peek() {

        if (isEmpty()) {
            return null;
        }

        return top.data;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    public boolean isFull() {
        return size == capacity;
    }

    public int size() {
        return size;
    }

    public int getCapacity() {
        return capacity;
    }
}
    public int getSize() {
        return size;
    }

    public int getCapacity() {
        return capacity;
    }
}


