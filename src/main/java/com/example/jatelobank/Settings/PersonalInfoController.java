package com.example.jatelobank.Settings;

import com.example.jatelobank.DatabaseConnection;
import com.example.jatelobank.SessionManager;
import com.example.jatelobank.User;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.stage.FileChooser;
import org.springframework.stereotype.Component;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URL;
import java.sql.*;
import java.util.ResourceBundle;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class PersonalInfoController implements Initializable {
    public ImageView imageView;
    public TextField FirstName;
    public TextField LastName;
    public TextField email;
    public TextField phoneNumber;
    public TextField streetAddress;
    public TextField ZIPcode;
    public TextField City;
    public TextField state;
    public TextField country;
    public Button saveButton;
    public Label label;
    public FontAwesomeIconView imageButton;
    public Label changeImageButton;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        DatabaseConnection connection = new DatabaseConnection();
        Connection connection1 = connection.getConn();
        User current = SessionManager.getInstance().getCurrentUser();
        if (current != null){
            String acc = current.getAccNo();

            String query = "select Image from Users where AccountNumber = '"+acc+"'";
            try {
                PreparedStatement ps = connection1.prepareStatement(query);
                ResultSet rs = ps.executeQuery();
                while (rs.next()){
                    Blob img  = rs.getBlob("Image");
                    if (img != null){
                        InputStream inputStream = img.getBinaryStream();
                        Image image = new Image(inputStream);
                        imageView.setImage(image);
                    }
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }

    }

    public void saveButt(ActionEvent event) {

        if (FirstName.getText().isEmpty()){
            label.setText("Please input your first name");

        } else if (!FirstName.getText().matches("[a-zA-Z]+")) {
            label.setText("Invalid first name");

        } else if (LastName.getText().isEmpty()){
            label.setText("Please input your last name");

        } else if (!LastName.getText().matches("[a-zA-Z]+")) {
            label.setText("Invalid last name");

        } else if (email.getText().isEmpty()){
            label.setText("Please input your email address");

        } else if (!emailValidator(email.getText())) {
            label.setText("PLease enter a valid email address");

        } else if (phoneNumber.getText().isEmpty()) {
            label.setText("Please input your phone number");

        } else if (!phoneNumber.getText().matches("\\d+") || !phoneNumber.getText().startsWith("07") || phoneNumber.getText().length() != 10) {
            label.setText("Invalid phone number type");

        } else if (streetAddress.getText().isEmpty()) {
            label.setText("Please input your street address");

        } else if (!streetAddress.getText().matches("\\d{3,},[a-zA-Z]+$")) {
            label.setText("Invalid street address");

        } else if (ZIPcode.getText().isEmpty()) {
            label.setText("Please input your ZIP code");

        } else if (!ZIPcode.getText().matches("\\d+") || ZIPcode.getText().length() != 5) {
            label.setText("Invalid ZIP code");

        } else if (City.getText().isEmpty()) {
            label.setText("Please input your City name");

        } else if (!City.getText().matches("[a-zA-Z]+")) {
            label.setText("Invalid city name");

        } else if (state.getText().isEmpty()) {
            label.setText("Please input your state name");

        } else if (!state.getText().matches("[a-zA-Z]+")) {
            label.setText("Invalid state name");
        } else if (country.getText().isEmpty()) {
            label.setText("Please input your Country");

        } else if (!country.getText().matches("[a-zA-Z]+")) {
            label.setText("Invalid country name");
        } else {

            DatabaseConnection connection = new DatabaseConnection();
            Connection connection1 = connection.getConn();

            User currentUser = SessionManager.getInstance().getCurrentUser();
            if (currentUser != null) {
                String acc = currentUser.getAccNo();


                String queryz = "select * from Users where Email = ? and AccountNumber != ?";
                try {
                    PreparedStatement ps = connection1.prepareStatement(queryz);
                    ps.setString(1, email.getText());
                    ps.setString(2,acc);

                    ResultSet rs = ps.executeQuery();
                    if (rs.next()) {
                        String emailz = rs.getString("Email");
                        if (email.getText().equals(emailz)) {
                            label.setText("Email address already taken");
                        }
                    } else {

                        //call the update class
                        update();

                        //clear the fields on updating the information
                        label.setText("Information updated successfully");
                        FirstName.clear();
                        LastName.clear();
                        email.clear();
                        phoneNumber.clear();
                        streetAddress.clear();
                        ZIPcode.clear();
                        City.clear();
                        state.clear();
                        country.clear();
                    }
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    public void update() {
        DatabaseConnection connection = new DatabaseConnection();
        Connection connection1 = connection.getConn();

        User currentUser = SessionManager.getInstance().getCurrentUser();
        if (currentUser != null) {
            String acc = currentUser.getAccNo();

            String query = "update Users set FirstName = ? , LastName = ? , Email = ? , PhoneNumber = ? , StreetAddress = ? , ZIPCode = ? , City = ? , State = ? , Country = ? where AccountNumber = ?";


            try (PreparedStatement ps = connection1.prepareStatement(query)) {
                ps.setString(1, FirstName.getText());
                ps.setString(2, LastName.getText());
                ps.setString(3, email.getText());
                ps.setString(4, phoneNumber.getText());
                ps.setString(5, streetAddress.getText());
                ps.setString(6, ZIPcode.getText());
                ps.setString(7, City.getText());
                ps.setString(8, state.getText());
                ps.setString(9, country.getText());
                ps.setString(10, acc);
                ps.executeUpdate();
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    public byte[] bufferedImageToByteArray(BufferedImage bufferedImage) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        try {
            ImageIO.write(bufferedImage,"png",byteArrayOutputStream);
            byteArrayOutputStream.flush();
            return byteArrayOutputStream.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


    public boolean emailValidator (String email){
            String EMAIL = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";

            Pattern pattern = Pattern.compile(EMAIL);
            Matcher matcher = pattern.matcher(email);

            boolean matches = matcher.matches();
            return matches;
    }

    public void imageButt(MouseEvent mouseEvent) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Image Files","*.png","*.jpg","*.jpeg"));

        File selectedFile = fileChooser.showOpenDialog(state.getContextMenu());
        if (selectedFile != null){
            Image image = new Image(selectedFile.toURI().toString());
            imageView.setImage(image);
        }
    }

    public void changeImageButton(MouseEvent mouseEvent) {
        DatabaseConnection connection = new DatabaseConnection();
        Connection connection1 = connection.getConn();

        User currentUser = SessionManager.getInstance().getCurrentUser();
        if (currentUser != null) {
            String acc = currentUser.getAccNo();

            String query = "update Users set Image = ? where AccountNumber = '"+acc+"'";
            try {
                PreparedStatement ps = connection1.prepareStatement(query);
                //set the image
                Image image = imageView.getImage();
                if (image != null){
                    BufferedImage bufferedImage = SwingFXUtils.fromFXImage(image,null);
                    byte[] imageBytes = bufferedImageToByteArray(bufferedImage);
                    ps.setBytes(1,imageBytes);
                    ps.executeUpdate();
                    label.setText("Image added successfully");
                }else {
                    label.setText("No image found");
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
