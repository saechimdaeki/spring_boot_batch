# 스프링 배치 멀티 스레드 프로세싱
## 기본 개념
- 단일스레드 vs 멀티 스레드
  - 프로세스 내 특정 작업을 처리하는 스레드가 하나일 경우 단일 스레드, 여러 개 일 경우 멀티 스레드로 정의 할 수 있다
  - 작업 처리에 있어서 단일스레드와 멀티 스레드의 선택 기준은 어떤 방식이 자원을 효율적으로 사용하고 성능처리에 유리한가 하는점이다
  - 일반적으로 복잡한 처리나 대용량 데이털르 다루는 작업일 경우 전체 소요시간 및 성능상의 이점을 가져오기 위해 멀티스레드 방식을 선택한다
  - 멀티 스레드 처리 방식은 데이터 동기화 이슈가 존재하기 때문에 최대한 고려해서 결정해야 한다

![image](https://user-images.githubusercontent.com/40031858/161078799-187a11f5-2dc0-47ea-a0d6-be5ea75cf83f.png)


![image](https://user-images.githubusercontent.com/40031858/161078886-2ad67582-8d69-44d6-a71c-61f273caaa66.png)

- 스프링 배치 스레드 모델
  - 스프링 배치는 기본적으로 단일 스레드 방식으로 작업을 처리한다
  - 성능 향상과 대규모 데이터 작업을 위한 비동기 처리 및 Scale out 기능을 제공한다
  - Local과 Remote 처리를 지원한다
#### 1. AsyncItemProcessor / AsyncItemWriter
- ItemProcessor에게 별도의 스레드가 할당되어 작업을 처리하는 방식
#### 2. Multi-threaded Step
- Step 내 Chunk구조인 ItemReader, ItemProcessor, ItemWriter마다 여러 스레드가 할당되어 실행하는 방법

#### 3. Remote Chunking
- 분산환경처럼 Step처리가 여러 프로세스로 분할되어 외부의 다른 서버로 전송되어 처리하는 방식

#### 4. Parallel Steps
- Step마다 스레드가 할당되어 여러개의 Step을 병렬로 실행하는 방법

#### 5. Partitioning
- Marster/Salve 방식으로서 Master가 데이터를 파티셔닝 한 다음 각 파티션에게 스레드를 할당하여 Slave가 독립적으로 작동하는 방식

## AsyncItemProcessor / AsyncItemWriter
- 기본개념
  - Step안에서ItemProcessor가 비동기적으로 동작하는 구조
  - AsyncItemProcessor와 AsyncItemWriter가 함께 구성이 되어야함
  - AsyncItemProcessor 로부터 AsyncItemWriter가 받는 최종 결과 값은 List< Future< T>> 타입이며 실행이 완료될 때 까지 대기한다
  - Spring-batch-integration 의존성이 필요하다
     ```xml
    <dependency>
      <groupId>org.springframework.batch</groupId>
      <artifactId>spring-batch-integartion</artifactId>
    </dependency>
     ```

![image](https://user-images.githubusercontent.com/40031858/161374445-378b0fee-19e8-43ad-83a2-c92201b5a34c.png)

![image](https://user-images.githubusercontent.com/40031858/161374469-6833d8ee-e044-4c3b-a282-79a3ddd85f97.png)

![image](https://user-images.githubusercontent.com/40031858/161374483-c1b06842-5227-4e4f-be52-0cbcf62109f3.png)

```java
public Step step() throws Exception{
  return stepBuilderFactory.get("step") //1
  .chunk(100) //2
  .reader(pagingItemReader())//3
  .processor(asyncItemProcessor()) //4
  .writer(asyncItemWriter()) //5
  .build(); //6
}
```

1. Step 기본 설정
2. 청크 개수 설정
3. ItemReader 설정- 비동기 실행 아님
4. 비동기 실행을 위한 AsyncItemProcessor설정
  - 스레드 풀 개수 만큼 스레드가 생성되어 비동기로 실행된다
  - 내부적으로 실제 ItemProcessor에게 실행을 위임하고 결과를 Future에 저장한다
5. AsyncItemWriter 설정
  - 비동기 실행 결과 값들을 모두 받아오기까지 대기함
  - 내부적으로 실제 ItemWriter에게 최종 결과값을 넘겨주고 실행을 위임한다
6. TaskletStep 생성

## Multi-threaded Step
- 기본개념
  - Step 내에서 멀티 스레드로 Chunk 기반 처리가 이루어지는 구조
  - TaskExecutorRepeatTemplate 이 반복자로 사용되며 설정한 개수(throttleLimit) 만큼의 스레드를 생성하여 수행한다


![image](https://user-images.githubusercontent.com/40031858/161408317-71a9f99d-f3a7-4226-8cc8-a781d8eb3a20.png)

![image](https://user-images.githubusercontent.com/40031858/161408335-d82cc2f8-0370-486d-9638-30041632cd95.png)

![image](https://user-images.githubusercontent.com/40031858/161408342-ae648e39-8875-44ab-8af8-9aff754f849e.png)

```java
public Step step() throws Exception{
    return stepBuilderFactory.get("step")
            .<Customer,Customer> chunk(100)
            .reader(pagingItemReader()) //1
            .processor(customerItemProcessor())
            .writer(customerItemWriter())
            .taskExecutor(taskExecutor) //2
            .build()
}
```
1. Thread-safe 한 ItemReader 설정
2. 스레드 생성 및 실행을 위한 taskExecutor 설정

## Parallel Steps
- 기본 개념
  - SplitState를 사용해서 여러 개의 Flow 들을 병렬적으로 실행하는 구조
  - 실행이 다 완료된 후 FlowExecutionStatus 결과들을 취합해서 다음 단계 결정을 한다

![image](https://user-images.githubusercontent.com/40031858/161429025-4d3651f8-6d41-438b-bc4d-f10adb805774.png)

![image](https://user-images.githubusercontent.com/40031858/161429034-8a62793e-aef1-4838-88c7-ceaf8269c086.png)

![image](https://user-images.githubusercontent.com/40031858/161429058-6c229c9b-787e-46f0-b471-f1401a3f7fb9.png)

```java
public Job job(){
  return jobBuilderFactory.get("job")
        .start(flow1()) // 1
        .split(TaskExecutor).add(flow2(),flow3()) // 2
        .next(flow4()) // 3
        .end()
        .build();
}
```

1. Flow 1을 생성한다
2. Flow2와 Flow 3을 생성하고 총 3개의 Flow를 합친다
   - taskExecutor에서 flow 개수만큼 스레드를 생성해서 각 flow를실행시킨다
3. Flow4은 split처리가 완료된 후 실행이 된다


## Partitioning
- 기본개념
  - MasterStep이 SlaveStep을 실행시키는 구조
  - SlaveStep은 각 스레드에 의해 독립적으로 실행이 됨
  - SlaveStep은 독립적인 StepExecution 파라미터 환경을 구성함
  - SlaveStep은 ItemReader/ItemProcessor/ItemWriter 등을 가지고 동작하며 독립적으로 병렬 처리한다
  - MasterStep은 PartitionStep이며 SlaveStep은 TaskletStep,FlowStep등이 올 수 있다.

![image](https://user-images.githubusercontent.com/40031858/161495616-7a7a23d8-54e8-4bfb-9b5b-930ffcc9c33e.png)

![image](https://user-images.githubusercontent.com/40031858/161495696-60b91f85-45f1-4ffa-be42-32c374b3ff11.png)

![image](https://user-images.githubusercontent.com/40031858/161495755-7e175b29-1208-45c7-998b-3137f264587e.png)

![image](https://user-images.githubusercontent.com/40031858/161495834-327a6b94-3a77-4c90-af6c-1fd8897f4057.png)

![image](https://user-images.githubusercontent.com/40031858/161495890-bc0ed54f-841a-47f8-8793-9c7bf46e7e57.png)

```java
public Step step() throws Exception{
    return stepBuilderFactory.get("masterStep") //1
          .partitioner("slaveStep", new ColumnRangePartitioner()) //2
          .step(slaveStep()) //3
          .gridSize(4) // 4
          .taskExecutor(ThreadPoolTaskExecutor()) //5
          .build(); //6
}
```
1. Step 기본 설정
2. PartitionStep 생성을 위한 PartitionStepBuilder가 생성되고 Partitioner를 설정
3. 슬레이브 역할을하는 Step을 설정: TaskletStep, FlowStep등이 올 수 있음
4. 파티션 구분을 위한 값 설정 : 몇 개의 파티션으로 나눌 것인지 사용됨
5. 스레드 풀 실행자 설정: 스레드 생성, 스레드 풀 관리
6. PartitionStep 생성: MasterStep의 역할 담당

## SynchronizedItemStreamReader
- 기본 개념
  - Thread-safe 하지 않은 ItemReader를 Thread-safe하게 처리하도록 하는 역할을한다
  - Spring Batch 4.0부터 지원한다

![image](https://user-images.githubusercontent.com/40031858/161908800-647e9907-4bea-4649-b57a-2548ee93598f.png)

![image](https://user-images.githubusercontent.com/40031858/161908861-93fb697d-c235-4534-b709-48a80c896df7.png)