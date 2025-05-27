package com.example.study.domain.member.entity

import com.example.study.domain.BaseEntity
import jakarta.persistence.*

@Table(name = "member_history")
@Entity
class MemberHistoryEntity(
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    val member: MemberEntity,

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    val status: MemberStatus
): BaseEntity() {

    companion object {
        fun toEntity(member: MemberEntity): MemberHistoryEntity {
            return MemberHistoryEntity(
                member = member,
                status = member.status
            )
        }
    }
}