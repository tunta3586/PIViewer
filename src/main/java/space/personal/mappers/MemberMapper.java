package space.personal.mappers;

import org.apache.ibatis.annotations.Mapper;
import space.personal.domain.Member;

@Mapper
public interface MemberMapper {
    void save(Member member);
    void delete(Member member);
    Member findMember(String username);
}