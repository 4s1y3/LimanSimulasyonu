package gui;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;
import logic.LimanYonetici;
import models.Arac;
import models.Feribot;

import java.util.List;

/**
 * Adım adım + otomatik simülasyon motoru.
 * Yalnızca LimanYonetici'nin public API'sini kullanır.
 */
public class SimulasyonMotoru {

    private final MainFrame frame;
    private LimanYonetici yonetici;

    private int     aktifIdx        = 0;
    private int     yuklenenSayi    = 0;
    private boolean calisiyor       = false;
    private double  hizCarpan       = 1.0;
    private Timeline autoTimeline;

    // Araçlar yol1 ve yol2'den sırayla alınır
    private boolean siradakiYol1 = true;

    public SimulasyonMotoru(MainFrame frame, LimanYonetici yonetici) {
        this.frame    = frame;
        this.yonetici = yonetici;
    }

    public void reset(LimanYonetici yeni) {
        durdur();
        this.yonetici  = yeni;
        aktifIdx       = 0;
        yuklenenSayi   = 0;
        calisiyor      = false;
        siradakiYol1   = true;
    }

    // ── Otomatik oynatma ─────────────────────────────
    public void oynat() {
        if (calisiyor) return;
        calisiyor = true;
        autoTimeline = new Timeline(new KeyFrame(
                Duration.millis(1800 / hizCarpan), e -> adimAt()
        ));
        autoTimeline.setCycleCount(Timeline.INDEFINITE);
        autoTimeline.play();
    }

    public void durdur() {
        calisiyor = false;
        if (autoTimeline != null) autoTimeline.stop();
    }

    public boolean isCalisiyor() { return calisiyor; }

    public void setHiz(double carpan) {
        this.hizCarpan = carpan;
        if (calisiyor) { durdur(); oynat(); }
    }

    // ── Tek adım ─────────────────────────────────────
    public void adimAt() {
        List<Feribot> feribotlar = yonetici.getFeribotlar();
        if (feribotlar == null || feribotlar.isEmpty()) {
            frame.olay("--:--", "Önce veri yükleyin", MainFrame.RED);
            durdur(); return;
        }
        if (aktifIdx >= feribotlar.size()) {
            frame.olay("--:--", "✅ Tüm seferler tamamlandı", MainFrame.EMERALD);
            durdur(); return;
        }

        Feribot aktif = feribotlar.get(aktifIdx);

        // Kalkış şartları sağlandıysa kalkış yap
        if (aktif.kalkisSartlariSaglandiMi(aktif.getRihtimGirisSaati())) {
            frame.olay(frame.saatStr(),
                    "⚓ Feribot " + aktif.getFeribotNo() + " (" + aktif.getSeferNo() + ") kalkış yaptı",
                    MainFrame.GOLD);
            aktifIdx++;
            siradakiYol1 = true;
            frame.yenile();
            return;
        }

        // Araç yükle — önce belirlenen yoldan, sonra diğerinden
        boolean yuklendi = false;
        for (int deneme = 0; deneme < 2 && !yuklendi; deneme++) {
            var kuyruk = siradakiYol1
                    ? yonetici.getYuklemeYolu1()
                    : yonetici.getYuklemeYolu2();
            int yolNo = siradakiYol1 ? 1 : 2;

            if (!kuyruk.isEmpty()) {
                Arac a = kuyruk.dequeue();
                if (aktif.aracYukle(a)) {
                    yuklenenSayi++;
                    boolean agir = a.getAracTipi() == 1;
                    frame.olay(frame.saatStr(),
                            a.getPlaka() + " → Feribot " + aktif.getFeribotNo()
                                    + " · " + (agir ? "Alt Kat" : "Üst Kat")
                                    + "  [Yol " + yolNo + "]",
                            agir ? MainFrame.TRK_C : MainFrame.SKY);
                    yuklendi = true;
                    siradakiYol1 = !siradakiYol1; // sıradaki yolu değiştir
                } else {
                    kuyruk.enqueue(a); // sığmadı, geri koy
                    siradakiYol1 = !siradakiYol1;
                }
            } else {
                siradakiYol1 = !siradakiYol1;
            }
        }

        if (!yuklendi) {
            // Her iki kuyruk da boşsa veya araç sığmıyorsa kalkış yap
            frame.olay(frame.saatStr(),
                    "⚓ Feribot " + aktif.getFeribotNo() + " kalkış yaptı (kuyruk boş)",
                    MainFrame.GOLD);
            aktifIdx++;
            siradakiYol1 = true;
        }

        frame.yenile();
    }

    // ── Getters ───────────────────────────────────────
    public int     getAktifIdx()         { return aktifIdx; }
    public int     getYuklenenSayi()     { return yuklenenSayi; }

    public Feribot getAktifFeribot() {
        List<Feribot> f = yonetici.getFeribotlar();
        if (f == null || f.isEmpty() || aktifIdx >= f.size()) return null;
        return f.get(aktifIdx);
    }
}
