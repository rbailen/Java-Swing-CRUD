package db;

import java.sql.Connection;
import java.sql.DriverManager;

public class DB {
    
    private Connection conn;
    
     /**
     * @return the conn
     */
    public Connection getConn() {
        return conn;
    }
    
    public void conectar(){
        conn = null;

        String url = "jdbc:derby://localhost:1527/";
        String dbName = "db";
        String driver = "org.apache.derby.jdbc.ClientDriver";
        String username = "db";
        String password = "db";
        
        try{
            Class.forName(driver).newInstance();
            conn = DriverManager.getConnection(url + dbName, username, password);
        }catch(Exception e){;
            e.printStackTrace();
        }
    }
    
    public void desconectar(){
        conn = null;
    }
}
