# 📘 KNOU CBT (Computer Based Test)

----

## 📌 소개
- KNOU CBT는 한국방송통신대학교 시험 대비를 위한 **온라인 시험 관리 시스템**입니다.  
- 관리자는 시험 문제와 과목을 등록/수정/삭제할 수 있고, 사용자는 실제 시험처럼 문제를 풀고 결과를 확인할 수 있습니다.

----

## 🎯 목적
- 종이 기반 시험 대비 학습의 불편함 개선
- 웹 기반 시험 제공으로 접근성과 학습 효율 향상
- 관리자와 사용자 모두에게 직관적인 UI 제공

----

## 🛠 사용 기술
- **Backend**: Spring Boot, Spring Security, MyBatis
- **Frontend**: Thymeleaf, Bootstrap
- **Build/Etc**: Gradle, Git

----

## ✨ 주요 기능

### 👩‍💻 사용자(User) 기능
- 로그인 및 권한에 따른 접근 제어
- 시험 목록 조회 및 응시
- 시험 문제 풀이 (타이머, 진행률 표시)
- 시험 결과 확인 및 리뷰
- 공지사항 열람

### 🛠 관리자(Admin) 기능
- 학과/과목 CRUD
    - 학과 삭제 시 연결된 과목이 있으면 삭제 불가 처리
- 시험/문제 CRUD (문제, 보기, 정답 관리 + 첨부파일)
- 공지사항 CRUD (Toast UI Editor 기반)

### 🌐 공통
- Spring Security 기반 로그인/권한 관리
- Thymeleaf Layout 적용 (일관된 화면 구조)
- 예외 처리 및 유효성 검증 적용

----

## 📸 화면 예시


### 👩‍💻 사용자(User) 화면
1. . **메인 화면**  
   ![메인 화면](https://img1.daumcdn.net/thumb/R1280x0/?scode=mtistory2&fname=https%3A%2F%2Fblog.kakaocdn.net%2Fdna%2FbTYec3%2FbtsQnW1FOOQ%2FAAAAAAAAAAAAAAAAAAAAAHGFFiAnWUoOr76F2LNG2czatCvlbV8a5pBV1r-Dc8kh%2Fimg.png%3Fcredential%3DyqXZFxpELC7KVnFOS48ylbz2pIh7yKj8%26expires%3D1759244399%26allow_ip%3D%26allow_referer%3D%26signature%3DL0Md%252Bmzz6wR2eoQ2hheT%252FrnLs90%253D)
---
2. **시험 목록**  
   ![시험 목록](https://img1.daumcdn.net/thumb/R1280x0/?scode=mtistory2&fname=https%3A%2F%2Fblog.kakaocdn.net%2Fdna%2Fcz1Lm1%2FbtsQoIojNQc%2FAAAAAAAAAAAAAAAAAAAAALBZqJ_bisaeo0XR5mt8SeYo-TxtlzwhYTQiOpDDBoZU%2Fimg.png%3Fcredential%3DyqXZFxpELC7KVnFOS48ylbz2pIh7yKj8%26expires%3D1759244399%26allow_ip%3D%26allow_referer%3D%26signature%3DAq4Ww6bXR1pUajOLp%252FYYbsd1zjY%253D)
---
3. **시험 응시 화면 (문제/타이머/프로그레스바)**  
   ![시험 응시 화면](https://img1.daumcdn.net/thumb/R1280x0/?scode=mtistory2&fname=https%3A%2F%2Fblog.kakaocdn.net%2Fdna%2FO9cXG%2FbtsQpbDJ3tZ%2FAAAAAAAAAAAAAAAAAAAAAJNAb522vyxWdwavsVWocYMGWh6E4KPJ9Nvkz5UMX40r%2Fimg.png%3Fcredential%3DyqXZFxpELC7KVnFOS48ylbz2pIh7yKj8%26expires%3D1759244399%26allow_ip%3D%26allow_referer%3D%26signature%3DY5klHUGGvzOHGlBTQm%252BXrbV9PSc%253D)
---
4. **시험 결과 및 리뷰 화면**  
   ![시험 결과](https://img1.daumcdn.net/thumb/R1280x0/?scode=mtistory2&fname=https%3A%2F%2Fblog.kakaocdn.net%2Fdna%2F6NtKl%2FbtsQq0VqOr4%2FAAAAAAAAAAAAAAAAAAAAAOJt_HCSU0xTMjCQHZAHKLL6qX8rojESk8SRj3drEjoG%2Fimg.png%3Fcredential%3DyqXZFxpELC7KVnFOS48ylbz2pIh7yKj8%26expires%3D1759244399%26allow_ip%3D%26allow_referer%3D%26signature%3DyWo76zS7qc9fPT5Kf%252B2CQ0MML0U%253D)
   ![시험 리뷰](https://img1.daumcdn.net/thumb/R1280x0/?scode=mtistory2&fname=https%3A%2F%2Fblog.kakaocdn.net%2Fdna%2FckU3F7%2FbtsQnYd1UzP%2FAAAAAAAAAAAAAAAAAAAAADagXCMw-bK_Aba7PLGKQuaEjMQNTPMUb63p2v0Q9W0u%2Fimg.png%3Fcredential%3DyqXZFxpELC7KVnFOS48ylbz2pIh7yKj8%26expires%3D1759244399%26allow_ip%3D%26allow_referer%3D%26signature%3D%252BokZBRQee75TCRLtOYwRGHvCTbo%253D)

---


### 🛠 관리자(Admin) 화면
1. **시험/문제 관리**  
   ![시험 관리](https://img1.daumcdn.net/thumb/R1280x0/?scode=mtistory2&fname=https%3A%2F%2Fblog.kakaocdn.net%2Fdna%2Fzj2sN%2FbtsQpWe5BsD%2FAAAAAAAAAAAAAAAAAAAAABj83ez1X9y7fD1BRK274WSR1riCueTvwltymX4dcoAt%2Fimg.png%3Fcredential%3DyqXZFxpELC7KVnFOS48ylbz2pIh7yKj8%26expires%3D1759244399%26allow_ip%3D%26allow_referer%3D%26signature%3DRGC8%252Brb7EC2cDtRqcfnR9uy0R9s%253D)
   ![문제 관리](https://img1.daumcdn.net/thumb/R1280x0/?scode=mtistory2&fname=https%3A%2F%2Fblog.kakaocdn.net%2Fdna%2FbipEq6%2FbtsQqI1NyFk%2FAAAAAAAAAAAAAAAAAAAAAFbZdSnQAx2t8KPLIKwvto_3GVAJeBmkN7awjOLBJuMP%2Fimg.png%3Fcredential%3DyqXZFxpELC7KVnFOS48ylbz2pIh7yKj8%26expires%3D1759244399%26allow_ip%3D%26allow_referer%3D%26signature%3DIBlkgrqxkPofl6tXORZKidGpRdo%253D)
   ![미리보기](https://img1.daumcdn.net/thumb/R1280x0/?scode=mtistory2&fname=https%3A%2F%2Fblog.kakaocdn.net%2Fdna%2FbzJaDi%2FbtsQqWyP9KQ%2FAAAAAAAAAAAAAAAAAAAAAA65XpNDksOUXum1cL3-nH82Y86zNisjSneRucqQkohe%2Fimg.png%3Fcredential%3DyqXZFxpELC7KVnFOS48ylbz2pIh7yKj8%26expires%3D1759244399%26allow_ip%3D%26allow_referer%3D%26signature%3DenxauCyExacKYTYj81KwLXCDfg8%253D)
----
2. **과목/학과 관리**  
   ![학과 관리](https://img1.daumcdn.net/thumb/R1280x0/?scode=mtistory2&fname=https%3A%2F%2Fblog.kakaocdn.net%2Fdna%2FbFrp0z%2FbtsQoiDrSjf%2FAAAAAAAAAAAAAAAAAAAAAI9bqb6wpD550l7mATpLBgvJ9vynXm3zg3M_LHwTQTV8%2Fimg.png%3Fcredential%3DyqXZFxpELC7KVnFOS48ylbz2pIh7yKj8%26expires%3D1759244399%26allow_ip%3D%26allow_referer%3D%26signature%3DLfJZeU0zUkAYcISMIPiNj1PQmxI%253D)
   ![과목 관리](https://img1.daumcdn.net/thumb/R1280x0/?scode=mtistory2&fname=https%3A%2F%2Fblog.kakaocdn.net%2Fdna%2FbgHGPf%2FbtsQpv24Xt5%2FAAAAAAAAAAAAAAAAAAAAAMa_LViJcX--OI4XF3TKhWshRDNKa6Ca6xKHybmy2Soh%2Fimg.png%3Fcredential%3DyqXZFxpELC7KVnFOS48ylbz2pIh7yKj8%26expires%3D1759244399%26allow_ip%3D%26allow_referer%3D%26signature%3DrntOWOwMiZ8URPwW8U3i6JatFsY%253D)
----
3. **공지사항 관리 (웹에디터 적용)**  
   ![공지사항 관리](https://img1.daumcdn.net/thumb/R1280x0/?scode=mtistory2&fname=https%3A%2F%2Fblog.kakaocdn.net%2Fdna%2FxpCgA%2FbtsQnTDQEt8%2FAAAAAAAAAAAAAAAAAAAAAIuyP2qa5M7YBjKNyaQKklm8-dA0WGoYVRqZJRKCJwXn%2Fimg.png%3Fcredential%3DyqXZFxpELC7KVnFOS48ylbz2pIh7yKj8%26expires%3D1759244399%26allow_ip%3D%26allow_referer%3D%26signature%3D3KnkramcKwqlOkgxKndOSAw7%252FoU%253D)


----

## 📕 개발 일지
- [[개발일지#000] 방통대CBT 제작계기 & 사용기술스택 & 요구사항](https://ddururiiiiiii.tistory.com/463)
- [[개발일지#001] 데이터베이스 설계 및 생성](https://ddururiiiiiii.tistory.com/464)
- [[개발일지#002] 스프링 프로젝트 생성 및 Mybatis 연결](https://ddururiiiiiii.tistory.com/465)
- [[개발일지#003] 기본 부트스트랩 적용 / 기출문제 전체조회 구현](https://ddururiiiiiii.tistory.com/467)
- [[개발일지#004] 기출문제 목록조회 (검색조회 및 페이지네이션 포함)](https://ddururiiiiiii.tistory.com/472)
- [[개발일지#005] 시험풀기 화면 구현 (레이아웃, 안푼문제, 소요시간 등)](https://ddururiiiiiii.tistory.com/473)
----
- [[개발일지#006] 학과(Department) 도메인 리빌딩 (리팩토링X)](https://ddururiiiiiii.tistory.com/699)
- [[개발일지#007] 과목(Subject) 도메인 리빌딩 (리팩토링X)](https://ddururiiiiiii.tistory.com/701)
- [[개발일지#008] 시험(Exam) 도메인 리빌딩 (리팩토링X)](https://ddururiiiiiii.tistory.com/702)
