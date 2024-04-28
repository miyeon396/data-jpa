package study.datajpa.repository;

import org.springframework.beans.factory.annotation.Value;

public interface UsernameOnly {
//    @Value("#{target.username + ' ' + target.age}") //오픈프로젝션 : 엔티티를 다 갖고와서 처리하는 것..
    String getUsername();
}
