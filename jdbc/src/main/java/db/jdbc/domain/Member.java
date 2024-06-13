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

    //이 부분을 @Data를 쓰면 자동으로 만들어줌
//    @Override
//    public boolean equals(Object o) {
//        if (this == o) return true;
//        if (o == null || getClass() != o.getClass()) return false;
//        Member member = (Member) o;
//        return money == member.money && Objects.equals(MemberId, member.MemberId);
//    }
}
