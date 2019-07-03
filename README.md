-   [Dorado](#dorado)
    -   [Features](#features)
    -   [Maven](#maven)
    -   [Quick start](#quick-start)
    -   [注解说明](#注解说明)
        -   [类注解](#类注解)
        -   [方法注解](#方法注解)
        -   [方法参数注解](#方法参数注解)
    -   [内置服务](#内置服务)
    -   [SpringBoot集成](#springboot集成)
    -   [swagger集成](#swagger集成)
    -   [性能测试](#性能测试)

Dorado
======

简单、快速、轻量级的http restful server实现，基于Netty4和JDK1.8+

Features
--------

-   HTTP/1.1 and HTTP/1.0协议支持
-   内置JSON/Protobuf序列化支持，JSON序列化框架使用Fastjson, 依赖内置；\
    如果使用protobuf序列化，需要自行添加protobuf依赖, protobuf版本2.x
-   Http路由支持,
    路由path支持任意的java正则表达式，通过{pathVariable:regex}这种方式支持
-   支持文件上传
-   Spring框架支持，默认支持注解方式初始化spring容器
-   SpringBoot集成
-   集成swagger restful API文档生成工具
-   过滤器支持，实现类似于spring mvc的interceptor功能
-   支持全局异常处理
-   支持统一响应格式处理

## Latest version

**0.0.51**

Maven
-----

```xml
<dependency>
    <groupId>ai.houyi</groupId>
    <artifactId>dorado-core</artifactId>
    <version>${dorado.version}</version>
</dependency>
```

Quick start
-----------

-   最简单的Dorado rest server

```java
public class Application {

    public static void main(String[] args) throws Exception {
        // create simple rest server, scanPackages现在可选，如果不设置
        //系统默认会自动找到main主类所在的包作为basePackage进行扫描
        DoradoServerBuilder.forPort(18888).build().start();
    }
}
```

-   更多定制化参数服务器

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

-   spring框架支持

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

-   Rest Controller

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

-   Filter

```java
@FilterPath(include = "/campaign/*")
public class DemoFilter implements Filter {

    @Override
    public boolean preFilter(HttpRequest request, HttpResponse response) {
        System.out.println("execute demo filter");
        response.sendError(403, "Forbidden");
        return false;
    }
}
```

-   文件上传支持  
    支持文件上传很简单，只需要将controller方法的参数设置未MultipartFile或者MultipartFile[]即可，单文件用MultipartFile,  
    多文件用MultipartFile[]

```java
@Path("/file/upload")
@POST
public String uploadFile(MultipartFile[] fs,String name) {
    for(MultipartFile f:fs) {
        System.out.println(f.getName());
        System.out.println(f.getContentType());
        System.out.println(f.getSize());
    }
    return String.format("name: %s, file info: %s", name,f.toString());
}
```

-   全局异常处理  
	dorado支持用户通过自定义的类来进行全局异常处理，处理异常的方法名必须是handleException，类上面增加ExceptionAdvice注解  
	
	```java
	@ExceptionAdvice
	public class TestExceptionAdvice {

		@ExceptionType(MyException.class)
		@Status(400) //Status注解来指定http响应状态码
		public String handleException(MyException ex) {
			return "cause: " + ex.getClass().getName() + "," + ex.getMessage();
		}
	
		@ExceptionType(Exception.class)
		public String handleException(Exception ex) {
			return "use default exception handler,cause: " + ex.getClass().getName() + "," + ex.getMessage();
		}
	}

	```
	
-	全局统一响应格式处理  
	在实际项目开发中，基于rest的服务一般都会定义统一的响应格式，如下面所示格式：  
	
	```json
	{
		"code": 0,
		"data": data,
		"msg": "OK"
	}
	```
	
	为了避免在每个方法的时候都要执行相同的处理操作，dorado支持定义一个ai.houyi.dorado.rest.http.MethodReturnValueHandler来  
	实现这个功能
	
	```java
	public class TestMethodReturnValueHandler implements MethodReturnValueHandler {

		@Override
		public Object handleMethodReturnValue(Object value, MethodDescriptor methodDescriptor) {
			return TestResp.builder().withCode(0).withMsg("OK").withData(value).build();
		}
		
		//自定义这个方法实现，用来控制哪些返回值需要被这个类进行处理
		public boolean supportsReturnType(MethodDescriptor returnType) {
		    return true;
		}
	}
	```
	
-   More examples

Please visit https://github.com/javagossip/dorado-examples

注解说明
--------

### 类注解

| 注解类型  | 描述  |
|:-------------: |:---------------:|
| Controller    | 控制器 |
| Path      | 控制器访问Path|
| FilterPath |过滤器过滤路径,包括include以及exclude属性|

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


内置服务
--------

-   Get Server configuration: **[http://{ip}:{port}/config]()**
-   Get Server status: **[http://{ip}:{port}/status]()**
-   List All services: **[http://{ip}:{port}/services]()**

SpringBoot集成
--------------

-   添加dorado-spring-boot-starter核心依赖

    ```xml
    <dependency>
        <groupId>ai.houyi</groupId>
        <artifactId>dorado-spring-boot-starter</artifactId>
        <version>${dorado.version}</version>
    </dependency>
    ```

-   基于springboot的dorado应用

    ```java
    @SpringBootApplication
    @EnableDorado       //用这个注解开启dorado server
    public class SpringBootApplication {

        public static void main(String[] args) throws Exception {
            SpringApplication.run(SpringBootApplication.class, args);
        }
    }
    ```

    或者直接使用DoradoSpringBootApplication来替换SpringBootApplication和EnableDorado,如下：
    
    ```java
      @DoradoSpringBootApplication
      public class SpringBootApplication {

            public static void main(String[] args) throws Exception {
                SpringApplication.run(SpringBootApplication.class, args);
            }
       }
    ```
    
-   Dorado框架的spring-boot配置参数

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
	
swagger集成
-----------

如果对swagger还不了解的话，参考：[https://swagger.io/]()

-   添加dorado-swagger-ui依赖

```xml
   <dependency>
       <groupId>ai.houyi</groupId>
       <artifactId>dorado-swagger-ui</artifactId>
       <version>${dorado.version}</version>
   </dependency>
```

-   项目启动类中使用EnableSwagger注解启用swagger

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

-   设置Api文档全局信息\
    实现**ai.houyi.dorado.swagger.ext.ApiContextBuilder**接口

```java
@Component //如果是集成spring或springboot环境的话，直接增加component注解即可
@Override
// 这里定制Api全局信息，如文档描述、license,contact等信息
public ApiContext buildApiContext() {
	Info info = new Info()
			.contact(new Contact().email("javagossip@gmail.com").name("weiping wang")
					.url("http://github.com/javagossip/dorado"))
			.license(new License().name("apache v2.0").url("http://www.apache.org"))
			.termsOfService("http://swagger.io/terms/").description("Dorado服务框架api接口文档")
			.title("dorado demo api接口文档").version("1.0.0");

	//构造api访问授权的apiKey
	ApiKey apiKey = ApiKey.builder().withName("Authorization").withIn("header").build();
	ApiContext apiContext = ApiContext.builder().withApiKey(apiKey)
			.withInfo(info).build();

	return apiContext;
}
```

非spring环境需要在resources/META-INF/services下的ai.houyi.dorado.swagger.ext.ApiContextBuilder文件中增加如下配置：  
**ai.houyi.dorado.demo.ApiContextBuilderImpl**

-   在controller实现里面增加swagger相关的注解即可自动生成在线的api doc

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

* spring-boot环境，可以直接引入dorado-swagger-spring-boot-starter依赖来进一步简化swagger的使用  

	```xml
	<dependency>
  		<groupId>ai.houyi</groupId>
  		<artifactId>dorado-swagger-spring-boot-starter</artifactId>
  		<version>${dorado.version}</version>
	</dependency> 
	```
	
	然后在application.properties配置文件中增加如下配置即可：
	
	```java
	dorado.swagger.title=Zhuque's dashboard api
	dorado.swagger.description=${dorado.swagger.title}
	dorado.swagger.license=Apache License
	dorado.swagger.licenseUrl=http://www.apache.org/licenses/LICENSE-2.0
	dorado.swagger.contact.name=weiping wang
	dorado.swagger.contact.email=javagossip@gmail.com
	dorado.swagger.contact.url=https://github.com/javagossip
	
	//API全局认证配置
	dorado.swagger.apiKey.name=Authorization
	dorado.swagger.apiKey.in=header or query
	```

-   浏览器访问如下地址即可  
    http://{host}:{port}/swagger-ui.html

性能测试
--------
