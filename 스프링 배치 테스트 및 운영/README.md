# 스프링 배치 테스트 및 운영
## Spring Batch Test
- 스프링 배치 4.1.x 이상 버전(부트 2.1) 기준
- pom.xml
```xml
<dependency>
  <groupId>org.springframework.batch</groupId>
  <artifactId>spring-batch-test</artifactId>
</dependency>
```
- @SpringBatchTest
  - 자동으로 ApplicationContext에 테스트에 필요한 여러 유틸 Bean을 등록해주는 어노테이션
    - `JobLauncherTestUitls`
      - launchJob(),launchStep()과 같은 스프링 배치 테스트에 필요한 유틸성 메소드 지원
    - `JobRepositoryTestUtils`
      - JobRepository를 사용해서 JobExecution을 생성 및 삭제 기능 메소드 지원
    - `StepScopeTestExecutionListener`
      - @StepScope컨텍스트를 생성해 주며 해당 컨텍스트를 통해 JobParameter등을 단위테스트에서 DI받을수 있음
    - `JobScopeTestExecutionListener`
      - @JobScope 컨텍스트를 생성해 주며 해당 컨텍스트를 통해 JobParameter 등을 단위테스트에서 DI받을 수 있다


![image](https://user-images.githubusercontent.com/40031858/162188269-b4bd28a2-09e3-4c81-b38f-d6a986006442.png)

```java
@ExtendWith(SpringExtension.class)
@SpringBatchTest
@SpringBootTest(classes={BatchJobConfiguration.class,TestBatchConfig.class})
public class BatchJobConfigurationTest{
  ...
}
```
- @SpringBatchTest – JobLauncherTestUtils, JobRepositoryTestUtils 등을 제공하는 어노테이션
- @SpringBootTest(classes={…}) – Job 설정 클래스 지정, 통합 테스트를 위한 여러 의존성 빈들을 주입 받기 위한 어노테이션

```java
@Configuration
@EnableAutoConfiguration
@EnableBatchProcessing
public class TestBatchConfig {}
```
- @EnableBatchProcessing - 테스트 시 배치환경 및 설정 초기화를 자동 구동하기 위한 어노테이션
- 테스트 클래스마다 선언하지 않고 공통으로 사용하기 위함

## JobExplorer / JobRegistry / JobOperator
- `JobExplorer`
  - JobRepository의 readonly 버전
  - 실행 중인 Job의 실행 정보인 JobExecution 또는 Step의 실행 정보인 StepExecution을 조회할 수 있다
- `JobRegistry`
  - 생성된 Job을 자동으로 등록, 추적 및 관리하며 여러 곳에서 job을 생성한 경우 ApplicationContext에서 job을 수집해서 사용할 수 있다
  - 기본 구현체로 map기반의 MapJobRegistry클래스를 제공한다
    - jobName을 Key로 하고 job을 값으로 하여 매핑한다
  - job 등록
    - JobRegistryBeanPostProcessor - BeanPostProcessor 단계에서 bean초기화 시 자동으로 JobRegistry에 Job을 등록시켜준다
- `JobOperator`
  - JobExplorer,JobRepository,JobRegistry,JobLauncher 를 포함하고 있으며 배치의 중단, 재시작, job요약 등의 모니터링이 가능하다
  - 기본 구현체로 SimpleJobOperator 클래스를 제공한다

![image](https://user-images.githubusercontent.com/40031858/162198870-fe5b103f-372d-4c24-aa3e-7c6c34768c56.png)

![image](https://user-images.githubusercontent.com/40031858/162198932-bc73e229-fa24-48d9-bcb9-6951938297c0.png)

![image](https://user-images.githubusercontent.com/40031858/162198990-2baed92f-b135-4881-ae52-f484e7468226.png)