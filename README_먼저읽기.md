# 글로브박스 수정 소스 인계

## 먼저 확인해 주세요
이 파일은 원본 Android Studio/Gradle 프로젝트가 아닙니다.
제공받은 memory-drive-0.31.11-release.apk를 기반으로 추가한 Java 코드,
WebView 화면(HTML/CSS/JS), 디자인 자산, APK 패치 차이를 정리한 인계 자료입니다.
settings.gradle, build.gradle, 원본 Kotlin/Compose 프로젝트는 포함되어 있지 않습니다.
이 ZIP만으로 Android Studio에서 원본 앱을 열고 바로 빌드할 수 없습니다.

주행 상세를 원본 구조에서 변경하고 유지보수하려면 원래 개발자가 보유한 최신
Android 소스 프로젝트 또는 Git 저장소가 필요합니다. 이 자료는 해당 프로젝트에
디자인과 수정 사항을 옮길 때 참고할 수 있습니다.

## 포함 파일
- native-java/: 추가 작성한 Java 화면/브리지/지도/미디어 코드
- web-runtime/: 최종 APK에 실제 포함한 웹 화면과 사진·영상·폰트
- native-assets/: 네이티브용 로고, 폰트, 시연 자료
- apk-patches/: 기존 smali와 수정된 smali의 차이(diff)
- scripts-reference/: 당시 패치·재패키징 스크립트. 구현 참고용입니다.
- manifest.json: 포함 파일 해시 및 비교 대상 최종 APK 해시

## 화면 연결
- GloveboxScreen.java: WebView와 원본 네이티브 화면 전환/브리지
- GloveboxFeatures.java: 원본 주행 기록 화면 및 기능 연결
- GloveboxMap.java: 원본 CommunityMapView와 RoadRoutes를 이용하는 지도 화면
- web-runtime/journal-native.js: 주행 기록 및 지도 바로가기
- web-runtime/routes-design.js, routes-design.css: 함께 나눈 길 디자인
- web-runtime/native-screen.js, native-screen.css: 네이티브 연동 화면 보정
- GloveboxSamples.java: 시연 기억과 이미지 연결

## 주행 기록과 지도
원본 주행 기록은 VehicleDriveJournalCard / VehicleRouteScene을 사용합니다.
지도는 원본의 함께 나눈 길 계열 기능(CommunityMapView / RoadRoutes)입니다.
상단에 나란히 있는 주행 기록·지도 바로가기는 수정본에서 추가했습니다.
실제 주행 데이터 수집은 계정 연결과 차량 동의가 필요합니다.
연결된 계정/보관 코스가 없는 환경이라 실제 서버 도로 경로 응답과 실차 주행 수집은
검증하지 못했습니다. 시연 사진은 실제 사용자 기록과 구분해야 합니다.

## 디자인
기본 텍스트: Pretendard. 일부 감성 소제목: 아름드리 꽃나무.
따뜻한 금색·유리 패널과 G 서랍 모양 로고를 사용합니다.
현재 배포 APK의 assets/glovebox-web과 assets/glovebox를 그대로 포함했습니다.
별도 초기 웹 프로젝트가 아닌 최종 번들 기준입니다.

## 빌드와 서명 주의
scripts-reference는 기존 작업 경로, 원본 APK, apktool 작업 폴더, SDK/JDK,
기존 unsigned APK 등에 의존합니다. 일부 이전 디자인 경로는 현재 없습니다.
즉시 재현 가능한 독립 빌드 패키지로 보장하지 않습니다.
개발 도구, 원본 APK, 서명키, 계정 정보, 녹화 영상, 캐시 등은 포함하지 않았습니다.
기존 수정 APK는 원 제작자 서명키가 아닌 로컬 테스트 키로 서명되었습니다.
원본 소스에 반영한 후 원래 개발자의 빌드·서명·테스트 절차를 따르세요.
