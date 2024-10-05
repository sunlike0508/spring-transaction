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


***`@Transactional` 애노테이션이 특정 클래스나 메서드에 하나라도 있으면 트랜잭션 AOP는 프록시를 만들어서 스프링 컨테이너에 등록한다.*** 

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

이것만 기억하면 스프링에서 발생하는 대부분의 우선순위를 쉽게 기억할 수 있다. 

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

앞서 배운 것처럼 `@Transactional` 을 적용하면 프록시 객체가 요청을 먼저 받아서 트랜잭션을 처리하고, 실제 객체를 호출해준다.

따라서 트랜잭션을 적용하려면 항상 프록시를 통해서 대상 객체(Target)을 호출해야 한다.

이렇게 해야 프록시에서 먼저 트랜잭션을 적용하고, 이후에 대상 객체를 호출하게 된다.

만약 프록시를 거치지 않고 대상 객체를 직접 호출하게 되면 AOP가 적용되지 않고, 트랜잭션도 적용되지 않는다.

<img width="927" alt="Screenshot 2024-10-03 at 16 26 45" src="https://github.com/user-attachments/assets/de455104-f2f2-4354-b70a-56762bddec8d">

AOP를 적용하면 스프링은 대상 객체 대신에 프록시를 스프링 빈으로 등록한다. 

따라서 스프링은 의존관계 주입시에 항상 실제 객체 대신에 프록시 객체를 주입한다. 

프록시 객체가 주입되기 때문에 대상 객체를 직접 호출하는 문제는 일반적으로 발생하지 않는다. 

하지만 **대상 객체의 내부에서 메서드 호출이 발생하면 프록시를 거치지 않고 대상 객체를 직접 호출하는 문제가 발생**한다. 

이렇게 되면 `@Transactional` 이 있어도 트랜잭션이 적용되지 않는다.

`@Transactional` 이 하나라도 있으면 트랜잭션 프록시 객체가 만들어진다. 

그리고 `callService` 빈을 주입 받으면 트랜잭션 프록시 객체가 대신 주입된다.

* **internalCall() 실행**
 
<img width="910" alt="Screenshot 2024-10-03 at 16 37 05" src="https://github.com/user-attachments/assets/49337e10-940f-4d12-9104-b2d24a4c39b7">

1. 클라이언트인 테스트 코드는 `callService.internal()` 을 호출한다. 여기서 `callService` 는 트랜잭션 프록시이다.
2. `callService` 의 트랜잭션 프록시가 호출된다.
3. `internal()` 메서드에 `@Transactional` 이 붙어 있으므로 트랜잭션 프록시는 트랜잭션을 적용한다. 
4. 트랜잭션 적용 후 실제 `callService` 객체 인스턴스의 `internal()` 을 호출한다.

실제 `callService` 가 처리를 완료하면 응답이 트랜잭션 프록시로 돌아오고, 트랜잭션 프록시는 트랜잭션을 완료한다.


* **externalCall() 실행**

<img width="912" alt="Screenshot 2024-10-03 at 16 40 01" src="https://github.com/user-attachments/assets/79c4ebf1-9c8f-416f-9236-732be5456357">

1. 클라이언트인 테스트 코드는 `callService.external()` 을 호출한다. 여기서 `callService` 는 트랜 잭션 프록시이다.
2. `callService` 의 트랜잭션 프록시가 호출된다.
3. `external()` 메서드에는 `@Transactional` 이 없다. 따라서 트랜잭션 프록시는 트랜잭션을 적용하지 않는다.
4. 트랜잭션 적용하지 않고, 실제 `callService` 객체 인스턴스의 `external()` 을 호출한다.
5. `external()` 은 내부에서 `internal()` 메서드를 호출한다. 그런데 여기서 문제가 발생한다.

**문제 원인**

자바 언어에서 메서드 앞에 별도의 참조가 없으면 `this` 라는 뜻으로 자기 자신의 인스턴스를 가리킨다.

결과적으로 자기 자신의 내부 메서드를 호출하는 `this.internal()` 이 되는데, 여기서 `this` 는 자기 자신을 가리키 므로, 실제 대상 객체( `target` )의 인스턴스를 뜻한다. 

결과적으로 이러한 내부 호출은 프록시를 거치지 않는다. 

따라서 트랜잭션을 적용할 수 없다. 결과적으로 `target` 에 있는 `internal()` 을 직접 호출하게 된 것이다.

**프록시 방식의 AOP 한계**

`@Transactional` 를 사용하는 트랜잭션 AOP는 프록시를 사용한다. 프록시를 사용하면 메서드 내부 호출에 프록시를 적용할 수 없다.

그렇다면 이 문제를 어떻게 해결할 수 있을까?

가장 단순한 방법은 내부 호출을 피하기 위해 `internal()` 메서드를 별도의 클래스로 분리하는 것이다.

## 트랜잭션 AOP 주의 사항 - 프록시 내부 호출2

### 클래스로 분리

<img width="936" alt="Screenshot 2024-10-03 at 16 48 30" src="https://github.com/user-attachments/assets/bee78843-17e4-4ca8-8061-739408167be9">

실제 호출되는 흐름을 분석해보자.

1. 클라이언트인 테스트 코드는 `callService.external()` 을 호출한다.
2. `callService` 는 실제 `callService` 객체 인스턴스이다.
3. `callService` 는 주입 받은 `internalService.internal()` 을 호출한다.
4. `internalService` 는 트랜잭션 프록시이다. `internal()` 메서드에 `@Transactional` 이 붙어 있으 므로 트랜잭션 프록시는 트랜잭션을 적용한다.
5. 트랜잭션 적용 후 실제 `internalService` 객체 인스턴스의 `internal()` 을 호출한다.

### public 메서드만 트랜잭션 적용

스프링의 트랜잭션 AOP 기능은 `public` 메서드에만 트랜잭션을 적용하도록 기본 설정이 되어있다. 

그래서 `protected` , `private` , `package-visible` 에는 트랜잭션이 적용되지 않는다. 

생각해보면 `protected` ,`package-visible` 도 외부에서 호출이 가능하다. 

따라서 이 부분은 앞서 설명한 프록시의 내부 호출과는 무관하고, 스프링이 막아둔 것이다.

스프링이 `public` 에만 트랜잭션을 적용하는 이유는 다음과 같다. 
```java
@Transactional
public class Hello {
    public method1();
    method2();
    protected method3();
    private method4();
}
```
이렇게 클래스 레벨에 트랜잭션을 적용하면 모든 메서드에 트랜잭션이 걸릴 수 있다. 

그러면 트랜잭션을 의도하지 않는 곳까지 트랜잭션이 과도하게 적용된다. 

트랜잭션은 주로 비즈니스 로직의 시작점에 걸기 때문에 대부분 외부에 열어준 곳을 시작점으로 사용한다. 

이런 이유로 `public` 메서드에만 트랜잭션을 적용하도록 설정되어 있다.

앞서 실행했던 코드를 `package-visible` 로 변경해보면 적용되지 않는 것을 확인할 수 있다.

참고로 `public` 이 아닌곳에 `@Transactional` 이 붙어 있으면 예외가 발생하지는 않고, 트랜잭션 적용만 무시된다.

**참고: 스프링 부트 3.0**

스프링 부트 3.0 부터는 `protected` , `package-visible` (default 접근제한자)에도 트랜잭션이 적용된다.

## 트랜잭션 AOP 주의 사항 - 초기화 시점

스프링 초기화 시점에는 트랜잭션 AOP가 적용되지 않을 수 있다.

초기화 코드(예: `@PostConstruct` )와 `@Transactional` 을 함께 사용하면 트랜잭션이 적용되지 않는다.

```java
@PostConstruct
@Transactional
public void initV1() {
    log.info("Hello init @PostConstruct");
}
```
왜냐하면 초기화 코드가 먼저 호출되고, 그 다음에 트랜잭션 AOP가 적용되기 때문이다. 따라서 초기화 시점에는 해당 메서드에서 트랜잭션을 획득할 수 없다.

가장 확실한 대안은 `ApplicationReadyEvent` 이벤트를 사용하는 것이다. 

```java
@EventListener(value = ApplicationReadyEvent.class)
@Transactional
public void init2() {
    log.info("Hello init ApplicationReadyEvent");
}
```
이 이벤트는 트랜잭션 AOP를 포함한 스프링이 컨테이너가 완전히 생성되고 난 다음에 이벤트가 붙은 메서드를 호출해 준다. 

따라서 `init2()` 는 트랜잭션이 적용된 것을 확인할 수 있다.

## 트랜잭션 옵션 소개

**value, transactionManager**

트랜잭션을 사용하려면 먼저 스프링 빈에 등록된 어떤 트랜잭션 매니저를 사용할지 알아야 한다. 

생각해보면 코드로 직접 트랜잭션을 사용할 때 분명 트랜잭션 매니저를 주입 받아서 사용했다. 

`@Transactional` 에서도 트랜잭션 프록시가 사용할 트랜잭션 매니저를 지정해주어야 한다.

사용할 트랜잭션 매니저를 지정할 때는 `value` , `transactionManager` 둘 중 하나에 트랜잭션 매니저의 스프링 빈의 이름을 적어주면 된다.

이 값을 생략하면 기본으로 등록된 트랜잭션 매니저를 사용하기 때문에 대부분 생략한다. 

그런데 사용하는 트랜잭션 매니저가 둘 이상이라면 다음과 같이 트랜잭션 매니저의 이름을 지정해서 구분하면 된다.

```java
 public class TxService {
     @Transactional("memberTxManager")
     public void member() {...}
     @Transactional("orderTxManager")
     public void order() {...}
 }
```

참고로 애노테이션에서 속성이 하나인 경우 위 예처럼 `value` 는 생략하고 값을 바로 넣을 수 있다.

**rollbackFor**

예외 발생시 스프링 트랜잭션의 기본 정책은 다음과 같다.

언체크 예외인 `RuntimeException` , `Error` 와 그 하위 예외가 발생하면 롤백한다. 

체크 예외인 `Exception` 과 그 하위 예외들은 커밋한다.

이 옵션을 사용하면 기본 정책에 추가로 어떤 예외가 발생할 때 롤백할지 지정할 수 있다. 

```java
@Transactional(rollbackFor = Exception.class)
```

예를 들어서 이렇게 지정하면 체크 예외인 `Exception` 이 발생해도 롤백하게 된다. (하위 예외들도 대상에 포함된다.) 

`rollbackForClassName` 도 있는데, `rollbackFor` 는 예외 클래스를 직접 지정하고, `rollbackForClassName` 는 예외 이름을 문자로 넣으면 된다.

**noRollbackFor**
앞서 설명한 `rollbackFor` 와 반대이다. 기본 정책에 추가로 어떤 예외가 발생했을 때 롤백하면 안되는지 지정할 수 있다.

예외 이름을 문자로 넣을 수 있는 `noRollbackForClassName` 도 있다.

**propagation**

트랜잭션 전파에 대한 옵션이다. 자세한 내용은 뒤에서 설명한다.

**isolation**

트랜잭션 격리 수준을 지정할 수 있다. 

기본 값은 데이터베이스에서 설정한 트랜잭션 격리 수준을 사용하는 `DEFAULT` 이다. 

대부분 데이터베이스에서 설정한 기준을 따른다. 

애플리케이션 개발자가 트랜잭션 격리 수준을 직접 지정하는 경우는 드물다.

* `DEFAULT` : 데이터베이스에서 설정한 격리 수준을 따른다. 
* `READ_UNCOMMITTED` : 커밋되지 않은 읽기 
* `READ_COMMITTED` : 커밋된 읽기
* `REPEATABLE_READ` : 반복 가능한 읽기
* `SERIALIZABLE` : 직렬화 가능

**timeout**

트랜잭션 수행 시간에 대한 타임아웃을 초 단위로 지정한다. 

기본 값은 트랜잭션 시스템의 타임아웃을 사용한다. 

운영 환경에 따라 동작하는 경우도 있고 그렇지 않은 경우도 있기 때문에 꼭 확인하고 사용해야 한다.

`timeoutString` 도 있는데, 숫자 대신 문자 값으로 지정할 수 있다. 

**label**

트랜잭션 애노테이션에 있는 값을 직접 읽어서 어떤 동작을 하고 싶을 때 사용할 수 있다. 일반적으로 사용하지 않는다.

**readOnly**

트랜잭션은 기본적으로 읽기 쓰기가 모두 가능한 트랜잭션이 생성된다.

`readOnly=true` 옵션을 사용하면 읽기 전용 트랜잭션이 생성된다. 

이 경우 등록, 수정, 삭제가 안되고 읽기 기능만 작동한다. (드라이버나 데이터베이스에 따라 정상 동작하지 않는 경우도 있다.) 

그리고 `readOnly` 옵션을 사용하면 읽 기에서 다양한 성능 최적화가 발생할 수 있다. 

`readOnly` 옵션은 크게 3곳에서 적용된다.

**1. 프레임워크**
JdbcTemplate은 읽기 전용 트랜잭션 안에서 변경 기능을 실행하면 예외를 던진다.

JPA(하이버네이트)는 읽기 전용 트랜잭션의 경우 커밋 시점에 플러시를 호출하지 않는다. 

읽기 전용이니 변경에 사용되는 플러시를 호출할 필요가 없다. 

추가로 변경이 필요 없으니 변경 감지를 위한 스냅샷 객체도 생성하지 않는다. 

이렇게 JPA에서는 다양한 최적화가 발생한다.
JPA 관련 내용은 JPA를 더 학습해야 이해할 수 있으므로 지금은 이런 것이 있다 정도만 알아두자. 

**JDBC 드라이버**
참고로 여기서 설명하는 내용들은 DB와 드라이버 버전에 따라서 다르게 동작하기 때문에 사전에 확인이 필요하다.

읽기 전용 트랜잭션에서 변경 쿼리가 발생하면 예외를 던진다.

읽기, 쓰기(마스터, 슬레이브) 데이터베이스를 구분해서 요청한다. 

읽기 전용 트랜잭션의 경우 읽기(슬레이브) 데이터베이스의 커넥션을 획득해서 사용한다.

**데이터베이스**

데이터베이스에 따라 읽기 전용 트랜잭션의 경우 읽기만 하면 되므로, 내부에서 성능 최적화가 발생한다.

## 예외와 트랜잭션 커밋, 롤백 - 기본

예외가 발생했는데,내부에서 예외를 처리하지 못하고, 트랜잭션 범위(`@Transactional가 적용된 AOP`) 밖으로 예외를 던지면 어떻게 될까?

<img width="920" alt="Screenshot 2024-10-03 at 21 03 05" src="https://github.com/user-attachments/assets/a40bead1-ea24-4d2b-9a79-0cb3b37b2a93">

예외 발생시 스프링 트랜잭션 AOP는 예외의 종류에 따라 트랜잭션을 커밋하거나 롤백한다.

언체크 예외인 `RuntimeException` , `Error` 와 그 하위 예외가 발생하면 트랜잭션을 롤백한다. 

체크 예외인 `Exception` 과 그 하위 예외가 발생하면 트랜잭션을 커밋한다.

물론 정상 응답(리턴)하면 트랜잭션을 커밋한다.

## 예외와 트랜잭션 커밋, 롤백 - 활용

스프링은 왜 체크 예외는 커밋하고, 언체크(런타임) 예외는 롤백할까?

스프링 기본적으로 체크 예외는 비즈니스 의미가 있을 때 사용하고, 런타임(언체크) 예외는 복구 불가능한 예외로 가정 한다.

체크 예외: 비즈니스 의미가 있을 때 사용

언체크 예외: 복구 불가능한 예외

참고로 꼭 이런 정책을 따를 필요는 없다. 

그때는 앞서 배운 `rollbackFor` 라는 옵션을 사용해서 체크 예외도 롤백하면 된다.

그런데 비즈니스 의미가 있는 **비즈니스 예외**라는 것이 무슨 뜻일까? 간단한 예제로 알아보자.

**비즈니스 요구사항**

주문을 하는데 상황에 따라 다음과 같이 조치한다.

1. **정상**: 주문시 결제를 성공하면 주문 데이터를 저장하고 결제 상태를 `완료` 로 처리한다.
2. **시스템 예외**: 주문시 내부에 복구 불가능한 예외가 발생하면 전체 데이터를 롤백한다.
3. **비즈니스 예외**: 주문시 결제 잔고가 부족하면 주문 데이터를 저장하고, 결제 상태를 `대기` 로 처리한다.
4. 
이 경우 **고객에게 잔고 부족을 알리고 별도의 계좌로 입금하도록 안내한다.**

이때 결제 잔고가 부족하면 `NotEnoughMoneyException` 이라는 체크 예외가 발생한다고 가정하겠다. 

이 예외는 시 스템에 문제가 있어서 발생하는 시스템 예외가 아니다. 

시스템은 정상 동작했지만, 비즈니스 상황에서 문제가 되기 때문에 발생한 예외이다. 

더 자세히 설명하자면, 고객의 잔고가 부족한 것은 시스템에 문제가 있는 것이 아니다. 

오히려 시스 템은 문제 없이 동작한 것이고, 비즈니스 상황이 예외인 것이다. 이런 예외를 비즈니스 예외라 한다. 

그리고 비즈니스 예 외는 매우 중요하고, 반드시 처리해야 하는 경우가 많으므로 체크 예외를 고려할 수 있다.

## 스프링 트랜잭션 전파

### 전파 1, 각자도생

```shell
DataSourceTransactionManager     : Committing JDBC transaction on Connection [HikariProxyConnection@1231949725 wrapping conn0: url=jdbc:h2:mem:e9b42f96-4958-41c1-b3d2-532b7500858a user=SA]
DataSourceTransactionManager     : Releasing JDBC Connection [HikariProxyConnection@1231949725 wrapping conn0: url=jdbc:h2:mem:e9b42f96-4958-41c1-b3d2-532b7500858a user=SA] after transaction
hello.springtx.propagation.BasicTxTest   : transaction222 start
DataSourceTransactionManager     : Creating new transaction with name [null]: PROPAGATION_REQUIRED,ISOLATION_DEFAULT
DataSourceTransactionManager     : Acquired Connection [HikariProxyConnection@603273695 wrapping conn0: url=jdbc:h2:mem:e9b42f96-4958-41c1-b3d2-532b7500858a user=SA] for JDBC transaction
DataSourceTransactionManager     : Switching JDBC Connection [HikariProxyConnection@603273695 wrapping conn0: url=jdbc:h2:mem:e9b42f96-4958-41c1-b3d2-532b7500858a user=SA] to manual commit
hello.springtx.propagation.BasicTxTest   : transaction22 commit start
DataSourceTransactionManager     : Initiating transaction commit
DataSourceTransactionManager     : Committing JDBC transaction on Connection [HikariProxyConnection@603273695 wrapping conn0: url=jdbc:h2:mem:e9b42f96-4958-41c1-b3d2-532b7500858a user=SA]
DataSourceTransactionManager     : Releasing JDBC Connection [HikariProxyConnection@603273695 wrapping conn0: url=jdbc:h2:mem:e9b42f96-4958-41c1-b3d2-532b7500858a user=SA] after transaction
```

**트랜잭션1**
`Acquired Connection [HikariProxyConnection@1064414847 wrapping conn0] for JDBC
transaction`
트랜잭션1을 시작하고, 커넥션 풀에서 `conn0` 커넥션을 획득했다.
`Releasing JDBC Connection [HikariProxyConnection@1064414847 wrapping conn0] after transaction`
트랜잭션1을 커밋하고, 커넥션 풀에 `conn0` 커넥션을 반납했다.

**트랜잭션2**
`Acquired Connection [HikariProxyConnection@ 778350106 wrapping conn0] for JDBC
transaction`
트랜잭션2을 시작하고, 커넥션 풀에서 `conn0` 커넥션을 획득했다.
`Releasing JDBC Connection [HikariProxyConnection@ 778350106 wrapping conn0] after transaction`
트랜잭션2을 커밋하고, 커넥션 풀에 `conn0` 커넥션을 반납했다.

**주의!**
로그를 보면 트랜잭션1과 트랜잭션2가 같은 `conn0` 커넥션을 사용중이다. 이것은 중간에 커넥션 풀 때문에 그런 것이다. 

트랜잭션1은 `conn0` 커넥션을 모두 사용하고 커넥션 풀에 반납까지 완료했다. 이후에 트랜잭션2가 `conn0` 를 커넥션 풀에서 획득한 것이다. 

따라서 둘은 완전히 다른 커넥션으로 인지하는 것이 맞다.

그렇다면 둘을 구분할 수 있는 다른 방법은 없을까?

히카리 커넥션 풀에서 커넥션을 획득하면 실제 커넥션을 그대로 반환하는 것이 아니라 내부 관리를 위해 히카리 프록시 커넥션이라는 객체를 생성해서 반환한다. 

물론 내부에는 실제 커넥션이 포함되어 있다. 

이 객체의 주소를 확인하면 커넥션 풀에서 획득한 커넥션을 구분할 수 있다.

* HikariProxyConnection@1064414847
* HikariProxyConnection@778350106


히카리 커넥션풀이 반환해주는 커넥션을 다루는 프록시 객체의 주소가 트랜잭션1은 `HikariProxyConnection@1000000` 이고, 

트랜잭션2는 `HikariProxyConnection@2000000` 으로 서로 다른 것을 확인할 수 있다.

결과적으로 `conn0` 을 통해 커넥션이 재사용 된 것을 확인할 수 있고, `HikariProxyConnection@1064414847` ,

`HikariProxyConnection@778350106` 을 통해 각각 커넥션 풀에서 커넥션을 조회한 것을 확인할 수 있다.


<img width="700" alt="Screenshot 2024-10-05 at 11 28 13" src="https://github.com/user-attachments/assets/911b722b-8003-4834-9ad5-2a42d18c2a91">

<img width="726" alt="Screenshot 2024-10-05 at 11 28 18" src="https://github.com/user-attachments/assets/27cdfe51-2a8c-4710-89a2-d565b10d28ee">

트랜잭션이 각각 수행되면서 사용되는 DB 커넥션도 각각 다르다.

이 경우 트랜잭션을 각자 관리하기 때문에 전체 트랜잭션을 묶을 수 없다. 

예를 들어서 트랜잭션1이 커밋하고, 트랜잭션2가 롤백하는 경우 트랜잭션1에서 저장한 데이터는 커밋되고, 트랜잭션2에서 저장한 데이터는 롤백된다. 

다음 예제를 확인해보자.

<img width="695" alt="Screenshot 2024-10-05 at 11 32 51" src="https://github.com/user-attachments/assets/709f7303-f4c4-4c1f-b015-224bc5180921">


### 전파 2 

트랜잭션을 각각 사용하는 것이 아니라, 트랜잭션이 이미 진행중인데, 여기에 추가로 트랜잭션을 수행하면 어떻게 될까?

기존 트랜잭션과 별도의 트랜잭션을 진행해야 할까? 아니면 기존 트랜잭션을 그대로 이어 받아서 트랜잭션을 수행해야 할까?

이런 경우 어떻게 동작할지 결정하는 것을 트랜잭션 전파(propagation)라 한다. 참고로 스프링은 다양한 트랜잭션 전파 옵션을 제공한다.

지금부터 설명하는 내용은 트랜잭션 전파의 기본 옵션인 `REQUIRED` 를 기준으로 설명한다. 

옵션에 대한 내용은 마지막에 설명한다. 뒤에서 설명할 것이므로 참고만 해두자.


**외부 트랜잭션이 수행중인데, 내부 트랜잭션이 추가로 수행됨**

<img width="698" alt="Screenshot 2024-10-05 at 11 36 59" src="https://github.com/user-attachments/assets/dff92d37-fb0e-4bd5-ad37-34ccbbd4d2a2">

외부 트랜잭션이 수행중이고, 아직 끝나지 않았는데, 내부 트랜잭션이 수행된다.

외부 트랜잭션이라고 이름 붙인 것은 둘 중 상대적으로 밖에 있기 때문에 외부 트랜잭션이라 한다. 

처음 시작된 트랜잭션으로 이해하면 된다.

내부 트랜잭션은 외부에 트랜잭션이 수행되고 있는 도중에 호출되기 때문에 마치 내부에 있는 것 처럼 보여서 내부 트랜잭션이라 한다.

<img width="690" alt="Screenshot 2024-10-05 at 11 36 30" src="https://github.com/user-attachments/assets/bcaa6c50-0a21-4471-947a-55eb88326dd1">

스프링에서 이 경우 외부 트랜잭션과 내부 트랜잭션을 묶어서 하나의 트랜잭션을 만들어준다. 

내부 트랜잭션이 외 부 트랜잭션에 참여하는 것이다. 

이것이 기본 동작이고, 옵션을 통해 다른 동작방식도 선택할 수 있다. (다른 동작 방식은 뒤에 설명한다.)

**물리 트랜잭션, 논리 트랜잭션**

<img width="687" alt="Screenshot 2024-10-05 at 11 36 06" src="https://github.com/user-attachments/assets/de12c269-c5df-406e-ade9-d5a31cacbf0b">






























