# YanoxClockRemote

LeEco Le 2 Pro 등 Android 4.4+의 Consumer IR 송신기를 이용하는 야녹스/3D LED Clock 테스트 리모컨입니다.

- 최소 Android: 4.4 (API 19)
- Carrier: 38 kHz
- 버튼: POWER, MODE, ALARM, TIMER, UP, S/S, DOWN, CLEAR
- IR 데이터: Mooas 3D LED Clock(B07SKTZK6Z)에서 학습된 Broadlink 코드를 microsecond pulse 배열로 변환

## 빌드
Android Studio에서 폴더를 열고 `Build > Build APK(s)`를 실행합니다.

## 주의
야녹스 시계가 동일 OEM IR 코드셋을 사용하는지는 실제 기기에서 확인해야 합니다. 버튼을 누르고 시계가 반응하지 않아도 앱/IR 송신기 고장이라고 단정할 수 없습니다.
