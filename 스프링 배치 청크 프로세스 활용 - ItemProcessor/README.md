# 스프링 배치 청크 프로세스 활용 - ItemProcessor
## CompositeItemProcessor
- 기본 개념
  - ItemProcessor 들을 연결(Chaining)해서 위임하면 각 ItemProcessor를 실행시킨다
  - 이전 ItemProcessor 반환 값은 다음 ItemProcessor값으로 연결된다
- API

```java
public ItemProcessor itemProcessor(){
  return new CompositeItemProcessorBuilder<>()
      .delegates(ItemProcessor<?,?>...delegates) //체이닝 할 ItemProcessor객체 설정
      .build();
}
```

![image](https://user-images.githubusercontent.com/40031858/160854469-9587ece6-4400-4b03-abe7-0f673f2d73ca.png)

![image](https://user-images.githubusercontent.com/40031858/160854608-92150a4f-1fdc-4ccd-90d0-d3447601b3e4.png)

## ClassifierCompositeItemprocessor
- 기본 개념
  - Classifier로 라우팅 패턴을 구현해서 ItemProcessor 구현체 중에서 하나를 호출하는 역할을 한다
- API
```java
public ItemProcessor itemProcessor(){
  return new ClassifierCompositeItemProcessorBuilder<>()
    .classifier(Classifier) //분류자 설정
    .build();
}
```
![image](https://user-images.githubusercontent.com/40031858/160856859-8215402a-38e9-4f07-ba00-c916e1948b3f.png)

![image](https://user-images.githubusercontent.com/40031858/160856924-ad3dcdf5-c90d-47f8-a6d7-9504f0479f4b.png)