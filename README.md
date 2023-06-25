# 배당금 조회 프로젝트

---

## 프로젝트 소개

- Spring Boot기반의 Back-End 실습 프로젝트입니다.
- Yahoo Finance에서 배당금 데이터를 스크랩하여 사용자에게 데이터를 제공합니다.

## 프로젝트 초기 설정
- Cache를 사용하기 위해 Redis Server가 필요합니다.
- Windows 운영 체제인 경우는, WSL을 통해 Redis를 설치 할 수 있습니다.
- src/main/resources/application.yml에서 Redis Server에 관한 설정을 수행합니다.
- 또한, Jwt토큰에 대한 Secret key을 최대한 복잡하게 지정해야합니다.

## 보안

- Spring Security와 JWT를 사용하여 인증을 구현하였습니다.

## 인증 API

- (POST) /auth/signup : 회원가입을 수행합니다.
```
(example)

Request Header:
{
    'Content-Type' : 'application/json'
}

Request Body:
{
    'username' : 'test',
    'password' : 'test',
    'roles' : ["ROLE_READ", "ROLE_WRITE"]
}

Response Body:
{
    "id":1,
    "username":"test",
    "password":"2$.xxxxxx ...",
    "roles":["ROLE_READ","ROLE_WRITE"],
    "enabled":false,
    "authorities":[{"authority":"ROLE_READ"},{"authority":"ROLE_WRITE"}],
    "accountNonLocked":false,
    "credentialsNonExpired":false,
    "accountNonExpired":false
}
```

- (POST) /auth/signin : 로그인을 수행합니다.
```
(example)

Request Header:
{
    'Content-Type' : 'application/json'
}

Request Parameter:
{
    'username' : 'test', 
    'password' : 'test'
}

(get jwt token)
Response Body:
'ey2xxxxxx.xxxxxxxx.xxxxxxxx'
```

## 배당금 관련 API

```
(공통 Request Header)
{
    'Authorization' : 'Bearer eyJhxxxx.xxxxxx.xxxxxx',
    'Content-Type' : 'application/json'
}
```

- (POST) /company : ticker를 통해 배당금 정보를 스크랩하고 저장합니다.
```
(example)

Request Body:
{
    'ticker': 'TEST'
}

Response Body:
{
    'ticker': 'TEST',
    'name': 'TEST INC.'
}
```

- (GET) /company : 회사 목록을 불러옵니다.
```
(example)

Request Parameter:
{
    'size' : 1
}

Response Body:
{
   'content': [{'id': 1, 'ticker': 'TEST', 'name': 'TEST INC.'}], 
   'pageable': {'sort': {'empty': True, 'unsorted': True, 'sorted': False}, 
   'offset': 0, 
   'pageNumber': 0, 
   'pageSize': 1, 
   'unpaged': False, 'paged': True}, 
   'totalElements': 2, 
   'last': False, 
   'totalPages': 2, 
   'size': 1, 'number': 0, 
   'sort': {'empty': True, 
   'unsorted': True, 'sorted': False}, 
   'numberOfElements': 1, 
   'first': True, 
   'empty': False 
}
```

- (GET) /company/autocomplete : 회사명 자동완성 반환
```
(example)

Request Parameter:
{
    'keyword' : 'te'
}

Response Body:
{
    ['TEST INC.']
}
```

- (GET) /finance/dividend/{companyName} : 회사명에 해당되는 배당금 조회
```
(example)

Response Body:
{
    "company":{"ticker":"TEST","name":"TEST INC."},
    "dividends":[{"date":"2023-01-01T00:00:00","dividend":"0.3"},
    {"date":"2023-01-01T00:00:00","dividend":"0.3"}]
}
```

- (DELETE) /company/{ticker} : ticker를 통해 회사 삭제
```
(example)

Response Body:
'TEST INC.'
```

## 기술 스택

| SKILLS                                                                                                  |
|---------------------------------------------------------------------------------------------------------|
| <img src="https://img.shields.io/badge/Spring Boot-6DB33F?style=flat&logo=springboot&logoColor=white"/> |
| <img src="https://img.shields.io/badge/Redis-DC382D?style=flat&logo=redis&logoColor=white"/>            |

## 사용된 Dependencies

| Spring Boot     |
|-----------------|
| Spring Security |
| JPA             |
| H2 DataBase     |
| jjwt            |
| Jsoup           |
| Lombok          |

