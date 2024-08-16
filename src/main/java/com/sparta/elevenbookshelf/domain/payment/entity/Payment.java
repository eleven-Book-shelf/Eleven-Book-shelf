package com.sparta.elevenbookshelf.domain.payment.entity;

import com.sparta.elevenbookshelf.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String tid;

    @Column(nullable = false)
    private String orderId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private int amount;

    @Column(nullable = false)
    private String status;

    @Builder
    public Payment(Long id, String tid, String orderId, User user, int amount, String status) {
        this.id = id;
        this.tid = tid;
        this.orderId = orderId;
        this.user = user;
        this.amount = amount;
        this.status = status;
    }

    public void updateStatus(String status) {
        this.status = status;
    }
}
