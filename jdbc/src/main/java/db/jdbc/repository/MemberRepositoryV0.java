package db.jdbc.repository;

import db.jdbc.connection.DBConnectionUtil;
import db.jdbc.domain.Member;
import lombok.extern.slf4j.Slf4j;

import java.sql.*;
import java.util.NoSuchElementException;

/**
 * JDBC - DriverManger 사용
 */
@Slf4j
public class MemberRepositoryV0 {
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

    /**
     * Statement = sql을 그래도 넣는 것
     * PrepareStatement = 파라미터를 바인딩, Statement를 상속받음
     */
    private void close(Connection con, Statement stmt, ResultSet rs){
        if(rs != null){
            try {
                rs.close();
            } catch (SQLException e) {
                log.info("error", e);
            }
        }
        if(stmt != null){
            try {
                stmt.close();
            } catch (SQLException e) {
                log.info("error", e);   //여기서 에러가 발생하면 딱히 처리할 수 있는게 없음
            }
        }

        //외부 TCP connection을 사용하는 것이므로 닫아줘여함
        //위에서 SQLExceotion이 터져도 이 밑에 메소드에 영향을 주지 않는다.
        if(con != null){
            try {
                con.close();
            } catch (SQLException e) {
                log.info("error", e);
            }
        }
    }

    private Connection getConnection() {
        return DBConnectionUtil.getConnection();
    }
}
