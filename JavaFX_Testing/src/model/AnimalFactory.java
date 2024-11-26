package model;

public class AnimalFactory {
    public static Animal createAnimal(String type, String name, int age, String gender, String attributes,String allergies, String medication) {
        switch (type.toLowerCase()) {
            case "dog":
                return new Dog(name, age, gender, attributes, allergies, medication); // Breed
            case "cat":
                return new Cat(name, age, gender, attributes, allergies, medication); // Fur color
            case "bird":
                return new Bird(name, age, gender, attributes, allergies, medication); // Wingspan
            default:
                throw new IllegalArgumentException("Invalid animal type: " + type);
        }
    }

	public static Animal createAnimal_withID(int ID,String type, String name, int age, String gender,String attributes, String allergies, String medication) {
		switch (type.toLowerCase()) {
        case "dog":
            return new Dog(ID, name, age, gender, attributes, allergies, medication); // Breed
        case "cat":
            return new Cat(ID, name, age, gender, attributes, allergies, medication); // Fur color
        case "bird":
            return new Bird(ID, name, age, gender,attributes, allergies, medication); // Wingspan
        default:
            throw new IllegalArgumentException("Invalid animal type: " + type);
		}
	}
}














