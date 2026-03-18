package com.visualnovel.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import java.time.LocalDateTime;
import java.util.Map;

/** API 요청/응답 DTO 모음 */
public class GameDto {

    // ── 게스트 로그인 요청 ────────────────────────────────────
    @Getter @Setter @NoArgsConstructor
    public static class GuestLoginRequest {
        // 아무 필드 없음 - 서버에서 UUID 자동 생성
    }

    // ── 이름 로그인 요청 ──────────────────────────────────────
    @Getter @Setter @NoArgsConstructor @AllArgsConstructor
    public static class NamedLoginRequest {
        @NotBlank(message = "이름을 입력해주세요.")
        @Size(min = 1, max = 20, message = "이름은 1~20자 사이여야 해요.")
        private String userName;
    }

    // ── 로그인 응답 ──────────────────────────────────────────
    @Getter @Setter @Builder
    public static class LoginResponse {
        private String  userId;
        private String  userType;    // GUEST | NAMED
        private String  userName;    // 이름 (NAMED만)
        private boolean hasSave;     // 기존 세이브 존재 여부
        private String  savedAt;     // 마지막 세이브 시각
        private String  message;
    }

    // ── 세이브 요청 ──────────────────────────────────────────
    @Getter @Setter @NoArgsConstructor @AllArgsConstructor
    public static class SaveRequest {
        private String              sceneId;
        private Map<String,Integer> affection;
        private String              currentBg;
    }

    // ── 세이브 응답 ──────────────────────────────────────────
    @Getter @Setter @Builder
    public static class SaveResponse {
        private String              sceneId;
        private Map<String,Integer> affection;
        private String              currentBg;
        private String              savedAt;
    }

    // ── 공통 API 응답 래퍼 ───────────────────────────────────
    @Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
    public static class ApiResponse<T> {
        private boolean success;
        private String  message;
        private T       data;

        public static <T> ApiResponse<T> ok(T data) {
            return ApiResponse.<T>builder().success(true).message("OK").data(data).build();
        }
        public static <T> ApiResponse<T> ok(String msg, T data) {
            return ApiResponse.<T>builder().success(true).message(msg).data(data).build();
        }
        public static <T> ApiResponse<T> fail(String msg) {
            return ApiResponse.<T>builder().success(false).message(msg).build();
        }
    }
}
