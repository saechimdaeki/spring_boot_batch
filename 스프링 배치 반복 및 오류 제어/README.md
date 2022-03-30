# 스프링 배치 반복 및 오류 제어
## Repeat
- 기본 개념
  - Spring Batch는 얼마나 작업을 반복해야 하는지 알려 줄수 있는 기능을 제공한다.
  - 특정 조건이 충족 될 때까지(또는 특정 조건이 아직 충족되지 않을 때까지) Job또는 Step을 반복하도록 배치 애플리케이션을 구성 할 수 있다
  - 스프링 배치에서는 Step의 반복과 Chunk 반복을 RepeatOperation을 사용해서 처리하고 있다
  - 기본 구현체로 RepeatTemplate를 제공한다
- 구조
![image](https://user-images.githubusercontent.com/40031858/160863438-bd423cf4-56e0-48bc-a533-b953167c7577.png)
- 반복을 종료할 것인지 여부를 결정하는 세가지 항목
  - RepeatStatus
    - 스프링 배치의 처리가 끝났는지 판별하기 위한 열거형(enum)
      - CONTINUABLE - 작업이 남아 있음
      - FINISHED - 더이상 반복 없음
  - CompletionPolicy
    - RepeatTemplate의 iterate 메소드 안에서 반복을 중단할지 결정
    - 실행 횟수 또는 완료시기, 오류 발생시 수행 할 작업에 대한 반복여부 결정
    - 정상 종료를 알리는데 사용된다
  - ExceptionHandler
    - RepeatCallback 안에서 예외가 발생하면 RepeatTemplate가 ExceptionHandler를 참조해서 예외를 다시 던질지 여부 결정
    - 예외를 받아서 다시 던지게 되면 반복 종료
    - 비정상 종료를 알리는데 사용된다

![image](https://user-images.githubusercontent.com/40031858/160863884-2082d2ca-8123-4c81-b757-5986c3687c55.png)

![image](https://user-images.githubusercontent.com/40031858/160863961-1c0ce438-7141-43cc-a288-d7b001cf56b1.png)

![image](https://user-images.githubusercontent.com/40031858/160864024-b9943b66-1ed2-4546-af12-5fa03b9d0739.png)

![image](https://user-images.githubusercontent.com/40031858/160864094-152b18e6-c7c6-4a12-a4f6-301b327a0bf1.png)

![image](https://user-images.githubusercontent.com/40031858/160864174-a230bb24-fb15-472c-aa21-bd9e24410ec4.png)