package study.datajpa.dto;

import lombok.Data;
import study.datajpa.entity.Member;

@Data //엔티티엔 데이터 웬만하면 쓰면 안됨
public class MemberDto {

    private Long id;
    private String username;
    private String teamName;

    public MemberDto(Long id, String username, String teamName) {
        this.id = id;
        this.username = username;
        this.teamName = teamName;
    }

    public MemberDto(Member member) {
        this.id = member.getId();
        this.username = member.getUsername();
    }
}
