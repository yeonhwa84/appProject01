package com.visualnovel.service;

import com.visualnovel.model.dto.GameDto;
import com.visualnovel.model.entity.VnSave;
import com.visualnovel.model.entity.VnUser;
import com.visualnovel.repository.VnSaveMapper;
import com.visualnovel.repository.VnUserMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.format.DateTimeFormatter;
import java.util.UUID;

/**
 * 유저 로그인 서비스
 *
 * [게스트]
 *   - UUID 자동 생성
 *   - 세션에만 저장 (DB에도 저장해서 세이브 가능)
 *   - 브라우저 닫으면 이어하기 불가 (세션 만료)
 *
 * [이름 로그인]
 *   - 같은 이름으로 접속 시 기존 세이브 이어하기 가능
 *   - 이름이 없으면 새 유저 생성
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final VnUserMapper userMapper;
    private final VnSaveMapper saveMapper;

    private static final DateTimeFormatter FMT =
        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    // ── 게스트 로그인 ────────────────────────────────────────────
    @Transactional
    public GameDto.LoginResponse loginAsGuest() {
        String userId = "guest_" + UUID.randomUUID().toString().replace("-", "").substring(0, 12);

        VnUser user = VnUser.builder()
            .userId(userId)
            .userType("GUEST")
            .userName(null)
            .build();

        userMapper.insertUser(user);
        log.info("[게스트 로그인] userId={}", userId);

        return GameDto.LoginResponse.builder()
            .userId(userId)
            .userType("GUEST")
            .userName(null)
            .hasSave(false)
            .message("게스트로 시작합니다. 게임을 즐겨요! 🌸")
            .build();
    }

    // ── 이름 로그인 ──────────────────────────────────────────────
    @Transactional
    public GameDto.LoginResponse loginByName(String userName) {
        // 기존 이름 유저 조회
        VnUser existing = userMapper.selectByName(userName);

        if (existing != null) {
            // 기존 유저 → 마지막 로그인 갱신
            userMapper.updateLastLogin(existing.getUserId());
            boolean hasSave = saveMapper.existsByUserId(existing.getUserId());

            String savedAt = null;
            if (hasSave) {
                VnSave save = saveMapper.selectByUserId(existing.getUserId());
                if (save != null && save.getSavedAt() != null) {
                    savedAt = save.getSavedAt().format(FMT);
                }
            }

            log.info("[이름 로그인 - 기존] userName={}, hasSave={}", userName, hasSave);
            return GameDto.LoginResponse.builder()
                .userId(existing.getUserId())
                .userType("NAMED")
                .userName(userName)
                .hasSave(hasSave)
                .savedAt(savedAt)
                .message(hasSave
                    ? "반가워요, " + userName + "님! 저장된 게임이 있어요. 🌸"
                    : "반가워요, " + userName + "님! 새로 시작할게요. 🌸")
                .build();
        }

        // 새 유저 생성
        String userId = "named_" + UUID.randomUUID().toString().replace("-", "").substring(0, 8);
        VnUser newUser = VnUser.builder()
            .userId(userId)
            .userType("NAMED")
            .userName(userName)
            .build();
        userMapper.insertUser(newUser);

        log.info("[이름 로그인 - 신규] userName={}, userId={}", userName, userId);
        return GameDto.LoginResponse.builder()
            .userId(userId)
            .userType("NAMED")
            .userName(userName)
            .hasSave(false)
            .message("처음 오셨군요, " + userName + "님! 환영해요. 🌸")
            .build();
    }
}
