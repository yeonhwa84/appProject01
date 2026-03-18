# ✿ 미연시 게임 - Vue 3 + Spring Boot + MySQL RDS

## 전체 구조

```
📁 visual-novel/
├── backend/                              ← Spring Boot (포트 8081)
│   ├── pom.xml
│   └── src/main/
│       ├── java/com/visualnovel/
│       │   ├── VisualNovelApplication.java
│       │   ├── config/
│       │   │   ├── WebConfig.java           ← CORS 설정
│       │   │   └── JsonTypeHandler.java     ← MyBatis JSON 핸들러
│       │   ├── controller/
│       │   │   └── GameController.java      ← 인증 + 세이브 API
│       │   ├── service/
│       │   │   ├── UserService.java         ← 게스트/이름 로그인
│       │   │   └── SaveService.java         ← 세이브/로드
│       │   ├── repository/
│       │   │   ├── VnUserMapper.java
│       │   │   └── VnSaveMapper.java
│       │   └── model/
│       │       ├── entity/ (VnUser, VnSave)
│       │       └── dto/GameDto.java
│       └── resources/
│           ├── application.yml              ← MySQL RDS 설정
│           ├── schema.sql                   ← DDL (최초 1회 실행)
│           └── mapper/
│               ├── VnUserMapper.xml
│               └── VnSaveMapper.xml
│
└── frontend/                             ← Vue 3 + Vite (포트 5173)
    ├── package.json
    ├── vite.config.js
    ├── index.html
    └── src/
        ├── App.vue                          ← 로그인 → 게임 화면 전환
        ├── main.js
        ├── views/
        │   ├── LoginView.vue                ← 게스트/이름 로그인 화면
        │   └── GameView.vue                 ← 게임 메인 화면
        └── store/
            ├── gameData.js                  ← ★ 스토리/캐릭터 수정은 여기!
            ├── api.js                       ← Spring Boot API 연동
            └── useBgm.js                    ← BGM/효과음 컴포저블
```

---

## DB 세팅 (최초 1회)

MySQL RDS에 접속해서 `schema.sql` 실행:

```sql
-- schema.sql 내용을 project01 DB에 실행
source schema.sql;
```

---

## 실행 방법

### 1. 백엔드

```bash
cd backend
mvn spring-boot:run
```

→ http://localhost:8081 실행

### 2. 프론트엔드

```bash
cd frontend
npm install
npm run dev
```

→ http://localhost:5173 실행 (게임 화면)

---

## GitHub 푸시 & 자동 배포

```bash
git add .
git commit -m "✿ Add visual novel game"
git push origin master
```

CodePipeline이 자동 빌드/배포해요.

### 프론트 빌드 포함 배포 시

```bash
# 프론트 빌드 (Spring Boot static에 출력)
cd frontend && npm run build

# 백엔드 패키징
cd ../backend && mvn clean package

# 깃 푸시
cd .. && git add . && git commit -m "✿ Build frontend" && git push origin master
```

---

## API 목록

| 메서드 | 경로 | 설명 |
|--------|------|------|
| POST | `/api/auth/guest` | 게스트 로그인 |
| POST | `/api/auth/login` | 이름 로그인 |
| GET  | `/api/auth/me`   | 세션 확인 |
| POST | `/api/auth/logout`| 로그아웃 |
| POST | `/api/save`      | 세이브 |
| GET  | `/api/save`      | 로드 |
| DELETE | `/api/save`    | 세이브 삭제 |

---

## 이미지/음악 파일 위치

```
frontend/public/
├── images/
│   ├── backgrounds/   ← 배경 PNG (1280×720 권장)
│   │   ├── classroom.png
│   │   ├── classroom_morning.png
│   │   ├── corridor.png
│   │   ├── library.png
│   │   ├── cafeteria.png
│   │   ├── council_room.png
│   │   └── sunset.png
│   └── characters/    ← 캐릭터 투명 PNG
│       ├── hana.png
│       ├── yui.png
│       └── seri.png
└── sounds/
    ├── bgm/           ← 배경음악 MP3 (loop)
    │   ├── bgm_title.mp3
    │   ├── bgm_spring.mp3
    │   ├── bgm_calm.mp3
    │   ├── bgm_morning.mp3
    │   ├── bgm_happy.mp3
    │   ├── bgm_elegant.mp3
    │   └── bgm_sunset.mp3
    └── sfx/           ← 효과음 MP3 (1회)
        ├── click.mp3
        └── select.mp3
```

> 이미지/음악 파일이 없어도 게임은 실행돼요.
> 캐릭터 이미지 없으면 색상 원으로 자동 대체됩니다.

---

## 스토리 추가하는 법

`frontend/src/store/gameData.js` 에서:

```js
// 씬 추가
SCENES['씬ID'] = {
  id:      '씬ID',
  speaker: '하나',          // null = 내레이션
  text:    '대사 내용',
  bg:      'classroom.png', // null = 이전 배경 유지
  charImg: 'hana.png',      // null = 캐릭터 숨김
  bgm:     'bgm_spring.mp3',// null = BGM 유지, 'stop' = 정지
  next:    '다음씬ID',       // null = 게임 종료

  // 선택지 (없으면 생략)
  choices: [
    { text: '선택지 텍스트', next: '씬ID', target: '하나', affection: +10 }
  ]
}
```

---

## 로그인 방식 차이

| 구분 | 게스트 | 이름 로그인 |
|------|--------|------------|
| 이름 | 불필요 | 필요 (1~20자) |
| 이어하기 | 세션 유지 중만 가능 | 언제든 가능 (같은 이름) |
| DB 저장 | O (세이브 가능) | O |
| 식별자 | `guest_랜덤` | `named_랜덤` |
