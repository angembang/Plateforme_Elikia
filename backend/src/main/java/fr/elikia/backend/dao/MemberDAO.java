package fr.elikia.backend.dao;

import fr.elikia.backend.bo.Member;
import fr.elikia.backend.dao.idao.IDAOMember;
import fr.elikia.backend.repository.MemberRepository;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class MemberDAO implements IDAOMember {
    private final MemberRepository memberRepository;

    public MemberDAO(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    @Override
    public List<Member> findAll() {
        return memberRepository.findAll();
    }

    @Override
    public Member findById(Long memberId){
        return memberRepository.findById(memberId).orElse(null);
    }

    public Member findByEmail(String memberEmail) {
        return memberRepository.findByEmail(memberEmail).orElse(null);

    }

    @Override
    public boolean deleteById(Long memberId){
        Member member = memberRepository.findById(memberId).orElse(null);
        if(member!=null){
            memberRepository.delete(member);
            return true;
        }
        return false;
    }

    @Override
    public Member create(Member member){
        // check if the member already exists
        if(memberRepository.findByEmail(member.getEmail()).isPresent()){
            return null;
        }
        // Save the member
        return memberRepository.save(member);
    }

    public Member update(Member member){
        // Check if the member exists
        if(!memberRepository.existsById(member.getUserId())) {
            return null;
        }
        return memberRepository.save(member);
    }

    public Member updateByAdmin(Member member) {
        // Check if the member exists
        if(!memberRepository.existsById(member.getUserId())) {
           return null;
        }
        return memberRepository.save(member);
    }


}
