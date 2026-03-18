package com.visualnovel.service;

import com.visualnovel.model.dto.GameDto;
import com.visualnovel.model.entity.VnSave;
import com.visualnovel.repository.VnSaveMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.format.DateTimeFormatter;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class SaveService {

    private final VnSaveMapper saveMapper;
    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    // ── 세이브 ──────────────────────────────────────────────────
    @Transactional
    public void save(String userId, GameDto.SaveRequest req) {
        VnSave save = VnSave.builder()
            .userId(userId)
            .sceneId(req.getSceneId())
            .affection(req.getAffection())
            .currentBg(req.getCurrentBg())
            .build();
        saveMapper.upsertSave(save);
        log.info("[세이브] userId={}, sceneId={}", userId, req.getSceneId());
    }

    // ── 로드 ────────────────────────────────────────────────────
    @Transactional(readOnly = true)
    public Optional<GameDto.SaveResponse> load(String userId) {
        VnSave save = saveMapper.selectByUserId(userId);
        if (save == null) return Optional.empty();

        return Optional.of(GameDto.SaveResponse.builder()
            .sceneId(save.getSceneId())
            .affection(save.getAffection())
            .currentBg(save.getCurrentBg())
            .savedAt(save.getSavedAt() != null ? save.getSavedAt().format(FMT) : null)
            .build());
    }

    // ── 삭제 ────────────────────────────────────────────────────
    @Transactional
    public void delete(String userId) {
        saveMapper.deleteByUserId(userId);
        log.info("[세이브 삭제] userId={}", userId);
    }
}
