package db.jdbc.repository;

import db.jdbc.domain.Member;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.support.JdbcUtils;

import javax.sql.DataSource;
import java.sql.*;
import java.util.NoSuchElementException;


/**
 * 애플리케이션이 같은 커넥션을 유지하기 위한 방법
 * 방법: 커넥션을 파라미터로 전달해서 같은 커넥션이 사용되도록 유지하는 것
 * JDBC - ConnectionParam
 */
@Slf4j
public class MemberRepositoryV2 {
    private final DataSource dataSource;    //dataSource 의존관계 주입

    public MemberRepositoryV2(DataSource dataSource) {
        this.dataSource = dataSource;
    }

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

    public Member findById(Connection con, String memberId) throws SQLException {
        String sql = "select * from member where member_id = ?";

        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try{
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
            //connection은 여기서 닫지 않는다.
            JdbcUtils.closeResultSet(rs);
            JdbcUtils.closeStatement(pstmt);
//            JdbcUtils.closeConnection(con); //닫으면 안됨
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

    public void update(Connection con, String memberId, int money) throws SQLException {
        String sql = "update member set money=? where member_id=?";

        PreparedStatement pstmt = null;

        try{
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
            //connection은 여기서 닫지 않는다.
            JdbcUtils.closeStatement(pstmt);
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
        JdbcUtils.closeConnection(con);
    }

    private Connection getConnection() throws SQLException {
        Connection con = dataSource.getConnection();
        log.info("get connection={}, class={}", con, con.getClass());
        return con;
    }
}
