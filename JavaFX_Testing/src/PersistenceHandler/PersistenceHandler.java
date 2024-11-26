package PersistenceHandler;

import java.sql.Connection;
import java.sql.SQLException;

public abstract class PersistenceHandler {
	
    public abstract Connection getConnection() throws SQLException;
}
