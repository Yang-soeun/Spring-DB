package db.jdbc.exception.basic;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

@Slf4j
public class UncheckedTest {

    @Test
    void unchecked_catch(){
        Service service = new Service();
        service.callCatch();
    }

    @Test
    void unchecked_throw(){
        Service service = new Service();
        assertThatThrownBy(() -> service.callThrow())
                .isInstanceOf(MyUnCheckedException.class);
    }
    /**
     * RuntimeException을 상속받은 예외는 언체크 예외가 된다.
     */
    static class MyUnCheckedException extends RuntimeException{
        public MyUnCheckedException(String message){
            super(message);
        }
    }

    /**
     * UnChecked 예외는
     * 예외를 잡거나, 던지지 않아도 된다.
     * 예외를 잡지 않으면 자동으로 밖으로 던진다.
     */
    public class Service{
        Repository repository = new Repository();

        public void callCatch(){ //필요한 경우 예외를 잡아서 처리하면 된다.
            try{
                repository.call();
            }catch (MyUnCheckedException e){
                log.info("예외 처리, message={}", e.getMessage(), e);
            }
        }

        /**
         * 체크 예외와 다르게 throws 선언을 하지 않아도 된다.
         */
        public void callThrow(){ //예외를 잡지 않아도 된다. 상위로 넘어간다.
            repository.call();
        }
    }

    static class Repository{
        public void call(){ //throws 생략 가능
            throw new MyUnCheckedException("ex");
        }
    }
}
