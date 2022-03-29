# 스프링 배치 청크 프로세스 활용 - ItemReader
## Flat Files - FlatFileItemReader
### 개념 및 API 소개
- 기본 개념
  - 2차원 데이터(표)로 표현된 유형의 파일을 처리하는 ItemReader
  - 일반적으로 고정 위치로 정의된 데이터 필드나 특수 문자에 의해 구별된 데이터의 행을 읽는다
  - Resource와 LineMapper 두 가지 요소가 필요하다
- 구조
![image](https://user-images.githubusercontent.com/40031858/160340653-887b9d3e-51eb-4645-9134-e0873bcefb61.png)

- Resource
  - FileSystemResource - new FileSystemResource("resource/path/config.xml")
  - ClassPathResource - new ClassPathResource("classpath:path/config.xml")
- LineMapper
  - 파일의 라인 한줄을 Object로 변환해서 FlatFileItemReader로 리턴한다
  - 단순히 문자열을 받기 때문에 문자열을 토큰화해서 객체로 매핑하는 과정이 필요하다
  - LineTokenizer와 FieldSetMapper를 사용해서 처리한다
  - FieldSet
    - 라인을 필드로 구분해서 만든 배열 토큰을 전달하면 토큰 필드를 참조 할 수 있도록 한다
    - JDBC의 ResultSet과 유사하다 ex)fs.readString(0),fs.readString("name")
  - LineTokenizer
    - 입력받은 라인을 FieldSet으로 변환해서 리턴한다
    - 파일마다 형식이 다르기 때문에 문자열을 FieldSet으로 변환하는 작업을 추상화시켜야 한다
  - FieldSetMapper
    - FieldSet 객체를 받아서 원하는 객체로 매핑해서 리턴한다
    - JdbcTemplate의 RowMapper와 동일한 패턴을 사용한다

![image](https://user-images.githubusercontent.com/40031858/160341526-6d434932-b0c1-40c8-a820-08bf32893d46.png)

![image](https://user-images.githubusercontent.com/40031858/160341581-9f319725-09dd-4ff9-a3b7-ccf25cbc5f5d.png)

```java
public FlatFileItemReader itemReader(){
  return new FlatFileItemReaderBuilder<T>()
    .name(String name) // 이름 설정, ExecutionContext내에서 구분하기 위한 keyfh wjwkd
    .resorce(Resource)//읽어야 할 리소스 설정
    .delimited().delimiter("|") //파일의 구분자를 기준으로 파일을 읽어들이는 설정
    .fixedLength() //파일의 고정길이를 기준으로 파일을 읽어들이는 설정
    .addColumns(Range..) // 고정길이 범위를 정하는 설정
    .names(String[] fieldNames) //LineTokenizer로 구분된 라인의 항목을 객체의 필드명과 매핑하도록 설정
    .targetType(Class class) //라인의 각 항목과 매핑할 객체 타입 설정
    .addComment(String Comment) // 무시할 라인의 코멘트 기호 설정
    .strict(boolean) //라인을 읽거나 토큰화 할 때 Parsing 예외가 발생하지 않도록 검증 생략하도록 설정
    .encoding(String encoding) // 파일 인코딩 설정
    .linesToSkip(int linesToSkip) // 파일 상단에 있는 무시할 라인 수 설정
    .saveState(boolean) //상태정보를 저장할 것인지 설정
    .setLineMapper(LineMapper) //LineMappera객체 설정
    .setFieldSetMapper(FieldSetMapper) //FieldSetMapper 객체 설정
    .setLineTokenizer(LinTokenizer) // LineTokenizer객체 설정
    .build();
}
```

### FlatFileItemReader - DelimetedLineTokenizer
- 기본 개념
  - 한 개 라인의 String을 구분자 기준으로 나누어 토큰화 하는 방식
- 구조
![image](https://user-images.githubusercontent.com/40031858/160385907-082d601a-bd42-44ba-ab37-5700e3d53b59.png)

![image](https://user-images.githubusercontent.com/40031858/160385973-31831c3a-f604-430d-9fe7-fc744ec037ad.png)

![image](https://user-images.githubusercontent.com/40031858/160386023-9e6388c8-8f58-4bcc-a981-a761c494b85e.png)

![image](https://user-images.githubusercontent.com/40031858/160386138-679cd17b-e08c-4512-997d-e69f7c476c2a.png)

### FlatFileItemReader - fixedlengthtokenizer
- 기본 개념
  - 한 개 라인의 String을 사용자가 설정한 고정길이 기준으로 나누어 토큰화 하는 방식
  - 범위는 문자열 형식으로 설정 할 수 있따
    - "1~4"또는 "1-3,4-6,7" 또는 "1-2,4-5,7-10"
    - 마지막 범위가 열려 있으면 나머지 행이 해당 열로 읽혀진다
- 구조
![image](https://user-images.githubusercontent.com/40031858/160388491-b673d8f9-a2c3-4f6f-bf1e-4f1be5002fa2.png)

![image](https://user-images.githubusercontent.com/40031858/160388564-7f8e1c3a-0a0b-40f8-addd-4901aa4ffe8a.png)

### FlatFileItemReader - Exception Handling
- 기본 개념
  - 라인을 읽거나 토큰화 할 때 발생하는 Parsing 예외를 처리할 수 있도록 예외 계층 제공
  - 토큰화 검증을 엄격하게 적용하지 않도록 설정하면 Parsing 예외가 발생하지 않도록 할 수 있다.

![image](https://user-images.githubusercontent.com/40031858/160391002-bf9c6e46-e571-45e7-a1f4-ce1739f75762.png)

- 토큰화 검증 기준 설정
```markdown
1. tokenizer.setColumns(new Range[]{new Range(1,5),new Range(6,10)})// 토큰길이:10자
2. tokenizer.setStrict(false); //토큰화 검증을 적용하지 않음
3. FieldSet tokens= tokenizer.tokenize("12345"); //라인길이:5 자
```
- LineTokenizer의 Strict 속성을 false로 설정하게 되면 Tokenizer가 라인 길이를 검증하지 않는다
- Tokenizer가 라인 길이나 컬럼명을 검증하지 않을 경우 예외가 발생하지 않는다
- FieldSet은 성공적으로 리턴이 되며 두번째 범위 값은 빈 토큰을 가지게 된다

## XML -StaxEventItemReader
### 개념 및 API 소개
- JAVA XML API
  - DOM 방식
    - 문서 전체를 메모리에 로드한 후 Tree형태로 만들어서 데이터를 처리하는 방식, pull 방식
    - 엘리멘트 제어는 유연하나 문서크기가 클 경우 메모리 사용이 많고 속도가 느림
  - SAX 방식
    - 문서의 항목을 읽을 때마다 이벤트가 발생하여 데이터를 처리하는 push 방식
    - 메모리 비용이 적고 속도가 빠른 장점은 있으나 엘리멘트 제어가 어령무
  - StAX방식(Streaming API for XML)
    - DOM과 SAX의 장점과 단점을 보완한 API모델로서 push와 pull을 동시에 제공함
    - XML문서를 읽고 쓸 수 있는 양방향 파서기 지원
    - XML파일의 항목에서 항목으로 직접 이동하면서 Stax파서기를 통해 구문 분석
    - 유형
      - Iterator API 방식
        - XMLEventReader의 nextEvent()를 호출해서 이벤트 객체를 가지고 옴
        - 이벤트 객체는 XML 태그 유형(요소, 텍스트, 주석 등)에 대한 정보를 제공함
      - Cursor API 방식
        - JDBC Resultset처럼 작동하는 API로서 XMLStreamReader는 XML문서의 다음 요소로 커서를 이동한다
        - 커서에서 직접 메소드를 호출하여 현재 이벤트에 대한 자세한 정보를 얻는다
- Spring-OXM
  - 스프링의 Object XML Mapping 기술로 XML바인딩 기술을 추상화함
    - Marshaller
      - marshall - 객체를 XML로 직렬화 하는 행위
    - Unmarchaller
      - unmarshall - XML을 객체로 역직렬화 하는 행위
    - Marshaller와 Unmarshaller 바인딩 기능을 제공하는 오픈소스로 JaxB2, Castor, XmlBeans, Xstream등이 있다
  - 스프링 배치는 특정한 XML 바인딩 기술을 강요하지 않고 Spring OXM에 위임한다
    - 바인딩 기술을 제공하는 구현체를 선택해서 처리하도록 한다
- Spring Batch XML
  - 스프링 배치에서는 StAX방식으로 XML문서를 처리하는 StaxEventItemReader를 제공한다
  - XML을 읽어 자바 객체로 매핑하고 자바 객체를 XML로 쓸 수 있는 트랜잭션 구조를 지원 

![image](https://user-images.githubusercontent.com/40031858/160511826-fc087e2a-cd88-4011-bf2f-1bf711dc949a.png)

![image](https://user-images.githubusercontent.com/40031858/160511860-76386b8f-de80-40ec-9a80-fbbf49ebbaf5.png)

- 기본 개념
  - Stax API 방식으로 데이터를 읽어들이는 ItemReader
  - Spring-OXM과 Xstream 의존성을 추가해야한다

![image](https://user-images.githubusercontent.com/40031858/160512597-38321efe-ee07-44e9-8f11-8374ac3999c3.png)

![image](https://user-images.githubusercontent.com/40031858/160512640-e984c39c-c228-4c4d-861d-17e3c51b3608.png)

![image](https://user-images.githubusercontent.com/40031858/160512682-147c0b4a-6a8f-4511-87b4-a71c3ea0a95f.png)

## Json - JsonItemReader
- 기본 개념
  - Json 데이터의 Parsing과 Binding을 JsonObjectReader 인터페이스 구현체에 위임하여 처리하는 ItemReader
  - 두가지 구현체 제공
    - JacksonJsonObjectReader
    - GsonJsonObjectReader

- 구조
![image](https://user-images.githubusercontent.com/40031858/160514937-5c85a252-b0e8-4470-af65-ec207bcc959b.png)

![image](https://user-images.githubusercontent.com/40031858/160514964-2c0b8afa-9550-4bd0-999a-5bc34c92bee3.png)


![image](https://user-images.githubusercontent.com/40031858/160515002-2bddc660-84fb-493e-8144-8d1cad3a4375.png)

---

## DB
### Cursor Based & Paging Based
- 기본 개념
  - 배치 애플리케이션은 실시간적 처리가 어려운 대용량 데이터를 다루며 이 때 DB I/O의 성능문제와 메모리 자원의 효율성 문제를 해결할 수 있어야 한다
  - 스프링 배치에서는 대용량 데이터 처리를 위한 두 가지 해결방안을 제시하고 있다
- Cursor Based 처리
  - JDBC ResultSet의 기본 메커니즘을 사용
  - 현재 행에 커설르 유지하며 다음 데이터를 호출하면 다음 행으로 커서를 이동하며 데이터 반환이 이루어지는 Streaming방식의 I/O다. 
  - ResultSet이 open 될 때마다 next() 메소드가 호출 되어 Database의 데이터가 반환되고 객체와 매핑이 이루어진다.
  - DB Connection 이 연결되면 배치 처리가 완료될 때 까지 데이터를 읽어오기 때문에 DB와 SocketTimeout을 충분히 큰 값으로 설정 필요
  - 모든 결과를 메모리에 할당하기 때문에 메모리 사용량이 많아지는 단점이 있다
  - Connection연결 유지 시간과 메모리 공간이 충분하다면 대량의 데이터 처리에 적합할 수 있따(fetchSize 조절)
- Paging Based 처리
  - 페이징 단위로 데이터를 조회하는 방식으로 Page Size만큼 한번에 메모리로 가지고 온 다음 한 개씩 읽는다.
  - 한페이지를 읽을때마다 Connection을 맺고 끊기 때문에 대량의 데이터를 처리하더라도 SocketTimeout예외가 거의 일어나지 않는다
  - 시작 행 번호를 지정하고 페이지에 반환시키고자 하는 행의 수를 지정한 후 사용 - Offset,Limit
  - 페이징 단위의 결과만 메모리에 할당하기 때문에 메모리 사용량이 적어지는 장점이 있다
  - Connection 연결 유지 시간이 길지 않고 메모리 공간을 효율적으로 사용해야 하는 데이터 처리에 적합할 수 있다.

![image](https://user-images.githubusercontent.com/40031858/160528285-5dadde9f-0573-4e7b-88df-4bc6934e7ec8.png)

### JdbcCursorItemReader
- 기본 개념
  - Cursor 기반의 JDBC 구현체로서 ResultSet과 함께 사용되며 Datasource에서 Connection을 얻어와서 SQL을 실행한다
  - Thread안정성을 보장하지 않기 때문에 멀티 스레드 환경에서 사용할 경우 동시성 이슈가 발생하지 않도록 별도 동기화 처리가 필요
- API

```java
public JdbcCursorItemReader itemReader(){
  return new JdbcCursorItemReaderBuilder<T>()
    .name("cursorItemReader")
    .fetchSize(int chunkSize) // Cursor방식으로 데이터를 가지고 올 때 한번에 메모리에 할당할 크기를 설정
    .dataSource(DataSource) //DB에 접근하기 위해 Datasource 설정
    .rowMapper(RowMapper) //쿼리 결과로 반환되는 데이터와 객체를 매핑하기 위한 RowMapper 설정
    .beanRowMapper(Class<T>) //별도의 RowMapper을 설정하지 않고 클래스 타입을 설정하면 자동으로 객체와 매핑
    .sql(String sql) //ItemReader가 조회할 때 사용할 쿼리 문장 설정
    .queryArguments(Object...args)//쿼리 파라미터 설정
    .maxItemCount(int count)//조회 할 최대 item 수
    .currentItemCount(int count)//조회 Item의 시작 지점
    .maxRows(int maxRows) // ResultSet오브젝트가 포함 할 수 있는 최대 행 수
    .build();
}
```

![image](https://user-images.githubusercontent.com/40031858/160529368-54b8cd17-b533-467d-8560-8ea8ed3fdc0c.png)

![image](https://user-images.githubusercontent.com/40031858/160529413-89acafec-e4ad-4de1-8378-82f3f4c62d42.png)

### JpaCursorItemReader
- 기본 개념
  - Spring Batch 4.3 버전부터 지원함
  - Cursor 기반의 JPA 구현체로서 EntityManagerFactory객체가 필요하며 쿼리는 JPQL을 사용
- API
```java
public JpaCursorItemReader itemReader(){
  return new JpaCursorItemReaderBuilder<T>()
    .name("cursorItemReader")
    .queryString(String JPQL) //ItemReader가 조회할 때 사용할 JPQL 문장 설정
    .EntityManagerFactory(EntityManagerFactory) // JPQL을 실행하는 EntityManager를 생성하는 팩토리
    .parameterValue(Map<String,Object> parameters)// 쿼리 파라미터 설정
    .maxItemCount(int count)// 조회 할 최대 item 수
    .currentItemCount(int count) //조회 item의 시작지점
    .build();
}
```

![image](https://user-images.githubusercontent.com/40031858/160532905-3a30b673-2a9f-4d54-9d5d-44c47e6f46c7.png)

![image](https://user-images.githubusercontent.com/40031858/160532946-66a5875c-01a2-4d2f-bfd7-cad2e168e2e3.png)

### JdbcPagingItemReader
- 기본 개념
  - Paging 기반의 JDBC 구현체로서 쿼리에 시작 행 번호(offset)와 페이지에서 반환 할 행 수(limit)를 지정해서 SQL을 실행한다
  - 스프링 배치에서 offset과 limit을 PageSize에 맞게 자동으로 생성해 주며 페이징 단위로 데이터를 조회할 때 마다 새로운 쿼리가 실행된다
  - 페이지마다 새로운 쿼리를 실행하기 때문에 페이징 시 결과 데이터의 순서가 보장될 수 있도록 order by 구문이 작성되도록 한다
  - 멀티 스레드 환경에서 Thread안정성을 보장하기 때문에 별도의 동기화를 할 필요가 없다
  - PagingQueryProvider
    - 쿼리 실행에 필요한 쿼리문을 ItemReader에게 제공하는 클래스
    - 데이터베이스마다 페이징 전략이 다르기 때문에 각 데이터 베이스 유형마다 다른 PagingQueryProvider를 사용한다
    - select절, from 절, sortKey는 필수로 설정해야 하며 where, group by 절은 필수가 아니다.

![image](https://user-images.githubusercontent.com/40031858/160534972-27105dba-5266-4666-832d-4957749636ee.png)

```java
public JdbcPagingItemReader itemReader(){
  return new JdbcPagingItemReaderBuilder<T>()
    .name("pagingItemReader")
    .pageSize(int pageSize) //페이지 크기 설정(쿼리 당 요청할 레코드 개수)
    .dataSource(DataSource) //DB에 접근하기 위해 Datasource설정
    .queryProvider(PagingQueryProvider) //DB페이징 전략에 따른 PagingQueryProvider 설정
    .rowMapper(Class<T>) //쿼리 결과로 반환되는 데이터와 객체를 매핑하기 위한 RowMapper설정

    //PagingQueryProvider API 시작
    .selectClause(String selectClause) //select절 설정
    .fromClause(String fromClause) //from 절 설정
    .whereClause(String whereClause) // where 절 설정
    .groupClause(String groupClause) // group절 설정
    .sortKeys(Map<String,Order> sortKeys) //정렬을 위한 유니크한 키 설정
    
    // PagingQueryProvider API 끝
    
    .paramaeterValues(Map<String,Object> parameters) // 쿼리 파라미터 설정
    .maxItemCount(int count)//조회 할 최대 item 수
    .currentItemCount(int count)//조회 Item의 시작 지점
    .maxRows(int maxRows)//ResultSet오브젝트가 포함 할 수 있는 최대 행 수
    .build();
}
```

![image](https://user-images.githubusercontent.com/40031858/160535585-535d5955-83c4-4112-bceb-7f0c76e98301.png)

![image](https://user-images.githubusercontent.com/40031858/160535611-e968faaa-f999-4d56-9485-a628d05c7815.png)

### JpaPagingItemReader
- 기본 개념
  - Paging 기반의 JPA구현체로서 EntityManagerFactory 객체가 필요하며 쿼리는 JPQL을 사용한다
- API
```java
public JpaPagingItemReader itemReader(){
  return new JpaPagingItemReaderBuilder<T>()
    .name("pagingITemReader")
    .pageSize(int count) //페이지 크기 설정(쿼리 당 요청할 레코드 수)
    .queryString(String JPQL) // ItemReader가 조회할 때 사용할 JPQL 문장 설정
    .EntityManagerFactory(EntityManagerFactory) //JPQL을 실행하는 EntityManager를 생성하는 팩토리
    .parameterValue(Map<String,Object> parameters) //쿼리 파라미터 설정
    .build();
}
```

![image](https://user-images.githubusercontent.com/40031858/160609492-9b1573dc-5411-4b3d-927e-fb157f6989c9.png)

![image](https://user-images.githubusercontent.com/40031858/160609556-6ee3a6fb-9432-4bff-a008-7ebb28cf76f8.png)