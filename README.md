# `카페 주문/결제 시스템`

## `Tech Stack`

- Spring Boot: 3.3.12
- Java: 17
- DB: H2, Embedded-Redis
- 기타: JPA, JWT

<br>

## `ERD`

<br>

## `API 명세서`

- [회원 가입](https://github.com/crossfit00/cafe-study/blob/master/docs/%ED%9A%8C%EC%9B%90%EA%B0%80%EC%9E%85.md)
- [회원 탈퇴]()

```http request
### 멤버 탈퇴
DELETE http://localhost:8080/member/2/withdraw
Content-Type: application/json
Authorization: Bearer eyJhbGci
```
```json
{
  "status": "success"
}
```
