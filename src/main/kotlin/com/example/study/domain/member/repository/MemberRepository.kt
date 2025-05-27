package com.example.study.domain.member.repository

import com.example.study.domain.member.entity.MemberEntity
import org.springframework.data.jpa.repository.JpaRepository


interface MemberRepository : JpaRepository<MemberEntity, Long> {
    fun existsByEmail(email: String): Boolean
}