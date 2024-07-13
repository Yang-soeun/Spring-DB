package db.jdbc.service;

import db.jdbc.domain.Member;
import db.jdbc.repository.MemberRepositoryV2;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * 트랜잭션 적용 - 파라미터 연동, 풀을 고려한 종료
 */
@RequiredArgsConstructor
@Slf4j
public class MemberServiceV2 {

    private final DataSource dataSource; //커넥션을 얻기 위해서 필요함
    private final MemberRepositoryV2 memberRepository;

    //핵심 비즈니스 로직과 JDBC 기술이 섞여 있어서 유지보수 하기 어렵다

    /**
     * 트랜잭션 문제: 트랜잭션을 적용하면서 생긴 문제
     * 1. 누수 문제: 트랜젹선을 적용하기 위해 JDBC 구현 기술이 서비스 계충에 누수되었다.
     * 2. 트랜잭션 동기화 문제:
     * - 같은 트랜잭션을 유지하기 위해 커넥션을 파라미터로 넘겨야 한다.
     * - 이때 똑같은 기능도 트랜잭션용 기능과 트랜잭션을 유지하지 않아도 되는 기능으로 분리해야 하는 문제가 발생한다.
     * 3. 트랜잭션 적용 반복 문제
     */
    public void accountTransfer(String fromId, String toId, int money) throws SQLException {
        Connection con = dataSource.getConnection();
        try{
            con.setAutoCommit(false); //트랜잭션 시작
            //비즈니스 로직
            bizLogin(con, fromId, toId, money);
            con.commit(); //성공 시 커밋
        }catch (Exception e){
            con.rollback(); //실패 시 롤백
            throw new IllegalStateException(e);
        }finally {
            release(con);
        }
    }

    /**
     * 예외 누수 문제: 데이터 접근 계층의 JDBC 구현 기술 예외가 서비스 계층으로 전파된다.
     * SQLException은 체크 예외이기 때문에 데이터 접근 계층을 호출한 서비스 계층에서 해당 예외를 잡아서 처리하거나 명시적으로 throws를 통해서 다시
     * 밖으로 던져야 한다.
     * SQLException은 JDBC 전용 기술이다. 향후 jpa나 다른 데이터 접근 기술을 사용하면, 그에 맞는 다른 예외로 변경해야 하고, 결국 서비스 코드도 수정해야 한다.
     */
    private void bizLogin(Connection con, String fromId, String toId, int money) throws SQLException {
        Member fromMember = memberRepository.findById(con, fromId);
        Member toMember = memberRepository.findById(con, toId);

        memberRepository.update(con, fromId, fromMember.getMoney() - money);
        validation(toMember);
        memberRepository.update(con, toId, toMember.getMoney() + money);
    }

    private static void release(Connection con) {
        if( con != null){
            try{
                con.setAutoCommit(true); //커넥션 풀을 고려해서 auto commit을 다시 true로 변경 해줘여 함
            }catch (Exception e){
                log.info("error", e);
            }
        }
    }

    private static void validation(Member toMember) {
        if(toMember.getMemberId().equals("ex")){
            throw new IllegalStateException("이체 중 예외 발생");
        }
    }
}
