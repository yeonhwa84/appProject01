package com.visualnovel.controller;

import com.visualnovel.model.dto.GameDto;
import com.visualnovel.service.SaveService;
import com.visualnovel.service.UserService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * 게임 API 컨트롤러
 *
 * POST   /api/auth/guest          게스트 로그인
 * POST   /api/auth/login          이름 로그인
 * POST   /api/auth/logout         로그아웃
 * GET    /api/auth/me             현재 세션 유저 정보
 *
 * POST   /api/save                세이브
 * GET    /api/save                로드
 * DELETE /api/save                세이브 삭제
 */
@RestController
@RequiredArgsConstructor
public class GameController {

    private final UserService userService;
    private final SaveService saveService;

    private static final String SESSION_USER_ID   = "userId";
    private static final String SESSION_USER_TYPE = "userType";
    private static final String SESSION_USER_NAME = "userName";

    // ══════════════════════════════════════════════════
    //  인증 API
    // ══════════════════════════════════════════════════

    /** 게스트 로그인 */
    @PostMapping("/api/auth/guest")
    public ResponseEntity<GameDto.ApiResponse<GameDto.LoginResponse>> loginAsGuest(
            HttpSession session) {

        GameDto.LoginResponse res = userService.loginAsGuest();
        setSession(session, res);
        return ResponseEntity.ok(GameDto.ApiResponse.ok(res.getMessage(), res));
    }

    /** 이름 로그인 */
    @PostMapping("/api/auth/login")
    public ResponseEntity<GameDto.ApiResponse<GameDto.LoginResponse>> loginByName(
            @RequestBody @Valid GameDto.NamedLoginRequest req,
            HttpSession session) {

        GameDto.LoginResponse res = userService.loginByName(req.getUserName());
        setSession(session, res);
        return ResponseEntity.ok(GameDto.ApiResponse.ok(res.getMessage(), res));
    }

    /** 로그아웃 */
    @PostMapping("/api/auth/logout")
    public ResponseEntity<GameDto.ApiResponse<Void>> logout(HttpSession session) {
        session.invalidate();
        return ResponseEntity.ok(GameDto.ApiResponse.ok("로그아웃 완료.", null));
    }

    /** 현재 세션 유저 정보 */
    @GetMapping("/api/auth/me")
    public ResponseEntity<GameDto.ApiResponse<GameDto.LoginResponse>> me(HttpSession session) {
        String userId = (String) session.getAttribute(SESSION_USER_ID);
        if (userId == null)
            return ResponseEntity.ok(GameDto.ApiResponse.fail("로그인이 필요해요."));

        GameDto.LoginResponse res = GameDto.LoginResponse.builder()
            .userId(userId)
            .userType((String) session.getAttribute(SESSION_USER_TYPE))
            .userName((String) session.getAttribute(SESSION_USER_NAME))
            .build();
        return ResponseEntity.ok(GameDto.ApiResponse.ok(res));
    }

    // ══════════════════════════════════════════════════
    //  세이브 API
    // ══════════════════════════════════════════════════

    /** 세이브 */
    @PostMapping("/api/save")
    public ResponseEntity<GameDto.ApiResponse<Void>> save(
            @RequestBody GameDto.SaveRequest req,
            HttpSession session) {

        String userId = requireLogin(session);
        if (userId == null)
            return ResponseEntity.ok(GameDto.ApiResponse.fail("로그인이 필요해요."));

        saveService.save(userId, req);
        return ResponseEntity.ok(GameDto.ApiResponse.ok("저장 완료!", null));
    }

    /** 로드 */
    @GetMapping("/api/save")
    public ResponseEntity<GameDto.ApiResponse<GameDto.SaveResponse>> load(HttpSession session) {
        String userId = requireLogin(session);
        if (userId == null)
            return ResponseEntity.ok(GameDto.ApiResponse.fail("로그인이 필요해요."));

        return saveService.load(userId)
            .map(data -> ResponseEntity.ok(GameDto.ApiResponse.ok("불러오기 완료!", data)))
            .orElse(ResponseEntity.ok(GameDto.ApiResponse.fail("저장된 데이터가 없어요.")));
    }

    /** 세이브 삭제 */
    @DeleteMapping("/api/save")
    public ResponseEntity<GameDto.ApiResponse<Void>> deleteSave(HttpSession session) {
        String userId = requireLogin(session);
        if (userId == null)
            return ResponseEntity.ok(GameDto.ApiResponse.fail("로그인이 필요해요."));

        saveService.delete(userId);
        return ResponseEntity.ok(GameDto.ApiResponse.ok("삭제 완료!", null));
    }

    // ── 헬퍼 ──────────────────────────────────────────────────────
    private void setSession(HttpSession session, GameDto.LoginResponse res) {
        session.setAttribute(SESSION_USER_ID,   res.getUserId());
        session.setAttribute(SESSION_USER_TYPE, res.getUserType());
        session.setAttribute(SESSION_USER_NAME, res.getUserName());
    }

    private String requireLogin(HttpSession session) {
        return (String) session.getAttribute(SESSION_USER_ID);
    }
}
