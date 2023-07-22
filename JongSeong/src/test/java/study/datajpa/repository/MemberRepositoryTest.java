package study.datajpa.repository;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import study.datajpa.dto.MemberDto;
import study.datajpa.entity.Member;
import study.datajpa.entity.Team;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@Rollback(false)
class MemberRepositoryTest {
    @Autowired MemberRepository memberRepository;
    @Autowired TeamRepository teamRepository;
    @Autowired MRepository mRepository;
    @PersistenceContext
    EntityManager em;

    @Test
    public void testMember(){
        System.out.println(memberRepository.getClass());
        Member member = new Member("memberA");
        Member savedMember = memberRepository.save(member);

        Member findMember = memberRepository.findById(savedMember.getId()).get();
        assertThat(findMember.getId()).isEqualTo(member.getId());
        assertThat(findMember.getUsername()).isEqualTo(member.getUsername());
    }

    @Test
    public void basicCRUD(){
        Member member1 = new Member("member1");
        Member member2 = new Member("member2");

        memberRepository.save(member1);
        memberRepository.save(member2);

        Member findMember1 = memberRepository.findById(member1.getId()).get();
        Member findMember2 = memberRepository.findById(member2.getId()).get();
        assertThat(findMember1).isEqualTo(member1);
        assertThat(findMember2).isEqualTo(member2);

        List<Member> all = memberRepository.findAll();
        assertThat(all.size()).isEqualTo(2);

        long count = memberRepository.count();
        assertThat(count).isEqualTo(2);

        memberRepository.delete(member1);
        memberRepository.delete(member2);

        long deledtedCount = memberRepository.count();
        assertThat(deledtedCount).isEqualTo(0);

    }

    @Test
    public void findByusernameAndAgeGreaterThan(){
        Member member1 = new Member("AAA", 10);
        Member member2 = new Member("AAA", 20);

        memberRepository.save(member1);
        memberRepository.save(member2);

        List<Member> result = memberRepository.findByUsernameAndAgeGreaterThan("AAA", 15);

        assertThat(result.get(0).getUsername()).isEqualTo("AAA");
        assertThat(result.get(0).getAge()).isEqualTo(20);
        assertThat(result.size()).isEqualTo(1);
    }
    @Test
    public void hello(){
        memberRepository.findTop3HelloBy();
    }

    @Test
    public void namedQuery(){
        Member member1 = new Member("AAA", 10);
        Member member2 = new Member("AAA", 20);

        memberRepository.save(member1);
        memberRepository.save(member2);

        List<Member> result = memberRepository.findByUsername("AAA");
        Member findMember = result.get(0);
        assertThat(findMember).isEqualTo(member1);
    }

    @Test
    public void testQuery(){
        Member member1 = new Member("AAA", 10);
        Member member2 = new Member("AAA", 20);

        memberRepository.save(member1);
        System.out.println(111);
        memberRepository.save(member2);
        System.out.println(111);

        List<Member> result = memberRepository.findUser("AAA", 10);

        assertThat(result.get(0)).isEqualTo(member1);
    }

    @Test
    public void findUsernameList(){
        Member member1 = new Member("AAA", 10);
        Member member2 = new Member("AAA", 20);

        memberRepository.save(member1);
        memberRepository.save(member2);

        List<String> result = memberRepository.findUsernameList();

        for (String s : result) {
            System.out.println(s);
        }
    }

    @Test
    public void findMemberDto(){
        Team team = new Team("TeamA");
        teamRepository.save(team);

        Member m1 = new Member("AAA", 10);
        m1.setTeam(team);
        memberRepository.save(m1);



        List<MemberDto> result = memberRepository.findMemberDto();
        for (MemberDto s : result) {
            System.out.println(s);
        }
    }

    @Test
    public void findByNames(){
        Member member1 = new Member("AAA", 10);
        Member member2 = new Member("BBB", 20);

        memberRepository.save(member1);
        memberRepository.save(member2);

        List<Member> result = memberRepository.findByNames(Arrays.asList("AAA", "BBB"));

        for (Member member : result) {
            System.out.println(member);
        }
    }

    @Test
    public void returnType(){
        Member member1 = new Member("AAA", 10);
        Member member2 = new Member("BBB", 20);

        memberRepository.save(member1);
        memberRepository.save(member2);

        List<Member> aaa = memberRepository.findListByUsername("AAA");//0개일때 null이 아님
        Member m = memberRepository.findMemberByUsername("AAA");//0개일 때 null noresultException 아님, 2개이상 일때 exception
        Optional<Member> o = memberRepository.findOptionalByUsername("AAA");//2개 이상일때 exception

    }

    @Test
    public void paging(){
        memberRepository.save(new Member("member1", 10));
        memberRepository.save(new Member("member2", 10));
        memberRepository.save(new Member("member3", 10));
        memberRepository.save(new Member("member4", 10));
        memberRepository.save(new Member("member5", 10));

        PageRequest pageRequest = PageRequest.of(0, 3, Sort.by(Sort.DEFAULT_DIRECTION.DESC, "username"));
        int age = 10;
        int offset = 0;
        int limit = 3;
        Page<Member> page = memberRepository.findByAge(age, pageRequest);
//        Slice<Member> page = memberRepository.findByAge(age, pageRequest);
        List<Member> content = page.getContent();
//        long totalElements = page.getTotalElements();

        assertThat(content.size()).isEqualTo(3);
//        assertThat(page.getTotalElements()).isEqualTo(5);
        assertThat(page.getNumber()).isEqualTo(0);


    }

    //벌크 연산은 DB만 건들이고 영속성 컨텍스트에는 반영되지 않음
    @Test
    public void bulkUpdate(){
        memberRepository.save(new Member("member1", 10));
        memberRepository.save(new Member("member2", 20));
        memberRepository.save(new Member("member3", 30));
        memberRepository.save(new Member("member4", 40));
        memberRepository.findById(1L).get().setAge(20);
        int resultCount = memberRepository.bulkAgePlus(20);
        em.flush();
        em.clear();

//        assertThat(resultCount).isEqualTo(3);
    }

    @Test
    public void findMemberLazy(){
        Team teamA = new Team("teamA");
        Team teamB = new Team("teamB");

        teamRepository.save(teamA);
        teamRepository.save(teamB);

        Member member1 = new Member("AAA", 10, teamA);
        Member member2 = new Member("BBB", 20, teamB);

        memberRepository.save(member1);
        memberRepository.save(member2);

        em.flush();
        em.clear();

        List<Member> members = memberRepository.findAll();
//        List<Member> members = memberRepository.findMemberFetchJoin();

        //N+1 문제
        for (Member member : members) {
            System.out.println(member.getUsername());
            System.out.println(member.getTeam().getName());

        }

    }

    @Test
    public void queryHint(){
        Member member = new Member("member1", 10);
        memberRepository.save(member);

        em.flush();
        em.clear();

        Member findMember = memberRepository.findReadOnlyByUsername("member1");
        findMember.setUsername("member2");

        em.flush();
    }

    @Test
    public void callCustom(){
        List<Member> memberCustom = memberRepository.findMemberCustom();
    }

    @Test
    public  void a(){
        Member m = new Member("ads");
        mRepository.save(m);
        List<Member> list = mRepository.findByUsernameLike("%se%");
        System.out.println("mRepository  = " + list.get(0));
    }

    @Test
    public void specBasic(){
        Team teamA = new Team("teamA");
        em.persist(teamA);

        Member m1 = new Member("m1", 0, teamA);
        Member m2 = new Member("m2", 0, teamA);
        em.persist(m1);
        em.persist(m2);

        em.flush();
        em.clear();

        Specification<Member> spec = MemberSpec.username("m1").and(MemberSpec.teamName("teamA")).and(MemberSpec.username("m1"));
        List<Member> result = memberRepository.findAll(spec);
    }

    @Test
    public void queryByExample(){
        Team teamA = new Team("teamA");
        em.persist(teamA);

        Member m1 = new Member("m1", 0, teamA);
        Member m2 = new Member("m2", 0, teamA);
        em.persist(m1);
        em.persist(m2);

        em.flush();
        em.clear();

        Member member = new Member("m1");

        Example<Member> example = Example.of(member);

        ExampleMatcher matcher = ExampleMatcher.matching().withIgnorePaths("age");

        List<Member> result = memberRepository.findAll(example);

        Assertions.assertThat(result.get(0).getUsername()).isEqualTo("m1");

    }

    @Test
    public void projections(){
        Team teamA = new Team("teamA");
        em.persist(teamA);

        Member m1 = new Member("m1", 0, teamA);
        Member m2 = new Member("m2", 0, teamA);
        em.persist(m1);
        em.persist(m2);

        em.flush();
        em.clear();

        List<UsernameOnlyDto> result = memberRepository.findProjectionsByUsername("m1");
        for (UsernameOnlyDto usernameOnly : result) {
            System.out.println(usernameOnly.getUsername());
            
        }
    }
}