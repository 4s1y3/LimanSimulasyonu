
package dataStructures;

import models.Arac;
public class MyHashTable {
    private Arac[] table;
    private int capacity;
    private int size;

    public HashTable(int capacity) {

        this.capacity = capacity;
        this.table = new Arac[capacity];
        this.size = 0;
    }

    private int hash(String plaka) {

        int sum = 0;

        for (int i = 0; i < plaka.length(); i++) {
            sum += plaka.charAt(i);
        }

        return sum % capacity;
    }

  
    public boolean exists(String plaka) {

        int index = hash(plaka);
        int startIndex = index;

        while (table[index] != null) {

            if (table[index].getPlaka().equals(plaka)) {
                return true;
            }

            
            index = (index + 1) % capacity;

            
            if (index == startIndex) {
                break;
            }
        }

        return false;
    }

 
    public boolean insert(Arac arac) {

       
        if (exists(arac.getPlaka())) {
            return false;
        }

        int index = hash(arac.getPlaka());

       
        while (table[index] != null) {
            index = (index + 1) % capacity;
        }

        table[index] = arac;
        size++;

        return true;
    }

    public int size() {
        return size;
    }
}
}
