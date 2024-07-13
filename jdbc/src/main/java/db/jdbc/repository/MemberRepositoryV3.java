package db.jdbc.repository;

import db.jdbc.domain.Member;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.jdbc.support.JdbcUtils;

import javax.sql.DataSource;
import java.sql.*;
import java.util.NoSuchElementException;


/**
 * 트랜잭션 - 트랜잭션 매니저
 * DatSourceUtils.getConnection()
 * DatSourceUtils.releaseConnection()
 */
@Slf4j
public class MemberRepositoryV3 {
    private final DataSource dataSource;    //dataSource 의존관계 주입

    public MemberRepositoryV3(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    /**
     * JDBC 반복 문제
     * 지금까지 작성한 MemberRepository 코드는 순수한 JDBC를 사용했다.
     * 이 코드들은 유사한 코드의 반복이 너무 많다 -> try, catch, finally
     * 커넥션을 열고, preparedStatement 를 사용하고, 결과를 매핑하고, 실행하고, 커넥션과 리소스를 정리한다.
     */
    public Member save(Member member) throws SQLException {
        String sql = "insert into member(member_id, money) values (?, ?)";
        Connection con = null;
        PreparedStatement pstmt = null; //사용해서 데이터베이스에 쿼리를 날림

        try{
            con = getConnection();
            pstmt = con.prepareStatement(sql);
            pstmt.setString(1, member.getMemberId()); //파라미터 바인딩
            pstmt.setInt(2, member.getMoney());
            pstmt.executeUpdate();  //실행 - insert 할 때, 숫자를 반환: insert건 수(영향을 받은 row 수)
            return member;
        }catch (SQLException e){
            log.error("db error", e);
            e.printStackTrace();
            throw e;
        } finally {
            close(con, pstmt, null);
        }
    }

    public Member findById(String memberId) throws SQLException {
        String sql = "select * from member where member_id = ?";

        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try{
            con = getConnection();
            pstmt = con.prepareStatement(sql);
            pstmt.setString(1, memberId);

            rs = pstmt.executeQuery();//조회 실행 명령어

            if(rs.next()){
                Member member = new Member();
                member.setMemberId(rs.getString("member_id"));
                member.setMoney(rs.getInt("money"));
                return member;
            } else {    //데이터가 없는 경우
                throw new NoSuchElementException("member not found memberId= " + memberId);
            }

        } catch(SQLException e){
            log.error("db error", e);
            throw e;
        } finally {
            close(con, pstmt, rs);  //이 순서대로 해제
        }
    }

    public void update(String memberId, int money) throws SQLException {
        String sql = "update member set money=? where member_id=?";

        Connection con = null;
        PreparedStatement pstmt = null;

        try{
            con = getConnection();
            pstmt = con.prepareStatement(sql);
            pstmt.setInt(1, money);
            pstmt.setString(2, memberId);
            int resultSize = pstmt.executeUpdate();
            log.info("resultSize={}", resultSize);
        }catch (SQLException e){
            log.error("db error", e);
            e.printStackTrace();
            throw e;
        } finally {
            close(con, pstmt, null);
        }
    }

    public void delete(String memberId) throws SQLException {
        String sql = "delete from member where member_id=?";

        Connection con = null;
        PreparedStatement pstmt = null;

        try{
            con = getConnection();
            pstmt = con.prepareStatement(sql);
            pstmt.setString(1, memberId);
            pstmt.executeUpdate();
        }catch (SQLException e){
            log.error("db error", e);
            e.printStackTrace();
            throw e;
        } finally {
            close(con, pstmt, null);
        }
    }

    private void close(Connection con, Statement stmt, ResultSet rs){
        JdbcUtils.closeResultSet(rs);
        JdbcUtils.closeStatement(stmt);
        //트랜잭션 동기화를 사용하려면 DataSourceUtils를 사용해야 한다.
        // con.close()해서 닫아버리면 커넥션이 유지되지 않는 문제가 발생한다.
        // 이 커넥션은 이후 로직과 트랜잭션을 종료(커밋, 롤백)할때 까지 살아있어야 한다.
        DataSourceUtils.releaseConnection(con, dataSource); //커밋을 바로 닫는것이 아니라, 동기화된 커넥션은 커넥션을 닫지 않고 그대로 유지한다.
    }

    private Connection getConnection() throws SQLException {
        //트랜잭션 동기화를 사용하려면 DataSourceUtils를 사용해야 한다.
        //트랜잭션 동기화 매니저가 관리하는 커넥션이 있으면 해당 커넥션을 반환한다.
        //없는 경우에는 새로 생성해서 반환한다.
        Connection con = DataSourceUtils.getConnection(dataSource); 
        log.info("get connection={}, class={}", con, con.getClass());
        return con;
    }
}
