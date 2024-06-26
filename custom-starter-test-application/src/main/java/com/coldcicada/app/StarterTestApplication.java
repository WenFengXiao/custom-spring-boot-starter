package com.coldcicada.app;

import com.coldcicada.redisson.spring.starter.configuration.MultiRedissonAutoConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.Banner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.bind.Bindable;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Import;

/**
 * @Author coldcicada
 * @Date 2024-06-26 09:41
 * @Description
 */
@SpringBootApplication
//@Import(MultiRedissonAutoConfiguration.class)
public class StarterTestApplication {

    private static final Logger logger = LoggerFactory.getLogger(StarterTestApplication.class);

    public static void main(String[] args) {

        //System.setProperty("app.id", "sand-component-example");

        SpringApplication application = new SpringApplication(StarterTestApplication.class);

        application.setBannerMode(Banner.Mode.OFF);

        ApplicationContext context = application.run(args);

        //Arrays.stream(context.getBeanDefinitionNames()).forEach(System.out::println);

        Binder binder = Binder.get(context.getEnvironment());

        String applicationName = binder.bind("spring.application.name", Bindable.of(String.class)).get();

        String activeProfile = context.getEnvironment().getActiveProfiles().length == 0 ? "default" : context.getEnvironment().getActiveProfiles()[0];

        String serverPort = binder.bind("server.port", Bindable.of(String.class)).get();

        logger.info("程序[" + applicationName + "]已启动：分支:" + activeProfile + " 端口:"+serverPort + " ID:"+context.getId());

    }

}
