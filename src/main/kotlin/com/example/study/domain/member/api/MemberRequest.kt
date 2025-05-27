package com.example.study.domain.member.api

import com.example.study.domain.member.entity.Gender
import com.example.study.domain.member.entity.MemberEntity
import com.example.study.domain.member.entity.MemberStatus
import jakarta.validation.constraints.NotBlank
import java.time.LocalDate

data class MemberRequest(
    @field:NotBlank
    val name: String,

    @field:NotBlank
    val email: String,

    @field:NotBlank
    val phoneNumber: String,

    val gender: Gender,

    val birth: LocalDate
) {
    fun toEntity(): MemberEntity {
        return MemberEntity(
            name = name,
            email = email,
            phoneNumber = phoneNumber,
            gender = gender,
            birth = birth,
            status = MemberStatus.SERVICE
        )
    }
}