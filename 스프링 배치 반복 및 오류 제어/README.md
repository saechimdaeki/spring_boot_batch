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

## FalutTolerant
- 기본 개념
  - 스프링 배치는 Job 실행 중에 오류가 발생할 경우 장애를 처리하기 위한 기능을 제공하며 이를 통해 복원력을 향상시킬 수 있다.
  - 오류가 발생해도 Step이 즉시 종료되지 않고 Retry 혹은 Skip 기능을 활성화 함으로써 내결함성 서비스가 가능하도록 한다
  - 프로그램의 내결함성을 위해 Skip과 Retry 기능을 제공한다
    - Skip
      - ItemReader / ItemProcessor / ItemWriter에 적용 할 수 있다
    - Retry
      - ItemProcessor / ItemWriter에 적용할 수 있다
  - FaultTolerant 구조는 청크 기반의 프로세스 기반위에 Skip과 Retry 기능이 추가되어 재정의 되어 있다
- 구조
![image](https://user-images.githubusercontent.com/40031858/161002859-01fb71bc-0383-4521-b99d-b9d901d9d887.png)


      StepBuilderFactory > StepBuilder > FaultTolerantStepBuilder > TaskletStep

```java
public Step batchStep(){
  return new stepBuilderFactory.get("batchStep")
    .<I,O>chunk(10)
    .reader(ItemReader)
    .writer(ItemWriter)
    .falutTolerant() // 내결함성 기능 활성화
    .skip(Class<? extends Throwable> type) //예외 발생시 Skip 할 예외 타입 설정
    .skipLimit(int skipLimit) //Skip 제한 횟수 설정
    .skipPolicy(SkipPolicy skipPolicy) //Skip을 어떤 조건과 기준으로 적용 할 것인지 정책 설정
    .noSkip(Class<? extends Throwable> type) // 예외 발생 시 Skip 하지 않을 예외 타입 설정
    .retry(Class<? extends Throwable> type) //예외 발생 시 Retry 할 예외 타입 설정
    .retryLimit(int retryLimit) //Retry 제한 횟수 설정
    .retryPolicy(RetryPolicy retryPolicy) //Retry를 어떤 조건과 기준으로 적용 할 것인지 정책 설정
    .backOffPolicy(BackOffPolicy backOffPolicy) //다시 Retry하기 까지의 지연시간(단위:ms)을 설정
    .noRetry(Class<? extends Throwable> type) //예외 발생 시 Retry하지 않을 예외 타입 설정
    .noRollback(Class<? extends Throwable> type) //예외 발생 시 Rollback 하지 않을 예외 타입 설정
    .build();

}
```

![image](https://user-images.githubusercontent.com/40031858/161003946-9bed648a-f2e3-4d6a-b6e7-d059e882ce24.png)

## Skip
- Skip은 데이털르 처리하는 동안 설정된 Exception이 발생했을 경우, 해당 데이터 처리를 건너뛰는 기능이다
- 데이터의 사소한 오류에 대해 Step의 실패처리 대신 Skip을 함으로써, 배치수행의 빈번한 실패를 줄일 수 있게한다

![image](https://user-images.githubusercontent.com/40031858/161029479-7b895237-2e14-49b5-8ecf-dc9d8555d3de.png)

- 오류 발생 시 스킵 설정에 의해서 Item2 번은 건너뛰고 Item3번부터 다시 처리한다.
- ItemReader는 예외가 발생하면 해당 아이템만 스킵하고 계속 진행한다
- ItemProcessor와 ItemWriter는 예외가 발생하면 Chunk의 처음으로 돌아가서 스킵된 아이템을 제외한 나머지 아이템들을 가지고 처리하게 된다
- Skip기능은 내부적으로 SkipPolicy를 통해서 구현되어 있다
- Skip 가능 여부를 판별하는 기준은 다음과 같다
  1. 스킵 대상에 포함된 예외인지 여부
  2. 스킵 카운터를 초과 했는지 여부

![image](https://user-images.githubusercontent.com/40031858/161029761-c7e6469a-0e6b-4c59-b487-d7c3f125fc58.png)

![image](https://user-images.githubusercontent.com/40031858/161029830-198aebe4-087e-4c08-aebe-75f32804be1c.png)


