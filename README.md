# Dorado

Http Restful API framework implemention with Netty4 

## Features

* HTTP/1.1 and HTTP/1.0 protocol support 
* Http Long-connection supported (also Connection: keep-alive)
* Http Restful serialized (usually as json) in string body (With Gson)
* Http Short Connection on async mode by default
* Http request mapping variable support

Annotation | From 
--- | --- 
@HeaderParam |  http header 
@RequestParam | http url query string or http body key value pairs 
@PathVariable | http uri path vairable with {pathVariable} 
@RequestBody | http body 

* Http request mapping method params type support

Class Type | Default value | Description
--- | --- | --- 
int,short,long | 0 | primitive
float,double | 0.0d | primitive
Integer,Short,Long,Float,Double| null | wrapper class
String | null | string value
byte[] | null | http body bytes
InputStream | null | http body stream
Class | null | from http body serializer parsed

## Usage

* 一个简单的Http server

```java
public class Application {
	public static void main(String[] args) throws Exception {
		// create simple rest server
		DoradoServerBuilder.forPort(18888).
		scanPackages("com.rtbstack.demo.controller", 
				"com.rtbstack.demo.controller1")
				.build().start();
	}
}
```

* 更多定制参数的Http server

```java

public class Application {
	public static void main(String[] args) throws Exception {
		// create simple rest server
		DoradoServerBuilder.forPort(18888).acceptors(2).ioWorkers(4)
				.minWorkers(10).maxWorkers(100)
				.backlog(1024)
				.maxConnection(500).maxPendingRequest(10000)
				.maxIdleTime(120).sendBuffer(256 * 1024)
				.recvBuffer(256 * 1024)
				.scanPackages("com.rtbstack.demo.controller",
						"com.rtbstack.demo.controller1")
		   .build().start();
	}
}
```

* Controller

```java
@Controller
@Path("/hello")
public class HelloWorldController {

	@Path("/greet/{greet}")
	public String greet(@PathVariable("greet") String greet) {
		return greet;
	}

	@GET
	@Path("/channel")
	public void bidding(HttpRequest request, HttpResponse response) {
		String channel = request.getParameter("channel");
		response.writeStringUtf8(channel);
	}

	@GET
	@Path("/image")
	//访问地址：/hello/image?url=xxxx
	public byte[] bytes(@RequestParam("url") String imageUrl) {
		return readBytesFromImageUrl(imageUrl);
	}
}

```

* Filter

```java
//TODO
```

* More examples 

Please visit https://github.com/javagossip/dorado/wiki/More-Examples

## Performance

java -server -Xmx4G -Xms4G -Xmn1536M -XX:+UseConcMarkSweepGC -XX:+UseParNewGC -XX:PermSize=256m -XX:MaxPermSize=256m -XX:+DisableExplicitGC

Http short connection
* Conccurent : 512 http connections 
* Qps : 40,000+
* Latency : < 10ms

Http long connection (Connection: keep-alive)
* Conccurent : 4096 http connections 
* Qps : 180,000 ~ 200,000
* Latency : < 50ms
