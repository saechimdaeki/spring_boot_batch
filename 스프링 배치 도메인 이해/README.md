# 스프링 배치 도메인 이해

## 1. Job

## 2. Step

## 3. ExecutionContext

## 4. JobRepository / JobLauncher

---

## Job

### 1. 기본 개념

- 배치 계층 구조에서 가장 상위에 있는 개념으로서 하나의 배치 작업 자체를 의미함
    - "API 서버의 접속 로그 데이터를 통계 서버로 옮기는 배치" 인 Job 자체를 의미한다
- Job Configuration을 통해 생성되는 객체 단위로서 배치작업을 어떻게 구성하고 실행할 것인지 전체적으로 설정하고 명세해 놓은 객체
- 배치 Job을 구성하기 위한 최상위 인터페이스이며 스프링 배치가 기본 구현체를 제공한다
- 여러 Step을 포함하고 있는 컨테이너로서 반드시 한개 이상의 Step으로 구성해야함

## 2. 기본 구현체

- `SimpleJob`
    - 순차적으로 Step을 실행시키는 Job
    - 모든 Job에서 유용하게 사용할 수 있는 표준 기능을 갖고 있음
- `FlowJob`
    - 특정한 조건과 흐름에 따라 Step을 구성하여 실행시키는 Job
    - Flow객체를 실행시켜서 작업을 진행함

![image](https://user-images.githubusercontent.com/40031858/141676514-67a29f3f-4514-4adf-b1b6-5b6ed7e546a1.png)

---



## JobInstance

### 1. 기본 개념

- Job 이 실행될 때 생성되는 Job 의 논리적 실행 단위 객체로서 고유하게 식별 가능한 작업 실행을 나타냄
- Job 의 설정과 구성은 동일하지만 Job 이 실행되는 시점에 처리하는 내용은 다르기 때문에 Job 의 실행을 구분해야 함
  - 예를 들어 하루에 한 번 씩 배치 Job이 실행된다면 매일 실행되는 각각의 Job 을 JobInstance 로 표현합니다.
- JobInstance 생성 및 실행
  - 처음 시작하는 Job + JobParameter 일 경우 새로운 JobInstance 생성
  - 이전과 동일한 Job + JobParameter 으로 실행 할 경우 이미 존재하는 JobInstance 리턴
    - 내부적으로 JobName + jobKey (jobParametes 의 해시값) 를 가지고 JobInstance 객체를 얻음
- Job 과는 1:M 관계

### 2. BATCH_JOB_INSTANCE 테이블과 매핑

- JOB_NAME (Job) 과 JOB_KEY (JobParameter 해시값) 가 동일한 데이터는 중복해서 저장할 수 없음

![image](https://user-images.githubusercontent.com/40031858/142878991-96f724e9-ecce-4c75-aa50-e8e636ca0043.png)

![image](https://user-images.githubusercontent.com/40031858/142879105-ea2f1858-0674-4b80-a506-0f4474f185d7.png)

---

## JobParameter

### 1. 기본 개념

- #### Job을 실행할 때 함께 포함되어 사용되는 파라미터를 가진 도메인 객체

- #### 하나의 Job에 존재할 수 있는 여러개의 JobInstance를 구분하기 위한 용도

- #### JobParameters와 JobInstance는 1:1 관계

### 2. 생성 및 바인딩

- #### 어플리케이션 실행 시 주입

  - #### Java -jar LogBatch.jar requestDate=20210101

- ### 코드로 생성

  - #### JobParameterBuilder, DefaultJobParametersConverter

- ### SpEL 이용

  - #### @Value("#{jobParameter[requestDate]}"), @JobScope, @StepScope 선언 필수

### 3. BATCH_JOB_EXECUTION_PARAM 테이블과 매핑

- #### JOB_EXECUTION 과 1:M의 관계

![image](https://user-images.githubusercontent.com/40031858/143023257-c2144e56-5295-4802-af97-d85d3cfc17a1.png)

---

## JobExecution

### 1. 기본 개념

- #### JobInstance에 대한 한번의 시도를 의미하는 객체로서 Job 실행 중에 발생한 정보들을 저장하고 있는 객체

  - #### 시작 시간, 종료 시간, 상태(시작됨, 완료, 실패), 종료상태의 속성을 가짐

- #### JobInstance와의 관계

  - #### JobExecution은 'FAILED' 또는 'COMPLETED' 등의 Job의 실행 결과 상태를 가지고 있음

  - #### JobExecution의 실행 상태 결과가 'COMPLETED' 면 JobInstance 실행이 완료된 것으로 간주해 재실행 불가

  - #### JobExecution의 실행 상태 결과가 'FAILED' 면 JobInstance 실행이 완료되지 않은것으로 간주해 재실행이 가능

    - #### JobParameter가 동일한 값으로 Job을 실행할지라도 JobInstance를 계속 실행할 수 있음

  - #### JobExecution의 실행 상태 결과가 'COMPLETED' 될 때까지 하나의 JobInstance 내에서 여러 번의 시도가 생길수있음

### 2. BATCH_JOB_EXECUTION 테이블과 매핑

- #### JobInstance와 JobExecution는 1:N 관계로서 JobInstance에 대한 성공/실패의 내역을 가지고 있음

![image](https://user-images.githubusercontent.com/40031858/143030908-94d9f334-f7d0-423e-bd70-9e41a0c4dabf.png)

![image-20211123221742323](/Users/kimjunseong/Library/Application Support/typora-user-images/image-20211123221742323.png)

![image-20211123221758115](/Users/kimjunseong/Library/Application Support/typora-user-images/image-20211123221758115.png)

---

## Step

### 1. 기본 개념

- #### Batch Job을 구성하는 독립적인 하나의 단계로서 실제 배치 처리를 정의하고 컨트롤하는데 필요한 모든 정보를 가지고 있는 도메인 객체

- #### 단순한 단일 태스크 뿐 아니라 입력과 처리 그리고 출력과 관련된 복잡한 비즈니스 로직을 포함하는 모든 설정들을 담고 있다

- #### 배치작업을 어떻게 구성하고 실행할 것인지 Job의 세부 작업을 Task기반으로 설정하고 명세해 놓은 객체

- #### 모든 Job은 하나 이상의 step으로 구성됨

### 2. 기본 구현체

- #### `TaskletStep`

  - #### 가장 기본이 되는 클래스로서 Tasklet 타입의 구현체들을 제어한다

- #### `PartitionStep`

  - #### 멀티 스레드 방식으로 Step을 여러개로 분리해서 실행한다

- #### `JobStep`

  - #### Step 내에서 Job을 실행하도록 한다

- #### `FlowStep`

  - #### Step 내에서 Flow를 실행하도록 한다

![image](https://user-images.githubusercontent.com/40031858/143253028-d9ef6d6c-52d4-4d1a-b8ee-eeafe2755e28.png)

![image](https://user-images.githubusercontent.com/40031858/143253090-204342dd-8097-414a-a755-13bd7d977820.png)

![image](https://user-images.githubusercontent.com/40031858/143253158-53b8f1fb-9137-457f-8f1b-f6d2958fc5d7.png)

---

## StepExecution

### 1. 기본 개념

- #### Step에 대한 한번의 시도를 의미하는 객체로서 Step 실행 중에 발생한 정보들을 저장하고 있는 객체

  - #### 시작시간, 종료시간, 상태(시작됨, 완료, 실패) , commit count, rollback count등의 속성을 가짐

- #### Step이 매번 시도될 때마다 생성되며 각 Step 별로 생성된다

- #### Job이 재시작하더라도 이미 성공적으로 완료된 Step은 재 실행되지 않고 실패한 Step만 실행된다

- #### 이전 단계 Step이 실패해서 현재 Step을 실행하지 않았다면 StepExecution을 생성하지 않는다.

  #### Step이 실제로 시작됐을 때만 StepExecution을 생성한다

- #### JobExecution 과의 관계

  - #### Step의 StepExecution이 모두 정상적으로 완료 되어야 JobExecution이 정상적으로 완료된다

  - #### Step의 StepExecution 중 하나라도 실패하면 JobExecution 은 실패한다

### 2. BATCH_STEP_EXECUTION 테이블과 매핑

- #### JobExecution와 StepExecution는 1:M 의 관계

- #### 하나의 Job에 여러 개의 Step으로 구성했을 경우 각 StepExecution은 하나의 JobExecution을 부모로 가진다



![image](https://user-images.githubusercontent.com/40031858/143383793-c4a281f6-655f-4b51-bf32-9cde3c521485.png)

![image](https://user-images.githubusercontent.com/40031858/143383844-69cf2cba-6432-4fed-9fd4-8e91d5048c47.png)

![image](https://user-images.githubusercontent.com/40031858/143383873-5fe742f0-ff24-4626-8b62-b61ff3f57939.png)

---

## StepContribution

### 1. 기본 개념

- #### 청크 프로세스의 변경 사항을 버퍼링 한 후 StepExecution 상태를 업데이트하는 도메인 객체

- #### 청크 커밋 직전에 StepExecution의 apply 메소드를 호출하여 상태를 업데이트 함

- #### ExitStatus의 기본 종료코드 외 사용자 정의 종료코드를 생성해서 적용할 수 있음

### 2. 구조

![image](https://user-images.githubusercontent.com/40031858/143437895-70c37666-8382-4856-8a77-28026e0a2e9b.png)

![image](https://user-images.githubusercontent.com/40031858/143437933-a35ea302-09dc-41ce-bbb6-b32ccbe8528a.png)

---

## ExecutionContext

### 1. 기본 개념

- #### 프레임워크에서 유지 및 관리하는 키/값으로 된 컬렉션으로 StepExecution 또는 JobExecution 객체의 상태(state)를 저장하는 공유 객체

- #### DB 에 직렬화 한 값으로 저장됨 - { “key” : “value”}

- #### 공유 범위

  - #### Step 범위 – 각 Step 의 StepExecution 에 저장되며 Step 간 서로 공유 안됨

  - #### Job 범위 – 각 Job의 JobExecution 에 저장되며 Job 간 서로 공유 안되며 해당 Job의 Step 간 서로 공유됨

- #### Job 재 시작시 이미 처리한 Row 데이터는 건너뛰고 이후로 수행하도록 할 때 상태 정보를 활용한다

### 2. 구조 

![image](https://user-images.githubusercontent.com/40031858/143529460-f99d8e9c-4ea1-4e0d-a79e-ef5689a994ad.png)

![image](https://user-images.githubusercontent.com/40031858/143529545-e722e2d3-2457-40e1-bb83-451b9a6e801b.png)

![image](https://user-images.githubusercontent.com/40031858/143529591-a0c05579-8e0d-4d33-b233-b4ff7a802d4b.png)

---

## JobRepository

### 1. 기본 개념

-  #### 배치 작업 중의 정보를 저장하는 저장소 역할

- #### Job이 언재 수행되었고, 언제 끝났으며, 몇 번이 실행되었고 실행에 대한 결과 등의 배치 작업의 수행과 관련된 모든 meta data를 저장함

  - #### JobLauncher, Job , Step 구현체 내부에서 CRUD 기능을 처리함

![image](https://user-images.githubusercontent.com/40031858/143668075-3347ad0a-4054-4975-938c-ddba19da66f6.png)

![image](https://user-images.githubusercontent.com/40031858/143668081-b9f41034-8785-47d1-bd45-83fe3eac2cd1.png)

- ### JobRepository 설정

  - #### @EnableBatchProcessing 애노테이션만 선언하면 JobRepository가 자동으로 빈으로 생성됨

  - #### BatchConfigurer 인터페이스를 구현하거나 BasicBatchConfigurer를 상속해서 JobRepository 설정을 커스터마이징할수있다

    - #### JDBC 방식으로 설정 - JobRepositoryFactoryBean

      - #### 내부적으로 AOP 기술을 통해 트랜잭션 처리를 해주고 있음

      - #### 트랜잭션 isolation의 기본값은 SERIALIZEBLE로 최고 수준, 다른 레벨(READ_COMMITED,REPETABLE_READ)로 지정가능

      - #### 메타티에블의 Table Prefix를 변경할 수 있음, 기본 값은 "BATCH_"임

    - #### In MeMory방식으로 설정 - MapJobRepositoryFactoryBean

      - #### 성능 등의 이유로 도메인 오브젝트를 굳이 데이터베이스에 저장하고 싶지 않을 경우

      - #### 보통 Test나 프로토타입의 빠른 개발이 필요할 때 사용

## 1. JDBC

```java
@Override
protected JobRepository createJobRepository() throws Exception {
	JobRepositoryFactoryBean factory = new JobRepositoryFactoryBean();
	factory.setDataSource(dataSource);
	factory.setTransactionManager(transactionManager);
	factory.setIsolationLevelForCreate("ISOLATION_SERIALIZABLE"); // isolation 수준, 기본값은SOLATION_SERIALIZABLE”
	factory.setTablePrefix(“SYSTEM_"); // 테이블 Prefix, 기본값은 “BATCH_”, BATCH_JOB_EXECUTION 가 	SYSTEM_JOB_EXECUTION 으로 변경됨
	factory.setMaxVarCharLength(1000); // varchar 최대 길이(기본값 2500)
	return factory.getObject(); // Proxy 객체가 생성됨 (트랜잭션 Advice 적용 등을 위해 AOP 기술 적용)
}
```

## 2. In Memory

```java
@Override
protected JobRepository createJobRepository() throws Exception {
	MapJobRepositoryFactoryBean factory = new MapJobRepositoryFactoryBean();
	factory.setTransactionManager(transactionManager); // ResourcelessTransactionManager 사용
	return factory.getObject();
}
```



---

## JobLauncher

### 1. 기본 개념

- #### 배치 Job을 실행시키는 역할을 한다.

- #### Job과 Job Parameters를 인자로 받으며 요청된 배치 작업을 수행한 후 최종 client 에게 JobExecution을 반환

- #### 스프링 부트 배치가 구동이되면 JobLauncher빈이 자동 생성됨

- #### Job tlfgod

  - #### JobLauncher.run(Job,JobParameters)

  - #### 스프링 부트 배치에서는 JobLaucnehrApplicationRunner가 자동적으로 JobLauncher를 실행시킨다

  - #### 동기적 실행

    - #### taskExecutor를 SyncTaskExecutor로 설정할 경우 (기본값은 SyncTaskExecutor)

    - #### JobExecution을 획득하고 배치 처리를 최종 완료한 이후 Client에게 JobExecution을 반환

    - #### 스케줄러에 의한 배치처리에 적합함- 배치 처리 시간이 길어도 상관없는 경우

  - #### 비 동기적 실행

    - #### taskExecutor가 SimpleAsyncTaskExecutor로 설정할 경우

    - #### JobExecution을 획득한 후 Client에게 바로 JobExecution을 반환하고 배치처리를 완료한다

    - #### HTTP 요청에 의한 배치처리에 적합함 - 배치처리 시간이 길 경우 응답이 늦어지지 않도록 함

### 2. 구조

![image](https://user-images.githubusercontent.com/40031858/143682434-384f412b-f24b-4495-842b-0de5c3c4217c.png)

![image](https://user-images.githubusercontent.com/40031858/143682441-e835fd6b-e617-4d80-8d8d-f208c4fbfea2.png)
