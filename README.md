# 🏠구해줘 홈즈🏠
<img width="952" alt="스크린샷 2024-12-29 오후 10 39 36" src="https://github.com/user-attachments/assets/b9491c6f-7fe8-4ff7-91ea-96e49bd063e3" />

</br>

## 1. 프로젝트 소개
**구해줘, 홈즈**는 `사용자 맞춤형 매물 추천 서비스`입니다.
이사를 준비하지만 어디로 가야 할지 고민이 되는 사용자들을 위해 구해줘, 홈즈는 사용자가 원하는 조건에 맞는 최적의 동네와 매물을 추천합니다.
1. 간단한 정보 입력만으로 추천 매물 리스트를 확인할 수 있습니다.
2. 사용자 조건에 맞는 상위 매물 5개를 그룹화하여 보여줍니다.
3. 매물의 주변 시설과 교통 정보를 함께 제공합니다. -> 추후 개발 예정

</br>

## 2. 팀원 구성
| **김민호** | **홍유진** |
| :------: |  :------: |
| [<img src="https://github.com/user-attachments/assets/55422569-b255-47c7-9b40-d65f7b18f6b7" height=150 width=150> <br/> @klaus9267](https://github.com/klaus9267) <br/> 백엔드 개발 | [<img src="https://github.com/user-attachments/assets/ae04bc5a-6885-4a3c-abb9-3465754f427e" height=150 width=150> <br/> @ujeans](https://github.com/ujeans) <br/> 프런트엔드 개발 |

</br>

## 3. 개발 환경
### Front-End
- `Next.JS`, `Tailwind CSS`, `Zustand`, `Storybook`

### Back-End
- `Spring Boot`, `Kotlin`, `AWS`, `MongoDB`

</br>

## 4. 개발 기간
2024.09 ~ 2024.12

</br>

## 5. 페이지별 기능
### [초기화면]
- 서비스 접속 초기 화면이 나오면 클릭을 통해 다음 페이지를 볼 수 있습니다.
  
| 초기 화면 |
| ---- |
| <img src="https://github.com/user-attachments/assets/1198eb78-d126-480b-8e37-726872a7fcd2" width="350" /> |

### [필터 적용]
- `react-daum-postcode` 라이브러리를 이용하여 `학교/회사와 같은 목적지`를 입력합니다.
- 목적지까지의 `희망 소요시간`을 설정합니다.
- 원하는 `매물 유형` 및 `거래 유형`을 다중 선택할 수 있습니다.
- 거래 유형에서 선택한 전세, 월세에 해당하는 `가격 슬라이드바`를 볼 수 있습니다.
- 해당 슬라이드바에서 `원하는 가격을 설정`합니다.
- `시간`, `예산` 중에 `더 중요한 것`을 선택 후 지역 추천 버튼을 누르면 다름 페이지로 이동합니다.

| 필터 적용 |
| ---- |
| <img src="https://github.com/user-attachments/assets/46403f8c-9639-47f2-92d6-18495d357872" width="350" /> |


### [TOP5]
- 필터를 적용한 조건에 맞는 매물들을 모아 지역을 기준으로 `Top 5 순위`를 매겨서 보여줍니다.
- 해당 지역에 속하는 `매물 개수`도 보여줍니다.
- 매물이 없는 경우는 `매물이 없다는 텍스트`를 보여줍니다.

| TOP5 리스트 | 매물이 없을 때 |
| ---- | ---- |
| <img width="350" alt="스크린샷 2024-12-29 오후 11 02 52" src="https://github.com/user-attachments/assets/99890619-7890-4998-9680-d20cad0e8b95" /> | <img width="350" alt="스크린샷 2024-12-29 오후 11 12 14" src="https://github.com/user-attachments/assets/b8107db1-c212-4011-b62d-d052d07e5c7f" /> |

### [매물 상세 목록]
- TOP5에서 보러가기를 클릭하면 상세 매물 목록이 보입니다.
- `상세 매물의 거래 유형, 가격, 지역, 소요시간`을 볼 수 있습니다.

| 매물 상세 목록 |
| ---- |
| <img width="350" alt="스크린샷 2024-12-29 오후 11 05 34" src="https://github.com/user-attachments/assets/fc252ae6-fcb5-49b6-a430-3d228881def6" /> |


### [매물 상세 정보]
- 매물 목록에서 원하는 매물을 클릭하면 해당 매물의 상세 정보를 알 수 있습니다.
- 아파트/오피스텔/빌라명과 주소를 볼 수 있습니다.
- `지도에서 해당 위치`를 파악할 수 있습니다.

| 매물 상세 목록 |
| ---- |
| <img width="900" alt="스크린샷 2024-12-29 오후 11 08 38" src="https://github.com/user-attachments/assets/aad32ec3-61c4-4d0e-826f-c60520502705" /> |

