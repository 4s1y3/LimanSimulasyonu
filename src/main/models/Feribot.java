/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package models;

/**
 *
 * @author idalozyurt
 */
public class Feribot {
    private String isim;
    private double kapasiteTon;
    private int kapasiteYolcu;
    //private MyStack<Arac>katlar;
    
    public Feribot (String isim ,double kapasiteTon,int kapasiteYolcu){
        this.isim=isim;
        this.kapasiteTon=kapasiteTon;
        this.kapasiteYolcu=kapasiteYolcu;
        //this.katlar=new MyStack<>();
    }
    
    public String getIsim(){
        return isim;
    }
    public double getKapasiteTon(){
        return kapasiteTon;
    }
    public int getKapasiteYolcu(){
        return kapasiteYolcu;
    }
    //public MyStack<Arac> getKatlar(){
     //   return katlar;
    //}
    
    
    public void setIsim(String Isim){
        this.isim=isim;
    }
    public void setKapasiteTon(double KapasiteTon){
        this.kapasiteTon=kapasiteTon;
    }
    public void setKapasiteYolcu(int KapasiteYolcu){
        this.kapasiteYolcu=kapasiteYolcu;
    }
   
    public boolean aracEkle(Arac arac){
      
        return false;
      
    }
}
    
    
    
    
    
    
    
    
    
    
    
    

