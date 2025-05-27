package com.example.study.infra.jwt

import com.example.study.common.exception.TokenInvalidException
import io.jsonwebtoken.Jwts
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.assertThrows
import kotlin.test.Test

class JwtProviderTest {

    private val jwtProperty = JwtProperty(
        secretKey = "test-secret-key",
        validTime = 1000 * 60 * 10
    )

    private val jwtProvider = JwtProvider(jwtProperty)

    @Test
    fun `accessToken을 생성한다`() {
        val payload = 1L
        val accessToken = jwtProvider.createAccessToken(payload)

        val parsedClaims = Jwts.parser()
            .setSigningKey(jwtProperty.secretKey)
            .parseClaimsJws(accessToken)
            .body

        assertEquals(payload.toString(), parsedClaims.subject)
    }

    @Test
    fun `accessToken의 Payload를 가져온다`() {
        val payload = 1L
        val accessToken = jwtProvider.createAccessToken(payload)

        val result = jwtProvider.getPayload(accessToken)

        assertEquals(payload, result)
    }

    @Test
    fun `유효하지 않은 토큰은 TokenInvalidException 발생한다`() {
        val invalidAccessToken = "accessToken"

        assertThrows<TokenInvalidException> {
            jwtProvider.getPayload(invalidAccessToken)
        }
    }
}