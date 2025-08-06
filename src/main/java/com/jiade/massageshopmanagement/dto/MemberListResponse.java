package com.jiade.massageshopmanagement.dto;


import com.jiade.massageshopmanagement.model.Member;
import java.util.List;

public class MemberListResponse {

    private List<Member> members;
    private int totalMembers;
    private int totalPages;
    private int currentPage;

    public MemberListResponse(List<Member> members, int totalMembers, int totalPages, int currentPage) {
        this.members = members;
        this.totalMembers = totalMembers;
        this.totalPages = totalPages;
        this.currentPage = currentPage;
    }

    public List<Member> getMembers() {
        return members;
    }

    public void setMembers(List<Member> members) {
        this.members = members;
    }

    public int getTotalMembers() {
        return totalMembers;
    }

    public void setTotalMembers(int totalMembers) {
        this.totalMembers = totalMembers;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(int totalPages) {
        this.totalPages = totalPages;
    }

    public int getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(int currentPage) {
        this.currentPage = currentPage;
    }
}
