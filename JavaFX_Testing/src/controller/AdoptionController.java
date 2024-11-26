package controller;

import application.Main;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.input.MouseEvent;
import model.Animal;
import model.Bird;
import model.Cat;
import model.Clinic;
import model.Dog;

public class AdoptionController {

    @FXML
    private Button adoptButton;

    @FXML
    private TableView<Animal> adoptionTable;

    @FXML
    private TableColumn<Animal, String> animalAgeColumn;

    @FXML
    private TableColumn<Animal, String> animalAttributeColumn;

    @FXML
    private TableColumn<Animal, String> animalGenderColumn;

    @FXML
    private TableColumn<Animal, String> animalNameColumn;

    @FXML
    private TableColumn<Animal, String> animalTypeColumn;

    @FXML
    private Button backButton;

    private ObservableList<Animal> animals;
    
    SessionManager sessionManager;

    public AdoptionController() {
    	sessionManager = SessionManager.getInstance();
    }

    @FXML
    void handleAnimalAdoption(MouseEvent event) {
        Animal selectedAnimal = adoptionTable.getSelectionModel().getSelectedItem();
        if (selectedAnimal == null) {
            showError("Please select an animal to adopt.");
            return;
        }
        try {
            // Update the status of the selected animal to 'adopted'
            boolean success = Clinic.updateAnimalAdoptionStatus(selectedAnimal, 1,this.sessionManager.getCurrentUser());

            if (success) {
                animals.remove(selectedAnimal); // Update the table to hide adopted animals
                showSuccess("Congratulations! You have adopted " + selectedAnimal.getName() + ".");
            } else {
                showError("Adoption failed. Please try again.");
            }
        } catch (Exception e) {
            showError("An unexpected error occurred: " + e.getMessage());
        }
    }

    @FXML
    void handleBackButton(MouseEvent event) {
        Main.changeScene("/view/Menu.fxml");
    }

    @FXML
    void initialize() {
        // Set up the table columns
    	animalNameColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getName()));
        animalTypeColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getType()));
        animalAgeColumn.setCellValueFactory(cellData -> new SimpleStringProperty(String.valueOf(cellData.getValue().getAge())));
        animalGenderColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getGender()));
        animalAttributeColumn.setCellValueFactory(cellData -> {
            Animal animal = cellData.getValue();

            // Determine the specific attribute based on the type of the animal
            if (animal instanceof Dog) {
                return new SimpleStringProperty(((Dog) animal).getBreed());
            } else if (animal instanceof Bird) {
                return new SimpleStringProperty(((Bird) animal).getwingspan());
            } else if (animal instanceof Cat) {
                return new SimpleStringProperty(((Cat) animal).getfurcolor());
            } else {
                return new SimpleStringProperty("Unknown");
            }
        });

        // Fetch data from the database and display only non-adopted animals
        animals = Clinic.fetchNonAdoptedAnimals();
        if (animals != null && !animals.isEmpty()) {
            adoptionTable.setItems(animals);
        } else {
            showError("No animals available for adoption at the moment.");
        }
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showSuccess(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Success");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
