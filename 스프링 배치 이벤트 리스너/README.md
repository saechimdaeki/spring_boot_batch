# 스프링 배치 이벤트 리스너
## 기본개념
- Listener는 배치 흐름중에 Job,Step,Chunk 단계의 실행 전후에 발생하는 이벤트를 받아용도에 맞게활용할 수 있도록 제공하는 인터셉터 개념의 클래스
- 각 단계별로 로그기록을 남기거나 소요된 시간을 계산하거나 실행상태 정보들을 참조 및 조회 할 수 있다
- 이벤트를 받기 위해서는 Listener를 등록해야 하며 등록은 API 설정에서 각 단계별로 지정할 수 있다
- `Listeners`
  - Job
    - JobExecutionListener - Job 실행 전후
  - Step
    - StepExecutionListener - Step 실행 전후
    - ChunkListener - Chunk 실행 전후(Tasklet 실행 전후), 오류 시점
    - ItemReadListener - ItemReader 실행 전후, 오류 시점, item이 null일 경우 호출 안됨
    - ItemProcessListener - ItemProcessor 실행 전후, 오류 시점, item이 null일 경우 호출 안됨
    - ItemWriterListener - ItemWriter 실행 전후, 오류 시점, item이 null일 경우 호출 안됨
  - SkipListener - 읽기 , 쓰기, 처리 Skip 실행 시점, Item 처리가 Skip될 경우 Skip 된 item을 추적함
  - RetryListener - Retry 시작, 종료, 에러 시점

- 구현 방법


![image](https://user-images.githubusercontent.com/40031858/161914232-8e7a4050-50d5-47cf-a71e-c4e8618da4c3.png)


![image](https://user-images.githubusercontent.com/40031858/161914299-42a4db12-6638-4adf-908b-ba4c9ed6aaef.png)


## JobExecutionListener / StepExecutionListener

![image](https://user-images.githubusercontent.com/40031858/161918347-b4136b38-3208-458d-bae2-bc4ac2685a95.png)


## ChunkListener / ItemReadListener /ItemProcessListener /ItemWriteListener

![image](https://user-images.githubusercontent.com/40031858/161922656-93fba87d-abd6-4476-9fba-0f62da8a98b3.png)

![image](https://user-images.githubusercontent.com/40031858/161922711-6a34120a-c595-4203-b3ea-dd3fc693ac5f.png)