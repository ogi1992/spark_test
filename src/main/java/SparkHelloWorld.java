import com.fasterxml.jackson.databind.ObjectMapper;
import connection.pooling.ConnectionPool;
import entities.User;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import static spark.Spark.get;

public class SparkHelloWorld {

    public static void main(String[] args) {
        get("/hello", (req, res) -> {
            ConnectionPool connectionPool = new ConnectionPool();
            ResultSet rsObj = null;
            Connection connObj = null;
            PreparedStatement pstmtObj = null;
            DataSource dataSource = connectionPool.getDataSource();

            try {
                // Performing Database Operation!
                System.out.println("\n=====Making A New Connection Object For Db Transaction=====\n");
                connObj = dataSource.getConnection();

                pstmtObj = connObj.prepareStatement("SELECT * FROM user");
                rsObj = pstmtObj.executeQuery();
                System.out.println("\n=====Releasing Connection Object To Pool=====\n");
                List<User> users = new ArrayList<>();
                while (rsObj.next()) {
                    User user = new User();
                    user.setId(rsObj.getInt("id"));
                    user.setFirstName(rsObj.getString("first_name"));
                    user.setLastName(rsObj.getString("last_name"));
                    users.add(user);
                }
                ObjectMapper mapper = new ObjectMapper();
                mapper.writeValue(res.raw().getOutputStream(), users);
                return null;
            } catch (Exception sqlException) {
                throw new Exception(sqlException.getLocalizedMessage());
            } finally {
                try {
                    // Closing ResultSet Object
                    if (rsObj != null) {
                        rsObj.close();
                    }
                    // Closing PreparedStatement Object
                    if (pstmtObj != null) {
                        pstmtObj.close();
                    }
                    // Closing Connection Object
                    if (connObj != null) {
                        connObj.close();
                    }
                } catch (Exception sqlException) {
                    sqlException.printStackTrace();
                }
            }
        });
    }
}
