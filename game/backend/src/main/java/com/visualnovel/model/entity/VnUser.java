package com.visualnovel.model.entity;

import lombok.*;
import java.time.LocalDateTime;

/**
 * 게임 유저 엔티티
 * - GUEST : 이름 없이 시작 (UUID 자동 생성)
 * - NAMED : 이름 입력 후 시작 (같은 이름으로 이어하기 가능)
 */
@Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
public class VnUser {

    private String        userId;     // UUID (게스트) 또는 "named_이름" (이름 로그인)
    private String        userType;   // GUEST | NAMED
    private String        userName;   // 이름 (NAMED만 사용)
    private LocalDateTime createdAt;
    private LocalDateTime lastLogin;

    public boolean isGuest() { return "GUEST".equals(userType); }
    public boolean isNamed() { return "NAMED".equals(userType); }
}
