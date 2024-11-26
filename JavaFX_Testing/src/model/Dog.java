package model;

public class Dog extends AnimalHistory implements Animal {
    private String name;
    private int age;
    private String gender;
	private int ID;
    private String breed;

    public Dog(String name, int age, String gender, String breed, String allergies, String medication) {
    	super(allergies,medication);
        this.name = name;
        this.age = age;
        this.gender = gender;
        this.breed = breed;
    }
    
    public Dog(int ID, String name, int age, String gender, String breed, String allergies, String medication) {
    	super(allergies,medication);
    	this.ID = ID;
        this.name = name;
        this.age = age;
        this.gender = gender;
        this.breed = breed;
    }
    
    @Override
    public int getId() { return ID; };
    @Override
    public String getName() { return name; }
    @Override
    public String getType() { return "Dog"; }
    @Override
    public int getAge() { return age; }
    @Override
    public String getGender() { return gender; }

    public String getBreed() { return breed; }

	@Override
	public void setId(int ID) {
		this.ID = ID;	
	}
}
