package model;

public class Cat extends AnimalHistory implements Animal {
    private String name;
	private int ID;
    private int age;
    private String gender;
    private String furcolor ;

    public Cat(String name, int age, String gender, String furcolor, String allergies, String medication ) {
    	super(allergies,medication);
        this.name = name;
        this.age = age;
        this.gender = gender;
        this.furcolor  = furcolor ;
    }
    
    public Cat(int ID, String name, int age, String gender, String furcolor, String allergies, String medication ) {
    	super(allergies,medication);
    	this.ID = ID;
        this.name = name;
        this.age = age;
        this.gender = gender;
        this.furcolor  = furcolor ;
    }
    
    @Override
    public int getId() { return ID; };
    @Override
    public String getName() { return name; }
    @Override
    public String getType() { return "Cat"; }
    @Override
    public int getAge() { return age; }
    @Override
    public String getGender() { return gender; }

    public String getfurcolor() { return furcolor; }

	@Override
	public void setId(int ID) {
		this.ID = ID;	
	}
}
