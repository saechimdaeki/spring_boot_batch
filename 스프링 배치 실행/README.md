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
