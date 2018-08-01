# Dorado
Simple,Fast,Lightweight http restful server implemention with Netty4 and JDK1.8+

## Features

* HTTP/1.1 and HTTP/1.0 protocol support 
* Http Long-connection supported (also Connection: Keep-Alive)
* Http Restful serialized supported (JSON and google Protobuf)
* Http Uri route mapping support
* Unsupported multipart/form-data request
* Unsupported HTTPS request

## Maven

```xml
<dependency>
    <groupId>mobi.f2time</groupId>
    <artifactId>dorado</artifactId>
    <version>0.0.1-SNAPSHOT</version>
</dependency>
```

## Quick start

* Simplest rest server

```java
public class Application {

	public static void main(String[] args) throws Exception {
		// create simple rest server
		DoradoServerBuilder.forPort(18888).build().start();
	}
}
```

* Rest server with more config parameters

```java

public class Application {

	public static void main(String[] args) throws Exception {
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
@Path("/campaign")
public class CampaignController {

	@Path("/{id:[0-9]+}")
	@GET
	public Campaign newCampaign(int id) {
		Campaign campaign = new Campaign();
		campaign.setId(id);
		campaign.setName("test campaign");

		return campaign;
	}

	@Path("/name")
	public String campaignName() {
		return String.format("hello_campaign", "");
	}

	@POST
	public Campaign save(Campaign campaign) {
		System.out.println(campaign);
		return campaign;
	}

	@Path("/{id}")
	@DELETE
	public void deleteCampaign(int id) {
		System.out.println("delete campaign, id: " + id);
	}

	@GET
	@Path("/{id}")
	public Campaign getCampaign(int id) {
		return Campaign.builder().withId(12)
				.withName("网易考拉推广计划")
				.build();
	}
}
```
* More examples 

Please visit https://github.com/javagossip/dorado-examples
## Support annotations
### class level

| Annotation type  | description  | 
|:-------------: |:---------------|
| Controller    | Indicates that an annotated class is a "Controller" (e.g. a web controller). | 
| Path      | Identifies the URI path that a resource class or class method will serve requests for |

### method level

| Annotation type  | description  | 
|:-------------: |:---------------|
| Path      | Identifies the URI path that a resource class or class method will serve requests for |
|GET|Indicates that the annotated method responds to HTTP GET requests|
|POST|Indicates that the annotated method responds to HTTP POST requests|
|PUT|Indicates that the annotated method responds to HTTP PUT requests|
|DELETE|Indicates that the annotated method responds to HTTP DELETE requests|
|Consume | Defines the media type that the methods of a controller class can accept|
|Produce | Defines the media type that the methods of a controller class can produce|

### parameter level

| annotation type  | description  |supported data type|
|:-------------: |:---------------|:------------:|
|RequestParam|Query or Form parameter|primitive type&wrapper class,String|
|PathVariable|Uri path variable, example: {var}|primitive type&wrapper class,String|
|HeaderParam|Request header value|primitive type&wrapper class,String|
|CookieParam|Request cookie value|primitive type&wrapper class,String|
|RequestBody|Http request body|String,byte[],InputStream or any serializable type|

## Performance
TODO
