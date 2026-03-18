export const CHARACTERS = {
  하나: { name:'하나', color:'#F9A8C9', image:'/images/characters/hana.png', description:'밝고 활발한 같은 반 친구' },
  유이: { name:'유이', color:'#93DDC8', image:'/images/characters/yui.png',  description:'조용하고 책을 좋아하는 도서부 부원' },
  세리: { name:'세리', color:'#C5B8F5', image:'/images/characters/seri.png', description:'학생회장, 냉정하지만 따뜻한 마음' }
}

export const SCENES = {
  // CHAPTER 1
  start:      { id:'start',      speaker:null,  text:'봄날, 전학 온 첫날.\n새 교실 문 앞에서 잠시 멈췄다.',     bg:'classroom.png',        charImg:null,       bgm:'bgm_spring.mp3', next:'s01' },
  s01:        { id:'s01',        speaker:null,  text:'문을 열자 세 사람이 동시에 이쪽을 바라봤다.',               bg:null,                   charImg:null,       bgm:null,             next:'s02' },
  s02:        { id:'s02',        speaker:'하나', text:'어머! 전학생이야? 나는 하나야! 잘 부탁해~',                bg:null,                   charImg:'hana.png', bgm:null,             next:'s03' },
  s03:        { id:'s03',        speaker:'유이', text:'...반갑습니다. 저는 유이예요.',                             bg:null,                   charImg:'yui.png',  bgm:null,             next:'s04' },
  s04:        { id:'s04',        speaker:'세리', text:'새 전입생이군요. 규칙은 잘 지켜주세요.',                    bg:null,                   charImg:'seri.png', bgm:null,             next:'choice_01' },
  choice_01:  { id:'choice_01',  speaker:null,  text:'누구에게 먼저 말을 걸까?', bg:null, charImg:null, bgm:null, next:null,
    choices:[
      { text:'하나에게 말 건다',     next:'hana_01',   target:'하나', affection:+10 },
      { text:'유이에게 말 건다',     next:'yui_01',    target:'유이', affection:+10 },
      { text:'세리에게 말 건다',     next:'seri_01',   target:'세리', affection:+10 },
      { text:'조용히 자리에 앉는다', next:'silent_01', target:null,   affection:0   }
    ]
  },
  hana_01:   { id:'hana_01',   speaker:'하나', text:'정말?! 같이 점심 먹자!',                       bg:null,           charImg:'hana.png',bgm:null,              next:'hana_02' },
  hana_02:   { id:'hana_02',   speaker:null,   text:'하나가 환하게 웃으며 손을 내밀었다.',             bg:null,           charImg:'hana.png',bgm:null,              next:null,
    choices:[{ text:'기꺼이 손을 잡는다', next:'hana_good', target:'하나', affection:+15 },{ text:'수줍게 고개를 끄덕인다', next:'hana_mid', target:'하나', affection:+5 }]
  },
  hana_good: { id:'hana_good', speaker:'하나', text:'야호! 우리 이미 친한 친구야!',                  bg:'corridor.png', charImg:'hana.png',bgm:null,              next:'ch1_end' },
  hana_mid:  { id:'hana_mid',  speaker:'하나', text:'헤헤, 부끄럼쟁이야? 귀엽다!',                  bg:'corridor.png', charImg:'hana.png',bgm:null,              next:'ch1_end' },
  yui_01:    { id:'yui_01',    speaker:'유이', text:'...저한테요? 도서관은 자주 가나요?',             bg:null,           charImg:'yui.png', bgm:null,              next:null,
    choices:[{ text:'자주 가는 편이에요', next:'yui_good', target:'유이', affection:+15 },{ text:'별로 안 가요', next:'yui_mid', target:'유이', affection:+3 }]
  },
  yui_good:  { id:'yui_good',  speaker:'유이', text:'그럼 함께 가요. 좋은 책 알려드릴게요.',          bg:'library.png',  charImg:'yui.png', bgm:'bgm_calm.mp3',    next:'ch1_end' },
  yui_mid:   { id:'yui_mid',   speaker:'유이', text:'...언제든 오고 싶으면 와요.',                   bg:'library.png',  charImg:'yui.png', bgm:'bgm_calm.mp3',    next:'ch1_end' },
  seri_01:   { id:'seri_01',   speaker:'세리', text:'...나한테요? 용기 있네요.',                     bg:null,           charImg:'seri.png',bgm:null,              next:null,
    choices:[{ text:'학생회 활동에 관심 있다고 한다', next:'seri_good', target:'세리', affection:+15 },{ text:'그냥 인사만 한다', next:'seri_mid', target:'세리', affection:+5 }]
  },
  seri_good: { id:'seri_good', speaker:'세리', text:'...다음에 학생회실로 오세요.',                   bg:'corridor.png', charImg:'seri.png',bgm:null,              next:'ch1_end' },
  seri_mid:  { id:'seri_mid',  speaker:'세리', text:'...잘 적응하길 바랍니다.',                      bg:null,           charImg:'seri.png',bgm:null,              next:'ch1_end' },
  silent_01: { id:'silent_01', speaker:null,   text:'조용히 자리에 앉았다.\n각자 할 일로 돌아갔다.',  bg:null,           charImg:null,      bgm:null,              next:'ch1_end' },
  ch1_end:   { id:'ch1_end',   speaker:null,   text:'첫날이 저물어 간다.\n어떤 인연이 시작되려 하는 걸까.', bg:'sunset.png', charImg:null, bgm:'bgm_sunset.mp3',  next:'ch2_start' },
  // CHAPTER 2
  ch2_start:       { id:'ch2_start',       speaker:null,  text:'― 2일차 ―\n아침 교실.',                                 bg:'classroom_morning.png',charImg:null,       bgm:'bgm_morning.mp3',   next:'ch2_s01' },
  ch2_s01:         { id:'ch2_s01',         speaker:'하나', text:'아! 왔어?! 오늘 점심 같이 먹자!',                        bg:null,                   charImg:'hana.png', bgm:null,                next:'ch2_s02' },
  ch2_s02:         { id:'ch2_s02',         speaker:'유이', text:'...안녕하세요. 어제는 잘 쉬었나요?',                      bg:null,                   charImg:'yui.png',  bgm:null,                next:'ch2_s03' },
  ch2_s03:         { id:'ch2_s03',         speaker:'세리', text:'좋은 아침이에요. 오늘 학생회 신입 모집 공고가 붙었어요.',  bg:null,                   charImg:'seri.png', bgm:null,                next:'ch2_choice_01' },
  ch2_choice_01:   { id:'ch2_choice_01',   speaker:null,  text:'오늘 점심, 누구와 함께할까?', bg:null, charImg:null, bgm:null, next:null,
    choices:[{ text:'하나와 함께 밥을 먹는다', next:'ch2_hana_01', target:'하나', affection:+8 },{ text:'유이를 도서관으로 찾아간다', next:'ch2_yui_01', target:'유이', affection:+8 },{ text:'학생회실에 가본다', next:'ch2_seri_01', target:'세리', affection:+8 }]
  },
  ch2_hana_01:     { id:'ch2_hana_01',     speaker:'하나', text:'진짜?! 내가 맛있는 자리 알아!',                          bg:'cafeteria.png',        charImg:'hana.png', bgm:'bgm_happy.mp3',     next:'ch2_hana_02' },
  ch2_hana_02:     { id:'ch2_hana_02',     speaker:'하나', text:'있잖아... 넌 왠지 편해. 그거 알아?',                     bg:'cafeteria.png',        charImg:'hana.png', bgm:null,                next:'ch2_hana_choice' },
  ch2_hana_choice: { id:'ch2_hana_choice', speaker:null,  text:'어떻게 대답할까?', bg:null, charImg:'hana.png', bgm:null, next:null,
    choices:[{ text:'나도 그래. 너랑 있으면 편해.', next:'ch2_hana_good', target:'하나', affection:+12 },{ text:'그래? 잘됐다.', next:'ch2_hana_mid', target:'하나', affection:+4 }]
  },
  ch2_hana_good:   { id:'ch2_hana_good',   speaker:'하나', text:'헤헤... 그럼 우리 진짜 친구야!',                         bg:'cafeteria.png',        charImg:'hana.png', bgm:null,                next:'ch2_end' },
  ch2_hana_mid:    { id:'ch2_hana_mid',    speaker:'하나', text:'뭐야, 반응 싱겁다! 그래도 좋아!',                        bg:'cafeteria.png',        charImg:'hana.png', bgm:null,                next:'ch2_end' },
  ch2_yui_01:      { id:'ch2_yui_01',      speaker:'유이', text:'...왔군요. 앉아요.',                                     bg:'library.png',          charImg:'yui.png',  bgm:'bgm_calm.mp3',      next:'ch2_yui_choice' },
  ch2_yui_choice:  { id:'ch2_yui_choice',  speaker:'유이', text:'봄에 관한 소설이에요. 읽어본 적 있나요?', bg:'library.png', charImg:'yui.png', bgm:null, next:null,
    choices:[{ text:'없어요. 읽어보고 싶어요.', next:'ch2_yui_good', target:'유이', affection:+12 },{ text:'소설은 잘 안 읽는데...', next:'ch2_yui_mid', target:'유이', affection:+5 }]
  },
  ch2_yui_good:    { id:'ch2_yui_good',    speaker:'유이', text:'...그럼 빌려드릴게요. 읽고 나면 이야기해요.',             bg:'library.png',          charImg:'yui.png',  bgm:null,                next:'ch2_end' },
  ch2_yui_mid:     { id:'ch2_yui_mid',     speaker:'유이', text:'...언제든 마음 바뀌면 빌려드릴게요.',                     bg:'library.png',          charImg:'yui.png',  bgm:null,                next:'ch2_end' },
  ch2_seri_01:     { id:'ch2_seri_01',     speaker:'세리', text:'왔군요. 학생회 신입 지원서예요.',                          bg:'council_room.png',     charImg:'seri.png', bgm:'bgm_elegant.mp3',   next:'ch2_seri_choice' },
  ch2_seri_choice: { id:'ch2_seri_choice', speaker:'세리', text:'처음 봤을 때부터 뭔가 다르다고 생각했어요.', bg:'council_room.png', charImg:'seri.png', bgm:null, next:null,
    choices:[{ text:'지원서를 받아 든다.', next:'ch2_seri_good', target:'세리', affection:+12 },{ text:'아직은 생각해볼게요.', next:'ch2_seri_mid', target:'세리', affection:+4 }]
  },
  ch2_seri_good:   { id:'ch2_seri_good',   speaker:'세리', text:'...잘 결정했어요. 기대할게요.',                           bg:'council_room.png',     charImg:'seri.png', bgm:null,                next:'ch2_end' },
  ch2_seri_mid:    { id:'ch2_seri_mid',    speaker:'세리', text:'...언제든 마음 바뀌면 오세요.',                           bg:'council_room.png',     charImg:'seri.png', bgm:null,                next:'ch2_end' },
  ch2_end:         { id:'ch2_end',         speaker:null,  text:'두 번째 날도 저물어 간다.\n이 인연은 어디로 흘러갈까.',    bg:'sunset.png',           charImg:null,       bgm:'bgm_sunset.mp3',    next:null }
}

export function getAffLabel(v) {
  if (v >= 80) return '♥♥♥ 매우 친밀'
  if (v >= 60) return '♥♥  친밀'
  if (v >= 40) return '♥   보통'
  if (v >= 20) return '    서먹'
  return '    냉담'
}
