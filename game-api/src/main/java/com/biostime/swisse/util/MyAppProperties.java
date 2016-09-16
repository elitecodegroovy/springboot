package com.biostime.swisse.util;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * description:
 * @Component @ConfigurationProperties(prefix="myapp") . The
 @ConfigurationProperties annotation tells Spring Boot that the class will be
 used for all the properties defined in the application.properties file that has
 the myapp prefix. Meaning that it will recognized when you have myapp.serverIp
 (or myapp.server-ip ), myapp.name , and myapp.description . The @Component
 annotation is used to make sure that the class is picked up as a bean.
 * @author elite_jigang@163.com
 */
@Component
@ConfigurationProperties(prefix="myapp")
public  class MyAppProperties {
    private String name;
    private String description;
    private String serverIp;

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    public String getServerIp() {
        return serverIp;
    }
    public void setServerIp(String serverIp) {
        this.serverIp = serverIp;
    }
}
