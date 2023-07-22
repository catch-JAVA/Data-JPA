package study.datajpa.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import study.datajpa.dto.MemberDto;
import study.datajpa.entity.Member;

import javax.persistence.QueryHint;
import java.util.List;
import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long>, MemberRepositoryCustom, JpaSpecificationExecutor {
    List<Member> findByUsernameAndAgeGreaterThan(String username, int age);

    List<Member> findTop3HelloBy();

    //@Query(name = "Member.findByUsername") 메서드명과 이름이 같으면 생략가능
    List<Member> findByUsername(@Param("username")String username);

    @Query("select m from Member m where m.username = :username and m.age = :age")
    List<Member> findUser(@Param("username") String username, @Param("age") int age);

    @Query("select m.username from Member m")
    List<String> findUsernameList();

    @Query("select new study.datajpa.dto.MemberDto(m.id, m.username, t.name) from Member m join m.team t")
    List<MemberDto> findMemberDto();

    @Query("select m from Member m where m.username in :names")
    List<Member> findByNames(@Param("names") List<String> names);


    List<Member> findListByUsername(String username);

    Member findMemberByUsername(String username);

    Optional<Member> findOptionalByUsername(String username);

//    Page<Member> findByAge(int age, Pageable pageable);
//    Slice<Member> findByAge(int age, Pageable pageable);

    @Query(value = "select m from Member m left join m.team t", countQuery = "select count(m.username) from Member m")
    Page<Member> findByAge(int age, Pageable pageable);

    @Modifying(clearAutomatically = true)    //벌크 연산은 DB만 건들이고 영속성 컨텍스트에는 반영되지 않음
    @Query("update Member m set m.age = m.age+1 where m.age >= :age")
    int bulkAgePlus(@Param("age") int age);

    @Query("select m from Member m left join fetch m.team")
    List<Member> findMemberFetchJoin();

    @Override
    @EntityGraph(attributePaths = {"team"})
    List<Member> findAll();

    @Query("select m from Member m")
    @EntityGraph(attributePaths = {"team"})
    List<Member> findMemberEntityGraph();

    @EntityGraph(attributePaths = {"team"})
    List<Member> findEntityGraphByUsername(String username);

    @QueryHints(value = @QueryHint(name = "org.hibernate.readOnly", value = "true"))
    Member findReadOnlyByUsername(String username);

    List<UsernameOnlyDto> findProjectionsByUsername(String username);


}
