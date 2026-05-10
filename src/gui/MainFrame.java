package gui;

import javafx.animation.*;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.*;
import javafx.geometry.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.*;
import javafx.scene.shape.*;
import javafx.scene.text.*;
import javafx.stage.*;
import javafx.util.Duration;
import logic.LimanYonetici;
import models.Arac;
import models.Feribot;

import java.util.*;

/**
 * Ana uygulama penceresi — profesyonel liman simülasyon arayüzü.
 * Veri yapısı ve logic kodlarına dokunulmamıştır.
 */
public class MainFrame extends Application {

    // ── Renkler ──────────────────────────────────────────
    static final String BG          = "#0d1b2a";
    static final String CARD        = "#152236";
    static final String CARD2       = "#1a2d42";
    static final String BORDER      = "#1e3a52";
    static final String GOLD        = "#f0a500";
    static final String GOLD_DIM    = "#c8880a";
    static final String BLUE_ACC    = "#2196f3";
    static final String TEAL        = "#00bcd4";
    static final String GREEN       = "#4caf50";
    static final String ORANGE      = "#ff9800";
    static final String RED         = "#f44336";
    static final String TEXT        = "#e8f4f8";
    static final String TEXT2       = "#8ab4c8";
    static final String CAR_COL     = "#2196f3";
    static final String TRUCK_COL   = "#ff9800";

    // ── State ─────────────────────────────────────────────
    LimanYonetici yonetici;
    SimulasyonMotoru motor;

    // ── UI ────────────────────────────────────────────────
    private Label saatLabel;
    private Label statFeribot, statArac, statHash, statKuyruk;
    private Label statFeribotSub, statHashSub, statKuyrukSub;
    private VBox  yol1Box, yol2Box, giseOncesiBox;
    private Label yol1Badge, yol2Badge, giseSayac;
    StackPane     feribotMerkez;
    private VBox  olayBox;
    private HBox  seferBar;
    private Button durButton;
    private Label  merkezBaslik;
    private Timeline saatTimeline;
    private int simSaat = 9, simDak = 0;

    @Override
    public void start(Stage stage) {
        yonetici = new LimanYonetici();
        motor    = new SimulasyonMotoru(this, yonetici);

        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color:" + BG + ";");

        root.setTop(buildTop());
        root.setCenter(buildCenter());
        root.setBottom(buildSeferBar());

        Scene scene = new Scene(root, 1440, 900);
        stage.setScene(scene);
        stage.setTitle("Liman Simülasyon Sistemi");
        stage.setMinWidth(1200);
        stage.setMinHeight(800);
        stage.show();

        startSaatTimer();
        Platform.runLater(this::veriYukle);
    }

    // ═══════════════════════════════════════════════════
    //  TOP  (header + stat cards + kontroller)
    // ═══════════════════════════════════════════════════
    private VBox buildTop() {
        VBox top = new VBox(0);
        top.getChildren().addAll(buildHeader(), buildStatCards(), buildKontroller());
        return top;
    }

    private HBox buildHeader() {
        HBox h = new HBox(14);
        h.setPadding(new Insets(14, 24, 14, 24));
        h.setAlignment(Pos.CENTER_LEFT);
        h.setStyle("-fx-background-color:#0a1520;");

        // Logo çember
        StackPane logo = new StackPane();
        Circle c = new Circle(24, Color.web(GOLD));
        Label anchor = new Label("⚓");
        anchor.setStyle("-fx-font-size:18px; -fx-text-fill:#0a1520;");
        logo.getChildren().addAll(c, anchor);

        VBox titleBox = new VBox(2);
        Label t1 = txt("LİMAN ", 20, true, TEXT);
        Label t1b= txt("SİMÜLASYONU", 20, true, GOLD);
        HBox trow = new HBox(0, t1, t1b);
        Label t2 = txt("Veri Yapıları Final  ·  Kuyruk  ·  Yığın  ·  Hash Tablosu", 11, false, TEXT2);
        titleBox.getChildren().addAll(trow, t2);

        Region sp = new Region(); HBox.setHgrow(sp, Priority.ALWAYS);

        // Saat
        HBox saatBox = new HBox(8);
        saatBox.setAlignment(Pos.CENTER);
        saatBox.setPadding(new Insets(6, 14, 6, 14));
        saatBox.setStyle("-fx-background-color:" + CARD + "; -fx-background-radius:8;");
        Label clock = txt("🕐", 13, false, GOLD);
        saatLabel = txt("Sim. Varış saati  09:00", 13, false, GOLD);
        saatBox.getChildren().addAll(clock, saatLabel);

        h.getChildren().addAll(logo, titleBox, sp, saatBox);
        return h;
    }

    private HBox buildStatCards() {
        HBox row = new HBox(12);
        row.setPadding(new Insets(14, 24, 14, 24));
        row.setStyle("-fx-background-color:#0a1520;");

        statFeribot   = txt("0/0", 32, true, TEXT);
        statArac      = txt("0",   32, true, TEXT);
        statHash      = txt("0",   32, true, TEXT);
        statKuyruk    = txt("0",   32, true, TEXT);
        statFeribotSub= txt("",    10, false, TEXT2);
        statHashSub   = txt("çakışma çözümü: linear probing", 10, false, TEXT2);
        statKuyrukSub = txt("yüklenmeyi bekliyor", 10, false, TEXT2);

        VBox c1 = statCard("🚢", "KALKAN FERİBOT",  statFeribot, statFeribotSub);
        VBox c2 = statCard("📦", "YÜKLENEN ARAÇ",   statArac,    txt("",10,false,TEXT2));
        VBox c3 = statCard("#",  "HASH KAYIT",       statHash,    statHashSub);
        VBox c4 = statCard("🔁", "KUYRUK TOPLAMI",   statKuyruk,  statKuyrukSub);

        for (VBox v : List.of(c1,c2,c3,c4)) HBox.setHgrow(v, Priority.ALWAYS);
        row.getChildren().addAll(c1, c2, c3, c4);
        return row;
    }

    private VBox statCard(String icon, String label, Label val, Label sub) {
        VBox card = new VBox(4);
        card.setPadding(new Insets(16, 20, 16, 20));
        card.setStyle("-fx-background-color:" + CARD + "; -fx-background-radius:10;");
        HBox iconRow = new HBox(8);
        iconRow.setAlignment(Pos.CENTER_LEFT);
        Label ic = txt(icon, 14, false, TEXT2);
        Label lb = txt(label, 11, true, TEXT2);
        iconRow.getChildren().addAll(ic, lb);
        card.getChildren().addAll(iconRow, val, sub);
        return card;
    }

    private HBox buildKontroller() {
        HBox h = new HBox(10);
        h.setPadding(new Insets(10, 24, 10, 24));
        h.setAlignment(Pos.CENTER_LEFT);
        h.setStyle("-fx-background-color:" + BG + "; -fx-border-color:" + BORDER + "; -fx-border-width:1 0 1 0;");

        durButton        = controlBtn("⏸  Duraklat",  CARD2);
        Button adimBtn   = controlBtn("→  Adımla",    CARD2);
        Button sifirBtn  = controlBtn("↺  Sıfırla",   CARD2);

        Region sp = new Region(); HBox.setHgrow(sp, Priority.ALWAYS);

        Label hizLbl = txt("Hız", 11, false, TEXT2);
        ToggleGroup hizGrp = new ToggleGroup();
        ToggleButton h1 = hizBtn("1×", hizGrp);
        ToggleButton h2 = hizBtn("2×", hizGrp); h2.setSelected(true);
        ToggleButton h5 = hizBtn("5×", hizGrp);

        Button ekleBtn = new Button("+ Araç Ekle");
        ekleBtn.setStyle("-fx-background-color:" + GOLD + "; -fx-text-fill:#0a1520;" +
                "-fx-font-weight:bold; -fx-font-size:12px; -fx-padding:8 18; -fx-background-radius:8; -fx-cursor:hand;");

        durButton.setOnAction(e -> motor.togglePause(durButton));
        adimBtn  .setOnAction(e -> motor.adimAt());
        sifirBtn .setOnAction(e -> sifirla());
        ekleBtn  .setOnAction(e -> new AracEkleForm(
                (Stage)ekleBtn.getScene().getWindow(), yonetici, this).showAndWait()
                .ifPresent(a -> { yenile(); olay(simSaatStr(), "Araç " + a.getPlaka() + " gişeden geçti", TEAL); }));

        h.getChildren().addAll(durButton, adimBtn, sifirBtn, sp, hizLbl, h1, h2, h5, ekleBtn);
        return h;
    }

    // ═══════════════════════════════════════════════════
    //  MERKEZ  (sol kuyruklar | feribot | sağ log)
    // ═══════════════════════════════════════════════════
    private HBox buildCenter() {
        HBox center = new HBox(0);
        VBox.setVgrow(center, Priority.ALWAYS);

        VBox left   = buildSolPanel();
        left.setMinWidth(270); left.setMaxWidth(310);

        feribotMerkez = new StackPane();
        feribotMerkez.setStyle("-fx-background-color:" + BG + ";");
        HBox.setHgrow(feribotMerkez, Priority.ALWAYS);
        refreshFeribotMerkez(null);

        VBox right  = buildSagPanel();
        right.setMinWidth(280); right.setMaxWidth(320);

        center.getChildren().addAll(left,
                separator(false), feribotMerkez, separator(false), right);
        return center;
    }

    // ─── Sol panel ────────────────────────────────────
    private VBox buildSolPanel() {
        VBox panel = new VBox(0);
        panel.setStyle("-fx-background-color:" + CARD + ";");

        // Yol 1
        yol1Badge = badge("0", BLUE_ACC);
        VBox yol1wrap = queuePanel("1. YÜKLEME YOLU", "FIFO", yol1Badge);
        yol1Box = (VBox) ((VBox)yol1wrap.getChildren().get(1)).getChildren().get(0);

        // Yol 2
        yol2Badge = badge("0", BLUE_ACC);
        VBox yol2wrap = queuePanel("2. YÜKLEME YOLU", "FIFO", yol2Badge);
        yol2Box = (VBox) ((VBox)yol2wrap.getChildren().get(1)).getChildren().get(0);

        // Gişe öncesi
        VBox gisePan = new VBox(0);
        gisePan.setStyle("-fx-background-color:#0f1e2e; -fx-border-color:" + BORDER + "; -fx-border-width:1 0 0 0;");
        giseSayac = txt("0", 11, false, TEXT2);
        HBox giseHdr = panelHeader("GİŞE ÖNCESİ", giseSayac);
        giseOncesiBox = new VBox(2);
        giseOncesiBox.setPadding(new Insets(8));
        ScrollPane giseSP = new ScrollPane(giseOncesiBox);
        giseSP.setFitToWidth(true); giseSP.setPrefHeight(220);
        giseSP.setStyle("-fx-background:" + BG + "; -fx-background-color:" + BG + "; -fx-border-color:transparent;");
        gisePan.getChildren().addAll(giseHdr, giseSP);

        panel.getChildren().addAll(yol1wrap, sep(), yol2wrap, sep(), gisePan);
        VBox.setVgrow(gisePan, Priority.ALWAYS);
        return panel;
    }

    private VBox queuePanel(String title, String type, Label badge) {
        VBox wrap = new VBox(0);
        HBox hdr = panelHeaderWithBadge(title, type, badge);
        VBox content = new VBox(6);
        content.setPadding(new Insets(10));
        VBox inner = new VBox(4);
        content.getChildren().add(inner);
        wrap.getChildren().addAll(hdr, content);
        return wrap;
    }

    private HBox panelHeader(String title, Label right) {
        HBox h = new HBox(8);
        h.setPadding(new Insets(10, 14, 10, 14));
        h.setAlignment(Pos.CENTER_LEFT);
        h.setStyle("-fx-background-color:#0f1e2e;");
        Label lbl = txt(title, 11, true, TEXT2);
        Region sp = new Region(); HBox.setHgrow(sp, Priority.ALWAYS);
        h.getChildren().addAll(lbl, sp, right);
        return h;
    }

    private HBox panelHeaderWithBadge(String title, String sub, Label badge) {
        HBox h = new HBox(8);
        h.setPadding(new Insets(10, 14, 10, 14));
        h.setAlignment(Pos.CENTER_LEFT);
        h.setStyle("-fx-background-color:#0f1e2e;");
        Label lbl = txt(title, 11, true, TEXT);
        Label slbl= txt(sub, 10, false, TEXT2);
        Region sp = new Region(); HBox.setHgrow(sp, Priority.ALWAYS);
        h.getChildren().addAll(lbl, slbl, sp, badge);
        return h;
    }

    // ─── Sağ panel (olay akışı) ───────────────────────
    private VBox buildSagPanel() {
        VBox panel = new VBox(0);
        panel.setStyle("-fx-background-color:" + CARD + ";");
        Label dot = new Label("●");
        dot.setStyle("-fx-text-fill:" + GREEN + "; -fx-font-size:10px;");
        HBox hdr = panelHeader("OLAY AKIŞI", dot);
        olayBox = new VBox(2);
        olayBox.setPadding(new Insets(8));
        ScrollPane sp = new ScrollPane(olayBox);
        sp.setFitToWidth(true);
        sp.setStyle("-fx-background:" + CARD + "; -fx-background-color:" + CARD + "; -fx-border-color:transparent;");
        VBox.setVgrow(sp, Priority.ALWAYS);
        panel.getChildren().addAll(hdr, sp);
        return panel;
    }

    // ─── Feribot merkez ───────────────────────────────
    void refreshFeribotMerkez(Feribot f) {
        feribotMerkez.getChildren().clear();
        if (f == null) {
            Label lbl = txt("Veri Yükleyin →  'Adımla' ile simülasyonu başlatın", 14, false, TEXT2);
            feribotMerkez.getChildren().add(lbl);
            return;
        }
        VBox box = new VBox(14);
        box.setMaxWidth(480);
        box.setAlignment(Pos.CENTER);
        box.setPadding(new Insets(24));

        // Başlık
        Label baslik = txt("● CANLI LİMAN GÖRÜNÜMÜ", 11, true, GREEN);
        Label saat   = txt(simSaatStr(), 18, true, GOLD);
        HBox topRow  = new HBox(); topRow.setAlignment(Pos.CENTER_LEFT);
        Region sp1   = new Region(); HBox.setHgrow(sp1, Priority.ALWAYS);
        topRow.getChildren().addAll(baslik, sp1, saat);

        // Feribot kart
        VBox ferikart = new VBox(12);
        ferikart.setPadding(new Insets(16));
        ferikart.setStyle("-fx-background-color:" + CARD2 + "; -fx-background-radius:12;" +
                "-fx-border-color:" + BORDER + "; -fx-border-radius:12; -fx-border-width:1;");

        // Feribot başlık badge
        HBox feriHdr = new HBox();
        feriHdr.setAlignment(Pos.CENTER);
        Label feriBadge = txt("🚢  FERİBOT " + f.getFeribotNo() + "  ·  " + f.getSeferNo(), 13, true, GOLD);
        feriBadge.setStyle("-fx-text-fill:" + GOLD + "; -fx-background-color:" + GOLD + "22;" +
                "-fx-padding:5 14; -fx-background-radius:20;");
        feriHdr.getChildren().add(feriBadge);

        // Üst kat
        VBox ustKat  = katGorsel("ÜST KAT · OTOMOBİL",  f.getUstKatDoluluk(),  5, false);
        VBox altKat  = katGorsel("GİRİŞ KATI · AĞIR VASITA", f.getAltKatDoluluk(), 5, true);

        // Progress bar
        int toplam = f.getUstKatDoluluk() + f.getAltKatDoluluk();
        ProgressBar pb = new ProgressBar(toplam / 10.0);
        pb.setMaxWidth(Double.MAX_VALUE);
        pb.setPrefHeight(8);
        pb.setStyle("-fx-accent:" + GOLD + "; -fx-background-color:#0a1520; -fx-background-radius:4;");

        Label rihtim = txt("⚓  TEK RIHTIM  ·  LİMAN GİRİŞİ", 10, true, TEXT2);
        rihtim.setAlignment(Pos.CENTER);
        HBox rl = new HBox(); rl.setAlignment(Pos.CENTER); rl.getChildren().add(rihtim);

        // Alt/Üst doluluk
        HBox doluluk = new HBox(16);
        doluluk.setAlignment(Pos.CENTER);
        doluluk.getChildren().addAll(
                txt("🚛 Alt " + f.getAltKatDoluluk() + "/5", 11, false, TRUCK_COL),
                txt("🚗 Üst " + f.getUstKatDoluluk() + "/5", 11, false, CAR_COL)
        );

        ferikart.getChildren().addAll(feriHdr, ustKat, altKat, pb, doluluk, rl);
        box.getChildren().addAll(topRow, ferikart);
        feribotMerkez.getChildren().add(box);
    }

    private VBox katGorsel(String title, int dolu, int kap, boolean agir) {
        VBox box = new VBox(8);
        box.setPadding(new Insets(10));
        box.setStyle("-fx-background-color:#0f1e2e; -fx-background-radius:8;");
        String renk = agir ? TRUCK_COL : CAR_COL;
        HBox hdr = new HBox(8);
        hdr.setAlignment(Pos.CENTER_LEFT);
        Label tl  = txt(title, 10, true, renk);
        Region sp = new Region(); HBox.setHgrow(sp, Priority.ALWAYS);
        Label cnt = txt(dolu + "/" + kap, 12, true, TEXT);
        hdr.getChildren().addAll(tl, sp, cnt);

        HBox slots = new HBox(8);
        slots.setAlignment(Pos.CENTER_LEFT);
        for (int i = 0; i < kap; i++) {
            VBox slot = new VBox();
            slot.setAlignment(Pos.CENTER);
            slot.setPrefSize(52, 40);
            slot.setStyle("-fx-background-color:" + (i < dolu ? renk+"33" : "#1a2d42") +
                    "; -fx-background-radius:6; -fx-border-color:" + renk + (i < dolu ? "" : "44") +
                    "; -fx-border-radius:6; -fx-border-width:1;");
            if (i < dolu) {
                Label ic = txt(agir ? "🚛" : "🚗", 18, false, renk);
                slot.getChildren().add(ic);
            }
            slots.getChildren().add(slot);
        }
        box.getChildren().addAll(hdr, slots);
        return box;
    }

    // ─── Sefer bar (alt) ──────────────────────────────
    private VBox buildSeferBar() {
        VBox outer = new VBox(0);
        outer.setStyle("-fx-background-color:#0a1520; -fx-border-color:" + BORDER + "; -fx-border-width:1 0 0 0;");
        HBox lbl = new HBox(8);
        lbl.setPadding(new Insets(8, 24, 6, 24));
        lbl.setAlignment(Pos.CENTER_LEFT);
        lbl.getChildren().add(txt("🚢  SEFER PROGRAMI", 11, true, TEXT2));
        seferBar = new HBox(12);
        seferBar.setPadding(new Insets(0, 24, 12, 24));
        outer.getChildren().addAll(lbl, seferBar);
        return outer;
    }

    void refreshSeferBar(List<Feribot> feribotlar, int aktifIdx) {
        seferBar.getChildren().clear();
        for (int i = 0; i < feribotlar.size(); i++) {
            Feribot f = feribotlar.get(i);
            String durum, durumRenk, durumBg;
            if (i < aktifIdx) {
                durum = "KALKTI"; durumRenk = GREEN; durumBg = GREEN + "22";
            } else if (i == aktifIdx) {
                durum = "RIHTIMDA"; durumRenk = GOLD; durumBg = GOLD + "22";
            } else {
                durum = "BEKLİYOR"; durumRenk = BLUE_ACC; durumBg = BLUE_ACC + "22";
            }
            VBox card = new VBox(4);
            card.setPadding(new Insets(10, 16, 10, 16));
            card.setStyle("-fx-background-color:" + CARD + "; -fx-background-radius:8;");
            card.setMinWidth(160);
            HBox hdr = new HBox(8);
            hdr.setAlignment(Pos.CENTER_LEFT);
            Label ic  = txt("🚢", 12, false, TEXT2);
            Label nm  = txt("Feribot " + f.getFeribotNo() + "  ·  " + f.getSeferNo(), 11, false, TEXT);
            Region sp = new Region(); HBox.setHgrow(sp, Priority.ALWAYS);
            Label dl  = txt(durum, 9, true, durumRenk);
            dl.setStyle("-fx-text-fill:" + durumRenk + "; -fx-background-color:" + durumBg +
                    "; -fx-padding:2 8; -fx-background-radius:10;");
            hdr.getChildren().addAll(ic, nm, sp, dl);
            Label saatLbl = txt(String.format("Plan. Kalkış: %.2f", f.getRihtimKalkisSaati()), 10, false, TEXT2);
            card.getChildren().addAll(hdr, saatLbl);
            seferBar.getChildren().add(card);
        }
    }

    // ═══════════════════════════════════════════════════
    //  YARDIMCI UI
    // ═══════════════════════════════════════════════════

    void refreshKuyruklar() {
        refreshKuyruk(yonetici.getYuklemeYolu1(), yol1Box, yol1Badge);
        refreshKuyruk(yonetici.getYuklemeYolu2(), yol2Box, yol2Badge);

        // Gişe öncesi (tüm araçlar)
        giseOncesiBox.getChildren().clear();
        List<Arac> tumAraclar = yonetici.getTumAraclar();
        giseSayac.setText(String.valueOf(tumAraclar.size()));
        List<Arac> sorted = new ArrayList<>(tumAraclar);
        sorted.sort(Comparator.comparingDouble(Arac::getGiseGirisSaati));
        int goster = Math.min(sorted.size(), 10);
        for (int i = 0; i < goster; i++) {
            Arac a = sorted.get(i);
            HBox row = new HBox(8);
            row.setAlignment(Pos.CENTER_LEFT);
            row.setPadding(new Insets(3, 6, 3, 6));
            boolean agir = a.getAracTipi() == 1;
            String col = agir ? TRUCK_COL : CAR_COL;
            Label saatL = txt(String.format("%05.2f", a.getGiseGirisSaati()), 10, false, TEXT2);
            Label plakaL= txt(a.getPlaka(), 10, true, col);
            Label tipL  = txt(agir ? "🚛" : "🚗", 12, false, col);
            row.getChildren().addAll(saatL, plakaL, tipL);
            giseOncesiBox.getChildren().add(row);
        }
        if (tumAraclar.size() > 10) {
            giseOncesiBox.getChildren().add(
                    txt("+ " + (tumAraclar.size()-10) + " daha…", 10, false, TEXT2));
        }
    }

    private void refreshKuyruk(dataStructures.MyQueue<Arac> q, VBox box, Label badge) {
        List<Arac> items = new ArrayList<>();
        while (!q.isEmpty()) items.add(q.dequeue());
        for (Arac a : items) q.enqueue(a);
        box.getChildren().clear();
        badge.setText(String.valueOf(items.size()));
        if (items.isEmpty()) {
            box.getChildren().add(txt("(boş)", 10, false, TEXT2));
            return;
        }
        // İlk araç (ÖN)
        Arac ilk = items.get(0);
        box.getChildren().add(queueItem(ilk, true));
        for (int i = 1; i < items.size(); i++)
            box.getChildren().add(queueItem(items.get(i), false));
    }

    private HBox queueItem(Arac a, boolean on) {
        HBox h = new HBox(10);
        h.setAlignment(Pos.CENTER_LEFT);
        h.setPadding(new Insets(7, 10, 7, 10));
        boolean agir = a.getAracTipi() == 1;
        String col = agir ? TRUCK_COL : CAR_COL;
        h.setStyle("-fx-background-color:" + col + "18; -fx-background-radius:6;");
        Label ic    = txt(agir ? "🚛" : "🚗", 14, false, col);
        Label plaka = txt(a.getPlaka(), 11, true, TEXT);
        Region sp   = new Region(); HBox.setHgrow(sp, Priority.ALWAYS);
        if (on) {
            Label onLbl = txt("ÖN", 9, true, GREEN);
            onLbl.setStyle("-fx-text-fill:" + GREEN + "; -fx-background-color:" + GREEN + "22;" +
                    "-fx-padding:2 6; -fx-background-radius:8;");
            h.getChildren().addAll(ic, plaka, sp, onLbl);
        } else {
            h.getChildren().addAll(ic, plaka);
        }
        return h;
    }

    void updateStats(int kalan, int toplam, int yuklenenArac, int aktifIdx) {
        statFeribot.setText(aktifIdx + "/" + toplam);
        statArac   .setText(String.valueOf(yuklenenArac));
        statHash   .setText(String.valueOf(yonetici.getTumAraclar().size()));
        int qToplam = yonetici.getYuklemeYolu1().size() + yonetici.getYuklemeYolu2().size();
        statKuyruk .setText(String.valueOf(qToplam));
    }

    void olay(String saat, String mesaj, String renk) {
        HBox row = new HBox(8);
        row.setPadding(new Insets(3, 6, 3, 6));
        row.setAlignment(Pos.CENTER_LEFT);
        Label sLbl = txt(saat, 10, false, TEXT2);
        sLbl.setMinWidth(40);
        Label mLbl = txt(mesaj, 10, false, renk);
        row.getChildren().addAll(sLbl, mLbl);
        // En üste ekle
        olayBox.getChildren().add(0, row);
        // Maks 50 olay
        if (olayBox.getChildren().size() > 50)
            olayBox.getChildren().remove(50);
    }

    // ═══════════════════════════════════════════════════
    //  AKSİYONLAR
    // ═══════════════════════════════════════════════════

    void veriYukle() {
        try {
            yonetici.yukle();
            motor.reset();
            yenile();
            olay(simSaatStr(), "Veriler yüklendi", GREEN);
        } catch (Exception e) {
            olay("--", "Hata: " + e.getMessage(), RED);
        }
    }

    void yenile() {
        refreshKuyruklar();
        refreshSeferBar(yonetici.getFeribotlar(), motor.getAktifIdx());
        Feribot f = motor.getAktifFeribot();
        refreshFeribotMerkez(f);
        int toplam  = yonetici.getFeribotlar().size();
        int yukl    = motor.getYuklenenAracSayisi();
        updateStats(toplam - motor.getAktifIdx(), toplam, yukl, motor.getAktifIdx());
    }

    private void sifirla() {
        yonetici = new LimanYonetici();
        motor    = new SimulasyonMotoru(this, yonetici);
        simSaat  = 9; simDak = 0;
        olayBox.getChildren().clear();
        feribotMerkez.getChildren().clear();
        seferBar.getChildren().clear();
        yol1Box.getChildren().clear();
        yol2Box.getChildren().clear();
        giseOncesiBox.getChildren().clear();
        statFeribot.setText("0/0"); statArac.setText("0");
        statHash.setText("0"); statKuyruk.setText("0");
        veriYukle();
    }

    // ═══════════════════════════════════════════════════
    //  YARDIMCILAR
    // ═══════════════════════════════════════════════════

    private void startSaatTimer() {
        saatTimeline = new Timeline(new KeyFrame(Duration.seconds(1.5), e -> {
            simDak += 5;
            if (simDak >= 60) { simDak = 0; simSaat++; }
            if (simSaat > 23) simSaat = 0;
            saatLabel.setText("Sim. Varış saati  " + simSaatStr());
        }));
        saatTimeline.setCycleCount(Timeline.INDEFINITE);
        saatTimeline.play();
    }

    String simSaatStr() {
        return String.format("%02d:%02d", simSaat, simDak);
    }

    static Label txt(String t, double size, boolean bold, String col) {
        Label l = new Label(t);
        l.setStyle("-fx-font-size:" + size + "px;" + (bold?"-fx-font-weight:bold;":"") +
                "-fx-text-fill:" + col + "; -fx-font-family:'Segoe UI';");
        return l;
    }

    static Label badge(String text, String col) {
        Label l = new Label(text);
        l.setStyle("-fx-text-fill:" + col + "; -fx-background-color:" + col + "22;" +
                "-fx-font-size:10px; -fx-font-weight:bold; -fx-padding:2 8; -fx-background-radius:10;");
        return l;
    }

    static Button controlBtn(String text, String bg) {
        Button b = new Button(text);
        b.setStyle("-fx-background-color:" + bg + "; -fx-text-fill:" + TEXT + ";" +
                "-fx-font-size:12px; -fx-padding:7 14; -fx-background-radius:7; -fx-cursor:hand;");
        b.setOnMouseEntered(e -> b.setOpacity(0.8));
        b.setOnMouseExited(e  -> b.setOpacity(1.0));
        return b;
    }

    static ToggleButton hizBtn(String text, ToggleGroup grp) {
        ToggleButton tb = new ToggleButton(text);
        tb.setToggleGroup(grp);
        tb.setStyle("-fx-background-color:#1a2d42; -fx-text-fill:" + TEXT + ";" +
                "-fx-font-size:11px; -fx-padding:6 12; -fx-background-radius:6; -fx-cursor:hand;");
        tb.selectedProperty().addListener((obs,o,sel) ->
                tb.setStyle(tb.getStyle().replace(
                                sel ? "#1a2d42" : GOLD, sel ? GOLD : "#1a2d42")
                        .replace(sel ? TEXT : "#0a1520", sel ? "#0a1520" : TEXT)));
        return tb;
    }

    static Node separator(boolean horizontal) {
        Region r = new Region();
        if (horizontal) { r.setPrefHeight(1); r.setStyle("-fx-background-color:#1e3a52;"); }
        else             { r.setPrefWidth(1);  r.setStyle("-fx-background-color:#1e3a52;"); }
        return r;
    }

    static Node sep() {
        Region r = new Region();
        r.setPrefHeight(1);
        r.setStyle("-fx-background-color:#1e3a52;");
        return r;
    }

    public static void main(String[] args) { launch(args); }
}
