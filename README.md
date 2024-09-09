spring boot를 사용한 간단한 게시판 구현

게시물 페이지네이션: 많은 게시물을 여러페이지로 나누어 보여주는 방법.
<br><br>
장점: 사용자는 한번에 너무 많은 정보를 확인하지 않아도 필요한 게시물을 쉽게 찾을 수 있다.
<br><br><br>
JPA 인터페이스 모음 -> 실제적으로 구현된 것이 아니라, 구현된 클래스와 매핑을 해주기 위해 사용되는 프레임워크.
<br><br>
JPA를 구현한 대표적인 오픈소스로는 HIBERNATE가 있다.
<br><br>
HIBERNATE란?
<br><br>
하이버네이트는 자바 언어를 위한 ORM 프레임워크며, JPA의 구현체로, JPA 인터페이스를 구현하며, 내부적으로 JDBC API를 사용한다.
JPA는 관계형 데이터베이스와 객체의 패러다임 불일치 문제를 해결할 수 있다는 점과 영속성 컨텍스트(엔티티를 영구 저정하는 환경) 제공이 큰 특징.
<br><br><br>
HTTP 에러 정리
<br><br>
<img src = "https://github.com/user-attachments/assets/c1915edd-027a-4ce6-b1b7-ba8c15fd4b50" width="500">
<br><br><br>
static 변수: 모든 클래스 인스턴스가 공유하는 변수로, 클래스 자체에 속합니다.

static 메서드: 클래스 인스턴스 없이 호출할 수 있는 메서드로, 오직 static 변수나 메서드만 접근할 수 있습니다.

static 블록: 클래스가 처음 로드될 때 한 번만 실행되는 블록으로, 초기화 작업에 사용됩니다.

static 클래스: 내부 클래스에서 사용되며, 바깥 클래스의 인스턴스와 독립적으로 존재할 수 있습니다.
<br><br><br>
주요 Spring 어노테이션

      @Service
      용도: 주로 서비스 클래스를 정의하는 데 사용됩니다.

      @Repository
      용도: 주로 데이터베이스 관련 작업을 수행하는 DAO 클래스에 사용됩니다.

      @Controller
      용도: HTTP 요청을 처리하고, 모델을 구성하여 뷰를 반환합니다.

      @RestController
      용도: RESTful 웹 서비스의 컨트롤러를 정의합니다. JSON 또는 XML 형태로 데이터를 반환합니다.

      @Autowired
      용도: 스프링 컨테이너에서 관리하는 빈을 자동으로 주입받습니다.

      @RequestMapping
      HTTP 요청을 처리할 메서드를 정의합니다.
      용도: 요청 URL, HTTP 메서드 등을 매핑합니다.

      @GetMapping, @PostMapping, @PutMapping, @DeleteMapping
      @RequestMapping의 단축형으로, 각각 HTTP GET, POST, PUT, DELETE 요청을 매핑합니다.
      용도: 각 HTTP 메서드에 대해 요청을 처리합니다.

      @RequestParam
      요청 파라미터를 메서드 파라미터로 바인딩합니다.
      용도: HTTP 요청의 쿼리 파라미터를 처리합니다.

      @PathVariable
      URI 경로 변수 값을 메서드 파라미터로 바인딩합니다.
      용도: URL 경로에서 변수 값을 추출합니다.

      @RequestBody
      HTTP 요청 본문을 메서드 파라미터로 바인딩합니다.
      용도: 요청 본문을 객체로 변환합니다.



