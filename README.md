# Eleven-Book-shelf

## 📚 웹툰과 웹소설을 좋아하는 사람들을 위한 커뮤니티 서비스 📚

## 📚 각 서비스 링크 🥓
| backend                                                           | frontend                                                                                                  |
|--------------------------------------------------------------------|----------------------------------------------------------------------------------------------------------|
|[![175937078](https://github.com/user-attachments/assets/f698da5a-41d6-4401-afe8-f5d05152794e)](https://github.com/eleven-Book-shelf/Eleven-Book-shelf) |[![175937078](https://github.com/user-attachments/assets/f698da5a-41d6-4401-afe8-f5d05152794e)](https://github.com/eleven-Book-shelf/Frontend)  |

## 🏂 팀원 소팀원 소개 🌌



# 💻주요기능
#### 정보 공유 커뮤니티, 코드 리뷰 기능, 코드 카타 & 챌린지 기능, 포인트 기능

<details>
<summary>커뮤니티 기능</summary>
<div markdown="1">


**검색 기능과 인기 검색어**
- 현재 사람들이 가장 많이 검색하는 검색어는 뭘까?
- 개발자는 오픈마인드! 서로 정보를 공유해봅시다
</div>
</details>

<details>
<summary>코드 리뷰</summary>
<div markdown="1">


**코드 리뷰**
- 어떤 언어든 OK
- 내 코드를 사람들과 공유해봅시다

</div>
</details>

<details>
<summary>코드 카타 & 챌린지 기능</summary>
<div markdown="1">


**오늘의 코드카타를 확인해보세요**
- 코드카타 참여하기를 누르면 팀이 매칭됩니다!



**팀원들과 소통하면서 코드카타를 풀어보세요**
- 웹 코드 컴파일 기능과 실시간 채팅으로 팀원들과 소통해보세요

</div>
</details>


<details>
<summary>포인트 기능</summary>
<div markdown="1">

![image](https://github.com/user-attachments/assets/e3c3d4eb-395b-4360-8fb8-0daf6de7268a)

**활동을 통해 포인트를 모으고 계급을 올려봅시다**
- 게임에선 브론즈였던 내가 여기선 어디까지 갈 수 있을까요?

</div>
</details>
</div>


# 🥤 기능 명세서 🥥

<details>
<summary> 🧲 API 명세서 🛠</summary>
<div markdown="1">

| API 보러가기|
|---------------------------------------------------------------- |
| https://teamsparta.notion.site/baeaf4f7765b4af0bfa2ebc58bbad509 |


</div>
</details>

<details>
<summary>🔑 협업룰 🔑</summary>
<div markdown="1">

## Github Rules

🍚 식사 시간	 
- 점심시간 : 12:00 ~ 14:00
- 저녁시간 : 18:00 ~ 19:00
* 위 시간 내에서 한 시간 내로 먹고오기! *

1. 회의할 때 마이크랑 캠 꼭 키기!
2. 계획표 잘 작성하기(당일 오전 9시에 작성)
3. 적극적으로 의견 내기
   4. 진행상황 자주 공유하기
   5. 튜터님에게 자주 찾아가기
6. 10분 이상 없을 때는 슬랙에 남기기(댓글로 복귀까지 꼭 표시)
7. ZEP에 자기 상태 표시하기(잠깐 비울 땐 자리비움 / 식사시간 땐 휴식중)
8. 슬랙은 관련된 내용은 댓글로만

| 작업 타입 | 작업내용 |
| --- | --- |
| ✨ update   | 해당 파일에 새로운 기능이 생김 |
| 🎉 add | 없던 파일을 생성함, 초기 세팅 |
| 🐛 bugfix | 버그 수정 |
| ♻️ refactor | 코드 리팩토링 |
| 🩹 fix | 코드 수정 |
| 🚚 move | 파일 옮김/정리 |
| 🔥 del | 기능/파일을 삭제 |
| 🍻 test | 테스트 코드를 작성 |
| 💄 style | css |
| 🙈 gitfix | gitignore 수정 |
| 🔨script | package.json 변경(npm 설치 등) |공유하기


</div>
</details>

<div id = "ERD">

# 🎫ERD🎫

![11 (1)](https://github.com/user-attachments/assets/73401de7-f528-4e34-b20d-9c7a82bb6f69)


</div>

<div id = "서비스 아키텍쳐">

# 📋 아키텍쳐 📋

![아키텍쳐 drawio (1)](https://github.com/user-attachments/assets/2c677bbe-1dfc-48ad-aeee-d9e5aeccacab)


<div id ="decision">

# 기술적 의사결정
<details>
<summary>SpringBatch, Quartz</summary>
<div markdown="1">

#### Spring Batch

• CodeKata와 챌린지 기능, 사용자 데이터 분석에서 대용량의 데이터를 효율적으로 처리를 위한 프레임워크로 활용




#### Quartz


• Cron 표현식을 통한 유연한 작업 스케줄링과 세밀한 시간 제어에 사용

• Spring Batch 데이터의 안정적인 스케쥴링 처리 환경을 구성에 활용


</div>
</details>

<details>
<summary>Redis(EmbeddedRedis)</summary>
<div markdown="1">


#### 다양한 데이터 타입과 기능 지원


• 직접적으로 DB를 사용하지 않고도 리소스를 덜 사용할 수 있는, 효율적이고 빠른 데이터 처리의 인메모리 방식

• String, Hash, Sorted Set, List, Set 등 다양한 자료 구조를 제공하여 실시간 채팅, 검색어 순위, 토큰 재발급에 활용


#### 테스트 환경에서 활용

• 테스트 코드에서 실제 Redis의 기능의 EmbeddedRedis 통해 로컬 환경에서 검증하고 테스트

</div>
</details>

<details>
<summary>GitHub Actions</summary>
<div markdown="1">

#### GitHub Repository와의 통합유연성
• GitHub과 통합하여 CI/CD 파이프라인을 구축가능, 타 독립적인 CI/CD 서버와 비교해 별도의 환경 설정이 불필요


#### 유연한 워크플로우
• 개발자 요구에 맞게 직접 액션을 만들어 워크플로우를 유연하게 변경하거나 확장, 빌드 및 배포 파이프라인을 쉽게
유지보수 가능
</div>
</details>

<details>
<summary>Dockerhub</summary>
<div markdown="1">

#### 컨테이너화를 통한 일관적인 배포
• FE와 BE를 독립적으로 컨테이너화하여 개별 유지보수와 추가적인 확장성 확보


#### Dockerhub 중앙관리
• 컨테이너 이미지 중앙 관리와, Scale Out 에 대응하여 동일한 환경 구성 유용성


#### CI/CD 파이프 라인 구축 유용성
• GitHub Actions와의 통합 유용성으로 빌드한 이미지를 Dockerhub에 푸시하고 자동화된 배포 지원
</div>
</details>

<div id ="trouble">

# 🛠️ 트러블 슈팅
<details>
<summary>WebDriver 요소 찾기 오류.</summary>
<div markdown="1">

#### xPath 값을 사용해서 해당 요소의 위치를 지정. 이후 지정된 데이터를 가져올때 해당 위치에 데이터가 다른 데이터로 존재함. 원하는 데이터가 아닌 다른 데이터를 받아오는 문제 발생.WebDriver는 정상적으로 실행중. xPath 값 정확함. 몇몇 작품에서 해당 xPath위치에다른 데이터가 존재하는 상황.

#### xPath 값을 상대경로에서 해당 위치만 사용하기 위해 절대 경로로 변경함
* 같은 위치에 다른 데이터이기에 같은 상황 발생.
#### HTML 구조 재확인 후 필요 데이터, 다른 데이터 xPath 값 추출 후 재확인.
* 동일함. xPath 절대 경로는 같은 위치임.
#### 데이터를 가져올때 확인이 불가하다면 가져온 후 걸러내도록 수정.
* 필요한 데이터와 불필요 데이터의 차이점이 ‘#’의 존재 유무로 확인.
  #이 포함된 데이터만 저장하도록 조건문과 반복문 사용.
  만약 #이 하나라도 포함되지 않아 빈 문자열을 저장하는 경우에는 ‘없음’ 저장으로 해당 필드에 데이터가 없다는 것을 명시적으로 표현함.


</div>
</details>

<details>
<summary>MySQL EC2와 백엔드 EC2 간에 연결 문제</summary>
<div markdown="1">

#### 백엔드 인스턴스와 데이터베이스 인스턴스를 분리 후, 연결 중 보안을 위해 MySQL 내부 설정에서 백엔드 인스턴스의 IP로만 요청을 허용하게끔 bind-address 를 지정함. 이때 데이터베이스 인스턴스가 백엔드 인스턴스의 요청을 정상적으로 받아주지 못하고 데이터베이스에 제대로 연결되지 못하는 문제가 발생.

#### 오타를 확인해 봤다
• 어디에도 오타나 문제될 것은 보이지 않았다.
#### 인바운드 규칙에 백엔드 IP 경로로 포트 허용을 확인해 봤다.
• 연결한 부분은 제대로 허용이 되어있었다.
#### 빌드한 백엔드 이미지 파일 재확인 해봤다.
• 이것도 이상이 없었다.
#### 로컬 프로젝트로 데이터베이스 인스턴스 접속시도를 해보았다.
• 이것도 접속 불가라 나오는 것을 보아하니 문제를 어느 정도 알게 됬다.
#### bind-address 를 데이터베이스 인스턴스의 ip로 지정했다.
• 정상적으로 접속이 되었다.

</div>
</details>

<details>
<summary>Nginx 를 이용한 프록시 문제</summary>
<div markdown="1">

#### 프론트와 백엔드를 한 Ec2에 동시에 배포했을때 백엔드에 요청을 제대로 보내지 못하는 문제가 발생함

• nginx의 default.conf 설정 파일을 수정했음에도 불구하고, 문제가 해결되지 않음

#### 백엔드에서 요청 내용의 URL을 전부 확인하게 했지만 문제를 확인

• URL은 입력은 제대로 되는것이 확인됨 하지만 요청을 제대로 받지 못함. 다른 방법으로 요청을 시도해 보면 프론트에 까지 재대로 반환되는 것을 봐선
CORS 문제는 아니였음

• default.conf 설정 파일을 좀더 자세하게 살펴본 결과 '/api/' 여야 했던 것이 'api/'으로 되어 있어 인식을 하지 못했던 것
수정후 정상적으로 진행

</div>
</details>
</div></div></div>
