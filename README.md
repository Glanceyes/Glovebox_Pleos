# Glovebox_Pleos

Glovebox는 차로 다시 방문한 장소에서 사진과 그때 들었던 음악을 함께 보고, 다른 운전자가 공유한 코스를 지도에서 열어보는 Pleos 차량 앱입니다.

이 저장소에는 공모전 제출 버전 **0.31.12 (51)** 의 Android 코드, 웹 화면, APK 패치와 빌드 스크립트가 들어 있습니다.

| 경로 | 구현 |
| --- | --- |
| `native-java/` | 사진 앨범과 주행 기록 연결, 계정별 YouTube 음악 추천·재생, 차량 및 휴대폰 미디어 제어 |
| `web-runtime/` | 앨범, 음악 보관함, 주행 지도, 다른 운전자의 프로필과 공유 코스 화면 |
| `integration/` | 원본 차량 앱에 적용하는 Android 패치, 런처 아이콘 리소스, Pleos 에뮬레이터용 iPhone 연동 |
| `apk-patches/`, `scripts-reference/` | 동료 작업본에서 인계된 변경 내역과 초기 빌드 스크립트 |

## 빌드

`build-integrated.py`에 팀 인계본의 **Glovebox-Pleos.apk**를 입력합니다. 스크립트는 기준 APK의 해시를 확인하고, 차량 앱의 기존 사진·영상·폰트에 이 저장소의 소스 변경을 적용합니다.

공모전 제출본의 용산동 앨범까지 재현하려면 팀이 보관한 `Glovebox_통합_수정소스.zip`의 `Glovebox/native-assets/recording/` 폴더를 이 저장소의 `native-assets/recording/`에 복사합니다. 이 폴더에는 촬영 사진 세 장과 사진별 위치·시각이 들어 있습니다. 필요한 도구와 빌드 명령은 [빌드 방법](빌드방법.md)에 있습니다.
