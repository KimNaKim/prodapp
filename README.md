# prodapp (Socket + JSON + MySQL 예제)

## 프로젝트 개요
- **구성**: `MyClient`(콘솔 클라이언트) ↔ `MyServer`(소켓 서버) ↔ `MySQL(productdb)`  
- **통신 방식**: 한 줄(JSON 1개) 단위로 요청/응답 (`BufferedReader.readLine()` / `BufferedWriter.write("\n")`)  
- **요청 포맷**: `RequestDTO`(method/querystring/body)  
- **응답 포맷**: `ResponseDTO`(msg/body)

## 클래스 목록 및 역할 (`src/main/java`)

### client
- **`client.MyClient`**
  - **역할**: 콘솔에서 명령을 입력받아 `RequestDTO`를 JSON으로 만들어 서버(`localhost:20000`)로 전송하고, 서버의 JSON 응답을 출력합니다.
  - **중요 포인트**: 입력 문자열을 **공백으로 split**하여 파라미터를 해석합니다.

### server
- **`server.MyServer`**
  - **역할**: `ServerSocket(20000)`으로 접속을 받아 클라이언트로부터 JSON 한 줄을 읽고, `RequestDTO`로 파싱하여 `ProductService`를 호출한 뒤 `ResponseDTO`를 JSON으로 응답합니다.
  - **종료**: 클라이언트가 문자열 `"exit"`를 보내면 서버 루프가 종료됩니다.

- **`server.ProductServiceInterface`**
  - **역할**: 상품 도메인 기능(등록/목록/상세/삭제) 인터페이스입니다.

- **`server.ProductService`**
  - **역할**: 서비스 계층. `ProductRepository`를 사용해 CRUD를 수행합니다.

- **`server.ProductRepository`**
  - **역할**: DB 접근 계층(JDBC). `products` 테이블에 대해 `insert / findAll / findById / deleteById`를 수행합니다.

- **`server.DBConnection`**
  - **역할**: MySQL JDBC 드라이버 로드 및 DB 커넥션 생성 헬퍼입니다.
  - **접속 정보(코드 하드코딩)**: `jdbc:mysql://localhost:3306/productdb`, user=`root`, password=`bitc5600!`

- **`server.Product`**
  - **역할**: 상품 엔티티(필드: `id`, `name`, `price`, `qty`)를 표현합니다.

### dto
- **`dto.RequestDTO`**
  - **역할**: 클라이언트 → 서버 요청 모델
  - **필드**
    - `method`: `"get" | "post" | "delete"`
    - `querystring`: 예) `{ "id": 3 }`
    - `body`: 예) `{ "name": "pen", "price": 1000, "qty": 2 }`

- **`dto.ResponseDTO<T>`**
  - **역할**: 서버 → 클라이언트 응답 모델
  - **필드**
    - `msg`: `"ok" | "id not found" | "body not found"` 등
    - `body`: 조회 결과(단건 `Product` 또는 목록 `List<Product>`)

## 실행/동작 방법 (중요)

### 1) 서버 먼저 실행
- `MyClient`는 시작하자마자 `localhost:20000`에 접속을 시도합니다.
- 따라서 **반드시 `server.MyServer`를 먼저 실행**한 뒤 `client.MyClient`를 실행하세요.

### 2) DB 준비 (MySQL)
- `DBConnection`이 아래 DB로 접속합니다:
  - DB: `productdb`
  - 테이블: `products`
- 예시 스키마:

```sql
create database if not exists productdb;
use productdb;

create table if not exists products (
  id int auto_increment primary key,
  name varchar(255) not null,
  price int not null,
  qty int not null
);
```

> DB 계정/비밀번호는 현재 코드에 하드코딩되어 있으니, 환경에 맞게 `server/DBConnection.java`를 수정해야 정상 동작합니다.

## MyClient 콘솔 명령어 (입력 규칙/주의사항)

`MyClient`는 입력을 **공백 기준으로 분리**해서 파라미터를 읽습니다. 아래 형식을 정확히 지켜주세요.

### 전체 목록 조회
- **명령**: `get`
- **설명**: `products` 전체 조회

### 단건 조회
- **명령**: `get <id>`
  - 예) `get 3`
- **주의**: `<id>`는 **정수**여야 합니다(정수가 아니면 `NumberFormatException` 가능).

### 상품 등록
- **명령**: `post <name> <price> <qty>`
  - 예) `post apple 1200 5`
- **주의**
  - `<price>`, `<qty>`는 **정수**여야 합니다.
  - `<name>`에 **공백이 포함되면 안 됩니다**. (공백이 들어가면 split 결과가 깨져서 파싱이 틀어집니다.)

### 상품 삭제
- **명령**: `delete <id>`
  - 예) `delete 3`
- **주의**: `<id>`는 **정수**여야 합니다.

### 종료
- **명령**: `exit`
- **설명**: 클라이언트가 `"exit"`를 서버에 전송하고 종료합니다. 서버도 `"exit"`를 받으면 루프를 종료합니다.

## 자주 발생하는 문제
- **서버 미실행**: `MyClient` 실행 시 `Connection refused`가 날 수 있습니다 → 서버 먼저 실행.
- **DB 접속 실패**: MySQL 미실행 / 계정 비번 불일치 / DB 미생성 → `DBConnection` 설정 및 스키마 확인.
- **입력 형식 오류**: `post`/`delete`/`get <id>`에서 숫자 자리에 문자가 오면 예외가 날 수 있습니다.
