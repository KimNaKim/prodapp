# 상품 관리 시스템 개발 태스크

## 프로젝트 현황 분석

### 완료된 부분 (약 30-40%)
- [x] 프로젝트 구조 설계 및 Gradle 설정
- [x] 소켓 기본 통신 구조 (MyClient, MyServer)
- [x] DB 연결 설정 (DBConnection)
- [x] 서비스 인터페이스 정의 (ProductServiceInterface)
- [x] ResponseDTO 기본 구조

### 미구현 부분
- [ ] Product 엔티티
- [ ] RequestDTO
- [ ] ProductRepository (CRUD)
- [ ] ProductService (비즈니스 로직)
- [ ] MyServer 요청 파싱 및 처리

---

## 개발 순서 및 태스크

### Phase 1: 엔티티 및 DTO 구현

#### Task 1.1: Product 엔티티 구현
**파일**: `src/main/java/server/Product.java`

```java
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Product {
    private int id;
    private String name;
    private int price;
    private int qty;
}
```

#### Task 1.2: RequestDTO 구현
**파일**: `src/main/java/dto/RequestDTO.java`

```java
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RequestDTO {
    private String method;                  // "get", "post", "delete"
    private Map<String, Object> querystring;  // 쿼리 파라미터 (Map 사용)
    private Product body;                   // POST 요청 시 상품 데이터
}
```

> **참고**: `querystring`은 `Map<String, Object>`를 사용하여 유연하게 처리

**각 명령어별 JSON 형식**:

1. **전체 목록 조회** (`get`)
```json
{
  "method": "get",
  "querystring": null,
  "body": null
}
```

2. **상세 조회** (`get 1`)
```json
{
  "method": "get",
  "querystring": {
    "id": 1
  },
  "body": null
}
```

3. **상품 등록** (`post 바나나 5000 20`)
```json
{
  "method": "post",
  "querystring": null,
  "body": {
    "name": "바나나",
    "price": 5000,
    "qty": 20
  }
}
```

4. **상품 삭제** (`delete 1`)
```json
{
  "method": "delete",
  "querystring": {
    "id": 1
  },
  "body": null
}
```

#### Task 1.3: ResponseDTO 완성
**파일**: `src/main/java/dto/ResponseDTO.java`

- Lombok @Data 어노테이션 추가
- @AllArgsConstructor, @NoArgsConstructor 추가

---

### Phase 2: Repository 계층 구현

#### Task 2.1: ProductRepository CRUD 메서드 구현
**파일**: `src/main/java/server/ProductRepository.java`

구현할 메서드:
1. `insert(String name, int price, int qty)` - INSERT SQL
2. `deleteById(int id)` - DELETE SQL
3. `findById(int id) : Product` - SELECT SQL (단일)
4. `findAll() : List<Product>` - SELECT SQL (전체)

**SQL 예시**:
```sql
-- 테이블 생성 (productdb에 필요)
CREATE TABLE product (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    price INT NOT NULL,
    qty INT NOT NULL
);
```

---

### Phase 3: Service 계층 구현

#### Task 3.1: ProductService 비즈니스 로직 구현
**파일**: `src/main/java/server/ProductService.java`

구현할 메서드:
1. `상품등록(String name, int price, int qty)` - Repository.insert() 호출
2. `상품목록() : List<Product>` - Repository.findAll() 호출
3. `상품상세(int id) : Product` - Repository.findById() 호출
4. `상품삭제(int id)` - Repository.deleteById() 호출

---

### Phase 4: 클라이언트/서버 통신 구현

#### Task 4.1: MyClient 입력 파싱 및 JSON 변환
**파일**: `src/main/java/client/MyClient.java`

구현 내용:
1. 키보드 입력 문자열 파싱 (split으로 분리)
2. 첫 번째 토큰으로 method 결정 (get/post/delete)
3. RequestDTO 객체 생성
4. Gson으로 JSON 문자열 변환
5. 서버로 JSON 전송

```java
// 예시 코드
String input = "post 바나나 5000 20";
String[] tokens = input.split(" ");
RequestDTO request = new RequestDTO();
request.setMethod(tokens[0]);

if (tokens[0].equals("post")) {
    Product body = new Product();
    body.setName(tokens[1]);
    body.setPrice(Integer.parseInt(tokens[2]));
    body.setQty(Integer.parseInt(tokens[3]));
    request.setBody(body);
} else if (tokens.length > 1) {
    // get 1 또는 delete 1 인 경우
    Map<String, Object> qs = new HashMap<>();
    qs.put("id", Integer.parseInt(tokens[1]));
    request.setQuerystring(qs);
}

String json = gson.toJson(request);
bw.write(json);
bw.newLine();
bw.flush();
```

#### Task 4.2: MyServer 요청 처리 로직 구현
**파일**: `src/main/java/server/MyServer.java`

구현 내용:
1. 클라이언트로부터 JSON 수신
2. Gson으로 RequestDTO 파싱
3. method에 따라 분기 처리
4. ProductService 메서드 호출
5. ResponseDTO 생성 및 JSON 변환
6. 클라이언트에 응답 전송

**클라이언트 입력 → JSON 변환 (MyClient에서 처리)**:
```
키보드 입력: "get"
→ JSON: {"method":"get","querystring":null,"body":null}

키보드 입력: "get 1"
→ JSON: {"method":"get","querystring":{"id":1},"body":null}

키보드 입력: "post 바나나 5000 20"
→ JSON: {"method":"post","querystring":null,"body":{"name":"바나나","price":5000,"qty":20}}

키보드 입력: "delete 1"
→ JSON: {"method":"delete","querystring":{"id":1},"body":null}
```

**서버 처리 로직 (MyServer에서 처리)**:
```java
// 1. JSON 수신 및 파싱
RequestDTO request = gson.fromJson(jsonString, RequestDTO.class);

// 2. method에 따라 분기
switch (request.getMethod()) {
    case "get":
        if (request.getQuerystring() == null) {
            // 전체 목록 조회
            List<Product> list = service.상품목록();
        } else {
            // 상세 조회 - Map에서 id 추출
            int id = ((Number) request.getQuerystring().get("id")).intValue();
            Product product = service.상품상세(id);
        }
        break;
    case "post":
        // 상품 등록
        Product body = request.getBody();
        service.상품등록(body.getName(), body.getPrice(), body.getQty());
        break;
    case "delete":
        // 상품 삭제 - Map에서 id 추출
        int id = ((Number) request.getQuerystring().get("id")).intValue();
        service.상품삭제(id);
        break;
}
```

---

### Phase 5: 통합 테스트

#### Task 5.1: 전체 기능 테스트
1. MySQL productdb 데이터베이스 및 product 테이블 생성
2. MyServer 실행
3. MyClient 실행
4. 각 명령어 테스트:
    - `post 바나나 5000 20` → 상품 등록 확인
    - `get` → 상품 목록 확인
    - `get 1` → 상품 상세 확인
    - `delete 1` → 상품 삭제 확인

---

## 기술 구현 가이드

### JSON 처리 (Gson)
```java
Gson gson = new Gson();

// 객체 → JSON 문자열
String json = gson.toJson(responseDTO);

// JSON 문자열 → 객체
RequestDTO request = gson.fromJson(jsonString, RequestDTO.class);
```

### JDBC 패턴
```java
Connection conn = DBConnection.getConnection();
PreparedStatement pstmt = conn.prepareStatement(sql);
pstmt.setString(1, value);
ResultSet rs = pstmt.executeQuery();
while (rs.next()) {
    // 데이터 처리
}
```

---

## 파일별 구현 체크리스트

| 파일 | 상태 | 우선순위 | 구현 내용 |
|------|------|----------|-----------|
| Product.java | 미구현 | 1 | 엔티티 필드 + Lombok |
| RequestDTO.java | 미구현 | 1 | method, querystring(Map), body 필드 |
| ResponseDTO.java | 부분구현 | 1 | Lombok 어노테이션 추가 |
| ProductRepository.java | 미구현 | 2 | CRUD SQL 구현 |
| ProductService.java | 스켈레톤 | 3 | Repository 호출 로직 |
| MyClient.java | 부분구현 | 4 | 키보드입력 → JSON 변환 로직 추가 |
| MyServer.java | 부분구현 | 4 | JSON 파싱 + 서비스 호출 로직 |
| DBConnection.java | 완료 | - | - |

---

## 데이터베이스 준비

### MySQL 테이블 생성 스크립트
```sql
CREATE DATABASE IF NOT EXISTS productdb;
USE productdb;

CREATE TABLE IF NOT EXISTS product (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    price INT NOT NULL,
    qty INT NOT NULL
);
```

---

## 예상 데이터 흐름

### 상품 등록 예시
```
[클라이언트]                                      [서버]
    │                                               │
    │  키보드 입력: "post 바나나 5000 20"           │
    │  ↓ (MyClient에서 JSON 변환)                  │
    │  {"method":"post","querystring":null,        │
    │   "body":{"name":"바나나","price":5000,      │
    │           "qty":20}}                          │
    │ ────────────────────────────────────────────> │
    │                                               │ 1. JSON 파싱 → RequestDTO
    │                                               │ 2. method="post" 확인
    │                                               │ 3. body에서 상품정보 추출
    │                                               │ 4. ProductService.상품등록() 호출
    │                                               │ 5. ProductRepository.insert() 호출
    │                                               │ 6. DB INSERT 실행
    │                                               │ 7. ResponseDTO 생성
    │  {"msg":"ok","body":null}                    │
    │ <──────────────────────────────────────────── │
    │                                               │
```

### 상품 목록 조회 예시
```
[클라이언트]                                      [서버]
    │                                               │
    │  키보드 입력: "get"                           │
    │  ↓ (JSON 변환)                               │
    │  {"method":"get","querystring":null,         │
    │   "body":null}                                │
    │ ────────────────────────────────────────────> │
    │                                               │ 1. method="get", querystring=null
    │                                               │ 2. ProductService.상품목록() 호출
    │                                               │ 3. 전체 상품 List 반환
    │  {"msg":"ok","body":[                        │
    │    {"id":1,"name":"바나나",...},             │
    │    {"id":2,"name":"사과",...}                │
    │  ]}                                           │
    │ <──────────────────────────────────────────── │
    │                                               │
```

### 상품 상세 조회 예시
```
[클라이언트]                                      [서버]
    │                                               │
    │  키보드 입력: "get 1"                         │
    │  ↓ (JSON 변환)                               │
    │  {"method":"get","querystring":{"id":1},     │
    │   "body":null}                                │
    │ ────────────────────────────────────────────> │
    │                                               │ 1. method="get", querystring.id=1
    │                                               │ 2. ProductService.상품상세(1) 호출
    │                                               │ 3. 단일 상품 반환
    │  {"msg":"ok","body":                         │
    │    {"id":1,"name":"바나나","price":5000,     │
    │     "qty":20}                                 │
    │  }                                            │
    │ <──────────────────────────────────────────── │
    │                                               │
```

---

## 참고 링크

- 실습 평가: https://getinthere.notion.site/3-2eb8a08b6c0d8039baccea3268e606dc