package db.jdbc.connection;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import static db.jdbc.connection.ConnectionConst.*;

@Slf4j
public class ConnectionTest {
    @Test
    void diverManger() throws SQLException {
        Connection con1 = DriverManager.getConnection(URL, USERNAME, PASSWORD);   //한번 호출하면 connetion한개 얻음
        Connection con2 = DriverManager.getConnection(URL, USERNAME, PASSWORD); //한개 더 얻음 총 2개 생성
        log.info("connection={}, class={}", con1, con1.getClass());
        log.info("connection={}, class={}", con2, con2.getClass());
    }

    @Test
    void dataSourceDriverManger() throws SQLException {
        //DriverMangerDataSource - 항상 새로운 커넥션을 획득
        /**
         * 생성하는 시점에는 URL, USERNAME, PASSWORD를 설정하고
         * 실제 getConnection 할때는 사용하지 않지만 -> 설정과 사용을 분리
         * DriverManger는 사용할때마다 설정 해야하는 큰 차이가 있음!!
         */
        DriverManagerDataSource dataSource = new DriverManagerDataSource(URL, USERNAME, PASSWORD);   //스프링에서 제공
        useDataSource(dataSource);
    }

    private void useDataSource(DataSource dataSource) throws SQLException {
        Connection con1 = dataSource.getConnection();
        Connection con2 = dataSource.getConnection();
        log.info("connection={}, class={}", con1, con1.getClass());
        log.info("connection={}, class={}", con2, con2.getClass());
    }
}
