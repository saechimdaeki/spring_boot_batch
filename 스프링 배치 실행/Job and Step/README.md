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
