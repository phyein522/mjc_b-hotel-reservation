package com.mjc.hotel.auth.google;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.List;

@Component
public class GoogleIdentityVerifier {
    private final String clientId;
    private final GoogleIdTokenVerifier verifier;

    public GoogleIdentityVerifier(@Value("${auth.google.client-id:}") String clientId) {
        this.clientId = clientId == null ? "" : clientId.trim();
        this.verifier = new GoogleIdTokenVerifier.Builder(
                new NetHttpTransport(),
                GsonFactory.getDefaultInstance()
        ).setAudience(List.of(this.clientId)).build();
    }

    public GoogleProfile verify(String credential) {
        if (!StringUtils.hasText(this.clientId)) {
            throw new IllegalStateException("GOOGLE_CLIENT_ID가 설정되지 않았습니다.");
        }

        try {
            GoogleIdToken idToken = this.verifier.verify(credential);
            if (idToken == null) {
                throw new IllegalArgumentException("유효하지 않은 Google ID 토큰입니다.");
            }

            GoogleIdToken.Payload payload = idToken.getPayload();
            String email = payload.getEmail();
            boolean emailVerified = Boolean.TRUE.equals(payload.getEmailVerified());
            if (!StringUtils.hasText(payload.getSubject()) || !StringUtils.hasText(email) || !emailVerified) {
                throw new IllegalArgumentException("인증된 Google 이메일 정보를 확인할 수 없습니다.");
            }

            String name = payload.get("name") instanceof String value && StringUtils.hasText(value)
                    ? value
                    : email.substring(0, email.indexOf('@'));
            return new GoogleProfile(payload.getSubject(), email.trim().toLowerCase(), name, true);
        } catch (GeneralSecurityException | IOException ex) {
            throw new IllegalArgumentException("Google ID 토큰 검증에 실패했습니다.", ex);
        }
    }

    public boolean isConfigured() {
        return StringUtils.hasText(this.clientId);
    }

    public String getClientId() {
        return this.clientId;
    }
}
