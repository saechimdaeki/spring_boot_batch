# 스프링 배치 청크 프로세스 활용 - ItemWriter
## Flat Files - FlatFileItemWriter
### 개념 및 API
- 기본 개념
  - 2차원 데이터(표)로 표현된 유형의 파일을 처리하는 ItemWriter
  - 고정 위치로 정의된 데이터 필드나 특수 문자에 의해 구별된 데이터의 행을 기록한다
  - Resource와 LineAggregator 두 가지가 요소가 필요하다
- 구조
![image](https://user-images.githubusercontent.com/40031858/160731486-34a218e1-6d9f-45fa-9b82-36fd0a36ec79.png)

- LineAggregator
  - Item을 받아서 String으로 변환하여 리턴한다
  - FieldExtractor를 사용해서 처리할 수 있다
  - 구현체
    - PassThroughLineAggragator, DelimitedLineAggregator, FormatterLineAggregator
- FieldExtractor
  - 전달 받은 Item 객체의 필드를 배열로 만들고 배열을 합쳐서 문자열을 만들도록 구현하도록 제공하는 인터페이스
  - 구현체
    - BeanWrapperFieldExtractor, PassThroughFieldExtractor

![image](https://user-images.githubusercontent.com/40031858/160731624-69aa461d-7b50-49ea-a887-f681bc4c3b4e.png)

![image](https://user-images.githubusercontent.com/40031858/160731647-eeb1f7da-d695-4e80-993e-6b6912abc632.png)

```java
public FlatFileItemWriter itemWriter(){
  return new FlatFileItemWriterBuilder<T>()
    .name(String name)
    .resource(Resource) // 쓰기할 리소스 설정
    .lineAggregator(LineAggregator<T>) //객체를 String으로 변환하는 LineAggregator객체 설정
    .append(boolean) //존재하는 파일에 내용을 추가할 것인지 여부 설정
    .fieldExtractor(FieldExtractor<T>) //객체 필드를 추출해서 배열로 만드는 FieldExtractor 설정
    .headerCallback(FlatFileHeaderCallback) // 헤더를 파일에 쓰기 위한 콜백 인터페이스
    .footerCallback(FlatFileFooterCallback) //푸터를 파일에 쓰기위한 콜백 인터페이스
    .shouldDeleteIfExists(boolean) //파일이 이미 존재한다면 삭제
    .shouldDeleteIfEmpty(boolean) //파일의 내용이 비어 있다면 삭제
    .delimited().delimiter(String delimiter) //파일의 구분자를 기준으로 파일을 작성하도록 설정
    .formatted().format(String format) //파일의 고정길이를 기준으로 파일을 작성하도록 설정
    .build();
}
```
### FlatFileItemWriter - DelimtedLineAggregator
- 기본 개념
  - 객체의 필드 사이에 구분자를 삽입해서 한 문자열로 반환한다
- 구조
![image](https://user-images.githubusercontent.com/40031858/160732600-d4f354d6-6ddd-4180-ab95-bb7a40ae26c9.png)

![image](https://user-images.githubusercontent.com/40031858/160732632-8d5b9571-e527-4177-851b-09ff5134a73f.png)

### FlatFileItemWriter - FormatterLineAggregator
- 기본 개념
  - 객체의 필드를 사용자가 설정한 Formatter 구문을 통해 문자열로 변환한다
- 구조
![image](https://user-images.githubusercontent.com/40031858/160732732-c6d8feec-96c7-495c-8661-b731f43f59d1.png)

![image](https://user-images.githubusercontent.com/40031858/160732754-5d2d0678-d9a1-431a-ad37-d5612278d7c7.png)

### XML - StaxEventItemWriter
- 기본 개념
  - XML 쓰는 과정은 읽기 과정에 대칭적이다.
  - StaxEventItemWriter는 Resource, marshaller, rootTagName가 필요하다
- API
```java
public StaxEventItemWriter itemWriter(){
  return new StaxEventItemWriterBuilder<T>()
    .name(String name)
    .resource(Resource) //쓰기할 리소스 설정
    .rootTagName() //조각단위의 루트가 될 이름 설정
    .overwriteOutput(boolean) //파일이 존재하면 덮어 쓸것인지 설정
    .marshaller(Marshaller) //Marshaller객체 설정
    .headerCallback() //헤더를 파일에 쓰기위한 콜백 인터페이스
    .footerCallback() //푸터를 파일에 쓰기위한 콜백 인터페이스
    .build();
}
```
![image](https://user-images.githubusercontent.com/40031858/160735803-e0dc44b8-9046-464c-8baf-4d29ddd1f670.png)

![image](https://user-images.githubusercontent.com/40031858/160735835-4f7bd488-e058-4278-b870-d31f04773b14.png)

### Json - JsonFIleItemWriter
- 기본 개념
  - 객체를 받아 JSON String으로 변환하는 역할을 한다
- API
```java
public JsonFileItemWriterBuilder itemWriter(){
  return JsonFileItemWriterBuilder<T>()
    .name(String name)
    .resource(Resource) //쓰기할 리소스 설정
    .append(boolean) //존재하는 파일에 내용을 추가할 것인지 여부 설정
    .jsonObjectMarshaller(JsonObjectMarshaller) //JsonObjectMarshaller 객체 설정
    .headerCallback(FlatFileHeaderCallback) //헤더를 파일에 쓰기위한 콜백 인터페이스
    .footerCallback(FlatFileFooterCallback) //푸터를 파일에 쓰기위한 콜백 인터페이스
    .shouldDeleteIfExists(boolean) //파일이 이미 존재한다면 삭제
    .shouldDeleteIfEmpty(boolean) //파일의 내용이 비어있다면 삭제
    .build();

}
```

![image](https://user-images.githubusercontent.com/40031858/160747674-bf6fa68a-54a1-44f5-931f-22c1fd9d2672.png)

---
# DB
## JdbcBatchItemWriter
- 기본 개념
  - JdbcCursorItemReader 설정과 마찬가지로 datasource를 지정하고, sql속성에 실행할 쿼리를 설정
  - JDBC의 Batch 기능을 사용하여 bulk insert/update/delete 방식으로 처리
  - 단건 처리가 아닌 일괄처리이기 때문에 성능에 이점을 가진다
- API
```java
public JdbcBatchItemWriter itemWriter(){
  return new JdbcBatchItemWriterBuilder<T>()
      .datasource(Datasource) //DB에 접근하기 위해 Datasource 설정
      .sql(String sql) //itemWriter가 사용할 쿼리 문장 설정
      .assertUpdates(boolean) //트랜잭션 이후 적어도 하나의 항목이 행을 업데이트 혹은 삭제하지 않을 경우 예외발생여부를 설정함, 기본값은 true
      .beanMapped() //Pojo기반으로 Insert SQL의 Values를 매핑
      .columnMapped() //Key,Value 기반으로 Insert SQL의 Values를 매핑
      .build();
}
```
![image](https://user-images.githubusercontent.com/40031858/160751255-4a76cf95-b4ef-406b-83ef-b3c07b0f8350.png)

![image](https://user-images.githubusercontent.com/40031858/160751286-c0b8a46b-5290-4dff-964f-259437f850c0.png)

## JpaItemWriter
- 기본 개념
  - JPA Entity 기반으로 데이터를 처리하며 EntityManagerFactory를 주입받아 사용한다
  - Entity를 하나씩 chunk 크기 만큼 insert혹은 merge한다음 flush한다
  - ItemReader나 ItemProcessor로 부터 아이템을 전달 받을 때는 Entity클래스 타입으로 받아야한다
- API
```java
public JpaItemWriter itemWriter(){
  return new JpaItemWriterBuilder<T>()
      .userPersist(boolean) //Entity를 persist()할것인지 여부 설정, false면 merge()처리
      .entityManagerFactory(EntityManagerFactory) //EntityManagerFactory 설정
      .build();
}
```

![image](https://user-images.githubusercontent.com/40031858/160753583-d77bd12d-a49f-4df2-950f-aebdf5659330.png)

## ItemWriterAdapter
- 기본 개념
  - 배치 Job 안에서 이미 있는 DAO나 다른 서비스를 ItemWriter안에서 사용하고자 할 때 위임 역할을 한다.

![image](https://user-images.githubusercontent.com/40031858/160755215-0840acd8-f826-4cbf-9a84-351701a87f63.png)
