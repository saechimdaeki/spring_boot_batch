# 스프링 배치 시작



- 스프링 배치 활성화

  - @EnableBatchProcessing  - 스프링 배치가 작동하기 위해 선언해야 하는 애노테이션

    ```java
    @SpringBootApplication
    @EnableBatchProcessing
    public class SpringBatchApplication {
        public static void main(String[] args) {
            SpringApplication.run(SpringBatchApplication.class, args);
        }
    }
    ```

  - 총 4개의 설정 클래스를 실행시키며 스프링 배치의 모든 초기화 및 실행 구성이 이루어진다

  - 스프링 부트 배치의 자동 설정 클래스가 실행됨으로 빈으로 등록된 모든 Job을 검색해서 초기화와 동시에 Job을 수행하도록 구성됨

- 스프링 배치 초기화 설정 클래스
  1. Batch AutoConfiguration
     - 스프링 배치가 초기화 될 때 자동으로 실행되는 설정 클래스
     - Job을 수행하는 JobLauncherApplicationRunner 빈을 생성
  2. SimpleBatchConfiguration
     - JobBuilderFactory 와 StepBuilderFactory 생성
     - 스프링 배치의 주요 구성 요소 생성 - 프록시 객체로 생성됨
  3. BatchConfigurerConfiguration
     - BasicBatchConfigurer
       - SimpleBatchConfiguration 에서 생성한 프록시 객체의 실제 대상 객체를 생성하는 설정 클래스
       - 빈으로 의존성 주입 받아서 주요 객체들을 참조해서 사용할 수 있다
     - JpaBatchConfigurer
       - JPA 관련 객체를 생성하는 설정 클래스
     - 사용자 정의 BatchConfigurer 인터페이스를 구현하여 사용할 수 있음

![image](https://user-images.githubusercontent.com/40031858/138558151-b3d55904-5ac6-4c16-beb7-f327a29a8e3c.png)



---

## DB 스키마 생성 및 이해

1. 스프링 배치 메타 데이터
   - 스프링 배치의 실행 및 관리를 위한 목적으로 여러 도메인들 (Job,Step,JobParameters...) 의 정보들을 저장, 업데이트, 조회할 수 있는 스키마 제공
   - 과거, 현재의 실행에 대한 세세한 정보, 실행에 대한 성공과 실패 여부들을 관리함으로서 배치 운용에 있어 리스크 발생시 빠른 대처 가능
   - DB와 연동할 경우 필수적으로 메타테이블이 생성되어야 함
2. DB스키마 제공
   - 파일 위치 : /org/springframework/batch/core/schema-*.sql
   - DB유형 별로 제공
3. 스키마 생성 설정
   - 수동 생성 - 쿼리 복사 후 직접 실행
   - 자동 생성 - spring.batch.jdbc.initalize-schema 설정
     - ALWAYS
       - 스크립트 항상 실행
       - RDBMS 설정이 되어 있을 경우 내장 DB보다 우선적으로 실행
     - EMBEDDED: 내장 DB일 때만 실행되며 스키마가 자동 생성됨, 기본 값
     - NEVER
       - 스크립트 항상 실행 안함
       - 내장 DB일 경우 스크립트가 생성이 안되기 때문에 오류 발생
       - 운영에서 수동으로 스크립트 생성후 설정하는 것을 권장 

---

- Job 관련 테이블
  - BATCH_JOB_INSTANCE
    - Job이 실행될 때 JobInstance 정보가 저장되며 job_name과 job_key를 키로 하여 하나의 데이터가 저장
    - 동일한 job_name과 job_key로 중복 저장될 수 없다
  - BATCH_JOB_EXECUTION
    - job의 실행정보가 저장되며 Job생성, 시작, 종료 시간, 실행상태, 메세지 등을 관리
  - BATCH_JOB_EXECUTION_PARAMS
    - Job과 함께 실행되는 JobParameter 정보를 저장
  - BATCH_JOB_EXECUTION_CONTEXT
    - Job의 실행동안 여러가지 상태 정보, 공유 데이터를 직렬화(Json 형식) 해서 저장
    - Step간 서로 공유 가능함
- Step 관련 테이블
  - BATCH_STEP_EXECUTION
    - Step의 실행정보가 저장되며 생성, 시작, 종료 시간, 실행상태, 메시지 등을 관리
  - BATCH_STEP_EXECUTION_CONTEXT
    - Step의 실행동안 여러가지 상태정보, 공유 데이터를 직렬화(Json 형식) 해서 저장
    - Step별로 저장되며 Step 간 서로 공유할 수 없음

---



![image](https://user-images.githubusercontent.com/40031858/138894742-ec1dd814-e46f-4291-a28e-3f580d2b9843.png)

- BATCH_JOB_INSTANCE

  ```sql
  CREATE TABLE BATCH_JOB_INSTANCE  (
    JOB_INSTANCE_ID BIGINT  PRIMARY KEY ,
    VERSION BIGINT,
    JOB_NAME VARCHAR(100) NOT NULL ,
    JOB_KEY VARCHAR(2500)
  );
  ```

  - JOB_INSTNACE_ID: 고유하게 식별할 수 있는 기본 키
  - VERSION : 업데이트 될 때 마다 1씩 증가
  - JOB_NAME : Job을 구성할 때 부여하는 Job의 이름
  - JOB_KEY: job_name과 jobParameter를 합쳐 해싱한 값을 저장

- BATCH_JOB_EXECUTION

  ```sql
  CREATE TABLE BATCH_JOB_EXECUTION  (
    JOB_EXECUTION_ID BIGINT  PRIMARY KEY ,
    VERSION BIGINT,
    JOB_INSTANCE_ID BIGINT NOT NULL,
    CREATE_TIME TIMESTAMP NOT NULL,
    START_TIME TIMESTAMP DEFAULT NULL,
    END_TIME TIMESTAMP DEFAULT NULL,
    STATUS VARCHAR(10),
    EXIT_CODE VARCHAR(20),
    EXIT_MESSAGE VARCHAR(2500),
    LAST_UPDATED TIMESTAMP,
    JOB_CONFIGURATION_LOCATION VARCHAR(2500) NULL,
    constraint JOB_INSTANCE_EXECUTION_FK foreign key (JOB_INSTANCE_ID)
    references BATCH_JOB_INSTANCE(JOB_INSTANCE_ID)
  ) ;
  ```

  - JOB_EXECUTION_ID : JobExecution을 고유하게 식별할 수 있는 기본키, JOB_INSTANCE와 일대 다 관계
  - VERSION : 업데이트 될 때마다 1씩 증가
  - JOB_INSTANCE_ID : JOB_INSTANCE의 키 저장
  - CREATE_TIME : 실행 (Execution) 이 생성된 시점을 TimeStamp 형식으로 기록
  - START_TIME : 실행(Execution) 이 시작된 지점을 TimeStamp 형식으로 기록
  - END_TIME : 실행이 종료된 시점을 TimeStamp으로 기록하며 Job 실행 도중 오류가 발생해서 Job이 중단된 경우 값이 저장되지않을수있음
  - STATUS : 실행 상태(BatchStatus)를 저장 (COMPLETED,FAILED,STOPPED...)
  - EXIT_CODE : 실행 종료코드(ExitStatus) 를 저장(COMPLETED, FAILED...)
  - EXIT_MESSAGE: Status가 실패일 경우 실패 원인 등의 내용을 저장
  - LAST_UPDATED: 마지막 실행(Execution) 시점을 TimeStamp 형식으로 기록

- BATCH_JOB_EXECUTION_PARAMS

  ```sql
  CREATE TABLE BATCH_JOB_EXECUTION_PARAMS  (
  	JOB_EXECUTION_ID BIGINT NOT NULL ,
  	TYPE_CD VARCHAR(6) NOT NULL ,
  	KEY_NAME VARCHAR(100) NOT NULL ,
  	STRING_VAL VARCHAR(250) ,
  	DATE_VAL DATETIME DEFAULT NULL ,
  	LONG_VAL BIGINT ,
  	DOUBLE_VAL DOUBLE PRECISION ,
  	IDENTIFYING CHAR(1) NOT NULL ,
  	constraint JOB_EXEC_PARAMS_FK foreign key (JOB_EXECUTION_ID)
  	references BATCH_JOB_EXECUTION(JOB_EXECUTION_ID)
  );
  ```

  - JOB_EXECUTION_ID : JobExecution 식별 키 , Job_EXECUTION과는 일대다 관계
  - TYPE_CD : STRING,LONG,DATE,DOBLE 타입 정보
  - KEY_NAME: 파라미터 키 값
  - STRING_VAL : 파라미터 문자 값
  - DATE_VAL : 파라미터 날짜 값
  - LONG_VAL : 파라미터 LONG 값
  - DOUBLE_VAL : 파라미터 DOUBLE 값
  - IDENTIFYING: 식별여부(TRUE,FALSE)

- BATCH_JOB_EXECUTION_CONTEXT

  ```sql
  CREATE TABLE BATCH_JOB_EXECUTION_CONTEXT  (
    JOB_EXECUTION_ID BIGINT PRIMARY KEY,
    SHORT_CONTEXT VARCHAR(2500) NOT NULL,
    SERIALIZED_CONTEXT CLOB,
    constraint JOB_EXEC_CTX_FK foreign key (JOB_EXECUTION_ID)
    references BATCH_JOB_EXECUTION(JOB_EXECUTION_ID)
  ) ;
  ```

  - JOB_EXECUTION_ID : JobExecution 식별 키, JOB_EXECUTION 마다 각 생성
  - SHORT_CONTEXT : JOB의 실행 상태 정보, 공유 데이터 등의 정보를 문자열로 저장
  - SERIALIZED_CONTEXT : 직렬화 (serialized) 된 전체 컨텍스트

- BATCH_STEP_EXECUTION

  ```sql
  CREATE TABLE BATCH_STEP_EXECUTION  (
    STEP_EXECUTION_ID BIGINT  PRIMARY KEY ,
    VERSION BIGINT NOT NULL,
    STEP_NAME VARCHAR(100) NOT NULL,
    JOB_EXECUTION_ID BIGINT NOT NULL,
    START_TIME TIMESTAMP NOT NULL ,
    END_TIME TIMESTAMP DEFAULT NULL,
    STATUS VARCHAR(10),
    COMMIT_COUNT BIGINT ,
    READ_COUNT BIGINT ,
    FILTER_COUNT BIGINT ,
    WRITE_COUNT BIGINT ,
    READ_SKIP_COUNT BIGINT ,
    WRITE_SKIP_COUNT BIGINT ,
    PROCESS_SKIP_COUNT BIGINT ,
    ROLLBACK_COUNT BIGINT ,
    EXIT_CODE VARCHAR(20) ,
    EXIT_MESSAGE VARCHAR(2500) ,
    LAST_UPDATED TIMESTAMP,
    constraint JOB_EXECUTION_STEP_FK foreign key (JOB_EXECUTION_ID)
    references BATCH_JOB_EXECUTION(JOB_EXECUTION_ID)
  ) ;
  ```

  - STE_EXECUTION_ID : Step의 실행정보를 고유하게 식별할 수 있는 기본키
  - VERSION : 업데이트 될 때마다 1씩 증가
  - STEP_NAME: Step을 구성할 때 부여하는 Step 이름
  - JOB_EXECUTION_ID : JobExecution 기본 키 , JobExecution과는 일대 다 관계
  - START_TIME : 실행(Execution) 이 시작된 시점을 TimeStamp 형식으로 기록
  - END_TIME: 실행이 종료된 시점을 TimeStamp으로 기록하며 Job 실행 도중 오류가 발생해서 Job이 중단된 경우 값이 저장되지 않을수있음
  - STATUS: 실행 상태(BatchStatus)를 저장(COMPLETED, FAILED, STOPPED...)
  - COMMIT_COUNT: 트랜잭션 당 커밋되는 수를 기록
  - READ_COUNT : 실행시점에 Read한 Item수를 기록
  - FILTER_COUNT: 실행도중 필터링된 Item수를 기록
  - WRITE_COUNT: 실행도중 저장되고 커밋된 Item수를 기록
  - READ_SKIP_COUNT : 실행도중 Read가 Skip된 Item 수를 기록
  - WRITE_SKIP_COUNT: 실행도중 write가 Skip된 Item수를 기록
  - PROCESS_SKIP_COUNT: 실행도중 Process가 Skip된 Item수를 기록
  - ROLLBACK_COUNT: 실행도중 rollback이 일어난 수를 기록
  - EXIT_CODE: 실행 종료코드(ExitStatus) 를 저장(COMPLETED,FAILED...)
  - EXIT_MESSAGE : Status가 실패일 경우 실패 원인 등의 내용을 저장
  - LAST_UPDATED: 마지막 실행(Execution) 시점을 TimeStamp 형식으로 기록

- BATCH_STEP_EXECUTION_CONTEXT

  ```sql
  CREATE TABLE BATCH_STEP_EXECUTION_CONTEXT  (
    STEP_EXECUTION_ID BIGINT PRIMARY KEY,
    SHORT_CONTEXT VARCHAR(2500) NOT NULL,
    SERIALIZED_CONTEXT CLOB,
    constraint STEP_EXEC_CTX_FK foreign key (STEP_EXECUTION_ID)
    references BATCH_STEP_EXECUTION(STEP_EXECUTION_ID)
  ) ;
  ```

  - STEP_EXECUTION_ID : StepExecution 식별 키 , STEP_EXECUTION 마다 각 생성
  - SHORT_CONTEXT : STEP의 실행 상태정보, 공유데이터 등의 정보를 문자열로 저장
  - SERIALIZED_CONTEXT: 직렬화(serialized) 된 전체 컨텍스트



