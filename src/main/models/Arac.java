/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package models;

/**
 *
 * @author idalozyurt
 */
public class Arac {
    private String plaka ;
    private String tip;
    private int yolcuSayısı;
    private double ağırlık;
    
    public Arac(String plaka ,String tip, int yolcuSayısı, double ağırlık){
    this.plaka=plaka;
    this.tip=tip;
    this.yolcuSayısı=yolcuSayısı;
    this.ağırlık=ağırlık;
    }
    
    
    public String getPlaka(){
        return plaka;
    }
    public String getTip(){
        return tip;
    }
    public int getYolcuSayısı(){
        return yolcuSayısı;
    }
    public double getAğırlık(){
        return ağırlık;
    }
    
    
    public void setPlaka(String plaka){
        this.plaka=plaka;
    }
    public void setTip(String tip){
        this.tip=tip;
    }
    public void setYolcuSayısı(int yolcuSayısı){
        this.yolcuSayısı=yolcuSayısı;
    }
    public void setAğırlık(double ağırık){
        this.ağırlık=ağırlık;
    }
    
    public String toString (){
        return tip +" - "+plaka+" ("+ yolcuSayısı +" yolcu, " + ağırlık +" ton)";
    }
    
    
    
    
    
    
    
    
    
    
    
}
