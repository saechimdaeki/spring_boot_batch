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