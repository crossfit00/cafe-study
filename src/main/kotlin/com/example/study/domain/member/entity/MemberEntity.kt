package com.example.study.domain.member.entity

import com.example.study.common.code.MemberErrorCode
import com.example.study.common.exception.ApiException
import com.example.study.domain.BaseEntity
import jakarta.persistence.*
import java.time.LocalDate

@Table(name = "member")
@Entity
class MemberEntity(
    @Column(name = "name", nullable = false)
    val name: String,

    @Column(name = "email", nullable = false)
    val email: String,

    @Column(name = "phone_number", nullable = false)
    val phoneNumber: String,

    @Enumerated(EnumType.STRING)
    @Column(name = "gender", nullable = false)
    val gender: Gender,

    @Column(name = "birth", nullable = false)
    val birth: LocalDate,

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    var status: MemberStatus
): BaseEntity() {

    fun updateWithDrawMember() {
        if (status != MemberStatus.SERVICE) {
            throw ApiException.from(
                errorCode = MemberErrorCode.E400_INVALID_MEMBER_STATUS_FOR_WITHDRAW,
                "Member Status($status)가 SERVICE 상태가 아닙니다.."
            )
        }

        this.status = MemberStatus.WITHDRAWN
    }
}