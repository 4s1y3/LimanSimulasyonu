package gui;

import javafx.geometry.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.*;
import logic.LimanYonetici;
import models.Arac;

public class AracEkleForm extends Dialog<Arac> {
    private final LimanYonetici yonetici;
    private final TextField plakaF = new TextField();
    private final TextField saatF  = new TextField();
    private final ToggleGroup grp  = new ToggleGroup();
    private final RadioButton otoR = new RadioButton("Otomobil");
    private final RadioButton agR  = new RadioButton("Ağır Vasıta");
    private final Label hataL      = new Label();

    public AracEkleForm(Stage owner, LimanYonetici y, MainFrame host) {
        this.yonetici = y;
        initOwner(owner); initModality(Modality.APPLICATION_MODAL);
        setTitle("Araç Ekle"); setHeaderText(null);

        getDialogPane().setContent(form());
        // Karanlık tema CARD değişkeni hatası kaldırılıp SURF (Aydınlık yüzey) kullanıldı
        getDialogPane().setStyle("-fx-background-color:" + MainFrame.SURF + "; -fx-font-family:'Segoe UI';");

        ButtonType ekle  = new ButtonType("Ekle",  ButtonBar.ButtonData.OK_DONE);
        ButtonType iptal = new ButtonType("İptal", ButtonBar.ButtonData.CANCEL_CLOSE);
        getDialogPane().getButtonTypes().addAll(ekle, iptal);

        Button eklB = (Button)getDialogPane().lookupButton(ekle);
        Button ipB  = (Button)getDialogPane().lookupButton(iptal);

        eklB.setStyle("-fx-background-color:" + MainFrame.SKY + "; -fx-text-fill:white; -fx-font-weight:bold; -fx-padding:8 20; -fx-background-radius:6; -fx-cursor:hand;");
        ipB.setStyle("-fx-background-color:#e2e8f0; -fx-text-fill:" + MainFrame.TEXT + "; -fx-padding:8 20; -fx-background-radius:6; -fx-cursor:hand;");

        eklB.addEventFilter(javafx.event.ActionEvent.ACTION, ev -> { if (!ekle()) ev.consume(); });
        setResultConverter(b -> b == ekle ? bul() : null);
    }

    private VBox form() {
        VBox v = new VBox(15);
        v.setPadding(new Insets(20, 24, 10, 24));
        v.setMinWidth(320);

        HBox hdr = new HBox(12);
        hdr.setAlignment(Pos.CENTER_LEFT);
        StackPane logo = new StackPane();
        Circle c = new Circle(18, Color.web(MainFrame.SKY));
        logo.getChildren().addAll(c, MainFrame.svgIcon(MainFrame.CAR_SVG, MainFrame.SURF, 1.0));
        hdr.getChildren().addAll(logo, MainFrame.lbl("Yeni Araç Kaydı", 16, true, MainFrame.TEXT));

        sf(plakaF, "Örn: 34ABC12");
        plakaF.setTextFormatter(new TextFormatter<>(ch -> { ch.setText(ch.getText().toUpperCase()); return ch; }));
        sf(saatF, "Örn: 9.30");

        otoR.setToggleGroup(grp); otoR.setSelected(true);
        agR.setToggleGroup(grp);
        otoR.setStyle("-fx-text-fill:" + MainFrame.CAR_C + "; -fx-font-weight:bold; -fx-font-size:13px;");
        agR.setStyle("-fx-text-fill:" + MainFrame.TRK_C + "; -fx-font-weight:bold; -fx-font-size:13px;");

        HBox rb = new HBox(20, otoR, agR);
        rb.setPadding(new Insets(10));
        rb.setStyle("-fx-background-color:#f8fafc; -fx-background-radius:8; -fx-border-color:#e2e8f0; -fx-border-radius:8;");

        hataL.setStyle("-fx-text-fill:" + MainFrame.RED + "; -fx-font-size:12px; -fx-font-weight:bold;");

        v.getChildren().addAll(hdr, hr(), row("Plaka", plakaF), row("Giriş Saati", saatF), row("Araç Tipi", rb), hataL);
        return v;
    }

    private VBox row(String l, javafx.scene.Node n) { return new VBox(6, MainFrame.lbl(l, 12, true, MainFrame.TEXT2), n); }

    private void sf(TextField tf, String p) {
        tf.setPromptText(p);
        tf.setStyle("-fx-background-color:#f8fafc; -fx-text-fill:" + MainFrame.TEXT + "; -fx-border-color:" + MainFrame.BORDER + "; -fx-border-radius:6; -fx-background-radius:6; -fx-padding:10 12; -fx-font-size:14px;");
    }

    private javafx.scene.Node hr() { javafx.scene.layout.Region r = new javafx.scene.layout.Region(); r.setPrefHeight(1); r.setStyle("-fx-background-color:" + MainFrame.BORDER + ";"); return r; }

    private boolean ekle() {
        hataL.setText("");
        String pl = plakaF.getText().trim().toUpperCase();
        if (pl.length() < 5) { hataL.setText("Uyarı: Plaka en az 5 karakter olmalıdır."); return false; }
        double s;
        try { s = Double.parseDouble(saatF.getText().trim().replace(',','.')); }
        catch (Exception e) { hataL.setText("Uyarı: Geçersiz saat formatı. (Örn: 9.30)"); return false; }
        int tip = otoR.isSelected() ? 2 : 1;
        if (!yonetici.aracEkle(pl, s, tip)) { hataL.setText("Uyarı: '" + pl + "' plakası sistemde zaten var!"); return false; }
        return true;
    }

    private Arac bul() {
        String pl = plakaF.getText().trim().toUpperCase();
        for (Arac a : yonetici.getTumAraclar()) if (a.getPlaka().equalsIgnoreCase(pl)) return a;
        return null;
    }
}