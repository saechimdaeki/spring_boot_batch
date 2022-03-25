# Job and Step

---

---

## JobBuilderFactory/ JobBuilder

### 1. 스프링 배치는 Job과 Step을 쉽게 생성 및 설정할 수 있도록 util 성격의 빌더 클래스들을 제공함

### 2. JobBuilderFactory

- JobBuilder를 생성하는 팩토리 클래스로서 get(String name) 메소드 제공
- JobBuilderFactory.get("jobName")
    - "jobName"은 스프링 배치가 Job을 실행시킬 때 참조하는 Job의 이름

### 3. JobBuilder

- Job을 구성하는 설정 조건에 따라 두 개의 하위 빌더 클래스를 생성하고 실제 Job 생성을 위임한다
- `SimpleJobBuilder`
    - SimpleJob을 생성하는 Builder 클래스
    - Job 실행과 관련된 여러 설정 API를 제공한다
- `FlowJobBUilder`
    - FlowJob을 생성하는 Builder 클래스
    - 내부적으로 FlowBuilder를 반환함으로써 Flow 실행과 관련된 여러 설정 API를 제공한다

![image](https://user-images.githubusercontent.com/40031858/145201239-33b67037-0197-4292-9e32-82f149a13e86.png)

![image](https://user-images.githubusercontent.com/40031858/145201274-44faf09c-383d-4da5-8daf-93c4219bf09b.png)

## SimpleJob

### 1. 기본개념

- SimpleJob은 Step을 실행시키는 Job구현체로서 SimpleJobBuilder에 의해 생성된다
- 여러 단계의 Step으로 구성할 수 있으며 Step을 순차적으로 실행시킨다
- 모든 Step의 실행이 성공적으로 완료되어야 Job이 성공적으로 완료된다
- 맨 마지막에 실행한 Step의 BatchStatus가 Job의 최종 BatchStatus가 된다

### 2. 흐름

![image](https://user-images.githubusercontent.com/40031858/146638074-c18de8bf-61bc-4dbf-bdba-1af724fafe9a.png)

### JobBuilderFactory > JobBuilder > SimpleJobBuilder > SimpleJob

```java
public Job batchJob(){
  return jobBuilderFactory.get("batchJob") // JobBuilder 를 생성하는 팩토리, Job 의 이름을 매개변수로 받음
    .start(Step) // 처음 실행 할 Step 설정, 최초 한번 설정, 이 메서드를 실행하면 SimpleJobBuilder 반환
    .next(Step) // 다음에 실행 할 Step 설정, 횟수는 제한이 없으며 모든 next() 의 Step 이 종료가 되면 Job 이 종료된다
    .incrementer(JobParametersIncrementer) // JobParameter 의 값을 자동을 증가해 주는 JobParametersIncrementer 설정
    .preventRestart(true) //Job 의 재 시작 가능 여부 설정, 기본값은 true
    .validator(JobParameterValidator) // JobParameter 를 실행하기 전에 올바른 구성이 되었는지 검증하는 JobParametersValidator 설정
    .listener(JobExecutionListener) // Job 라이프 사이클의 특정 시점에 콜백 제공받도록 JobExecutionListener 설정
    .buiold();// SimpleJob 생성
}
```

![image](https://user-images.githubusercontent.com/40031858/146638126-ab981e6a-c258-4587-bd8d-61f58727da6d.png)

---



## Start() / next()

```java
public Job batchJob(){
  return jobBuilderFactory.get("batchJob")
    .start(Step) //처음 실행 할 Step 설정, 최초 한번 설정, SimpleJobBuilder가 생성되고 반환된다
    .next(Step)// 다음에 실행할 Step 들을 순차적으로 연결하도록 설정
    .incrementer() // 여러 번 설정이 가능하며 모든 next()의 Step이 종료가 되면 Job이 종료된다
    .validator()
    .preventRestart()
    .listener()
    .build();
}
```

![image](https://user-images.githubusercontent.com/40031858/146666878-575e6843-22ed-4e83-aa31-574ac37ef2e3.png)

## validator()

### 1. 기본 개념

- Job 실행에 꼭 필요한 파라미터를 검증하는 용도
- DefaultJobParametersValidator 구현체를 지원하며, 좀 더 복잡한 제약 조건이 있다면 인터페이스를 직접 구현할 수도 있음

### 2. 구조

![image](https://user-images.githubusercontent.com/40031858/146667183-08b059b2-6f9a-49ef-82ff-938f4a5f8747.png)

![image](https://user-images.githubusercontent.com/40031858/146667189-ed8b40b2-3f04-4c54-9f1a-64cb597f56f3.png)

## preventRestart()

### 1. 기본개념

- Job의 재 시작 여부를 설정
- 기본 값은 true이며 false로 설정시 "이 Job은 재시작을 지원하지 않는다" 라는 의미
- Job이 실패해도 재시작이 안되며 Job을 재시작하려고 하면 JobRestartException이 발생
- 재 시작과 관련있는 기능으로 Job을 처음 실행하는 것과는 아무런 상관 없음

### 2. 흐름도

![image](https://user-images.githubusercontent.com/40031858/146667655-cb8e4869-74ce-4924-9776-82151b58f152.png)

```java
public Job batchJob(){
  return jobBuilderFactory.get("batchJob")
    .start()
    .next()
    .incrementer()
    .validator()
    .preventRestart() //재시작을 하지 않음 (restartable = false)
    .listener()
    .build();
}
```

## incrementer()

### 1. 기본 개념

- JobParameters에서 필요한 값을 증가시켜 다음에 사용될 JobParameters 오브젝트를 리턴 
- 기존의 JobParameter 변경없이 Job을 여러 번 시작하고자 할 때
- RunIdIncrementer 구현체를 지원하며 인터페이스를 직접 구현할 수 있음

![image](https://user-images.githubusercontent.com/40031858/146668012-3fcce14d-640b-4336-a629-9c75c0983142.png)

### 2. 구조

![image-20211219170245963](/Users/kimjunseong/Library/Application Support/typora-user-images/image-20211219170245963.png)

---

## SimpleJob 아키텍쳐

![image](https://user-images.githubusercontent.com/40031858/146668362-125fd93b-d427-4715-a7a7-60ed49a8e572.png)

![image](https://user-images.githubusercontent.com/40031858/146668370-3884ec91-6e40-4e4e-9fbb-b9da3350fe46.png)

----
# Step

## StepBuilderFactory / StepBuilder

### 1. StepBuilderFactory
- StepBuilder를 생성하는 팩토리 클래스로서 get(String name) 메소드 제공
- StepBuilderFactory.get("stepName")
  - "stepName"으로 Step을 생성

### 2. StepBuilder
- Step을 구성하는 설정조건에 따라 다섯 개의 하위 빌더 클래스를 생성하고 실제 Step생성을 윙미한다
- `TaskletStepBuilder`
  - TaskletStep을 생성하는 기본 빌더 클래스
- `SimpleStepBuilder`
  - TaskletStep을 생성하며 내부적으로 청크 기반의 작업을 처리하는 ChunkOrientedTasklet 클래스를 생성
- `PartitionStepBuilder`
  - PartitionStep을 생성하며 멀티 스레드 방식으로 Job을 실행
- `JobStepBuilder`
  - JobStep을 생성하여 Step안에서 Job을 실행한다
- `FlowStepBuilder`
  - FlowStep을 생성하여 Step안에서 Flow를 실행한다

![image](https://user-images.githubusercontent.com/40031858/148684014-d95b1786-8b77-4310-934f-f930fa96d041.png)

![image](https://user-images.githubusercontent.com/40031858/148684039-b6b96893-5489-4cff-95ad-325c33a6c090.png)

## TaskletStep
### 1. 기본 개념
- 스프링 배치에서 제공하는 Step의 구현체로서 Tasklet을 실행시키는 도메인 객체
- RepeatTemplate를 사용해서 Tasklet의 구문을 트랜잭션 경계 내에서 반복해서 실행함
- Task기반과 Chunk기반으로 나누어서 Tasklet을 실행함

### 2. Task vs Chunk 기반 비교
- 스프링 배치에서 Step의 실행 단위는 크게 2가지로 나누어짐
  - chunk 기반
    - 하나의 큰 덩어리를 n개씩 나눠서 실행한다는 의미로 대량 처리를 하는 경우 효과적으로 설계됨
    - ItemReader, ItemProcessor, ItemWriter를 사용하며 청크 기반 전용 Tasklet인 ChunkOrientedTasklet 구현체가 제공된다
  - Task 기반
    - ItemReader와 ItemWriter와 같은 청크 기반의 작업 보다 단일 작업 기반으로 처리되는 것이 더효율적인 경우
    - 주로 Tasklet 구현체를 만들어 사용
    - 대량 처리를 하는 경우 chunk 기반에 비해 더 복잡한 구현 필요

![image](https://user-images.githubusercontent.com/40031858/159492019-b23a9a31-1161-4150-896e-724addbc27ab.png)

![image](https://user-images.githubusercontent.com/40031858/159492081-8a0e7a7f-b3e8-495d-8548-0e3f902f1f9d.png)

![image](https://user-images.githubusercontent.com/40031858/159492164-170cf6ef-77cf-42ed-a4a3-49120bc97d60.png)

## TaskletStep - tasklet()
### 1. 기본개념
- Tasklet 타입의 클래스를 설정한다
  - `Tasklet`
    - Step 내에서 구성되고 실행되는 도메인 객체로서 주로 단일 태스크를 수행하기위한 것
    - TaskletStep에 의해 반복적으로 수행되며 반환값에 따라 계속 수행 혹은 종료한다
    - RepeatStatus - Tasklet의 반복 여부 상태 값
      - `RepeatStatus.FINISHED` - Tasklet 종료, RepeatStatus을 null로 반환하면 RepeatStatus.FINISHED로 해석
      - `RepeatStatus.CONTINUABLE` - Tasklet 반복
      - RepeatStatus.FINISHED가 리턴되거나 실패 예외가 던져지기 전까지 TaskletStep에 의해 while문 안에서 반복적 호출됨(무한루프 주의)
- 익명 클래스 혹은 구현 클래스를 만들어서 사용한다
- 이 메소드를 실행하게 되면 TaskletStepBuilder가 반환되어 관련 API를 설정할 수 있다.
- Step에 오직 하나의 Tasklet 설정이 가능하며 두개 이상을 설정 했을 경우 마지막에 설정한 객체가 실행된다
  
### 2. 구조
![image](https://user-images.githubusercontent.com/40031858/159695029-311c9ab7-c7a3-41de-89af-6b7a13a1b74a.png)



![image](https://user-images.githubusercontent.com/40031858/159695097-825d3d03-c69f-471e-8a92-eff9cb3def2f.png)

## TaskletStep - startLimit()
### 1. 기본 개념
- Step의 실행 횟수를 조정할 수 있다
- Step 마다 설정할 수 있다
- 설정 값을 초과해서 다시 실행하려고 하면 StartLimitExceededException이 발생
- start-limit의 디폴트 값은 Integer.MAX_VALUE

### 2. API
```java
public Step batchStep(){
  return stepBuilderFactory.get("batchStep")
  .tasklet(Tasklet)
  .startLimit(10) // 
  .allowStartIfComplete(true)
  .listener(StepExecutionListener)
  .build();
}

```

## TaskletStep - allowStartIfComplete()
### 1. 기본 개념
- 재시작 가능한 job에서 Step의 이전 성공 여부와 상관없이 항상 step을 실행하기 위한 설정
- 실행 마다 유효성을 검증하는 Step이나 사전 작업이 꼭 필요한 Step 등
- 기본적으로 COMPLETED 상태를 가진 Step은 Job 재 시작시 실행하지 않고 스킵한다
- allow-start-if-complete 가 "true"로 설정된 step은 항상 실행한다

### 2. 흐름도
![image](https://user-images.githubusercontent.com/40031858/159697916-2e27a562-8bf7-4c59-9d64-08125edcd689.png)

![image](https://user-images.githubusercontent.com/40031858/159698006-c65f52b7-d424-4bcf-a673-81795daedcef.png)

## TaskletStep 아키텍처
![image](https://user-images.githubusercontent.com/40031858/159850751-9cb9a936-de8d-4512-9317-aaff85c947cc.png)


![image](https://user-images.githubusercontent.com/40031858/159850812-a9b9e0ac-b21f-43a1-a2e3-f240fb235d84.png)

![image](https://user-images.githubusercontent.com/40031858/159850858-496ea68b-877a-4294-bfbc-0145ee7b904f.png)

## JobStep
### 1. 기본 개념
- Job에 속하는 Step중 외부의 Job을 포함하고 있는 Step
- 외부의 Job이 실패하면 해당 Step이 실패하므로 결국 최종 기본 Job도 실패한다
- 모든 메타데이터는 기본 Job과 외부 Job별로 각각 저장된다
- 커다란 시스템을 작은 모듈로 쪼개고 job의 흐름을 관리하고자 할 때 사용할수 있다

### 2. API 소개
    StepBuilderFactory > StepBuilder > JobStepBuilder > JobStep

```java
public Step jobStep(){
  return stepBuilderFactory.get("jobStep") //StepBuilder를 생성하는 팩토리, Step의 이름을 매개변수로 받음
        .job(Job) // JobStep 내에서 실행 될 Job설정, JobStepBuilder 반환
        .launcher(JobLauncher) // Job을 실행할 JobLauncher 설정
        .parametersExtractor(JobParametersExtractor) // Step의 ExecutionContext를 Job이 실행되는 데 필요한 JobParameters로 변환
        .build(); //JobStep을 생성 
}
```

![image](https://user-images.githubusercontent.com/40031858/159933815-37648ae0-785a-4d83-b937-f02bde6bd1d8.png)

![image](https://user-images.githubusercontent.com/40031858/159933921-06efbcba-dcf1-46ec-b8d4-457ac95791cf.png)

![image](https://user-images.githubusercontent.com/40031858/159934006-82ef9c39-b61f-4a27-9267-68adbebd06e6.png)


# FlowJob
## 1. 개념 및 API 소개
### 1. 기본개념
- Step을 순차적으로만 구성하는 것이 아닌 특정한 상태에 따라 흐름을 전환하도록 구성할 수 있으며 FlowJobBuilder에 의해 생성
  - Step이 실패하더라도 Job은 실패로 끝나지 않도록 해야 하는 경우
  - Step이 성공 했을 때 다음에 실행해야 할 Step을 구분해서 실행 해야 하는 경우
  - 특정 Step은 전혀 실행되지 않게 구성해야하는 경우
- Flow와 Job의 흐름을 구성하는데만 관여하고 실제 비즈니스 로직은 Step에서 이뤄진다
- 내부적으로 SimpleFlow객체를 포함하고 있으며 Job 실행 시 호출한다

### 2. SimpleJob vs FLowJob
![image](https://user-images.githubusercontent.com/40031858/160034539-afd51865-5b3c-43b5-a9aa-0b5e9077ffac.png)

    JobBuilderFactory > JobBuilder > JobFlowBuilder > FlowBuilder > FlowJob
```java
public Job batchJob(){
  return jobBuilderFactory.get("batchJob")
        .start(Step) // Flow시작하는 Step 설정
        .on(String pattern) // Step의 실행 결과로 돌려받는 종료 상태(ExitStatus)를 캐치하여 매칭하는 패턴, TransitionBuilder 반환
        .to(Step) //다음으로 이동할 Step지정
        .stop()/fail()/end()/stopAndRestart() // Flow를 중지 / 실패 / 종료하도록 Flow종료
        .from(Step) // 이전 단계에서 정의한 Step의 Flow를 추가적 정의
        .next(Step) // 다음으로 이동할 Step 지정
        .end() // build()앞에 위치하면 FlowBuilder를 종료하고 SimpleFlow 객체 생성
        .build(); // FlowJob 생성하고 flow 필드에 SimpleFlow wjwkd
}

// Flow: 흐름을 정의하는 역할
// Transition: 조건에 따라 흐름을 전환시키는 역할
```

![image](https://user-images.githubusercontent.com/40031858/160034889-493f9217-7256-4dbe-a88c-4344288a7855.png)

![image](https://user-images.githubusercontent.com/40031858/160034907-250e2a2f-7ef1-45c3-8d0b-aea26c258ccb.png)

## FlowJob - start() / next()
```java
public Job batchJob(){
  return jobBuilderFactory.get("batchJob")
        .start(Flow) //처음 실행 할 Flow 설정, JobFlowBuilder가 반환된다. 여기에 Step이 인자로 오게되면 SimpleJobBuilder가 반환
        .next(step or Flow or JobExecutionDecider)
        .on(String pattern)\
        .to(Step)
        .stop() / fail() / end() / stopAndRestart()
        .end()
        .build();
}
```

![image](https://user-images.githubusercontent.com/40031858/160071288-96c324db-84b5-4c43-bf27-c29b4fc12479.png)


## Transition

### 배치 상태 유형
- `BatchStatus`
  - JobExecution과 StepExecution의 속성으로 Job과 Step의 종료 후 최종 결과 상태가 무엇인지 정의
  - `SimpleJob`
    - 마지막 Step의 BatchStatus값을 Job의 최종 BatchStatus값으로 반영
    - Step이 실패할 경우 해당 Step이 마지막 Step이 된다
  - `FlowJob`
    - Flow내 Step의 ExitStatus값을 FlowExecutionStatus 값으로 저장
    - 마지막 Flow의 FlowExecutionStatus값을 Job의 최종 BatchStatus값으로 반영
- COMPLETED, STARTING, STARTED, STOPPING, STOPPED, FAILED, ABANDONED, UNKNOWN
- ABANDONED는 처리를 완료 했지만 성공하지 못한 단계와 재시작시 건너 뛰어야하는 단계

![image](https://user-images.githubusercontent.com/40031858/160074945-461989e5-0dfc-4023-adf4-775cf22519f0.png)

- `ExitStatus`
  - JobExecution과 StepExecution의 속성으로 Job과 Step의 실행 후 어떤 상태로 종료되었는지 정의
  - 기본적으로 ExitStatus는 BatchStatus와 동일한 값으로 설정된다
  - `SimpleJob`
    - 마지막 Step의 ExitStatus 값을 Job의 최종 ExitStatus값으로 반영
  - `FlowJob`
    - Flow 내 Step의 ExitStatus값을 FlowExecutionStatus 값으로 저장
    - 마지막 Flow의 FlowExecutionStatus 값을 Job의 최종 ExitStatus 값으로 반영
- UNKOWN, EXECUTING, COMPLETED, NOOP, FAILED , STOPPED
- exitCode 속성으로 참조


![image](https://user-images.githubusercontent.com/40031858/160075347-386a2eeb-7284-40ec-925b-91e9b40e18c3.png)

- `FlowExecutionStatus`
  - FlowExecution의 속성으로 Flow의 실행 후 최종 결과 상태가 무엇인지 정의
  - Flow내 Step이 실행되고 나서 ExitStatus값을 FlowExecutionStatus 값으로 저장
  - FlowJob의 배치 결과 상태에 관여함
  - COMPLETED, STOPPED, FAILED, UNKNOWN

![image](https://user-images.githubusercontent.com/40031858/160075513-aacf74b7-6874-4eed-8abf-07cb80a31f9e.png)

![image](https://user-images.githubusercontent.com/40031858/160075565-efb283aa-f683-4708-91bd-ddb89af96492.png)

## Transition -  on()/ to()/ stop(),fail(),end(),stopAndRestart()
```java
public Job batchJob(){
  return jobBuilderFactory.get("batchJob")
        .start(Flow)
        .next(Step or Flow or JobExecutionDecider)
        .on(String pattern) //TransitionBuilder 반환
        .to(Step or Flow or JobExecutionDecider)
        .stop()/fail()/end()/stopAndRestart(Step or Flow or JobExecutionDecider)
        .end()
        .build();
}

```
- 기본 개념
  - `Transition`
    - Flow 내 Step의 조건부 전환(전이)을 정의함
    - Job의 API 설정에서 on(String pattern) 메소드를 호출하면 TransitionBuilder가 반환되어 Transition Flow를 구성할 수 있음
    - Step의 종료상태(ExitStatus) 가 어떤 pattern과도 매칭되지 않으면 스프링 배치에서 예외를 발생하고 Job은 실패
    - transition은 구체적인 것부터 그렇지 않은 순서로 적용된다
  - `API`
    - `on(String pattern)`
      - Step의 실행 결과로 돌려받는 종료상태(ExitStatus)와 매칭하는 패턴 스키마, BatchStatus와 매칭하는 것이 아님
      - pattern과 ExitStatus와 매칭이 되면 다음으로 실행할 Step을 지정할 수 있다
      - 특수문자는 두 가지만 허용
        - "*": 0 개이상의 문자와 매칭, 모든 ExitStatus와 매칭된다
        - "?": 정확히 1개의 문자와 매칭
        - ex) "c*t"는 "cat"과 "count"에 매칭되고 ,"c?t"는 "cat"에는 매칭되지만 "count"는 X
  - `to()`
    - 다음으로 실행할 단계를 지정
  - `from()`
    - 이전 단계에서 정의한 Transition을 새롭게 추가 정의함

---
- `Job을 중단하거나 종료하는 Transition API`
  - Flow가 실행되면 FlowExecutionStatus에 상태값이 저장되고 최종적으로 Job의 BatchStatus와 ExitStatus에 반영된다
  - Step의 BatchStatus 및 ExitStatus에는 아무런 영향을 주지 않고 Job의 상태만을 변경한다
  - `stop()`
    - FlowExecutionStatus가 STOPPED 상태로 종료되는 transition
    - Job의 BatchStatus와 ExitStatus가 STOPPED으로 종료됨
  - `fail()`
    - FlowExecutionStatus가 FAILED 상태로 종료되는 transition
    - Job의 BatchStatus와 ExitStatus가 FAILED으로 종료됨
  - `end()`
    - FlowExecutionStatus가 COMPLETED 상태로 종료되는 transition
    - Job의 BatchStatus와 ExitStatus가 COMPLETED으로 종료됨
    - Step의 ExitStatus가 FAILED이더라도 Job의 BatchStatus가 COMPLETED로 종료하도록 가능하며 이 때 Job의 재시작은 불가능
  - `stopAndRestart(Step or Flow or JobExecutionDecider)`
    - stop() transition과 기본 흐름은 도일
    - 특정 step에서 작업을 중단하도록 설정하면 중단 이전의 Step만 COMPLETED 저장되고 이후의 step은 실행되지 않고 STOPPED 상태로 Job종료
    - Job이 다시 실행됐을 때 실행해야 할 step을 restart인자로 넘기면 이전에 COMPLETED로 저장된 step은 건너뛰고 중단 이후 step부터 시작 


![image](https://user-images.githubusercontent.com/40031858/160128198-ec3cdc1c-62ea-418b-80de-e7faf949dd91.png)

![image](https://user-images.githubusercontent.com/40031858/160128271-497be716-00d9-4158-b2fa-5cc9b532dc68.png)

![image](https://user-images.githubusercontent.com/40031858/160128318-6a7a8049-ba12-46ae-92e6-f7889016719a.png)
