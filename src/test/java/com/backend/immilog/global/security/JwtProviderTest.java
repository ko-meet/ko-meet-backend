package com.backend.immilog.global.security;

import com.backend.immilog.global.model.TokenIssuanceDTO;
import com.backend.immilog.user.application.UserDetailsServiceImpl;
import com.backend.immilog.user.enums.Countries;
import com.backend.immilog.user.enums.UserRole;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.util.ReflectionTestUtils;

import javax.crypto.SecretKey;
import java.util.Base64;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@DisplayName("JwtProvider 클래스")
class JwtProviderTest {

    @Mock
    private UserDetailsServiceImpl userDetailsService;

    @InjectMocks
    private JwtProvider jwtProvider;

    private SecretKey secretKey;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        String key =
                "c2VjcmV0S2V5U3RyaW5nc2VjcmV0S2V5U3RyaW5nc2VjcmV0S2V5U3RyaW5nc2VjcmV0S2V5U3RyaW5n";  // Base64로 인코딩된 문자열
        secretKey = Keys.hmacShaKeyFor(Base64.getDecoder().decode(key));
        jwtProvider = new JwtProvider(userDetailsService);
        ReflectionTestUtils.setField(jwtProvider, "issuer", "issuer");
        ReflectionTestUtils.setField(jwtProvider, "secretKey", secretKey);
        ReflectionTestUtils.setField(jwtProvider, "secretKeyString", key);
        jwtProvider.init();  // init 메서드를 필드 설정 후에 호출
    }

    @Test
    @DisplayName("JwtProvider 객체 생성")
    void testIssueAccessToken() {
        TokenIssuanceDTO tokenIssuanceDTO = TokenIssuanceDTO.builder()
                .id(1L)
                .email("test@example.com")
                .userRole(UserRole.ROLE_USER)
                .country(Countries.SOUTH_KOREA)
                .build();

        String token = jwtProvider.issueAccessToken(tokenIssuanceDTO);
        assertNotNull(token);

        Claims claims = Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody();

        assertEquals("1", claims.getSubject());
        assertEquals("test@example.com", claims.get("email"));
        assertEquals("ROLE_USER", claims.get("userRole"));
        assertEquals("KR", claims.get("country"));
    }

    @Test
    @DisplayName("Refresh Token 발급")
    void testIssueRefreshToken() {
        String token = jwtProvider.issueRefreshToken();
        assertNotNull(token);

        Jws<Claims> claimsJws = Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token);

        assertEquals("issuer", claimsJws.getBody().getIssuer());
    }

    @Test
    @DisplayName("토큰 유효성 검사")
    void testValidateToken_Valid() {
        TokenIssuanceDTO tokenIssuanceDTO = TokenIssuanceDTO.builder()
                .id(1L)
                .email("test@example.com")
                .userRole(UserRole.ROLE_USER)
                .country(Countries.SOUTH_KOREA)
                .build();

        String token = jwtProvider.issueAccessToken(tokenIssuanceDTO);

        boolean isValid = jwtProvider.validateToken(token);
        assertTrue(isValid);
    }

    @Test
    @DisplayName("토큰 유효성 검사 - 유효하지 않은 토큰")
    void testValidateToken_Invalid() {
        String invalidToken = "invalid.token.value";
        boolean isValid = jwtProvider.validateToken(invalidToken);
        assertFalse(isValid);
    }

    @Test
    @DisplayName("토큰에서 ID 추출")
    void testGetIdFromToken() {
        TokenIssuanceDTO tokenIssuanceDTO = TokenIssuanceDTO.builder()
                .id(1L)
                .email("test@example.com")
                .userRole(UserRole.ROLE_USER)
                .country(Countries.SOUTH_KOREA)
                .build();

        String token = jwtProvider.issueAccessToken(tokenIssuanceDTO);

        Long id = jwtProvider.getIdFromToken(token);
        assertEquals(1L, id);
    }

    @Test
    @DisplayName("토큰에서 이메일 추출")
    void testGetEmailFromToken() {
        TokenIssuanceDTO tokenIssuanceDTO = TokenIssuanceDTO.builder()
                .id(1L)
                .email("test@example.com")
                .userRole(UserRole.ROLE_USER)
                .country(Countries.SOUTH_KOREA)
                .build();

        String token = jwtProvider.issueAccessToken(tokenIssuanceDTO);

        String email = jwtProvider.getEmailFromToken(token);
        assertEquals("test@example.com", email);
    }

    @Test
    @DisplayName("Authentication 객체 조회")
    void testGetAuthentication() {
        TokenIssuanceDTO tokenIssuanceDTO = TokenIssuanceDTO.builder()
                .id(1L)
                .email("test@example.com")
                .userRole(UserRole.ROLE_USER)
                .country(Countries.SOUTH_KOREA)
                .build();

        String token = jwtProvider.issueAccessToken(tokenIssuanceDTO);

        UserDetails mockUserDetails = mock(UserDetails.class);
        when(userDetailsService.loadUserByUsername("test@example.com"))
                .thenReturn(mockUserDetails);

        Authentication authentication = jwtProvider.getAuthentication(token);

        assertNotNull(authentication);
        assertEquals(mockUserDetails, authentication.getPrincipal());
        verify(userDetailsService).loadUserByUsername("test@example.com");
    }
}
