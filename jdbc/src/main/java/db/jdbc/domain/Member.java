package db.jdbc.domain;

import lombok.Data;

@Data
public class Member {
    private String MemberId;
    private int money;

    public Member() {
    }

    public Member(String memberId, int money) {
        MemberId = memberId;
        this.money = money;
    }
}
