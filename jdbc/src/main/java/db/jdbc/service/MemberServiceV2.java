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
