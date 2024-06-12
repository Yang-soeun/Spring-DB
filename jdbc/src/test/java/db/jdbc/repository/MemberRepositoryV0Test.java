package db.jdbc.repository;

import db.jdbc.domain.Member;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;

class MemberRepositoryV0Test {
    MemberRepositoryV0 repositoryV0 = new MemberRepositoryV0();

    @Test
    void save() throws SQLException {
        Member member = new Member("memberV0", 10000);
        repositoryV0.save(member);
    }
}