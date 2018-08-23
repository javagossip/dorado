# Dorado
简单、快速、轻量级的http restful server实现，基于Netty4和JDK1.8+

## Features

* HTTP/1.1 and HTTP/1.0协议支持
* 内置JSON/Protobuf序列化支持，JSON序列化框架使用Fastjson, 依赖内置；  
  如果使用protobuf序列化，需要自行添加protobuf依赖, protobuf版本2.x
* Http路由支持, 路由path支持任意的java正则表达式，通过{pathVariable:regex}这种方式支持
* 支持文件上传
* Spring框架支持，默认支持注解方式初始化spring容器
* SpringBoot集成
* 集成swagger restful API文档生成工具


## Maven

```xml
<dependency>
    <groupId>ai.houyi</groupId>
    <artifactId>dorado-core</artifactId>
    <version>0.0.14</version>
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
	    <version>0.0.14</version>
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
	

## swagger集成
如果对swagger还不了解的话，参考：[https://swagger.io/]()  

* 添加dorado-swagger-ui依赖

 ```xml
	<dependency>
	    <groupId>ai.houyi</groupId>
	    <artifactId>dorado-swagger-ui</artifactId>
	    <version>0.0.14</version>
    </dependency>
```

* 项目启动类中使用EnableSwagger注解启用swagger  

```java
@EnableSwagger
public class Application {
	public static void main(String[] args) throws Exception {
		// create simple rest server
		DoradoServerBuilder.forPort(18888).maxPacketLength(1024*1024*10)
		   .build().start();
	}
}
```

* 设置Api文档全局信息  
实现**mobi.f2time.dorado.swagger.ext.ApiInfoBuilder**接口  

```java
@Component //如果是集成spring或springboot环境的话，直接增加component注解即可
public class ApiInfoBuilderImpl implements ApiInfoBuilder {

	@Override
	//这里定制Api全局信息，如文档描述、license,contact等信息
	public Info buildInfo() {
		return new Info()
				.contact(new Contact().email("javagossip@gmail.com").name("weiping wang")
						.url("http://github.com/javagossip/dorado"))
				.license(new License().name("apache v2.0").url("http://www.apache.org"))
				.termsOfService("http://swagger.io/terms/").description("Dorado服务框架api接口文档")
				.title("dorado demo api接口文档").version("1.0.0");
	}
}
```
非spring环境需要在resources/META-INF/services下的mobi.f2time.dorado.swagger.ext.ApiInfoBuilder文件中增加如下配置：  
**mobi.f2time.dorado.demo.ApiInfoBuilderImpl**

* 在controller实现里面增加swagger相关的注解即可自动生成在线的api doc

```java
@Controller
@Path("/campaign")
@Api(tags = { "营销活动管理" })
public class CampaignController {
	@Autowired
	private CampaignService campaignService;

	@Path("/{id:[0-9]+}")
	@GET
	@ApiOperation("新建campaign")
	public Campaign newCampaign(@PathVariable("id") int id) {
		Campaign campaign = new Campaign();
		campaign.setId(id);
		campaign.setName("test campaign");

		return campaign;
	}
}
```

* 浏览器访问如下地址即可  
	http://{host}:{port}/swagger-ui.html  
	
## 性能测试



