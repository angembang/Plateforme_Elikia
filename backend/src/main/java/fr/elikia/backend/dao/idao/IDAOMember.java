package fr.elikia.backend.dao.idao;

import fr.elikia.backend.bo.Member;

import java.util.List;

public interface IDAOMember {
    public List<Member> findAll();

    public Member findById(Long memberId);

    public Member findByEmail(String memberEmail);

    public boolean deleteById(Long memberId);

    public Member create(Member member);

    public Member update(Member member);

    public Member updateByAdmin(Member member);
}
