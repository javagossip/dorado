# Dorado
简单、快速、轻量级的http restful server实现，基于Netty4和JDK1.8+

## Features

* HTTP/1.1 and HTTP/1.0协议支持
* 内置JSON/Protobuf序列化支持，JSON序列化框架使用Fastjson, 依赖内置；  
  如果使用protobuf序列化，需要自行添加protobuf依赖, protobuf版本2.x
* Http路由支持, 路由path支持任意的java正则表达式，通过{pathVariable:regex}这种方式支持
* Spring框架支持，默认支持注解方式初始化spring容器
* SpringBoot集成


## Maven

```xml
<dependency>
    <groupId>ai.houyi</groupId>
    <artifactId>dorado-core</artifactId>
    <version>0.0.11</version>
</dependency>
```

## Quick start

* 最简单的Dorado rest server

```java
public class Application {

	public static void main(String[] args) throws Exception {
		// create simple rest server, scanPackages现在可选，如果不设置
		//系统默认会自动找到main主类所在的包作为basePackage进行扫描
		DoradoServerBuilder.forPort(18888).build().start();
	}
}
```

* 更多定制化参数服务器

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
* spring框架支持
    
    DoradoServerBuilder.forPort(port).<font color=red>**springOn()**</font>
    
    由于框架本身不直接依赖spring框架，如果启用spring之后，务必自行添加spring框架相关依赖到项目中，否则系统启动会出现ClassNotFoundException

```java

public class Application {

	public static void main(String[] args) throws Exception {
		DoradoServerBuilder.forPort(18888).springOn()
				.scanPackages("com.rtbstack.demo",
						"com.rtbstack.demo.controller1")
		       .build().start();
	}
}
```

* Rest Controller

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

## 注解说明
### 类注解

| 注解类型  | 描述  | 
|:-------------: |:---------------:|
| Controller    | 控制器 | 
| Path      | 控制器访问Path|

### 方法注解

| 注解类型  | 描述  | 
|:-------------: |:---------------:|
| Path      | 资源访问路径，实际访问path为：controllerPath+methodPath |
|GET|方法仅支持Http GET请求|
|POST|方法仅支持Http POST请求|
|PUT|方法仅支持HTTP PUT请求|
|DELETE|方法仅支持HTTP DELETE请求|
|Consume | 方法参数支持的MediaType, 如：application/json|
|Produce | 方法响应MediaType, 如：application/json|

### 方法参数注解

| 注解类型  | 描述  |参数支持数据类型|
|:-------------: |:---------------:|:------------:|
|RequestParam|Query or Form parameter|Primitive type&wrapper class,String|
|PathVariable|Uri path variable, example: {var}|Primitive type&wrapper class,String|
|HeaderParam|Request header value|Primitive type&wrapper class,String|
|CookieParam|Request cookie value|Primitive type&wrapper class,String|
|RequestBody|Http request body|String, byte[],InputStream or any serializable type|

## 内置服务

* Get Server configuration: **[http://{ip}:{port}/config]()**
* Get Server status: **[http://{ip}:{port}/status]()**
* List All services: **[http://{ip}:{port}/services]()**

## SpringBoot集成

* 添加dorado-spring-boot-starter核心依赖

	```xml
	<dependency>
	    <groupId>ai.houyi</groupId>
	    <artifactId>dorado-spring-boot-starter</artifactId>
	    <version>0.0.11</version>
    </dependency>
	```
* 基于springboot的dorado应用

	```java
	@SpringBootApplication
	@EnableDorado       //用这个注解开启dorado server
	public class SpringBootApplication {
	
		public static void main(String[] args) throws Exception {
			SpringApplication.run(SpringBootApplication.class, args);
		}
	}
	```
* Dorado框架的spring-boot配置参数

	|参数名|描述|默认值|
	|:-----------|:----------:|:-----------:|
	|dorado.port|dorado server监听端口|18888|
	|dorado.backlog|backlog队列大小|10000|
	|dorado.acceptors|dorado acceptor count|cpu核心数*2|
	|dorado.io-workers|dorado io worker count|cpu核心数*2|
	|dorado.min-workers|业务线程池最小线程数|100|
	|dorado.max-workers|业务线程池最大线程数|100|
	|dorado.max-connections|服务器最大连接数|100000|
	|dorado.max-pending-request|业务线程池队列长度|10000|
	|dorado.send-buffer|send buffer size|256k|
	|dorado.recv-buffer|recv buffer size|256k|
	|dorado.max-idle-time|连接最大空闲时间|8h|
	|dorado.max-packet-length|http请求包体大小|1M|
	

## 性能测试



