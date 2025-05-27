package com.example.study.event

import org.springframework.stereotype.Component
import org.springframework.transaction.event.TransactionPhase
import org.springframework.transaction.event.TransactionalEventListener

@Component
class MemberEventHandler(
) {
    /**
     * 탈퇴 후에 발생해야 하는 개인정보 파기 또는 멤버 관련 데이터를 정리하기 위한 이벤트 통합 로직
     *
     * 현재 스펙상 따로 로직 작성할 것은 없지만 구조만 만들어 놓음
     */
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    fun listenMemberEventHandler(memberHistoryEvent: MemberHistoryEvent) {
        // TODO: 멤버 탈퇴 후 비동기 후처리 로직
    }
}