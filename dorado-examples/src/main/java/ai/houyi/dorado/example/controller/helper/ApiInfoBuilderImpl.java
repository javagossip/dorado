/**
 * 
 */
package ai.houyi.dorado.example.controller.helper;

import ai.houyi.dorado.swagger.ext.ApiInfoBuilder;
import io.swagger.annotations.SecurityDefinition;
import io.swagger.models.Contact;
import io.swagger.models.Info;
import io.swagger.models.License;

/**
 * @author marta
 *
 */
public class ApiInfoBuilderImpl implements ApiInfoBuilder {

    @Override
    //这里定制Api全局信息，如文档描述、license,contact等信息
    public Info buildInfo() {
    	
    	SecurityDefinition  securityDef = null;
        return new Info()
                .contact(new Contact().email("javagossip@gmail.com").name("weiping wang")
                        .url("http://github.com/javagossip/dorado"))
                .license(new License().name("apache v2.0").url("http://www.apache.org"))
                .termsOfService("http://swagger.io/terms/").description("Dorado服务框架api接口文档")
                .title("dorado demo api接口文档").version("1.0.0");
    }
}
