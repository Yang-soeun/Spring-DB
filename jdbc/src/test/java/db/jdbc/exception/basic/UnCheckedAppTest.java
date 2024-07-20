package db.jdbc.exception.basic;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

@Slf4j
public class UnCheckedAppTest {
    @Test
    void unchecked(){
        Controller controller = new Controller();
        assertThatThrownBy(() -> controller.request())
                .isInstanceOf(RuntimeSQLException.class);
    }

    @Test
    void printEx(){
        Controller controller = new Controller();
        try{
            controller.request();
        } catch (Exception e){
//            e.printStackTrace(); // 이렇게 하는 것 보다 로그를 남기는 방법이 좋음, 이 방식은 System.out을 사용함
            log.info("ex", e);  // 로그를 출력할 때 마지막 파라미터에 예외를 넣어주면 로그에 스택 트레이스를 출력할 수 있다. or 예외만 파라미터에 전달
        }
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
//                throw new RuntimeSQLException();  // 이런식으로 e를 전달하지 않으면 무슨 이유때문에 RuntimeException이 발생했는지 알 수 없다.
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
        public RuntimeSQLException(Throwable cause){    // 이렇게 하면 내부에 기존 exception을 가지고 있음
            super(cause);
        }
    }
}
