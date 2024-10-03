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

스프링에서 우선순위는 항상 **더 구체적이고 자세한 것이 높은 우선순위를 가진다**. 

이것만 기억하면 스프링에서 발생하 는 대부분의 우선순위를 쉽게 기억할 수 있다. 

그리고 더 구체적인 것이 더 높은 우선순위를 가지는 것은 상식적으로 자연스럽다.

예를 들어서 메서드와 클래스에 애노테이션을 붙일 수 있다면 더 구체적인 메서드가 더 높은 우선순위를 가진다. 

인터페이스와 해당 인터페이스를 구현한 클래스에 애노테이션을 붙일 수 있다면 더 구체적인 클래스가 더 높은 우선순위를 가진다.

스프링의 `@Transactional` 은 다음 두 가지 규칙이 있다.

1. 우선순위 규칙
2. 클래스에 적용하면 메서드는 자동 적용

**우선순위**

트랜잭션을 사용할 때는 다양한 옵션을 사용할 수 있다. 

그런데 어떤 경우에는 옵션을 주고, 어떤 경우에는 옵션을 주지 않으면 어떤 것이 선택될까? 

예를 들어서 읽기 전용 트랜잭션 옵션을 사용하는 경우와 아닌 경우를 비교해보자. 

(읽기 전용 옵션에 대한 자세한 내용은 뒤에서 다룬다. 여기서는 적용 순서에 집중하자.)

`LevelService` 의 타입에 `@Transactional(readOnly = true)` 이 붙어있다.

`write()` : 해당 메서드에 `@Transactional(readOnly = false)` 이 붙어있다.

이렇게 되면 타입에 있는 `@Transactional(readOnly = true)` 와 해당 메서드에 있는`@Transactional(readOnly = false)` 둘 중 하나를 적용해야 한다.

클래스 보다는 메서드가 더 구체적이므로 메서드에 있는 `@Transactional(readOnly = false)` 옵션을 사용한 트랜잭션이 적용된다.


### 인터페이스에 @Transactional 적용

인터페이스에도 `@Transactional` 을 적용할 수 있다. 

이 경우 다음 순서로 적용된다. 구체적인 것이 더 높은 우선순위를 가진다고 생각하면 바로 이해가 될 것이다.

1. 클래스의 메서드 (우선순위가 가장 높다.)
2. 클래스의 타입
3. 인터페이스의 메서드
4. 인터페이스의 타입 (우선순위가 가장 낮다.)

클래스의 메서드를 찾고, 만약 없으면 클래스의 타입을 찾고 만약 없으면 인터페이스의 메서드를 찾고 그래도 없으면 인터페이스의 타입을 찾는다.

그런데 인터페이스에 `@Transactional` 사용하는 것은 스프링 공식 메뉴얼에서 권장하지 않는 방법이다. 

AOP를 적용하는 방식에 따라서 인터페이스에 애노테이션을 두면 AOP가 적용이 되지 않는 경우도 있기 때문이다. 

가급적 구체 클래스에 `@Transactional` 을 사용하자.

**참고**
스프링은 인터페이스에 `@Transactional` 을 사용하는 방식을 스프링 5.0에서 많은 부분 개선했다. 

과거에는 구체 클래스를 기반으로 프록시를 생성하는 CGLIB 방식을 사용하면 인터페이스에 있는 `@Transactional` 을 인식하지 못했다. 

스프링 5.0 부터는 이 부분을 개선해서 인터페이스에 있는 `@Transactional` 도 인식한다. 

하지만 다른 AOP 방식에서 또 적용되지 않을 수 있으므로 공식 메뉴얼의 가이드대로 가급적 구체 클래스에 `@Transactional` 을 사용하자.

CGLIB 방식은 스프링 핵심 원리 - 고급편에서 다룬다.


## 트랜잭션 AOP 주의 사항 - 프록시 내부 호출1

`@Transactional` 을 사용하면 스프링의 트랜잭션 AOP가 적용된다.

트랜잭션 AOP는 기본적으로 프록시 방식의 AOP를 사용한다.

앞서 배운 것처럼 `@Transactional` 을 적용하면 프록시 객체가 요청을 먼저 받아서 트랜잭션을 처리하고, 실제 객체 를 호출해준다.

따라서 트랜잭션을 적용하려면 항상 프록시를 통해서 대상 객체(Target)을 호출해야 한다.

이렇게 해야 프록시에서 먼저 트랜잭션을 적용하고, 이후에 대상 객체를 호출하게 된다.

만약 프록시를 거치지 않고 대상 객체를 직접 호출하게 되면 AOP가 적용되지 않고, 트랜잭션도 적용되지 않는다.



AOP를 적용하면 스프링은 대상 객체 대신에 프록시를 스프링 빈으로 등록한다. 

따라서 스프링은 의존관계 주입시에 항상 실제 객체 대신에 프록시 객체를 주입한다. 

프록시 객체가 주입되기 때문에 대상 객체를 직접 호출하는 문제는 일반 적으로 발생하지 않는다. 

하지만 **대상 객체의 내부에서 메서드 호출이 발생하면 프록시를 거치지 않고 대상 객체를 직접 호출하는 문제가 발생**한다. 

이렇게 되면 `@Transactional` 이 있어도 트랜잭션이 적용되지 않는다.














