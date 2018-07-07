
import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.*;
import java.util.Arrays;
import java.util.Scanner;

/**
 * Secure authentication database demo.
 *
 * - Must first supply credentials
 *   of postgresql admin to allow this app
 *   to access database.
 *
 * - After user enters their username and password,
 *   the password is hashed using MD5 and compared
 *   with the hash value of the password stored in
 *   the database. The plain-text value of the password
 *   is never transmitted nor stored in the database.
 *
 * - SQL injection vulnerabilities are mitigated
 *   through strong type checking within the method
 *   that generates the database query.
 *
 * @author uzr
 */
public class Login {

    private static final int MAX_NAME = 8;

    private static Connection connection = null;

    /**
     * Method to connect to postgresql database.
     * */
    private static Connection connect() {
        if (connection != null) {
            System.out.println("Problem when connecting to the database");
        }
        String url = "jdbc:postgresql://localhost:5432/";

        // explicitly set char encoding
        Scanner dbName = new Scanner(System.in, "utf-8");
        Scanner dbWord = new Scanner(System.in, "utf-8");

        System.out.print("\nConnect to database as (username): ");
        String dbUser = dbName.next();

        System.out.print("Enter Password: ");
        String dbPass = dbWord.next();

        try {
            connection = DriverManager.getConnection(url, dbUser, dbPass);

            if (connection != null) {
                System.out.println("Connecting to database...");
            }
        } catch (SQLException e) {
            System.out.println("Problem when connecting to the database");
            e.printStackTrace();
        }
        return connection;
    }


    /**
     * Method to generate MD5 hash of password.
     * */
    private static String hashPassword(char[] password) {
        String generatedHash = null;
        String inPass = Arrays.toString(password);

        try {
            MessageDigest md;
            byte[] bytes;
            md = MessageDigest.getInstance("MD5");

            if (md != null) {
                // explicitly set char encoding, and catch exception
                md.update(inPass.getBytes(Charset.forName("UTF-8")));
            }

            assert md != null;
            bytes = md.digest();


            StringBuilder sb = new StringBuilder();
            for (byte aByte : bytes) {
                sb.append(Integer.toString((aByte & 0xff) + 0x100, 16).substring(1));
            }

            generatedHash = sb.toString();

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        return generatedHash;
    }

    private static PreparedStatement stmnt = null;
    private static ResultSet rs = null;


    /**
     * Method for database operations.
     * */
    private static void doPrivilegedAction(String username, char[] password) throws SQLException {
        Connection connection = connect();
        if (connection == null) {
            //handle error
            System.out.println("No connection to authdb!");
        }
        try {
            String pwd = hashPassword(password);


            /* ******************************************************
             * Ensure that the length of the username is legitimate *
             ********************************************************/
            if ((username.length() > MAX_NAME)) {
                //handle error
                System.out.println("ERROR: Username must be less than 8 chars!");
            }

            //String sqlString = ("SELECT * FROM public.authtable WHERE username = '" + username + "' AND password = '" + pwd + "';");
            String sqlString = "SELECT * FROM public.authtable WHERE username=? AND password=?";
            System.out.println("Query: " + sqlString + "\n");



            /* *****************************************************
             * Using set*() methods of the PreparedStatement class *
             * to guard against SQL injection vulnerabilities.     *
             *******************************************************/
            if (connection != null) {
                stmnt = connection.prepareStatement(sqlString);
            }

            if (stmnt != null) {
                stmnt.setString(1, username);
                stmnt.setString(2, pwd);
                rs = stmnt.executeQuery();

                if (rs != null && !rs.next()) {
                    throw new SecurityException("UserName or Password Incorrect!");
                }
            }

            /*
             * Arbitrary Access Point:
             *
             * if authenticated, proceed
             * */
            System.out.println("You have been authenticated!!");


        } finally {
            //close resources when done.
            close(rs, stmnt, connection);
        }
    }


    /**
     * Method to properly close all resources.
     * */
    private static void close(ResultSet rs, Statement ps, Connection conn) {
        if (rs != null) {
            try {
                rs.close();

            } catch (SQLException e) {
                System.out.println("The result set cannot be closed.");
                e.printStackTrace();
            }
        }
        if (ps != null) {
            try {
                ps.close();
            } catch (SQLException e) {
                System.out.println("The statement cannot be closed.");
                e.printStackTrace();
            }
        }
        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException e) {
                System.out.println("The data source connection cannot be closed.");
                e.printStackTrace();
            }
        }
    }


    /**
     * Main Method
     **/
    public static void main(String[] args) {
        /*Scanner uName = new  Scanner(System.in);
        Scanner pWord = new  Scanner(System.in);*/

        /*System.out.print("Enter UserName: ");
        String username = uName.next();*/

        /*System.out.print("Enter Password: ");
        char[] passwd = pWord.next().toCharArray();*/


        //hard coded user input
        String uNameHC = "dbuser1";
        String myPassHC = "pa$$word1";


        char[] passHC = myPassHC.toCharArray();


        // Used to test hash function, this value is compared
        // to the value stored in the database.
        String myPass = hashPassword(passHC);
        System.out.println("MD5 Hash: " + myPass);


        try {
            //doPrivilegedAction(username, passwd);
            doPrivilegedAction(uNameHC, passHC);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}

