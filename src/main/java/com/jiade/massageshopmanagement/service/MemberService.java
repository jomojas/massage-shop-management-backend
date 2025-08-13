package com.jiade.massageshopmanagement.service;

import com.jiade.massageshopmanagement.dto.*;
import com.jiade.massageshopmanagement.enums.OperationModule;
import com.jiade.massageshopmanagement.enums.OperationType;
import com.jiade.massageshopmanagement.model.Member;
import com.jiade.massageshopmanagement.model.MemberRecharge;
import com.jiade.massageshopmanagement.mapper.MemberMapper;
import com.jiade.massageshopmanagement.model.RechargeRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.time.LocalDateTime;
import java.util.NoSuchElementException;

@Service
public class MemberService {

    // This class will contain methods to handle member-related operations
    // such as retrieving members, adding new members, updating member information, etc.
    @Autowired
    private MemberMapper memberMapper;
    @Autowired
    private OperationLogService operationLogService;
    // Example method to retrieve members based on various filters
    public MemberListResponse getMembers(String keyword, BigDecimal minBalance, BigDecimal maxBalance,
                                         LocalDateTime startDate, LocalDateTime endDate, int page, int size,
                                          String sortBy, String order) {
        int offset = (page - 1) * size;
        List<Member> members = memberMapper.selectMembersByFilters(
                keyword, minBalance, maxBalance, startDate, endDate, sortBy, order, offset, size);
        int totalMembers = memberMapper.countMembersByFilters(
                keyword, minBalance, maxBalance, startDate, endDate);
        int totalPages = (int) Math.ceil((double) totalMembers / size);

        return new MemberListResponse(members, totalMembers, totalPages, page); // Placeholder return statement
    }


    public MemberListResponse getDeletedMembers(String keyword, BigDecimal minBalance, BigDecimal maxBalance,
                                                LocalDateTime startDate, LocalDateTime endDate, int page, int size,
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

            // 日志记录
            Member member = memberMapper.selectById(memberId); // 查询被删会员信息
            String logDetail;
            if (member != null) {
                logDetail = String.format(
                        "删除会员，ID：%d，姓名：%s，电话：%s",
                        memberId,
                        member.getName(),
                        member.getPhone()
                );
            } else {
                logDetail = String.format("删除会员，ID：%d", memberId);
            }

            operationLogService.recordLog(
                    OperationType.DELETE,
                    OperationModule.MEMBER,
                    logDetail
            );
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

            // 日志记录
            Member member = memberMapper.selectById(memberId); // 查询恢复后的会员信息
            String logDetail;
            if (member != null) {
                logDetail = String.format(
                        "恢复会员，ID：%d，姓名：%s，电话：%s",
                        memberId,
                        member.getName(),
                        member.getPhone()
                );
            } else {
                logDetail = String.format("恢复会员，ID：%d", memberId);
            }

            operationLogService.recordLog(
                    OperationType.RESTORE,
                    OperationModule.MEMBER,
                    logDetail
            );
        } catch (Exception e) {
            // Log the error if needed
            throw new RuntimeException("恢复会员失败: " + memberId, e);
        }
    }

    @Transactional // This annotation ensures that both member creation and recharge record insertion are treated as a single transaction
    public void addMemberWithRecharge(MemberAddRequest request) {
        try {
            if (request.getBalance() == null || request.getBalance().compareTo(BigDecimal.ZERO) <= 0) {
                throw new IllegalArgumentException("充值金额必须大于0");
            }
            // 1. Insert member
            Member member = new Member();
            member.setName(request.getName());
            member.setPhone(request.getPhone());
            member.setBalance(request.getBalance());
            member.setDescription(request.getDescription());
            memberMapper.insertMember(member);

            // 2. Insert recharge record
            MemberRecharge recharge = new MemberRecharge();
            recharge.setMemberId(member.getId());
            recharge.setAmount(request.getBalance());
            recharge.setRemark(
                (request.getDescription() != null ? request.getDescription() : "") + " 首次充值成为会员"
            );
            recharge.setRechargeTime(LocalDateTime.now());
            memberMapper.insertRecharge(recharge);

            // 日志记录
            String logDetail = String.format(
                    "新增会员，ID：%d，姓名：%s，电话：%s，首次充值金额：%s，备注：%s",
                    member.getId(),
                    member.getName(),
                    member.getPhone(),
                    request.getBalance(),
                    (request.getDescription() != null ? request.getDescription() : "") + " 首次充值成为会员"
            );

            operationLogService.recordLog(
                    OperationType.CREATE,
                    OperationModule.MEMBER,
                    logDetail
            );
        } catch (IllegalArgumentException e) {
            throw e;
        } catch (Exception e) {
            if (e.getMessage().contains("Duplicate entry") && e.getMessage().contains("member.phone")) {
                throw new RuntimeException("手机号已存在");
            }
            throw new RuntimeException("添加会员失败", e);
        }
    }

    @Transactional
    public void rechargeOfMember(Long memberId, RechargeRequestDTO request) {
        try {
            if (request.getAmount() == null || request.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
                throw new IllegalArgumentException("充值金额必须大于0");
            }
            // 1. Update member balance
            int affectedRows = memberMapper.updateMemberBalance(memberId, request.getAmount());
            if (affectedRows == 0) {
                throw new NoSuchElementException("该会员不存在");
            }
            // 2. Insert recharge record
            MemberRecharge recharge = new MemberRecharge();
            recharge.setMemberId(memberId);
            recharge.setAmount(request.getAmount());
            recharge.setRemark(request.getRemark());
            recharge.setRechargeTime(LocalDateTime.now());
            memberMapper.insertRecharge(recharge);

            // 日志记录
            Member member = memberMapper.selectById(memberId);
            String logDetail;
            if (member != null) {
                logDetail = String.format(
                        "会员充值，ID：%d，姓名：%s，电话：%s，充值金额：%s，备注：%s",
                        memberId,
                        member.getName(),
                        member.getPhone(),
                        request.getAmount(),
                        request.getRemark()
                );
            } else {
                logDetail = String.format(
                        "会员充值，ID：%d，充值金额：%s，备注：%s",
                        memberId,
                        request.getAmount(),
                        request.getRemark()
                );
            }

            operationLogService.recordLog(
                    OperationType.CREATE,
                    OperationModule.MEMBER,
                    logDetail
            );
        } catch (IllegalArgumentException e) {
            throw e;
        } catch (NoSuchElementException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("充值失败", e);
        }
    }

    public void modifyMemberInfo(Long memberId, MemberModifyRequest request) {
        try {
            if (request.getBalance() == null || request.getBalance().compareTo(BigDecimal.ZERO) < 0) {
                throw new IllegalArgumentException("会员余额不能小于0");
            }
            // Update member info
            Member member = new Member();
            member.setName(request.getName());
            member.setPhone(request.getPhone());
            member.setBalance(request.getBalance());
            member.setDescription(request.getDescription());
            int affectedRows = memberMapper.updateMemberInfo(memberId, member);
            if (affectedRows == 0) {
                throw new NoSuchElementException("该会员不存在");
            }

            // 日志记录
            Member oldMember = memberMapper.selectById(memberId);

            String logDetail = String.format(
                    "更新会员信息，ID：%d，原姓名：%s，原电话：%s，原余额：%s，原备注：%s；新姓名：%s，新电话：%s，新余额：%s，新备注：%s",
                    memberId,
                    oldMember != null ? oldMember.getName() : "",
                    oldMember != null ? oldMember.getPhone() : "",
                    oldMember != null ? oldMember.getBalance() : "",
                    oldMember != null ? oldMember.getDescription() : "",
                    request.getName(),
                    request.getPhone(),
                    request.getBalance(),
                    request.getDescription()
            );

            operationLogService.recordLog(
                    OperationType.UPDATE,
                    OperationModule.MEMBER,
                    logDetail
            );
        } catch (IllegalArgumentException e) {
            throw e;
        } catch (NoSuchElementException e) {
            throw e;
        } catch (Exception e) {
            if (e.getMessage().contains("Duplicate entry") && e.getMessage().contains("member.phone")) {
                throw new RuntimeException("手机号已存在");
            }
            throw new RuntimeException("更新失败", e);
        }
    }

    public RechargeRecordResponse getRechargeRecords(String keyword, BigDecimal minAmount, BigDecimal maxAmount,
                                                     LocalDateTime startDate, LocalDateTime endDate, int page, int size,
                                                     String sortBy, String order) {
        int offset = (page - 1) * size;
        List<RechargeRecord> rechargeRecords = memberMapper.selectRechargeRecordsByFilters(
                keyword, minAmount, maxAmount, startDate, endDate, sortBy, order, offset, size);
        int totalRecords = memberMapper.countRechargeRecordsByFilters(
                keyword, minAmount, maxAmount, startDate, endDate);
        int totalPages = (int) Math.ceil((double) totalRecords / size);

        return new RechargeRecordResponse(rechargeRecords, totalRecords, totalPages, page); // Placeholder return statement
    }

    public void modifyRechargeRecord(Long recordId, RechargeModifyRequest request) {
        try {
            if (request.getAmount() == null || request.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
                throw new IllegalArgumentException("充值金额必须大于0");
            }
            // Update member info
            RechargeRecord record = new RechargeRecord();
            record.setAmount(request.getAmount());
            record.setRemark(request.getRemark());
            int affectedRows = memberMapper.modifyRechargeRecord(recordId, record);
            if (affectedRows == 0) {
                throw new NoSuchElementException("该充值记录不存在");
            }

            // 日志记录
            RechargeRecord oldRecord = memberMapper.selectRechargeRecordById(recordId);

            String logDetail = String.format(
                    "更新充值记录，ID：%d，原金额：%s，原备注：%s；新金额：%s，新备注：%s",
                    recordId,
                    oldRecord != null ? oldRecord.getAmount() : "",
                    oldRecord != null ? oldRecord.getRemark() : "",
                    request.getAmount(),
                    request.getRemark()
            );

            operationLogService.recordLog(
                    OperationType.UPDATE,
                    OperationModule.MEMBER,
                    logDetail
            );
        } catch (IllegalArgumentException e) {
            throw e;
        } catch (NoSuchElementException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("修改失败" + e.getMessage(), e);
        }
    }
}
