package com.example.study.infra.jwt

import com.example.study.common.exception.TokenInvalidException
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import org.springframework.stereotype.Component
import java.util.*

@Component
class JwtProvider(
    private val jwtProperty: JwtProperty
) {

    private fun createToken(payload: Long, secretKey: String, tokenValidTime: Long): String {
        return Jwts.builder()
            .setSubject(payload.toString())
            .signWith(SignatureAlgorithm.HS256, secretKey)
            .setExpiration(Date(System.currentTimeMillis() + tokenValidTime))
            .compact()
    }

    fun createAccessToken(payload: Long): String {
        return createToken(
            payload,
            jwtProperty.secretKey,
            jwtProperty.validTime
        )
    }

    fun getPayload(accessToken: String): Long {
        try {
            val claims = Jwts.parser()
                .setSigningKey(jwtProperty.secretKey)
                .parseClaimsJws(accessToken)
                .body

            return claims.subject.toLong()
        } catch (e: Throwable) {
            throw TokenInvalidException("AccessToken({$accessToken)이 만료 됐거나 잘못된 토큰 입니다.")
        }
    }
}