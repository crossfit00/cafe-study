package com.example.study.event

data class MemberHistoryEvent(
    val memberId: Long,
    // TODO: 멤버 탈퇴 이벤트 연동에 필요한 정보 추가 (현재 스펙상 사용처 없지만 생성은 해놓음)
) {
    companion object {
        fun from(memberId: Long): MemberHistoryEvent {
            return MemberHistoryEvent(memberId = memberId)
        }
    }
}