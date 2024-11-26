package PersistenceHandler;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import java.time.*;
import model.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class DBHandler extends PersistenceHandler {

    private static DBHandler instance;
    private Connection connection;
    private static final String URL = "jdbc:mysql://localhost:3306/veterinarysystem";
    private static final String USER = "root";
    private static final String PASSWORD = "mypass123";

    // Private constructor to prevent instantiation
    private DBHandler() {
        try {
        	connection = DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Get the single instance of SQLHandler
    public static synchronized DBHandler getInstance() {
        if (instance == null) {
            instance = new DBHandler();
        }
        return instance;
    }
    
    public Object verifyLogin(String username, String password) {
    	if ("johann.walt@admin.com".equals(username) && "adminpassword".equals(password)) {
            return Admin.getInstance();  // Return the singleton instance of Admin
        }
    	
        String userQuery = "SELECT name, email, phoneNumber, password, loginname FROM user WHERE loginname = ? AND password = ?";
        String doctorQuery = "SELECT doctorid, name, email, phoneNumber, loginname, password FROM doctor WHERE loginname = ? AND password = ?";

        try (Connection conn = getConnection();
             PreparedStatement userStmt = conn.prepareStatement(userQuery);
             PreparedStatement doctorStmt = conn.prepareStatement(doctorQuery)) {

            // Check in the User table
            userStmt.setString(1, username);
            userStmt.setString(2, password);
            ResultSet userResult = userStmt.executeQuery();

            if (userResult.next()) {
                // Match found in User table
                return new User(
                    userResult.getString("name"),
                    userResult.getString("email"),
                    userResult.getString("phoneNumber"),
                    userResult.getString("password"),
                    userResult.getString("loginname")
                );
            }

            // Check in the Doctor table
            doctorStmt.setString(1, username);
            doctorStmt.setString(2, password);
            ResultSet doctorResult = doctorStmt.executeQuery();

            if (doctorResult.next()) {
                // Match found in Doctor table
                return new Doctor(
                    doctorResult.getInt("doctorid"),
                    doctorResult.getString("name"),
                    doctorResult.getString("email"),
                    doctorResult.getInt("phoneNumber"),
                    doctorResult.getString("loginname"),
                    doctorResult.getString("password")
                );
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        // No match found in either table
        return null;
    }
    
    public boolean registerUser(User user) {
        // SQL query to insert a new user into the database
        String query = "INSERT INTO user (name, email, phonenumber, password, loginname) VALUES (?, ?, ?, ?, ?)";

        try (	Connection connection = getConnection();
        		PreparedStatement stmt = connection.prepareStatement(query)) {
            // Set the values from the User object
            stmt.setString(1, user.getName());
            stmt.setString(2, user.getEmail());
            stmt.setString(3, user.getPhoneNumber());
            stmt.setString(4, user.getPassword());
            stmt.setString(5, user.getUsername());
            
            // Execute the query
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;  // Return true if the user was successfully registered
        } catch (SQLException e) {
            e.printStackTrace();
            return false;  // Return false if there is an error
        }
    }

    public boolean insertReferral(Referral referral) {
        String query = "INSERT INTO referafriend (referralname, referralemail) VALUES (?, ?)";
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setString(1, referral.getReferralName());
            statement.setString(2, referral.getReferralEmail());

            int rowsAffected = statement.executeUpdate();
            return rowsAffected > 0; // Return true if insertion was successful

        } catch (SQLIntegrityConstraintViolationException e) {
            System.out.println("Error: Duplicate email entry.");
            return false;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public ObservableList<DoctorAppointment> fetchAvailableSlots() {
        ObservableList<DoctorAppointment> availableSlots = FXCollections.observableArrayList();
        String query = "SELECT d.doctorid, d.name, da.timeslot FROM doctor d "
                     + "JOIN doctor_appointment da ON d.doctorid = da.doctorid "
                     + "WHERE da.timeslot > NOW()";

        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(query);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                int doctorId = resultSet.getInt("doctorid");
                String doctorName = resultSet.getString("name");
                Timestamp timeslot = resultSet.getTimestamp("timeslot");

                availableSlots.add(new DoctorAppointment(doctorId, doctorName, timeslot));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return availableSlots;
    }
    
    public int bookAppointment(User user, Doctor doctor, Animal animal, DoctorAppointment appointmentTime, String checkUpMode) {
        String insertQuery = "INSERT INTO appointment (userid, doctorid, animalid, appointmenttime, typeofcheckup) VALUES (?, ?, ?, ?, ?)";
        String deleteQuery = "DELETE FROM doctor_appointment WHERE doctorid = ? AND timeslot = ?";
        String checkupQuery = "INSERT INTO checkup (appointmentid, checkup_status, hourstaken , amount) VALUES (?, ?,0,0)";

        try (Connection connection = getConnection();
             PreparedStatement insertStatement = connection.prepareStatement(insertQuery, Statement.RETURN_GENERATED_KEYS);
             PreparedStatement deleteStatement = connection.prepareStatement(deleteQuery);
             PreparedStatement checkupStatement = connection.prepareStatement(checkupQuery)) {

            // Insert appointment
            insertStatement.setInt(1, this.fetchUserIdByUsername(user.getUsername()));
            insertStatement.setInt(2, doctor.getDoctorId());
            insertStatement.setInt(3, animal.getId());
            insertStatement.setTimestamp(4, appointmentTime.getTimeSlot());
            insertStatement.setString(5, checkUpMode);
            int rowsInserted = insertStatement.executeUpdate();

            // Retrieve the generated appointment ID
            int appointmentId = -1;
            try (ResultSet generatedKeys = insertStatement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    appointmentId = generatedKeys.getInt(1);
                } else {
                    throw new SQLException("Failed to retrieve appointment ID.");
                }
            }

            // Remove the booked time slot
            deleteStatement.setInt(1, doctor.getDoctorId());
            deleteStatement.setTimestamp(2, appointmentTime.getTimeSlot());
            int rowsDeleted = deleteStatement.executeUpdate();

            // Insert into checkup table
            checkupStatement.setInt(1, appointmentId);
            checkupStatement.setBoolean(2, false); // Set checkup_status to false
            int checkupInserted = checkupStatement.executeUpdate();

            // Return success status based on all operations
            if(rowsInserted > 0 && rowsDeleted > 0 && checkupInserted > 0) {
            	return appointmentId;
            }

        } catch (SQLException e) {
            e.printStackTrace();
            return 0;
        }
		return 0;
    }

    
    public int getLastCheckupId() {
        String query = "SELECT checkupid FROM checkup ORDER BY checkupid DESC LIMIT 1";
        try (Connection conn = getConnection(); 
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            
            if (rs.next()) {
                return rs.getInt("checkupid");  // Return the last checkupid
            } else {
                // No checkup records exist in the table
                return -1;  // Return a default value indicating no records
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
            return -1;  // Return a default value indicating an error
        }
    }

    public Certificate createCertificate(User user, Doctor doctor, Animal animal, Checkup checkup) {
        String query = "INSERT INTO certificate (userid, doctorid, animalid, checkupid, approval) VALUES (?, ?, ?, ?, 0)";
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {

            // Set the parameters for the query
            statement.setInt(1, this.fetchUserIdByUsername(user.getUsername()));
            statement.setInt(2, doctor.getDoctorId());
            statement.setInt(3, animal.getId());
            statement.setInt(4, checkup.getCheckupid());

            // Execute the query
            int rowsInserted = statement.executeUpdate();

            if (rowsInserted > 0) {
                // Retrieve the generated certificate ID
                try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        int certificateId = generatedKeys.getInt(1);

                        // Return the created Certificate object
                        return new Certificate(certificateId, checkup.getCheckupid(),
                        		 this.fetchUserIdByUsername(user.getUsername()), doctor.getDoctorId(),
                                               animal.getId(), false);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null; // Return null if the insertion fails
    }

    
    public Checkup getCheckupByAppointmentId(int appointmentId,User user, Animal animal) throws SQLException {
        // Query the database to fetch checkup details for the given appointment ID
        String query = "SELECT * FROM Checkup WHERE appointmentId = ?";
        String query_checkuptype = "SELECT typeofcheckup FROM appointment a JOIN checkup c ON a.appointmentid = c.appointmentid WHERE a.appointmentid = ?";
        
        try (Connection connection = getConnection();
        	PreparedStatement statement_checkup = connection.prepareStatement(query);
        	PreparedStatement statement_type = connection.prepareStatement(query_checkuptype)){
        	
        	statement_checkup.setInt(1, appointmentId);
        	statement_type.setInt(1, appointmentId);
        	
            ResultSet resultSet_checkup = statement_checkup.executeQuery();
            ResultSet resultSet_type = statement_type.executeQuery();
            
            Checkup checkup=null;
            
            if (resultSet_checkup.next()) {
                // Extract details from the result set
            	int id = resultSet_checkup.getInt("checkupid");
                String checkupStatus = resultSet_checkup.getString("checkup_status");
                int hoursTaken = resultSet_checkup.getInt("hoursTaken");
                
                if(resultSet_type.next()) {
                	String type = resultSet_type.getString("typeofcheckup");
                	
                	// Create and return the Checkup object
                	checkup = new Checkup(id,appointmentId,user.getName(),animal.getName(),type,checkupStatus);
                	checkup.setHoursTaken(hoursTaken);
                }
                return checkup;
            } else {
                throw new SQLException("No checkup found for appointment ID: " + appointmentId);
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving checkup: " + e.getMessage());
            throw e;
        }
    }
    
    public String getallergies(Animal animal) {
    	// return allergies
        if (animal instanceof Dog) {
            return ((Dog) animal).getAllergies();
        } else if (animal instanceof Cat) {
        	return ((Cat) animal).getAllergies();
        } else if (animal instanceof Bird) {
        	return ((Bird) animal).getAllergies();
        }
        return "";
    }
    
    public String getmedication(Animal animal) {
    	// return medication
        if (animal instanceof Dog) {
            return ((Dog) animal).getMedications();
        } else if (animal instanceof Cat) {
        	return ((Cat) animal).getMedications();
        } else if (animal instanceof Bird) {
        	return ((Bird) animal).getMedications();
        }
        return "";
    }


    public int saveOrFetchAnimal(Animal animal, User user) {
        String query = "SELECT animalid FROM animal WHERE animalname = ? AND age = ? AND gender = ? AND animaltype = ?";

        try (Connection connection = getConnection();
             PreparedStatement stmt = connection.prepareStatement(query)) {

            stmt.setString(1, animal.getName());
            stmt.setInt(2, animal.getAge());
            stmt.setString(3, animal.getGender());
            stmt.setString(4, animal.getType());
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getInt("animalid"); // Return existing ID
            }

            // Insert into the `animal` table
            query = "INSERT INTO animal (animalname, animaltype, age, gender, adoptionstatus) VALUES (?, ?, ?, ?, ?)";
            try (PreparedStatement insertStmt = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
                insertStmt.setString(1, animal.getName());
                insertStmt.setString(2, animal.getType());
                insertStmt.setInt(3, animal.getAge());
                insertStmt.setString(4, animal.getGender());
                insertStmt.setBoolean(5, true); // Default adoption status
                insertStmt.executeUpdate();

                rs = insertStmt.getGeneratedKeys();
                if (rs.next()) {
                    int animalId = rs.getInt(1);

                    // Insert into specific animal table
                    if (animal instanceof Dog) {
                        String specificQuery = "INSERT INTO dog (animalid, breed) VALUES (?, ?)";
                        try (PreparedStatement specificStmt = connection.prepareStatement(specificQuery)) {
                            specificStmt.setInt(1, animalId);
                            specificStmt.setString(2, ((Dog) animal).getBreed());
                            specificStmt.executeUpdate();
                        }
                    } else if (animal instanceof Cat) {
                        String specificQuery = "INSERT INTO cat (animalid, furcolor) VALUES (?, ?)";
                        try (PreparedStatement specificStmt = connection.prepareStatement(specificQuery)) {
                            specificStmt.setInt(1, animalId);
                            specificStmt.setString(2, ((Cat) animal).getfurcolor());
                            specificStmt.executeUpdate();
                        }
                    } else if (animal instanceof Bird) {
                        String specificQuery = "INSERT INTO bird (animalid, wingspan) VALUES (?, ?)";
                        try (PreparedStatement specificStmt = connection.prepareStatement(specificQuery)) {
                            specificStmt.setInt(1, animalId);
                            specificStmt.setString(2, ((Bird) animal).getwingspan());
                            specificStmt.executeUpdate();
                        }
                    }

                    // Insert into `animal_user` table
                    String animalUserQuery = "INSERT INTO animal_user (userid, animalid) VALUES (?, ?)";
                    try (PreparedStatement animalUserStmt = connection.prepareStatement(animalUserQuery)) {
                        animalUserStmt.setInt(1, this.fetchUserIdByUsername(user.getUsername()));
                        animalUserStmt.setInt(2, animalId);
                        animalUserStmt.executeUpdate();
                    }

                    // Insert into `animalhistory` table
                    String historyQuery = "INSERT INTO animalhistory (animalid, allergies, medications) VALUES (?, ?, ?)";
                    String allergies = this.getallergies(animal);
                    String medications = this.getmedication(animal);
                    
                    try (PreparedStatement historyStmt = connection.prepareStatement(historyQuery)) {
                        historyStmt.setInt(1, animalId);
                        historyStmt.setString(2, allergies);
                        historyStmt.setString(3, medications);
                        historyStmt.executeUpdate();
                    }

                    return animalId;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1; // On failure
    }
    
    public ObservableList<Animal> fetchNonAdoptedAnimals() {
        ObservableList<Animal> animals = FXCollections.observableArrayList();
        String query = "SELECT a.animalid, a.animalname, a.animaltype, a.age, a.gender, " +
                "d.breed AS dogAttribute, c.furcolor AS catAttribute, b.wingspan AS birdAttribute, " +
                "animalhistory.allergies, animalhistory.medications " +
                "FROM animal a " +
                "LEFT JOIN dog d ON a.animalid = d.animalid " +
                "LEFT JOIN cat c ON a.animalid = c.animalid " +
                "LEFT JOIN bird b ON a.animalid = b.animalid " +
                "LEFT JOIN animalhistory ON a.animalid = animalhistory.animalid " +
                "WHERE a.adoptionstatus = 0;";


        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                // Extract common attributes
                int id = rs.getInt("animalid");
                String type = rs.getString("animaltype");
                String name = rs.getString("animalname");
                int age = rs.getInt("age");
                String gender = rs.getString("gender");
                String allergies = rs.getString("allergies");
                String medication = rs.getString("medications");

                // Determine specific attribute based on the type
                String specificAttribute = null;
                switch (type.toLowerCase()) {
                    case "dog":
                        specificAttribute = rs.getString("dogAttribute");
                        break;
                    case "cat":
                        specificAttribute = rs.getString("catAttribute");
                        break;
                    case "bird":
                        specificAttribute = rs.getString("birdAttribute"); // Convert wingspan to string
                        break;
                    default:
                        System.err.println("Error: Unrecognized animal type in database: " + type);
                }

                if (specificAttribute != null) {
                    // Use AnimalFactory to create the appropriate Animal object
                    Animal animal = AnimalFactory.createAnimal_withID(
                        id, type, name, age, gender, specificAttribute, allergies, medication
                    );
                    animals.add(animal);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return animals;
    }


    public boolean updateAnimalAdoptionStatus(Animal animal, int adoptedStatus, User user) {
    	String query_mapping = "INSERT INTO animal_user (userid, animalid) VALUES (?, ?)";
        String query = "UPDATE Animal SET adoptionstatus = ? WHERE animalid = ?";
        try (Connection conn = getConnection();
        	 PreparedStatement stmt_mapping = conn.prepareStatement(query_mapping);
             PreparedStatement stmt = conn.prepareStatement(query)) {
        	
            stmt.setInt(1, adoptedStatus); // 1 for adopted, 0 for not adopted
            stmt.setInt(2, animal.getId());
            int rowsAffected = stmt.executeUpdate();
            
            stmt_mapping.setInt(1, this.fetchUserIdByUsername(user.getUsername()));
            stmt_mapping.setInt(2, animal.getId());
            
            return stmt_mapping.executeUpdate() > 0 && rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public int fetchUserIdByUsername(String username) {
        String query = "SELECT userid FROM user WHERE loginname = ?";
        try (Connection connection = this.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setString(1, username);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                return resultSet.getInt("userid");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1; // Return -1 if the user ID is not found
    }
    
    public boolean isUsernameTaken(String username) {
        String query = "SELECT COUNT(*) FROM user WHERE loginname = ?";
        try (Connection connection = this.getConnection();
        		PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0; // If count > 0, username is taken
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public List<String> getUserPets(int userId) {
        String query = "SELECT a.animalname FROM animal_user au " +
                       "JOIN animal a ON au.animalid = a.animalid " +
                       "WHERE au.userid = ?";
        List<String> pets = new ArrayList<>();
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                pets.add(rs.getString("animalname"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return pets;
    }

    
    public Animal getPetByName(String petName, User user) {
    	if(petName==null) {
    		return null;
    	}
        String query = " SELECT a.animalid, a.animalname, a.animaltype, a.age, a.gender,animalhistory.allergies, animalhistory.medications, d.breed, c.furcolor, b.wingspan\r\n"
        		+ " FROM animal a \r\n"
        		+ " LEFT JOIN dog d ON a.animalid = d.animalid \r\n"
        		+ " LEFT JOIN cat c ON a.animalid = c.animalid \r\n"
        		+ " LEFT JOIN bird b ON a.animalid = b.animalid \r\n"
        		+ " LEFT JOIN animalhistory ON a.animalid = animalhistory.animalid \r\n"
        		+ " LEFT JOIN animal_user au ON au.animalid = a.animalid   \r\n"
        		+ " WHERE au.userid = ? and a.animalname = ?;";
        Animal animal = null;
        try (Connection conn = getConnection();
                PreparedStatement stmt = conn.prepareStatement(query)){
        		stmt.setInt(1,  this.fetchUserIdByUsername(user.getUsername()));
        		stmt.setString(2,  petName);
        		ResultSet rs = stmt.executeQuery();

               while (rs.next()) {
                   // Extract common attributes
                   int id = rs.getInt("animalid");
                   String type = rs.getString("animaltype");
                   int age = rs.getInt("age");
                   String gender = rs.getString("gender");
                   String allergies = rs.getString("allergies");
                   String medication = rs.getString("medications");

                   // Determine specific attribute based on the type
                   String specificAttribute = null;
                   switch (type.toLowerCase()) {
                       case "dog":
                           specificAttribute = rs.getString("breed");
                           break;
                       case "cat":
                           specificAttribute = rs.getString("furcolor");
                           break;
                       case "bird":
                           specificAttribute = rs.getString("wingspan"); // Convert wingspan to string
                           break;
                       default:
                           System.err.println("Error: Unrecognized animal type in database: " + type);
                   }

                   if (specificAttribute != null) {
                       // Use AnimalFactory to create the appropriate Animal object
                	 //create animal
                     animal = AnimalFactory.createAnimal_withID(id, type, petName, age, gender, specificAttribute, allergies, medication);
                       
                   }
               }
           } catch (SQLException e) {
               e.printStackTrace();
           }
        return animal;
    }
    
    
    public Map<String, String> getAnimalDetailsById(int animalId) {
        // SQL query to fetch animal details
        String query = "SELECT type, name, age, gender, attribute AS specificAttribute FROM Animals WHERE id = ?";
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, animalId);
            ResultSet rs = statement.executeQuery();
            
            if (rs.next()) {
                Map<String, String> animalDetails = new HashMap<>();
                animalDetails.put("type", rs.getString("type"));
                animalDetails.put("name", rs.getString("name"));
                animalDetails.put("age", rs.getString("age"));
                animalDetails.put("gender", rs.getString("gender"));
                animalDetails.put("specificAttribute", rs.getString("specificAttribute"));
                return animalDetails;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    public boolean isCheckUpDone(Animal selectedPet) throws SQLException {
        String query = "SELECT COUNT(*)\r\n"
        		+ "FROM appointment as a JOIN checkup as c\r\n"
        		+ "ON a.appointmentid = c.appointmentid\r\n"
        		+ "WHERE a.animalid = ? AND checkup_status = 1;";
        try (	Connection conn = getConnection();
        		PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, selectedPet.getId());
            ResultSet rs = stmt.executeQuery();
            return rs.next() && rs.getInt(1) > 0;
        }
    }

    public boolean isCertificateApproved(Animal selectedPet) throws SQLException {
        String query = "SELECT approval FROM certificate WHERE animalid = ?";
        try (	Connection conn = getConnection();
        		PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, selectedPet.getId());
            ResultSet rs = stmt.executeQuery();
            return rs.next() && rs.getBoolean("approval");
        }
    }

    public boolean generateCertificate(int userId) {
        String query = "UPDATE certificate SET approval = 1 WHERE userid = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, userId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public Doctor findDoctorById(int doctorId) {
        String query = "SELECT doctorid, name, email, phoneNumber, loginname, password FROM doctor WHERE doctorid = ?";
        DBHandler dbHandler = DBHandler.getInstance();

        try (Connection conn = dbHandler.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, doctorId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return new Doctor(
                    rs.getInt("doctorid"),
                    rs.getString("name"),
                    rs.getString("email"),
                    rs.getInt("phoneNumber"),
                    rs.getString("loginname"),
                    rs.getString("password")
                );
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null; // Return null if no doctor is found
    }

    public Boolean insertDoctorData(String name, String email, String contact, String loginName, String password) throws SQLException {
        // Establish a connection to the database (use your own database connection details)
        String insertSQL = "INSERT INTO doctor (name, email, phonenumber, loginname, password) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement preparedStatement = connection.prepareStatement(insertSQL)) {
            preparedStatement.setString(1, name);
            preparedStatement.setString(2, email);
            preparedStatement.setString(3, contact);
            preparedStatement.setString(4, loginName);
            preparedStatement.setString(5, password);

            int rowsAffected = preparedStatement.executeUpdate();
            if (rowsAffected > 0) {
                return true;
            } else {
                return false;
            }
        }
    }
    
    public List<Appointment> fetchDoctorAppointments(int doctorId) {
        List<Appointment> appointmentList = new ArrayList<>();

        String query = """
                 SELECT 
	            ap.appointmentid,
	            u.name AS username,
	            a.animalname AS animalname,
	            ap.appointmenttime,
	            ap.typeofcheckup
		        FROM 
		            appointment ap
		        INNER JOIN 
		            user u ON ap.userid = u.userid
		        INNER JOIN 
		            animal a ON ap.animalid = a.animalid
		        WHERE 
		            ap.doctorid = ?;
                """;

        try { 
        	connection = DriverManager.getConnection(URL, USER, PASSWORD);
        	PreparedStatement preparedStatement = connection.prepareStatement(query); 
            preparedStatement.setInt(1, doctorId);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    String username = resultSet.getString("username");
                    String animalname = resultSet.getString("animalname");
                    LocalDateTime appointmentTime = resultSet.getTimestamp("appointmenttime").toLocalDateTime();
                    String typeOfCheckup = resultSet.getString("typeofcheckup");
                    
                    // Create an Appointment object and add it to the list
                    Appointment appointment = new Appointment(username, animalname, appointmentTime, typeOfCheckup);
                    appointmentList.add(appointment);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace(); // Log exception for debugging
        }

        return appointmentList;
    }
    
    public Boolean insertDoctorAppointment(int doctorId, LocalDate date, LocalTime time) {
    	LocalDateTime dateTime = LocalDateTime.of(date, time);

        // SQL query to insert the doctor appointment
        String query = "INSERT INTO doctor_appointment (doctorid, timeslot) VALUES (?, ?)";

        try (Connection connection = getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            // Set parameters for the prepared statement
            preparedStatement.setInt(1, doctorId);
            preparedStatement.setTimestamp(2, Timestamp.valueOf(dateTime));

            // Execute the query and check if the insert was successful
            int rowsAffected = preparedStatement.executeUpdate();

            // Return true if one or more rows were inserted, false otherwise
            return rowsAffected > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public List<Checkup> loadCheckupsForDoctor(int doctorId) {
    	List<Checkup> checkupList = new ArrayList<>();
    	
        String query = """
                SELECT  
                	c.appointmentid,
                    u.name AS username, 
                    an.animalname,  
                    a.typeofcheckup, 
                    c.checkup_status,
                    c.checkupid
                FROM 
                    appointment a
                JOIN 
                    user u ON a.userid = u.userid
                JOIN 
                    animal an ON a.animalid = an.animalid
                JOIN 
                    checkup c ON a.appointmentid = c.appointmentid
                WHERE 
                    a.doctorid = ?;
                """;

        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            
            preparedStatement.setInt(1, doctorId);
            ResultSet resultSet = preparedStatement.executeQuery();

            // Process the result set
            while (resultSet.next()) {
            	int id = resultSet.getInt("checkupid");
            	int app_id = resultSet.getInt("appointmentid");
                String username = resultSet.getString("username");
                String animalName = resultSet.getString("animalname");
                String checkupType = resultSet.getString("typeofcheckup");
                boolean checkupStatus = resultSet.getBoolean("checkup_status");
                
                String checkupState = "";
                if(checkupStatus == true)	checkupState = "Performed";
                else						checkupState = "Not Performed";
                
                Checkup checkup = new Checkup(id,app_id, username, animalName, checkupType, checkupState);
                checkupList.add(checkup);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return checkupList;
    }
    
    public boolean updateCheckup(int appointmentId, int hours, Boolean status, float amount) {
        String query = " UPDATE Checkup SET hourstaken = ?, checkup_status = ?, amount = ? WHERE appointmentId = ?";
        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, hours);
            stmt.setBoolean(2, status);
            stmt.setInt(4, appointmentId);
            stmt.setFloat(3, amount);

            int rowsUpdated = stmt.executeUpdate();
            return rowsUpdated > 0; // Return true if at least one row was updated
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public String getAnimalHistory(int appointmentId) {
        String history = "";

        // SQL to get animalid from the checkup table using appointmentid
        String getAnimalIdQuery = "SELECT a.animalid " +
                "FROM appointment a " +
                "INNER JOIN checkup c ON a.appointmentid = c.appointmentid " +
                "WHERE c.appointmentid = ?";

        // SQL to get animal history for the retrieved animalid
        String getAnimalHistoryQuery = "SELECT allergies, medications FROM animalhistory WHERE animalid = ?";

        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement stmt1 = conn.prepareStatement(getAnimalIdQuery)) {

            stmt1.setInt(1, appointmentId);
            try (ResultSet rs1 = stmt1.executeQuery()) {
                if (rs1.next()) {
                    int animalId = rs1.getInt("animalid");

                    // Step 2: Get the animal history using animalid
                    try (PreparedStatement stmt2 = conn.prepareStatement(getAnimalHistoryQuery)) {
                        stmt2.setInt(1, animalId);
                        try (ResultSet rs2 = stmt2.executeQuery()) {
                            while (rs2.next()) {
                                String allergies = rs2.getString("allergies");
                                String medications = rs2.getString("medications");

                                // Append the data to the history
                                history+="Allergies: " + allergies;
                                history+="\nMedications: " + medications;
                            }
                        }
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return history;
    }
    
    public ObservableList<Certificate> getCertificatesForDoctor(int doctorId) {
        String query = """
            SELECT 
                c.certificateid,
                u.name AS username,
                a.animalname,
                ck.checkup_status
            FROM 
                certificate c
            INNER JOIN 
                user u ON c.userid = u.userid
            INNER JOIN 
                animal a ON c.animalid = a.animalid
            INNER JOIN 
                checkup ck ON c.checkupid = ck.checkupid
            WHERE 
                c.doctorid = ?;
        """;

        ObservableList<Certificate> certificates = FXCollections.observableArrayList();

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, doctorId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
            	int certificateid = rs.getInt("certificateid");
                String username = rs.getString("username");
                String animalName = rs.getString("animalname");
                String checkupStatus = rs.getBoolean("checkup_status") ? 
                                       "Checkup Performed" : "Checkup Not Performed";

                certificates.add(new Certificate(certificateid, username, animalName, checkupStatus));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return certificates;
    }
    
    public boolean updateCertificateApproval(int certificateId) {
        String updateQuery = "UPDATE certificate SET approval = ? WHERE certificateid = ?";

        try (Connection conn = getConnection(); PreparedStatement stmt = conn.prepareStatement(updateQuery)) {
            stmt.setBoolean(1, true);
            stmt.setInt(2, certificateId);
            
            int rowsUpdated = stmt.executeUpdate();
            return rowsUpdated > 0;  // Return true if update was successful
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Example of a method to execute a query
    public ResultSet executeQuery(String query) {
        try {
            Statement stmt = connection.createStatement();
            return stmt.executeQuery(query);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    // Example of a method to execute an update
    public int executeUpdate(String query) {
        try {
            Statement stmt = connection.createStatement();
            return stmt.executeUpdate(query);
        } catch (SQLException e) {
            e.printStackTrace();
            return 0;
        }
    }

    // Close the connection when done
    public void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

	@Override
	public Connection getConnection() throws SQLException {
		connection = DriverManager.getConnection(URL, USER, PASSWORD);
	    return connection;
	}

}
