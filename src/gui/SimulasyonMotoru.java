package gui;

import javafx.scene.control.Button;
import logic.LimanYonetici;
import models.Arac;
import models.Feribot;

import java.util.ArrayList;
import java.util.List;

/**
 * Adım adım simülasyon motoru.
 * LimanYonetici'nin mevcut metodlarını kullanır,
 * veri yapısı koduna dokunmaz.
 */
public class SimulasyonMotoru {

    private final MainFrame frame;
    private LimanYonetici yonetici;

    private int aktifFeribotIdx = 0;
    private int yuklenenAracSayisi = 0;
    private boolean durduruldu = false;
    private int yol1Sayac = 0, yol2Sayac = 0;

    public SimulasyonMotoru(MainFrame frame, LimanYonetici yonetici) {
        this.frame    = frame;
        this.yonetici = yonetici;
    }

    public void reset() {
        aktifFeribotIdx    = 0;
        yuklenenAracSayisi = 0;
        durduruldu         = false;
        yol1Sayac          = 0;
        yol2Sayac          = 0;
    }

    /** Bir adım ilerlet: bir araç yükle ya da ferbot değiştir. */
    public void adimAt() {
        List<Feribot> feribotlar = yonetici.getFeribotlar();
        if (feribotlar.isEmpty()) {
            frame.olay("--", "Önce veri yükleyin", MainFrame.RED);
            return;
        }
        if (aktifFeribotIdx >= feribotlar.size()) {
            frame.olay(frame.simSaatStr(), "✅ Tüm seferler tamamlandı", MainFrame.GREEN);
            frame.yenile();
            return;
        }

        Feribot aktif = feribotlar.get(aktifFeribotIdx);

        // Kalkış koşulu sağlandıysa sonraki feribota geç
        if (aktif.isFull() || aktif.kalkisSartlariSaglandiMi(9.0 + aktifFeribotIdx)) {
            frame.olay(frame.simSaatStr(),
                    "⚓ Feribot " + aktif.getFeribotNo() + " kalkış yaptı", MainFrame.GOLD);
            aktifFeribotIdx++;
            frame.yenile();
            return;
        }

        // Sırayla Yol1 ve Yol2'den araç yükle
        boolean yuklendi = false;
        if (!yonetici.getYuklemeYolu1().isEmpty()) {
            Arac a = yonetici.getYuklemeYolu1().dequeue();
            if (aktif.aracYukle(a)) {
                yuklenenAracSayisi++;
                yol1Sayac++;
                frame.olay(frame.simSaatStr(),
                        a.getPlaka() + " → Feribot " + aktif.getFeribotNo() +
                                (a.getAracTipi()==1 ? " alt kat" : " üst kat"), MainFrame.TEAL);
                yuklendi = true;
            } else {
                yonetici.getYuklemeYolu1().enqueue(a);
            }
        }
        if (!yuklendi && !yonetici.getYuklemeYolu2().isEmpty()) {
            Arac a = yonetici.getYuklemeYolu2().dequeue();
            if (aktif.aracYukle(a)) {
                yuklenenAracSayisi++;
                yol2Sayac++;
                frame.olay(frame.simSaatStr(),
                        a.getPlaka() + " → Feribot " + aktif.getFeribotNo() +
                                (a.getAracTipi()==1 ? " alt kat" : " üst kat"), MainFrame.BLUE_ACC);
                yuklendi = true;
            } else {
                yonetici.getYuklemeYolu2().enqueue(a);
            }
        }
        if (!yuklendi) {
            frame.olay(frame.simSaatStr(), "Kuyruk boş, feribot kalkıyor", MainFrame.ORANGE);
            aktifFeribotIdx++;
        }

        frame.yenile();
    }

    public void togglePause(Button btn) {
        durduruldu = !durduruldu;
        btn.setText(durduruldu ? "▶  Devam" : "⏸  Duraklat");
    }

    public int getAktifIdx()            { return aktifFeribotIdx; }
    public int getYuklenenAracSayisi()  { return yuklenenAracSayisi; }

    public Feribot getAktifFeribot() {
        List<Feribot> list = yonetici.getFeribotlar();
        if (list.isEmpty() || aktifFeribotIdx >= list.size()) return null;
        return list.get(aktifFeribotIdx);
    }
}
