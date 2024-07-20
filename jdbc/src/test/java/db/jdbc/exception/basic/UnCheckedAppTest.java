package db.jdbc.exception.basic;

import org.junit.jupiter.api.Test;

import java.sql.SQLException;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class UnCheckedAppTest {
    @Test
    void unchecked(){
        Controller controller = new Controller();
        assertThatThrownBy(() -> controller.request())
                .isInstanceOf(RuntimeSQLException.class);
    }

    static class Controller {
        Service service = new Service();

        public void request(){
            service.logic();
        }
    }

    static class Service{
        Repository repository = new Repository();
        NetworkClient networkClient = new NetworkClient();

        public void logic(){
            repository.call();
            networkClient.call();
        }
    }

    static class NetworkClient{
        public void call(){
            throw new RuntimeConnectionException("연결 실패");
        }
    }

    static class Repository{
        public void call(){
            try {
                runSQL();
            } catch (SQLException e) {
                throw new RuntimeSQLException(e);  //런타임 예외로 바꿔서 던짐
            }
        }

        private void runSQL() throws SQLException {
            throw new SQLException("ex");
        }
    }

    static class RuntimeConnectionException extends RuntimeException{
        public RuntimeConnectionException(String message){
            super(message);
        }
    }

    static class RuntimeSQLException extends RuntimeException{
        public RuntimeSQLException(Throwable cause){
            super(cause);
        }
    }
}
