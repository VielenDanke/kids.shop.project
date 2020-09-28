package kz.danke.shop.discovery.server;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;

@SpringBootApplication
@EnableEurekaServer
public class ShopDiscoveryServerApplication {

	@Value("${test.url}")
	private String testUrl;

	public static void main(String[] args) {
		SpringApplication.run(ShopDiscoveryServerApplication.class, args);
	}

	@EventListener(ContextRefreshedEvent.class)
	public void refreshedEvent() {
		System.out.println(testUrl);
	}
}
