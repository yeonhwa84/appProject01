package com.visualnovel.model.entity;

import lombok.*;
import java.time.LocalDateTime;
import java.util.Map;

/** 세이브 데이터 엔티티 */
@Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
public class VnSave {

    private Long              saveId;
    private String            userId;
    private String            sceneId;
    private Map<String,Integer> affection;  // MyBatis JSON 타입 핸들러로 처리
    private String            currentBg;
    private LocalDateTime     savedAt;
}
