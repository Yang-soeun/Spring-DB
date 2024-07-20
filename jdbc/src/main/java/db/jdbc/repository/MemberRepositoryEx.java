package db.jdbc.repository;

import db.jdbc.domain.Member;

import java.sql.SQLException;

/**
 * 체크예외를 사용하는 경우, 인터페이스에도 해당 체크 예외가 선언이 되어 있어야 하는 문제가 있다.
 */
public interface MemberRepositoryEx {
    Member save(Member member) throws SQLException;
    Member findById(String memberId) throws SQLException;
    void update(String memberId, int money) throws SQLException;
    void delete(String memberId) throws SQLException;
}
