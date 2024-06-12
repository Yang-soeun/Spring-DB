package db.jdbc.connection;

import lombok.extern.slf4j.Slf4j;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import static db.jdbc.connection.ConnectionConst.*;

@Slf4j
public class DBConnectionUtil {
    //jdbc 표쥰 인터페이스가 제공하는 connection
    public static Connection getConnection() {
        try {
            //라이브러리에 있는 driver를 알아서 찾음
            Connection connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);   //데이터베이스에 연결하기 위해서
            log.info("get connection={}, class={}", connection, connection.getClass());
            return connection;
        } catch (SQLException e) {
            throw new IllegalStateException(e);
        }
    }
}
