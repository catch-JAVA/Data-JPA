package study.datajpa.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import study.datajpa.entity.Member;

import java.util.List;

public interface MRepository extends JpaRepository<Member, Long> {
    public List<Member> findByUsernameLike(String like);

}
