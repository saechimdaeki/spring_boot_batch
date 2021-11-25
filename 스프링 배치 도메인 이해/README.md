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
