package com.visualnovel;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * ✿ 미연시 게임 - Spring Boot 백엔드
 *
 * DB  : MySQL RDS (project01)
 * ORM : MyBatis (전자정부 스타일 Mapper XML)
 * 인증 : 세션 기반 (게스트 / 이름 로그인)
 *
 * API:
 *   POST /api/auth/guest   게스트 로그인
 *   POST /api/auth/login   이름 로그인
 *   GET  /api/auth/me      세션 확인
 *   POST /api/save         세이브
 *   GET  /api/save         로드
 */
@SpringBootApplication
@MapperScan("com.visualnovel.repository")
public class VisualNovelApplication {
    public static void main(String[] args) {
        SpringApplication.run(VisualNovelApplication.class, args);
    }
}
