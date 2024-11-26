package model;

public class Bird extends AnimalHistory implements Animal {
	private int ID;
    private String name;
    private int age;
    private String gender;
    private String wingspan;

    public Bird(String name, int age, String gender, String wingspan, String allergies, String medication) {
    	super(allergies,medication);
    	this.name = name;
        this.age = age;
        this.gender = gender;
        this.wingspan = wingspan;
    }
    
    public Bird(int ID, String name, int age, String gender, String wingspan, String allergies, String medication) {
    	super(allergies,medication);
    	this.ID = ID;
        this.name = name;
        this.age = age;
        this.gender = gender;
        this.wingspan = wingspan;
    }
    
    @Override
    public int getId() { return ID; };
    @Override
    public String getName() { return name; }
    @Override
    public String getType() { return "Bird"; }
    @Override
    public int getAge() { return age; }
    @Override
    public String getGender() { return gender; }

    public String getwingspan() { return wingspan; }

	@Override
	public void setId(int ID) {
		this.ID = ID;	
	}
}
