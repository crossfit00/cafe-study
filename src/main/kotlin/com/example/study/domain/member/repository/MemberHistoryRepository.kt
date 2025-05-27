package com.example.study.domain.member.repository

import com.example.study.domain.member.entity.MemberHistoryEntity
import org.springframework.data.jpa.repository.JpaRepository

interface MemberHistoryRepository : JpaRepository<MemberHistoryEntity, Long>