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

