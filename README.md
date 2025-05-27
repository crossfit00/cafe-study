# `카페 주문/결제 시스템`

## `Tech Stack`

- Spring Boot: 3.3.12
- Java: 17
- DB: H2, Embedded-Redis
- 기타: JPA, JWT

<br>

## `ERD`

<img width="1117" alt="Image" src="https://github.com/user-attachments/assets/847244a1-3b75-44ee-b9f3-74976f427b4e" />

<br>

## `API 명세서`

- Member
  - [회원 가입](https://github.com/crossfit00/cafe-study/blob/master/docs/%ED%9A%8C%EC%9B%90%EA%B0%80%EC%9E%85.md)
  - [회원 탈퇴](https://github.com/crossfit00/cafe-study/blob/master/docs/%ED%9A%8C%EC%9B%90%ED%83%88%ED%87%B4.md)
- Order
  - [주문 생성](https://github.com/crossfit00/cafe-study/blob/master/docs/%EC%A3%BC%EB%AC%B8%20%EC%83%9D%EC%84%B1.md)
  - [주문 취소](https://github.com/crossfit00/cafe-study/blob/master/docs/%EC%A3%BC%EB%AC%B8%20%EC%B7%A8%EC%86%8C.md)
- Payment
  - [결제 생성](https://github.com/crossfit00/cafe-study/blob/master/docs/%EA%B2%B0%EC%A0%9C%20%EC%83%9D%EC%84%B1.md)

<br>

## `실행`

```
java -jar cafe-study-0.0.1-SNAPSHOT.jar 
```

- 로컬 자바 버전 17 이어야 실행 가능합니다.

<br>

## `내용`

### `인증/인가`

- JWT AccessToken 사용하여 사용자 인증 및 인가 진행
- 추후에 RefreshToken 적용해서 고도화 해볼 수 있을 듯
- 회원, 주문, 결제, 인증 서버 모두 분리될 정도로 고도화 된다면 JWK 사용하여 공개키 방식 적용 해봐도 좋을 듯함

<br>

### `주문/결제`

- 주문/결제시에는 동시성 처리가 중요하여 Redis 분산락을 적용
- 동일 제품을 여러명에서 결제하는 상황에 대한 테스트 코드를 작성하여 분산락이 적절히 적용되는지 확인 ([참고 Link](https://github.com/crossfit00/cafe-study/blob/master/src/test/kotlin/com/example/study/domain/order/service/OrderServiceIntegrationTest.kt#L90))
- 좀 더 유연하게 한다면 Kafka 사용해서 동일 파티션으로 처리하여 순서 보장해서 동시성 방어하도록 처리하는 것도 가능하지 않을까 생각

<br>

### `이력 관리`

- 회원 탈퇴, 주문 취소, 결제 취소와 같은 경우는 status를 바꾸며 soft delete를 진행하지만 이력 관리는 필연적으로 해야 할 거 같아서 추가함
- 회원 탈퇴의 경우 `"30일 이내 탈퇴 철회 서비스 이용이 가능합니다."` 라는 조건이 있기 때문에 탈퇴한지 30일이 지난 멤버는 `WITHDRWAW -> END` 상태로 변경할 필요가 있음
  - 간단한 batch 모듈을 하나 생성하고, Jenkins를 사용해서 하루에 한번 멤버 탈퇴 체크 API 호출하는 script로 관리할 수 있을 듯
  - 이력 데이터는 데이터가 많이 쌓이기 때문에 하루에 한번 배치로 조회할 때 인덱스를 잘 활용하도록 설계해야 함 
- 이력을 계속 MySQL에 쌓으면 데이터 양이 엄청나게 많아질 것이기 때문에 보관 정책을 정해봐도 좋을듯 하다.
  - 예를들면, 한달 이상된 데이터는 ElasticSearch에 저장한다던지?
  - 또는 로그 저장에 용이한 데이터베이스 선택해서 사용해도 좋을 듯 함

<br>

### `성능`

- 현재 스펙상 JPA를 사용하였는데, Bulk Insert, Update 같은 것들은 JPA 특성상 효율이 좋지 않기 때문에 MyBatis, JDBC Template 같은 것을 사용해서 Batch Write를 적용해봐도 좋을 듯 함
- 분산락이나 외부 결제 API Call (Mocking) 같이 무거운 로직들은 Ngrinder 통해서 성능/부하 테스트 해서 개선해보면 좋을듯