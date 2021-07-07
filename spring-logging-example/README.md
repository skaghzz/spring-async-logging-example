# Asynchronous logging example on spring boot using logback

## Summary
비동기식 로깅에 대해 알아봅시다.   
spring boot에서 logback을 이용하여 비동기식 로깅하는 방법에 대해 알아봅시다.

## What is asynchronous logging
- synchronously file write 하면 file writing에 대한 딜레이가 발생하게 된다.   
- file i/o는 인메모리 작업이 아니기 때문에 딜레이가 커진다.
- 비동기식 로깅은 '로그 발생'과 '로그 쓰기'를 분리시킨다.
  - thread A는 로그가 발생하면 인메모리 큐에 집어넣기만 한다.
  - thread B는 큐에서 데이터를 꺼내서 file write만 수행한다.
- logback에서 비동기식 로깅을 위한 방법을 지원해준다.

## What is logback
- looging의 기능을 제공하는 프로젝트   
- log4j 에서 떨어져 나온 프로젝트라서 log4j와 유사하지만 기능/성능이 더 좋다.   
- slf4j를 지원하기 떄문에 사용자는 쉽게 log4j를 logback으로 변경할 수 있다.   
- spring boot에선 spring-boot-starter-logging을 추가하여 이용가능하다.(spring-boot-starter-web에 포함되어있다)

## Feature of logback AsyncAppender
- 비동기식으로 로그를 남긴다.
- AsyncAppender는 큐에 있는 쓰기 대기상태의 로그 메시지를 꺼내오는 event dispatcher 역할이라서 실제 로그남기는 건 appender를 참조시켜줘야 한다.
- AsyncAppeder가 생성한 worker thread가 큐에서 메시지 하나 꺼내서 AsyncAppender에 연결된 appender에 넘겨주면(dispatch) appender가 로그를 write하는 작업을 알아서(비동기)로 처리한다.
- AsyncAppender는 발생한 로그를 BlockingQueue에 보관한다.
- 큐의 용량이 80%가 차면 TRACE, DEBUG, INFO 수준의 로그는 큐에서 제거한다.
- 서버가 멈추거나 재배포 될 때는 maxFlushTime 만큼 큐에 남아있던 걸 처리하고 끝난다.
    - maxFlushTime 을 0으로 하면 queue를 모두 처리할떄까지 기다리게 된다.
- AsyscAppender의 속성값은 아래와 같다
  - queueSize : BlockingQueue 사이즈, default는 256이다.
  - discardingThreshold : 큐 사이즈 몇 % 남았을 때 log들을 drop 시킬지 수치
  - includeCallerData : 로그 호출한 곳에 대한 정보를 표시할지 여부
  - maxFlushTime : 위에서 기술함
  - neverBlock : 큐가 다 찼을 떄, 메시지를 넣을지 말지 결정. true로 하면 메시지 넣는것을 기다리지 않고 날려버리지만 속도는 빨라짐

## Related files
[logback-local.xml](../spring-logging-example/src/main/resources/logback-local.xml)   
[application.properites](../spring-logging-example/src/main/resources/application.properties)

## Pros and Cons of asynchronous logging
- 장점
  - application 입장에서는 로그 발생 시 file i/o 작업이 사라지므로 빨라진다
- 단점
  - 비동기식이라 서비스의 성공 != 로그기록 성공이다. 서비스가 성공했지만 다양한 이유(큐메모리 초과, 시스템 종교 등)떄문에 로그의 손실이 발생할 수 있다.
  - 이에 따라 큐 메모리 관리 등 관리대상이 늘어나게 된다.

## 결론
비동기식 로깅은 데이터의 로깅이 많이 필요하고, 속도가 더 중요하다면 좋은 선택지라고 생각된다.
클래스마다 로깅 방법을 지정할 수 있기 떄문에 부분적으로 적용해보는 것도 좋은 방법일 것 같다.

## reference Link
- [공식문서](http://logback.qos.ch/manual/appenders.html#AsyncAppender)
- [kangwoojin님 블로그](https://kangwoojin.github.io/programing/logback-async-appender/)
