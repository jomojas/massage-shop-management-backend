package com.jiade.massageshopmanagement.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import com.jiade.massageshopmanagement.model.Member;

public class MemberDTO {
    private Long id;
    private String name;
    private String phone;
    private BigDecimal balance;
    private String description;
    private LocalDateTime createdTime;

    public MemberDTO() {}

    public MemberDTO(Long id, String name, String phone, BigDecimal balance, String description, LocalDateTime createdTime) {
        this.id = id;
        this.name = name;
        this.phone = phone;
        this.balance = balance;
        this.description = description;
        this.createdTime = createdTime;
    }

    // Getters and setters

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public BigDecimal getBalance() { return balance; }
    public void setBalance(BigDecimal balance) { this.balance = balance; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public LocalDateTime getCreatedTime() { return createdTime; }
    public void setCreatedTime(LocalDateTime createdTime) { this.createdTime = createdTime; }

    // Optional: static factory method for conversion
    public static MemberDTO fromEntity(Member member) {
        return new MemberDTO(
                member.getId(),
                member.getName(),
                member.getPhone(),
                member.getBalance() != null ? member.getBalance() : BigDecimal.ZERO,
                member.getDescription(),
                member.getCreatedTime()
        );
    }
}
