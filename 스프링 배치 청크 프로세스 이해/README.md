# 스프링 배치 청크 프로세스 이해
## Chunk
### 기본 개념
- Chunk란 여러 개의 아이템을 묶은 하나의 덩어리, 블록을 의미
- 한번에 하나씩 아이템을 입력 받아 Chunk단위의 덩어리르 만든 후 Chunk 단위로 트랜잭션을 처리함, 즉 Chunk 단위의 Commit과 Rollback이 이루어짐
- 일반적으로 대용량 데이터를 한번에 처리하는 것이 아닌 청크 단위로 쪼개어서 더 이상 처리할 데이터가 없을 때까지 반복해서 입출력하는데 사용됨

![image](https://user-images.githubusercontent.com/40031858/160267572-99803086-99a7-45db-a93e-1915cca92732.png)

- `Chunk<I> vs Chunk<O>`
  - Chunk< I>는 ItemReader로 읽은 하나의 아이템을 Chunk에서 정한 개수만큼 반복해서 저장하는 타입
  - CHunk< O>는 ItemReader로 부터 전달받은 Chunk< I>를 참조해서 ItemProcessor에서 적절하게 가공, 필터링한 다음 ItemWriter에 전달하는 타입

![image](https://user-images.githubusercontent.com/40031858/160267609-91138da6-ee5f-49ad-8e5c-15df8a2b0cd8.png)

![image](https://user-images.githubusercontent.com/40031858/160267620-9f525f66-51a4-4407-8127-a6b6fc1876cd.png)

![image](https://user-images.githubusercontent.com/40031858/160267629-e2281bc5-7e88-44a1-8889-d6e69955b872.png)

## ChunkOrientedTasklet
### 기본 개념
- ChunkOrientedTasklet은 스프링 배치에서 제공하는 Tasklet의 구현체로서 Chunk 지향 프로세싱을 담당하는 도메인 객체
- ItemReader, ItemWriter, ItemProcessor를 사용해 Chunk기반의 데이터 입출력 처리를 담당한다
- TaskletStep에 의해서 반복적으로 실행되며 ChunkOrientedTasklet이 실행 될 때마다 매번 새로운 트랜잭션이 생성되어 처리가 이루어진다
- exception이 발생할 경우, 해당 Chunk는 롤백 되며 이전에 커밋한 Chunk는 완료된 상태가 유지된다
- 내부적으로 ItemReader를 핸들링하는 ChunkProvider와 ItemProcessor, ItemWriter를 핸들링하는 ChunkProcessor타입의 구현체를 가진다

### 구조

![image](https://user-images.githubusercontent.com/40031858/160276905-0c8f3d1f-fde0-458d-acdd-94343f52524f.png)

![image](https://user-images.githubusercontent.com/40031858/160276922-606fb375-a948-4bf5-a43a-d95250d99932.png)

![image](https://user-images.githubusercontent.com/40031858/160276939-a212bd0d-3183-4f43-81d2-8ec296f329c9.png)

    StepBuilderFactory > StepBuilder > SimplestepBuilder > TaskletStep

```java
public Step chunkStep(){
    return stepBuilderFactory.get("chunkStep")
            .<I,O>chunk(10) //chunk size설정, chunk size는 commit interval을 의미함, input,output 제네릭 타입 설정
            .<I,O>chunk(CompletionPolicy) // Chunk프로세스를 완료하기 위한 정책 설정 클래스 지정
            .reader(itemReader()) //소스로 부터 item을 읽거나 가져오는 itemReader구현체 설정
            .writer(itemWriter()) //item을 목적지에 쓰거나 보내기 위한 itemWriter 구현체 설정
            .processor(itemProcessor()) //item을 변형, 가공, 필터링 하기 위한 ItemProcessor구현체 설정
            .stream(ItemStream()) // 재시작 데이터를 관리하는 콜백에대한 스트림 등록
            .readerIsTransactionalQueue() // item이 Jms,Message Queue Server와 같은 트랜잭션 외부에서 읽혀지고 캐시할 것인지 여부, 기본값은 false
            .listener(ChunkListener) //Chunk프로세스가 진행되는 특정 시점에 콜백 제공받도록 chunkListener설정
            .build();
}
```

![image](https://user-images.githubusercontent.com/40031858/160277075-a8746e69-ba73-4f28-876e-46adccf299ba.png)
## 위 그림의 오른쪽 칠해진 부분이 SimpleStepBuilder여야함 (오타)

---
## ChunkOrientedTasklet - chunkProvider / ChunkProcessor

### ChunkProvider
- 기본 개념
  - ItemReader를 사용해서 소스로부터 아이템을 Chunk size만큼 읽어서 Chunk 단위로 만들어 제공하는 도메인 객체
  - Chunk< I>를 만들고 내부적으로 반복문을 사용해서 ItemReader.read()를 계속 호출하면서 item을 Chunk에 쌓는다
  - 외부로부터 ChunkProvider 가 호출될 때마다 항상 새로운 Chunk 가 생성된다
  - 반복문 종료 시점
    - Chunk size 만큼 item을 읽으면 반복문 종료되고 ChunkProcessor로 넘어감
    - ItemReader가 읽은 item이 null 일 경우반복문 종료 및 해당 Step 반복문까지 종료
  - 기본 구현체로서 SimpleChunkProvider와 FaultTolerantChunkProvider가 있다
- 구조
![image](https://user-images.githubusercontent.com/40031858/160285351-c84e37f1-7d18-4ad1-8396-f220442d5ce3.png)
  
![image](https://user-images.githubusercontent.com/40031858/160285373-48904842-c908-41fd-8f2f-efce688a5ecb.png)

### ChunkProcessor
- 기본 개념
  - ItemProcessor를 사용해서 Item을 변형, 가공, 필터링하고 ItemWriter를 사용해서 Chunk 데이터를 저장, 출력한다
  - Chunk< O>를 만들고 앞에서 넘어온 Chunk< I>의 item을 한 건씩 처리한 후 Chunk< O>에 저장한다
  - 외부로부터 ChunkProcessor가 호출될 때마다 항상 새로운 Chunk가 생성된다
  - ItemProcessor는 설정 시 선택사항으로서 만약 객체가 존재하지 않을 경우 ItemReader에서 읽은 item 그대로가 Chunk< O>에 저장된다
  - ItemProcessor 처리가 완료되면 Chunk< O>에 있는 List< Item>을 ItemWriter에게 전달한다
  - ItemWriter 처리가 완료되면 Chunk 트랜잭션이 종료하게 되고 Step 반복문에서 ChunkOrientedTasklet가 새롭게 실행된다
  - ItemWriter는 Chunk size만큼 데이터를 Commit 처리 하기 때문에 Chunk size는 곧 Commit Interval이 된다
  - 기본 구현체로서 SimpleChunkProcessor와 FaultTolerantChunkProcessor가 있다
- 구조
![image](https://user-images.githubusercontent.com/40031858/160285513-9770dd5a-da84-46e0-8b03-fa9b9b70754d.png)

![image](https://user-images.githubusercontent.com/40031858/160285526-4fccd7ee-d54f-4c29-8e56-ece5ce13e22c.png)
