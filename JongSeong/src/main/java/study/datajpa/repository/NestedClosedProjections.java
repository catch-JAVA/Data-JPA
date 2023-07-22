package study.datajpa.repository;

public interface NestedClosedProjections {
    String getUsername();
    TeamInfo getTeam();

    interface TeamInfo{
        String getName();
    }//중첩 구조는 최적화가 안됨
}
