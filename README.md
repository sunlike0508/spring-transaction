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

### 전파 1

### 전파 2, 각자도생

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


### 전파 3 

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

스프링은 이해를 돕기 위해 논리 트랜잭션과 물리 트랜잭션이라는 개념을 나눈다.

논리 트랜잭션들은 하나의 물리 트랜잭션으로 묶인다.

물리 트랜잭션은 우리가 이해하는 실제 데이터베이스에 적용되는 트랜잭션을 뜻한다. 

실제 커넥션을 통해서 트랜 잭션을 시작( `setAutoCommit(false))` 하고, 실제 커넥션을 통해서 커밋, 롤백하는 단위이다.

논리 트랜잭션은 트랜잭션 매니저를 통해 트랜잭션을 사용하는 단위이다.

이러한 논리 트랜잭션 개념은 트랜잭션이 진행되는 중에 내부에 추가로 트랜잭션을 사용하는 경우에 나타난다. 

단순히 트랜잭션이 하나인 경우 둘을 구분하지는 않는다. (더 정확히는 `REQUIRED` 전파 옵션을 사용하는 경우에 나타나고, 이 옵션은 뒤에서 설명한다.)

그럼 왜 이렇게 논리 트랜잭션과 물리 트랜잭션을 나누어 설명하는 것일까?

트랜잭션이 사용중일 때 또 다른 트랜잭션이 내부에 사용되면 여러가지 복잡한 상황이 발생한다. 

이때 논리 트랜잭션 개 념을 도입하면 다음과 같은 단순한 원칙을 만들 수 있다.

**원칙**

**모든 논리 트랜잭션이 커밋되어야 물리 트랜잭션이 커밋된다.** 

**하나의 논리 트랜잭션이라도 롤백되면 물리 트랜잭션은 롤백된다.**

풀어서 설명하면 이렇게 된다. 모든 트랜잭션 매니저를 커밋해야 물리 트랜잭션이 커밋된다. 하나의 트랜잭션 매니저라도 롤백하면 물리 트랜잭션은 롤백된다.

<img width="687" alt="Screenshot 2024-10-05 at 11 42 14" src="https://github.com/user-attachments/assets/2b5b21c1-b403-4c90-b243-1038d31812bc">

## 전파 4

```java
@Test
void inner_commit() {
    log.info("외부 트랜잭션 시작");
    TransactionStatus outer = txManager.getTransaction(new  DefaultTransactionAttribute());
    log.info("outer.isNewTransaction()={}", outer.isNewTransaction());
    log.info("내부 트랜잭션 시작");
    
    TransactionStatus inner = txManager.getTransaction(new DefaultTransactionAttribute());
    log.info("inner.isNewTransaction()={}", inner.isNewTransaction()); 
    log.info("내부 트랜잭션 커밋");
    txManager.commit(inner);
    
    log.info("외부 트랜잭션 커밋");
    txManager.commit(outer);
}
```

```shell
ringtx.propagation.BasicTxTest   : outer.inNew() = true
ringtx.propagation.BasicTxTest   : 내부 트랜잭션 시작
DataSourceTransactionManager     : Participating in existing transaction
ringtx.propagation.BasicTxTest   : innter.inNew() = false
ringtx.propagation.BasicTxTest   : 내부 트랜잭션 커밋
ringtx.propagation.BasicTxTest   : 외부 트랜잭션 커밋
```
* 외부 트랜잭션이 수행중인데, 내부 트랜잭션을 추가로 수행했다.
* 외부 트랜잭션은 처음 수행된 트랜잭션이다.
* 이 경우 신규 트랜잭션( `isNewTransaction=true` )이 된다.
* 내부 트랜잭션을 시작하는 시점에는 이미 외부 트랜잭션이 진행중인 상태이다.
* 이 경우 내부 트랜잭션은 외부 트랜잭션에 참여한다.
* 트랜잭션 참여
  * 내부 트랜잭션이 외부 트랜잭션에 참여한다는 뜻은 내부 트랜잭션이 외부 트랜잭션을 그대로 이어 받아서 따른다는 뜻이다. 
  * 다른 관점으로 보면 외부 트랜잭션의 범위가 내부 트랜잭션까지 넓어진다는 뜻이다. 
  * 외부에서 시작된 물리적인 트랜잭션의 범위가 내부 트랜잭션까지 넓어진다는 뜻이다. 
  * 정리하면 **외부 트랜잭션과 내부 트랜잭션이 하나의 물리 트랜잭션으로 묶이는 것**이다. 

* 내부 트랜잭션은 이미 진행중인 외부 트랜잭션에 참여한다. 이 경우 신규 트랜잭션이 아니다 ( `isNewTransaction=false` ).
* 예제에서는 둘다 성공적으로 커밋했다.

내부 트랜잭션을 시작할 때 `Participating in existing transaction` 이라는 메시지를 확인할 수 있다. 

이 메시지는 내부 트랜잭션이 기존에 존재하는 외부 트랜잭션에 참여한다는 뜻이다.

실행 결과를 보면 외부 트랜잭션을 시작하거나 커밋할 때는 DB 커넥션을 통한 물리 트랜잭션을 시작( `manual commit` )하고, DB 커넥션을 통해 커밋 하는 것을 확인할 수 있다. 

그런데 내부 트랜잭션을 시작하거나 커밋할 때는 DB 커넥션을 통해 커밋하는 로그를 전혀 확인할 수 없다.

정리하면 외부 트랜잭션만 물리 트랜잭션을 시작하고, 커밋한다.

만약 내부 트랜잭션이 실제 물리 트랜잭션을 커밋하면 트랜잭션이 끝나버리기 때문에, 트랜잭션을 처음 시작한 외부 트랜잭션까지 이어갈 수 없다. 

따라서 내부 트랜잭션은 DB 커넥션을 통한 물리 트랜잭션을 커밋하면 안된다. 

스프링은 이렇게 여러 트랜잭션이 함께 사용되는 경우, **처음 트랜잭션을 시작한 외부 트랜잭션이 실제 물리 트랜 잭션을 관리**하도록 한다. 

이를 통해 트랜잭션 중복 커밋 문제를 해결한다.

<img width="692" alt="Screenshot 2024-10-05 at 11 54 10" src="https://github.com/user-attachments/assets/68c2dcb5-58a7-4407-ab1f-96a6fda06dc6">

<img width="686" alt="Screenshot 2024-10-05 at 11 53 17" src="https://github.com/user-attachments/assets/bae706b3-4df9-4558-9da4-8ab8680ba9f2">

**요청 흐름 - 외부 트랜잭션**

1. `txManager.getTransaction()` 를 호출해서 외부 트랜잭션을 시작한다.
2. 트랜잭션 매니저는 데이터소스를 통해 커넥션을 생성한다.
3. 생성한 커넥션을 수동 커밋 모드( `setAutoCommit(false)` )로 설정한다. 

**물리 트랜잭션 시작**

4. 트랜잭션 매니저는 트랜잭션 동기화 매니저에 커넥션을 보관한다.
5. 트랜잭션 매니저는 트랜잭션을 생성한 결과를 `TransactionStatus` 에 담아서 반환하는데, 여기에 신규 트랜잭션의 여부가 담겨 있다. `isNewTransaction` 를 통해 신규 트랜잭션 여부를 확인할 수 있다. 트랜잭션을 처음 시작했으므로 신규 트랜잭션이다.( `true` )
6. 로직1이 사용되고, 커넥션이 필요한 경우 트랜잭션 동기화 매니저를 통해 트랜잭션이 적용된 커넥션을 획득해서 사용한다.

**요청 흐름 - 내부 트랜잭션**

7. `txManager.getTransaction()` 를 호출해서 내부 트랜잭션을 시작한다.
8. 트랜잭션 매니저는 트랜잭션 동기화 매니저를 통해서 기존 트랜잭션이 존재하는지 확인한다.
9. 기존 트랜잭션이 존재하므로 기존 트랜잭션에 참여한다. 기존 트랜잭션에 참여한다는 뜻은 사실 아무것도 하지 않는다는 뜻이다.
   이미 기존 트랜잭션인 외부 트랜잭션에서 물리 트랜잭션을 시작했다. 그리고 물리 트랜잭션이 시작된 커넥 션을 트랜잭션 동기화 매니저에 담아두었다.
   따라서 이미 물리 트랜잭션이 진행중이므로 그냥 두면 이후 로직이 기존에 시작된 트랜잭션을 자연스럽게 사용하게 되는 것이다.
   이후 로직은 자연스럽게 트랜잭션 동기화 매니저에 보관된 기존 커넥션을 사용하게 된다.
10. 트랜잭션 매니저는 트랜잭션을 생성한 결과를 `TransactionStatus` 에 담아서 반환하는데, 여기에서 `isNewTransaction` 를 통해 신규 트랜잭션 여부를 확인할 수 있다. 여기서는 기존 트랜잭션에 참여했기 때문에 신규 트랜잭션이 아니다. ( `false` )
11. 로직2가 사용되고, 커넥션이 필요한 경우 트랜잭션 동기화 매니저를 통해 외부 트랜잭션이 보관한 커넥션을 획득해서 사용한다.

<img width="686" alt="Screenshot 2024-10-05 at 11 49 20" src="https://github.com/user-attachments/assets/109fd235-7420-4054-8a83-950fe9fdb02d">

**응답 흐름 - 내부 트랜잭션**

12. 로직2가 끝나고 트랜잭션 매니저를 통해 내부 트랜잭션을 커밋한다. 
13. 트랜잭션 매니저는 커밋 시점에 신규 트랜잭션 여부에 따라 다르게 동작한다. 이 경우 신규 트랜잭션이 아니기 때문에 실제 커밋을 호출하지 않는다. 이 부분이 중요한데, 실제 커넥션에 커밋이나 롤백을 호출하면 물 리 트랜잭션이 끝나버린다. 아직 트랜잭션이 끝난 것이 아니기 때문에 실제 커밋을 호출하면 안된다. 물리 트랜잭션은 외부 트랜잭션을 종료할 때 까지 이어져야한다.

**응답 흐름 - 외부 트랜잭션**

14. 로직1이 끝나고 트랜잭션 매니저를 통해 외부 트랜잭션을 커밋한다.
15. 트랜잭션 매니저는 커밋 시점에 신규 트랜잭션 여부에 따라 다르게 동작한다. 외부 트랜잭션은 신규 트랜잭션이다. 따라서 DB 커넥션에 실제 커밋을 호출한다.
16. 트랜잭션 매니저에 커밋하는 것이 논리적인 커밋이라면, 실제 커넥션에 커밋하는 것을 물리 커밋이라 할 수 있다. 실제 데이터베이스에 커밋이 반영되고, 물리 트랜잭션도 끝난다.

**핵심 정리**

여기서 핵심은 트랜잭션 매니저에 커밋을 호출한다고해서 항상 실제 커넥션에 물리 커밋이 발생하지는 않는다는 점이다.

신규 트랜잭션인 경우에만 실제 커넥션을 사용해서 물리 커밋과 롤백을 수행한다. 신규 트랜잭션이 아니면 실제 물리 커넥션을 사용하지 않는다.

이렇게 트랜잭션이 내부에서 추가로 사용되면 트랜잭션 매니저에 커밋하는 것이 항상 물리 커밋으로 이어지지  는다. 

그래서 이 경우 논리 트랜잭션과 물리 트랜잭션을 나누게 된다. 또는 외부 트랜잭션과 내부 트랜잭션으로  나누어 설명하기도 한다.

트랜잭션이 내부에서 추가로 사용되면, 트랜잭션 매니저를 통해 논리 트랜잭션을 관리하고, 모든 논리 트랜잭션이 커밋되면 물리 트랜잭션이 커밋된다고 이해하면 된다.

## 전파 5 - 외부 롤백

<img width="690" alt="Screenshot 2024-10-05 at 12 02 15" src="https://github.com/user-attachments/assets/01ffba3d-a672-4c63-a1cf-e07cf46cd2a3">

논리 트랜잭션이 하나라도 롤백되면 전체 물리 트랜잭션은 롤백된다.

따라서 이 경우 내부 트랜잭션이 커밋했어도, 내부 트랜잭션 안에서 저장한 데이터도 모두 함께 롤백된다.

<img width="707" alt="Screenshot 2024-10-05 at 12 08 29" src="https://github.com/user-attachments/assets/4816f934-b495-44d8-8df4-edb28749eaec">

**응답 흐름 - 내부 트랜잭션**

1. 로직2가 끝나고 트랜잭션 매니저를 통해 내부 트랜잭션을 커밋한다.
2. 트랜잭션 매니저는 커밋 시점에 신규 트랜잭션 여부에 따라 다르게 동작한다. 이 경우 신규 트랜잭션이 아니기 때문에 실제 커밋을 호출하지 않는다. 이 부분이 중요한데, 실제 커넥션에 커밋이나 롤백을 호출하면 물 리 트랜잭션이 끝나버린다. 아직 트랜잭션이 끝난 것이 아니기 때문에 실제 커밋을 호출하면 안된다. 물리 트랜잭션은 외부 트랜잭션을 종료할 때 까지 이어져야한다.

**응답 흐름 - 외부 트랜잭션**

3. 로직1이 끝나고 트랜잭션 매니저를 통해 외부 트랜잭션을 롤백한다.
4. 트랜잭션 매니저는 롤백 시점에 신규 트랜잭션 여부에 따라 다르게 동작한다. 외부 트랜잭션은 신규 트랜잭션이다. 따라서 DB 커넥션에 실제 롤백을 호출한다.
5. 트랜잭션 매니저에 롤백하는 것이 논리적인 롤백이라면, 실제 커넥션에 롤백하는 것을 물리 롤백이라 할 수 있다. 실제 데이터베이스에 롤백이 반영되고, 물리 트랜잭션도 끝난다.

## 전파 6 - 내부 롤백

<img width="688" alt="Screenshot 2024-10-05 at 12 10 01" src="https://github.com/user-attachments/assets/beaaea07-4fae-46b8-908d-c149598efe44">

이 상황은 겉으로 보기에는 단순하지만, 실제로는 단순하지 않다. 내부 트랜잭션이 롤백을 했지만, 내부 트랜잭션은 물리 트랜잭션에 영향을 주지 않는다. 

그런데 외부 트랜잭션은 커밋을 해버린다. 지금까지 학습한 내용을 돌아보면 외부 트랜잭션만 물리 트랜잭션에 영향을 주기 때문에 물리 트랜잭션이 커밋될 것 같다.

전체를 롤백해야 하는데, 스프링은 이 문제를 어떻게 해결할까? 지금부터 함께 살펴보자.

```shell
외부 트랜잭션 시작
Creating new transaction with name [null]:
PROPAGATION_REQUIRED,ISOLATION_DEFAULT
Acquired Connection [HikariProxyConnection@220038608 wrapping conn0] for JDBC transaction
Switching JDBC Connection [HikariProxyConnection@220038608 wrapping conn0] to manual commit
내부 트랜잭션 시작
Participating in existing transaction
내부 트랜잭션 롤백
Participating transaction failed - marking existing transaction as rollback-only
Setting JDBC transaction [HikariProxyConnection@220038608 wrapping conn0] rollback-only
외부 트랜잭션 커밋
Global transaction is marked as rollback-only but transactional code requested commit
Initiating transaction rollback
Rolling back JDBC transaction on Connection [HikariProxyConnection@220038608 wrapping conn0]
Releasing JDBC Connection [HikariProxyConnection@220038608 wrapping conn0] after transaction
```

외부 트랜잭션 시작

물리 트랜잭션을 시작한다. 내부 트랜잭션 시작

`Participating in existing transaction`

기존 트랜잭션에 참여한다.

내부 트랜잭션 롤백

`Participating transaction failed - marking existing transaction as rollback-only`

내부 트랜잭션을 롤백하면 실제 물리 트랜잭션은 롤백하지 않는다. 대신에 기존 트랜잭션을 롤백 전용으로 표시한다.

외부 트랜잭션 커밋

외부 트랜잭션을 커밋한다.

`Global transaction is marked as rollback-only`

커밋을 호출했지만, 전체 트랜잭션이 롤백 전용으로 표시되어 있다. 따라서 물리 트랜잭션을 롤백한다.

<img width="708" alt="Screenshot 2024-10-05 at 12 15 03" src="https://github.com/user-attachments/assets/611182c1-62ff-450a-ae2d-092da09e3980">

**응답 흐름 - 내부 트랜잭션**

1. 로직2가 끝나고 트랜잭션 매니저를 통해 내부 트랜잭션을 롤백한다. (로직2에 문제가 있어서 롤백한다고 가정한다.)
2. 트랜잭션 매니저는 롤백 시점에 신규 트랜잭션 여부에 따라 다르게 동작한다. 
   이 경우 신규 트랜잭션이 아니기 때문에 실제 롤백을 호출하지 않는다. 이 부분이 중요한데, 실제 커넥션에 커밋이나 롤백을 호출하면 물리 트랜잭션이 끝나버린다. 
    아직 트랜잭션이 끝난 것이 아니기 때문에 실제 롤백을 호출하면 안된다. 물리트랜잭션은 외부 트랜잭션을 종료할 때 까지 이어져야한다.
3. 내부 트랜잭션은 물리 트랜잭션을 롤백하지 않는 대신에 트랜잭션 동기화 매니저에 `rollbackOnly=true` 라는 표시를 해둔다.

**응답 흐름 - 외부 트랜잭션**

4. 로직1이 끝나고 트랜잭션 매니저를 통해 외부 트랜잭션을 커밋한다.
5. 트랜잭션 매니저는 커밋 시점에 신규 트랜잭션 여부에 따라 다르게 동작한다. 외부 트랜잭션은 신규 트랜잭션이다. 
   따라서 DB 커넥션에 실제 커밋을 호출해야 한다. 
   이때 먼저 트랜잭션 동기화 매니저에 롤백 전용 ( `rollbackOnly=true` ) 표시가 있는지 확인한다. 
   롤백 전용 표시가 있으면 물리 트랜잭션을 커밋하는 것이 아니라 롤백한다.
6. 실제 데이터베이스에 롤백이 반영되고, 물리 트랜잭션도 끝난다.
7. 트랜잭션 매니저에 커밋을 호출한 개발자 입장에서는 분명 커밋을 기대했는데 롤백 전용 표시로 인해 실제로는 롤백이 되어버렸다.
   이것은 조용히 넘어갈 수 있는 문제가 아니다. 시스템 입장에서는 커밋을 호출했지만 롤백이 되었다는 것은 분명하게 알려주어야 한다.
   예를 들어서 고객은 주문이 성공했다고 생각했는데, 실제로는 롤백이 되어서 주문이 생성되지 않은 것이다. 스프링은 이 경우 `UnexpectedRollbackException` 런타임 예외를 던진다. 
   그래서 커밋을 시도했지 만, 기대하지 않은 롤백이 발생했다는 것을 명확하게 알려준다.

**정리**

논리 트랜잭션이 하나라도 롤백되면 물리 트랜잭션은 롤백된다.

내부 논리 트랜잭션이 롤백되면 롤백 전용 마크를 표시한다.

외부 트랜잭션을 커밋할 때 롤백 전용 마크를 확인한다. 롤백 전용 마크가 표시되어 있으면 물리 트랜잭션을 롤백 하고, `UnexpectedRollbackException` 예외를 던진다.

## 전파7 - REQUIRES_NEW

이번에는 외부 트랜잭션과 내부 트랜잭션을 완전히 분리해서 사용하는 방법에 대해서 알아보자.

외부 트랜잭션과 내부 트랜잭션을 완전히 분리해서 각각 별도의 물리 트랜잭션을 사용하는 방법이다. 

그래서 커밋과 롤 백도 각각 별도로 이루어지게 된다.

이 방법은 내부 트랜잭션에 문제가 발생해서 롤백해도, 외부 트랜잭션에는 영향을 주지 않는다. 

반대로 외부 트랜잭션에 문제가 발생해도 내부 트랜잭션에 영향을 주지 않는다.

이 방법을 사용하는 구체적인 예는 이후에 알아보고 지금은 작동 원리를 이해해보자.

**REQUIRES_NEW**

<img width="684" alt="Screenshot 2024-10-05 at 12 29 05" src="https://github.com/user-attachments/assets/60383b28-6627-42ff-b47b-40de5bbe37e1">

이렇게 물리 트랜잭션을 분리하려면 내부 트랜잭션을 시작할 때 `REQUIRES_NEW` 옵션을 사용하면 된다. 

외부 트랜잭션과 내부 트랜잭션이 각각 별도의 물리 트랜잭션을 가진다.

별도의 물리 트랜잭션을 가진다는 뜻은 DB 커넥션을 따로 사용한다는 뜻이다.

이 경우 내부 트랜잭션이 롤백되면서 로직 2가 롤백되어도 로직 1에서 저장한 데이터에는 영향을 주지 않는다. 

최종적으로 로직2는 롤백되고, 로직1은 커밋된다.

**실행 결과 - inner_rollback_requires_new()** 

```shell
외부 트랜잭션 시작
Creating new transaction with name [null]:
PROPAGATION_REQUIRED,ISOLATION_DEFAULT
Acquired Connection [HikariProxyConnection@1064414847 wrapping conn0] for JDBC transaction
Switching JDBC Connection [HikariProxyConnection@1064414847 wrapping conn0] to manual commit
outer.isNewTransaction()=true
내부 트랜잭션 시작
Suspending current transaction, creating new transaction with name [null]
Acquired Connection [HikariProxyConnection@778350106 wrapping conn1] for JDBC transaction
Switching JDBC Connection [HikariProxyConnection@778350106 wrapping conn1] to manual commit
inner.isNewTransaction()=true
내부 트랜잭션 롤백
Initiating transaction rollback
Rolling back JDBC transaction on Connection [HikariProxyConnection@778350106 wrapping conn1]
Releasing JDBC Connection [HikariProxyConnection@778350106 wrapping conn1] after transaction
Resuming suspended transaction after completion of inner transaction
외부 트랜잭션 커밋
Initiating transaction commit
Committing JDBC transaction on Connection [HikariProxyConnection@1064414847 wrapping conn0]
Releasing JDBC Connection [HikariProxyConnection@1064414847 wrapping conn0] after transaction
```

**외부 트랜잭션 시작**

외부 트랜잭션을 시작하면서 `conn0` 를 획득하고 `manual commit` 으로 변경해서 물리 트랜잭션을 시작한다. 

외부 트랜잭션은 신규 트랜잭션이다.( `outer.isNewTransaction()=true` )

**내부 트랜잭션 시작**

내부 트랜잭션을 시작하면서 `conn1` 를 획득하고 `manual commit` 으로 변경해서 물리 트랜잭션을 시작한다. 

내부 트랜잭션은 외부 트랜잭션에 참여하는 것이 아니라, `PROPAGATION_REQUIRES_NEW` 옵션을 사용했기 때문에 완전히 새로운 신규 트랜잭션으로 생성된다.( `inner.isNewTransaction()=true` )

**내부 트랜잭션 롤백**

내부 트랜잭션을 롤백한다.

내부 트랜잭션은 신규 트랜잭션이기 때문에 실제 물리 트랜잭션을 롤백한다. 내부 트랜잭션은 `conn1` 을 사용하므로 `conn1` 에 물리 롤백을 수행한다.

**외부 트랜잭션 커밋**

외부 트랜잭션을 커밋한다.

외부 트랜잭션은 신규 트랜잭션이기 때문에 실제 물리 트랜잭션을 커밋한다. 외부 트랜잭션은 `conn0` 를 사용하므로 `conn0` 에 물리 커밋을 수행한다.

<img width="694" alt="Screenshot 2024-10-05 at 12 29 10" src="https://github.com/user-attachments/assets/da3a6d31-fa37-4f87-acca-0a2db75a224d">

**요청 흐름 - 외부 트랜잭션**

1. `txManager.getTransaction()` 를 호출해서 외부 트랜잭션을 시작한다.
2. 트랜잭션 매니저는 데이터소스를 통해 커넥션을 생성한다.
3. 생성한 커넥션을 수동 커밋 모드( `setAutoCommit(false)` )로 설정한다. - **물리 트랜잭션 시작**
4. 트랜잭션 매니저는 트랜잭션 동기화 매니저에 커넥션을 보관한다.
5. 트랜잭션 매니저는 트랜잭션을 생성한 결과를 `TransactionStatus` 에 담아서 반환하는데, 여기에 신규
   트랜잭션의 여부가 담겨 있다. `isNewTransaction` 를 통해 신규 트랜잭션 여부를 확인할 수 있다. 트랜
   잭션을 처음 시작했으므로 신규 트랜잭션이다.( `true` )
6. 로직1이 사용되고, 커넥션이 필요한 경우 트랜잭션 동기화 매니저를 통해 트랜잭션이 적용된 커넥션을 획득
   해서 사용한다.

**요청 흐름 - 내부 트랜잭션**

7. **REQUIRES_NEW 옵션**과 함께 `txManager.getTransaction()` 를 호출해서 내부 트랜잭션을 시작
   한다.
   트랜잭션 매니저는 `REQUIRES_NEW` 옵션을 확인하고, 기존 트랜잭션에 참여하는 것이 아니라 새로운 트 랜잭션을 시작한다.
8. 트랜잭션 매니저는 데이터소스를 통해 커넥션을 생성한다.
9. 생성한 커넥션을 수동 커밋 모드( `setAutoCommit(false)` )로 설정한다. - 

**물리 트랜잭션 시작**

10. 트랜잭션 매니저는 트랜잭션 동기화 매니저에 커넥션을 보관한다.
    이때 `con1` 은 잠시 보류되고, 지금부터는 `con2` 가 사용된다. (내부 트랜잭션을 완료할 때 까지 `con2` 가 사용된다.)
11. 트랜잭션 매니저는 신규 트랜잭션의 생성한 결과를 반환한다. `isNewTransaction == true`
12. 로직2가 사용되고, 커넥션이 필요한 경우 트랜잭션 동기화 매니저에 있는 `con2` 커넥션을 획득해서 사용한
    다.

<img width="697" alt="Screenshot 2024-10-05 at 12 32 12" src="https://github.com/user-attachments/assets/1f1c9031-d07b-4b2f-bb22-590240e55319">

**응답 흐름 - 내부 트랜잭션**
1. 로직2가 끝나고 트랜잭션 매니저를 통해 내부 트랜잭션을 롤백한다. (로직2에 문제가 있어서 롤백한다고 가
   정한다.)
2. 트랜잭션 매니저는 롤백 시점에 신규 트랜잭션 여부에 따라 다르게 동작한다. 현재 내부 트랜잭션은 신규 트
   랜잭션이다. 따라서 실제 롤백을 호출한다.
3. 내부 트랜잭션이 `con2` 물리 트랜잭션을 롤백한다.
   트랜잭션이 종료되고, `con2` 는 종료되거나, 커넥션 풀에 반납된다. 이후에 `con1` 의 보류가 끝나고, 다시 `con1` 을 사용한다.

**응답 흐름 - 외부 트랜잭션**

4. 외부 트랜잭션에 커밋을 요청한다.
5. 외부 트랜잭션은 신규 트랜잭션이기 때문에 물리 트랜잭션을 커밋한다.
6. 이때 `rollbackOnly` 설정을 체크한다. `rollbackOnly` 설정이 없으므로 커밋한다.
7. 본인이 만든 `con1` 커넥션을 통해 물리 트랜잭션을 커밋한다.
   트랜잭션이 종료되고, `con1` 은 종료되거나, 커넥션 풀에 반납된다.

**정리**

* `REQUIRES_NEW` 옵션을 사용하면 물리 트랜잭션이 명확하게 분리된다.
* `REQUIRES_NEW` 를 사용하면 데이터베이스 커넥션이 동시에 2개 사용된다는 점을 주의해야 한다.

## 전파8 - 다양한 전파 옵션

스프링은 다양한 트랜잭션 전파 옵션을 제공한다. 전파 옵션에 별도의 설정을 하지 않으면 `REQUIRED` 가 기본으로 사용된다.

참고로 실무에서는 대부분 `REQUIRED` 옵션을 사용한다. 

그리고 아주 가끔 `REQUIRES_NEW` 을 사용하고, 나머지는 거의 사용하지 않는다. 

그래서 나머지 옵션은 이런 것이 있다는 정도로만 알아두고 필요할 때 찾아보자.

**REQUIRED**

가장 많이 사용하는 기본 설정이다. 기존 트랜잭션이 없으면 생성하고, 있으면 참여한다. 트랜잭션이 필수라는 의미로 이해하면 된다. (필수이기 때문에 없으면 만들고, 있으면 참여한다.)

기존 트랜잭션 없음: 새로운 트랜잭션을 생성한다. 기존 트랜잭션 있음: 기존 트랜잭션에 참여한다.

**REQUIRES_NEW**

항상 새로운 트랜잭션을 생성한다.

기존 트랜잭션 없음: 새로운 트랜잭션을 생성한다. 기존 트랜잭션 있음: 새로운 트랜잭션을 생성한다.

**SUPPORT**

트랜잭션을 지원한다는 뜻이다. 기존 트랜잭션이 없으면, 없는대로 진행하고, 있으면 참여한다.

기존 트랜잭션 없음: 트랜잭션 없이 진행한다. 기존 트랜잭션 있음: 기존 트랜잭션에 참여한다.

**NOT_SUPPORT**

트랜잭션을 지원하지 않는다는 의미이다.

기존 트랜잭션 없음: 트랜잭션 없이 진행한다.

기존 트랜잭션 있음: 트랜잭션 없이 진행한다. (기존 트랜잭션은 보류한다)

**MANDATORY**

의무사항이다. 트랜잭션이 반드시 있어야 한다. 기존 트랜잭션이 없으면 예외가 발생한다.

기존 트랜잭션 없음: `IllegalTransactionStateException` 예외 발생 기존 트랜잭션 있음: 기존 트랜잭션에 참여한다.

**NEVER**

트랜잭션을 사용하지 않는다는 의미이다. 기존 트랜잭션이 있으면 예외가 발생한다. 기존 트랜잭션도 허용하지 않는 강한 부정의 의미로 이해하면 된다.

기존 트랜잭션 없음: 트랜잭션 없이 진행한다.

기존 트랜잭션 있음: `IllegalTransactionStateException` 예외 발생

**NESTED**

기존 트랜잭션 없음: 새로운 트랜잭션을 생성한다.

기존 트랜잭션 있음: 중첩 트랜잭션을 만든다.

중첩 트랜잭션은 외부 트랜잭션의 영향을 받지만, 중첩 트랜잭션은 외부에 영향을 주지 않는다. 중첩 트랜잭션이 롤백 되어도 외부 트랜잭션은 커밋할 수 있다.

외부 트랜잭션이 롤백 되면 중첩 트랜잭션도 함께 롤백된다.

**참고**

JDBC savepoint 기능을 사용한다. DB 드라이버에서 해당 기능을 지원하는지 확인이 필요하다. 중첩 트랜잭션은 JPA에서는 사용할 수 없다.

**트랜잭션 전파와 옵션**

`isolation` , `timeout` , `readOnly` 는 트랜잭션이 처음 시작될 때만 적용된다. 트랜잭션에 참여하는 경우에는 적용되지 않는다.

예를 들어서 `REQUIRED` 를 통한 트랜잭션 시작, `REQUIRES_NEW` 를 통한 트랜잭션 시작 시점에만 적용된다.






