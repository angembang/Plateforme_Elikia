package fr.elikia.backend.dao.idao;

import fr.elikia.backend.bo.Member;

import java.util.List;

public interface IDAOMember {
    List<Member> findAll();

    Member findById(Long memberId);

    Member findByEmail(String memberEmail);

    boolean deleteById(Long memberId);

    Member create(Member member);

    Member update(Member member);

    Member updateByAdmin(Member member);
}
