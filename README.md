# 스프링 트랜잭션

## 스프링 트랜잭션 추상화

**선언적 트랜잭션 관리 vs 프로그래밍 방식 트랜잭션 관리**

* 선언적 트랜잭션 관리(Declarative Transaction Management)

`@Transactional` 애노테이션 하나만 선언해서 매우 편리하게 트랜잭션을 적용하는 것을 선언적 트랜잭션 관리라 한다.

선언적 트랜잭션 관리는 과거 XML에 설정하기도 했다.

이름 그대로 해당 로직에 트랜잭션을 적용하겠다 라고 어딘가에 선언하기만 하면 트랜잭션이 적용되는 방식 이다.

* 프로그래밍 방식의 트랜잭션 관리(programmatic transaction management)

트랜잭션 매니저 또는 트랜잭션 템플릿 등을 사용해서 트랜잭션 관련 코드를 직접 작성하는 것을 프로그래 밍 방식의 트랜잭션 관리라 한다.

프로그래밍 방식의 트랜잭션 관리를 사용하게 되면, 애플리케이션 코드가 트랜잭션이라는 기술 코드와 강하게 결합된다.

선언적 트랜잭션 관리가 프로그래밍 방식에 비해서 훨씬 간편하고 실용적이기 때문에 실무에서는 대부분 선언적 트랜잭션 관리를 사용한다.

### 선언적 트랜잭션과 AOP

`@Transactional` 을 통한 선언적 트랜잭션 관리 방식을 사용하게 되면 기본적으로 프록시 방식의 AOP가 적용된다.

프록시 도입 전: 서비스에 비즈니스 로직과 트랜잭션 처리 로직이 함께 섞여있다.

프록시 도입 후: 트랜잭션 프록시가 트랜잭션 처리 로직을 모두 가져간다. 그리고 트랜잭션을 시작한 후에 실제 서 비스를 대신 호출한다. 트랜잭션 프록시 덕분에 서비스 계층에는 순수한 비즈니즈 로직만 남길 수 있다.


**프록시 도입 후 전체 과정**

<img width="915" alt="Screenshot 2024-10-03 at 15 53 58" src="https://github.com/user-attachments/assets/de15d82e-2b5e-48ff-993e-9a5ba5b13710">

트랜잭션은 커넥션에 `con.setAutocommit(false)` 를 지정하면서 시작한다. 

같은 트랜잭션을 유지하려면 같은 데이터베이스 커넥션을 사용해야 한다.

이것을 위해 스프링 내부에서는 트랜잭션 동기화 매니저가 사용된다.

`JdbcTemplate` 을 포함한 대부분의 데이터 접근 기술들은 트랜잭션을 유지하기 위해 내부에서 트랜잭션 동기화 매니저를 통해 리소스(커넥션)를 동기화한다.

내가(개발자가) 직접 트랜잭션 매니저를 호출해서 트랜잭션을 관리하는게 아닌 AOP가 대신 트랜잭션 매니저를 호출하여 관리.

나는 비즈니스 로직에만 집중할 수 있게 해줌.

**스프링이 제공하는 트랜잭션 AOP**

스프링의 트랜잭션은 매우 중요한 기능이고, 전세계 누구나 다 사용하는 기능이다. 

스프링은 트랜잭션 AOP를 처리하기 위한 모든 기능을 제공한다. 

스프링 부트를 사용하면 트랜잭션 AOP를 처리하기 위해 필요한 스프링 빈들도 자동으로 등록해준다.

개발자는 트랜잭션 처리가 필요한 곳에 `@Transactional` 애노테이션만 붙여주면 된다. 

스프링의 트랜잭션 AOP는 이 애노테이션을 인식해서 트랜잭션을 처리하는 프록시를 적용해준다.

## 트랜잭션 적용 확인

**스프링 컨테이너에 트랜잭션 프록시 등록**

<img width="900" alt="Screenshot 2024-10-03 at 16 07 12" src="https://github.com/user-attachments/assets/6923387f-9be2-4fa8-802c-8d96a8ef6c67">


`@Transactional` 애노테이션이 특정 클래스나 메서드에 하나라도 있으면 트랜잭션 AOP는 프록시를 만들어서 스프링 컨테이너에 등록한다. 

그리고 실제 `basicService` 객체 대신에 프록시인 `basicService$ $CGLIB` 를 스프링 빈에 등록한다. 

그리고 프록시는 내부에 실제 `basicService` 를 참조하게 된다. 여기서 핵 심은 실제 객체 대신에 프록시가 스프링 컨테이너에 등록되었다는 점이다.

클라이언트인 `txBasicTest` 는 스프링 컨테이너에 `@Autowired BasicService basicService` 로 의존관계 주입을 요청한다. 

스프링 컨테이너에는 실제 객체 대신에 프록시가 스프링 빈으로 등록되어 있기 때문에 프록시를 주입한다.

프록시는 `BasicService` 를 상속해서 만들어지기 때문에 다형성을 활용할 수 있다. 

따라서 `BasicService` 대신에 프록시인 `BasicService$$CGLIB` 를 주입할 수 있다.


**트랜잭션 프록시 동작 방식**

<img width="908" alt="Screenshot 2024-10-03 at 16 10 09" src="https://github.com/user-attachments/assets/7c23f45b-f0d7-4811-946e-41e3987a6682">

클라이언트가 주입 받은 `basicService$$CGLIB` 는 트랜잭션을 적용하는 프록시이다.


## 트랜잭션 적용 위치

























