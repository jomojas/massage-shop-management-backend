package com.jiade.massageshopmanagement.service;

import com.jiade.massageshopmanagement.dto.MemberDTO;
import com.jiade.massageshopmanagement.dto.MemberListResponse;
import com.jiade.massageshopmanagement.dto.MemberAddRequest;
import com.jiade.massageshopmanagement.model.Member;
import com.jiade.massageshopmanagement.model.MemberRecharge;
import com.jiade.massageshopmanagement.mapper.MemberMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.time.LocalDateTime;

@Service
public class MemberService {

    // This class will contain methods to handle member-related operations
    // such as retrieving members, adding new members, updating member information, etc.
    @Autowired
    private MemberMapper memberMapper;
    // Example method to retrieve members based on various filters
    public MemberListResponse getMembers(String keyword, Double minBalance, Double maxBalance,
                                          String startDate, String endDate, int page, int size,
                                          String sortBy, String order) {
        int offset = (page - 1) * size;
        List<Member> members = memberMapper.selectMembersByFilters(
                keyword, minBalance, maxBalance, startDate, endDate, sortBy, order, offset, size);
        int totalMembers = memberMapper.countMembersByFilters(
                keyword, minBalance, maxBalance, startDate, endDate);
        int totalPages = (int) Math.ceil((double) totalMembers / size);

        return new MemberListResponse(members, totalMembers, totalPages, page); // Placeholder return statement
    }


    public MemberListResponse getDeletedMembers(String keyword, Double minBalance, Double maxBalance,
                                                String startDate, String endDate, int page, int size,
                                                String sortBy, String order) {
        int offset = (page - 1) * size;
        // Add isDeleted = true filter
        List<Member> members = memberMapper.selectDeletedMembersByFilters(
                keyword, minBalance, maxBalance, startDate, endDate, sortBy, order, offset, size);
        int totalMembers = memberMapper.countDeletedMembersByFilters(
                keyword, minBalance, maxBalance, startDate, endDate);
        int totalPages = (int) Math.ceil((double) totalMembers / size);

        return new MemberListResponse(members, totalMembers, totalPages, page);
    }

    public void deleteMemberLogically(Long memberId) {
        try {
            int affectedRows = memberMapper.logicalDeleteMember(memberId);
            if(affectedRows == 0){
                throw new RuntimeException("删除会员失败: " + memberId + ". 会员可能不存在或已被删除.");
            }
        } catch (Exception e) {
            // Log the error if needed
            throw new RuntimeException("删除会员失败: " + memberId, e);
        }
    }

    public void restoreMemberLogically(Long memberId) {
        try {
            int affectedRows = memberMapper.logicalRestoreMember(memberId);
            if (affectedRows == 0) {
                throw new RuntimeException("恢复会员失败: " + memberId + ". 会员可能不存在或已被恢复.");
            }
        } catch (Exception e) {
            // Log the error if needed
            throw new RuntimeException("恢复会员失败: " + memberId, e);
        }
    }

    @Transactional // This annotation ensures that both member creation and recharge record insertion are treated as a single transaction
    public MemberDTO addMemberWithRecharge(MemberAddRequest request) {
        // 1. Insert member
        Member member = new Member();
        member.setName(request.getName());
        member.setPhone(request.getPhone());
        member.setBalance(request.getBalance());
        member.setDescription(request.getDescription());
        try {
            memberMapper.insertMember(member);
        } catch (Exception e) {
            if (e.getMessage().contains("Duplicate entry") && e.getMessage().contains("member.phone")) {
                throw new RuntimeException("手机号已存在");
            }
            throw new RuntimeException("添加会员失败", e);
        }

        // 2. Insert recharge record
        MemberRecharge recharge = new MemberRecharge();
        recharge.setMemberId(member.getId());
        recharge.setAmount(request.getBalance());
        recharge.setRemark(
            (request.getDescription() != null ? request.getDescription() : "") + " 首次充值成为会员"
        );
        recharge.setRechargeTime(LocalDateTime.now());
        memberMapper.insertRecharge(recharge);

        // 3. Return DTO
        return MemberDTO.fromEntity(member);
    }
}
