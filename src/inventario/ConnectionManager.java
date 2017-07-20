package inventario;

//import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;
import java.io.FileInputStream;
import java.sql.*;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ConnectionManager {

    private static Connection conn = null;
    private static PreparedStatement ps = null;
    private static ResultSet rs = null;
    private static String conUser = "";
    private static String conPassword = "";
    private static String conDburl = "";

    public ConnectionManager() throws Exception {

    }

    //Get connection
    public static Connection getConnection() {
        try {
            //Get database properties
            Properties prop = new Properties();
            prop.load(new FileInputStream("config.properties"));
            System.out.println("Obteniendo Propiedades de archivo");
            conUser = prop.getProperty("user");
            conPassword = prop.getProperty("password");
            conDburl = prop.getProperty("dburl");
            //Get connection
            System.out.println("Instanciando conexion ...");
            Connection con = DriverManager.getConnection(conDburl, conUser, conPassword);
            System.out.println("Conexion exitosa...");
            return con;
        } catch (Exception ex) {
            System.out.println("Error de conexion: " + ex.getMessage());
            return null;
        }
    }

    public static boolean loginAutentication(String usuario, String clave) throws SQLException {

        String clavedb = "";
        //Allows to know if there is at least one result that matches in db
        int count = 0;
        //getting connection
        System.out.println("llamando el metodo para obtener conexion...");
        conn = getConnection();

        try {
            //Prepare statement
            System.out.println("preparando un statement...");
            ps = conn.prepareStatement("SELECT clave FROM usuario where usuario = ?");
            //set parameters
            System.out.println("configurando parametros...");
            ps.setString(1, usuario);
            //execute
            System.out.println("ejecutando resultset...");
            rs = ps.executeQuery();
            while (rs.next()) {
                count++;
                System.out.println("obteniendo clave del resultset...");
                clavedb = rs.getString("clave");
            }
            System.out.println("datos a comparar, count: " + count + " claveIngresada: " + clave + " clavedb: " + clavedb);
            System.out.println("retornando resultado...");
            //if there is at least one result and the password matches
            //return true, else return false
            return count > 0 && clavedb.equals(clave);

        } catch (SQLException ex) {
            //Logger.getLogger(ConnectionManager.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println("Error: " + ex.getMessage());
            return false;
        } finally {
            if (ps != null) {
                ps.close();
            }
            if (conn != null) {
                conn.close();
            }
            if (rs != null) {
                rs.close();
            }
        }

    }

    public static String getUserName(String userName) throws SQLException {        
        conn = getConnection();
        String name = "", lastName = "";
        //Allows to know if there is at least one result that matches in db
        int count = 0;
        try {
            //Prepare statment
            ps = conn.prepareStatement("SELECT nombre, apellido FROM empleado \n"
                                     + "JOIN usuario ON idempleado = idempleado\n"
                                     + "WHERE usuario = ?;");
            //configure statement
            ps.setString(1, userName);
            //execute query and store in resultset
            rs = ps.executeQuery();
            while(rs.next()){
                name = rs.getString("nombre");
                lastName = rs.getString("apellido");
                count ++;
            }
            //if there is a match return name and lastname
            if(count > 0)
                return name + " " + lastName;
            else
                return "";
            
        } catch (Exception ex) {
            System.out.println("error (getUserName): " + ex.getMessage());
            return "";
        } finally {
            if (ps != null) {
                ps.close();
            }
            if (conn != null) {
                conn.close();
            }
            if (rs != null) {
                rs.close();
            }
        }
    }
}
