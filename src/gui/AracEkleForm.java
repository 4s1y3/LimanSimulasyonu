package gui;

import javafx.geometry.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.*;
import logic.LimanYonetici;
import models.Arac;

/**
 * Araç ekleme modalı — veri yapısı koduna dokunulmamıştır.
 */
public class AracEkleForm extends Dialog<Arac> {

    private final LimanYonetici yonetici;
    private final TextField plakaField = new TextField();
    private final TextField saatField  = new TextField();
    private final ToggleGroup tipGroup = new ToggleGroup();
    private final RadioButton otoRb    = new RadioButton("🚗  Otomobil");
    private final RadioButton agirRb   = new RadioButton("🚛  Ağır Vasıta");
    private final Label hataLbl        = new Label();

    public AracEkleForm(Stage owner, LimanYonetici yonetici, MainFrame host) {
        this.yonetici = yonetici;
        initOwner(owner);
        initModality(Modality.APPLICATION_MODAL);
        setTitle("Araç Ekle");
        setHeaderText(null);

        getDialogPane().setContent(buildForm());
        getDialogPane().setStyle("-fx-background-color:" + MainFrame.CARD + ";");

        ButtonType ekle  = new ButtonType("✅  Ekle",  ButtonBar.ButtonData.OK_DONE);
        ButtonType iptal = new ButtonType("✖  İptal", ButtonBar.ButtonData.CANCEL_CLOSE);
        getDialogPane().getButtonTypes().addAll(ekle, iptal);

        Button ekleBtn  = (Button) getDialogPane().lookupButton(ekle);
        Button iptalBtn = (Button) getDialogPane().lookupButton(iptal);

        ekleBtn .setStyle("-fx-background-color:" + MainFrame.GREEN + "; -fx-text-fill:white;" +
                "-fx-font-weight:bold; -fx-font-size:12px; -fx-padding:8 20; -fx-cursor:hand;");
        iptalBtn.setStyle("-fx-background-color:#2a1a1a; -fx-text-fill:white;" +
                "-fx-font-size:12px; -fx-padding:8 20; -fx-cursor:hand;");

        ekleBtn.addEventFilter(javafx.event.ActionEvent.ACTION, ev -> {
            if (!dogrulaVeEkle()) ev.consume();
        });

        setResultConverter(btn -> btn == ekle ? bulunenArac() : null);
    }

    private VBox buildForm() {
        VBox form = new VBox(14);
        form.setPadding(new Insets(20, 24, 10, 24));
        form.setMinWidth(340);
        form.setStyle("-fx-background-color:" + MainFrame.CARD + ";");

        // Başlık
        HBox hdr = new HBox(10);
        hdr.setAlignment(Pos.CENTER_LEFT);
        StackPane logo = new StackPane();
        Circle c = new Circle(16, Color.web(MainFrame.GOLD));
        Label an = MainFrame.txt("⚓", 12, false, "#0a1520");
        logo.getChildren().addAll(c, an);
        hdr.getChildren().addAll(logo, MainFrame.txt("Yeni Araç Ekle", 15, true, MainFrame.TEXT));

        // Alanlar
        styleField(plakaField, "Örn: 34ABC12");
        plakaField.setTextFormatter(new TextFormatter<>(ch -> {
            ch.setText(ch.getText().toUpperCase()); return ch; }));
        styleField(saatField, "Örn: 9.30");

        otoRb .setToggleGroup(tipGroup); otoRb.setSelected(true);
        agirRb.setToggleGroup(tipGroup);
        otoRb .setStyle("-fx-text-fill:" + MainFrame.CAR_COL   + "; -fx-font-size:13px;");
        agirRb.setStyle("-fx-text-fill:" + MainFrame.TRUCK_COL + "; -fx-font-size:13px;");
        HBox radios = new HBox(20, otoRb, agirRb);
        radios.setAlignment(Pos.CENTER_LEFT);
        radios.setPadding(new Insets(8, 10, 8, 10));
        radios.setStyle("-fx-background-color:#0a1520; -fx-background-radius:8;");

        hataLbl.setStyle("-fx-text-fill:" + MainFrame.RED + "; -fx-font-size:11px;");
        hataLbl.setWrapText(true);

        form.getChildren().addAll(hdr, sep(),
                row("🔤  Plaka",       plakaField),
                row("⏰  Giriş Saati", saatField),
                row("🚘  Araç Tipi",   radios),
                hataLbl);
        return form;
    }

    private VBox row(String label, javafx.scene.Node field) {
        return new VBox(4, MainFrame.txt(label, 11, true, MainFrame.TEXT2), field);
    }

    private void styleField(TextField tf, String prompt) {
        tf.setPromptText(prompt);
        tf.setStyle("-fx-background-color:#0a1520; -fx-text-fill:" + MainFrame.TEXT +
                "; -fx-prompt-text-fill:#3a6a7a; -fx-border-color:" + MainFrame.BORDER +
                "; -fx-border-radius:6; -fx-background-radius:6; -fx-padding:8 12; -fx-font-size:13px;");
    }

    private javafx.scene.Node sep() {
        javafx.scene.layout.Region r = new javafx.scene.layout.Region();
        r.setPrefHeight(1);
        r.setStyle("-fx-background-color:" + MainFrame.BORDER + ";");
        return r;
    }

    private boolean dogrulaVeEkle() {
        hataLbl.setText("");
        String plaka  = plakaField.getText().trim().toUpperCase();
        String saatSt = saatField.getText().trim();
        if (plaka.isEmpty())              { hata("Plaka boş olamaz.");           return false; }
        if (plaka.length() < 5)           { hata("Plaka en az 5 karakter.");     return false; }
        double saat;
        try { saat = Double.parseDouble(saatSt.replace(',','.')); }
        catch (Exception e) { hata("Geçersiz saat. Örn: 9.30");                 return false; }
        int tip = otoRb.isSelected() ? 2 : 1;
        if (!yonetici.aracEkle(plaka, saat, tip)) {
            hata("'" + plaka + "' zaten kayıtlı!"); return false;
        }
        return true;
    }

    private Arac bulunenArac() {
        String plaka = plakaField.getText().trim().toUpperCase();
        for (Arac a : yonetici.getTumAraclar())
            if (a.getPlaka().equalsIgnoreCase(plaka)) return a;
        return null;
    }

    private void hata(String msg) { hataLbl.setText("⚠  " + msg); }
}
