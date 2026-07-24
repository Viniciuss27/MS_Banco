package vinix;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.config.server.EnableConfigServer;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

@EnableConfigServer
@EnableEurekaServer
@SpringBootApplication
public class EurekaConfigApplication /*implements CommandLineRunner*/{

	public static void main(String[] args) {
		SpringApplication.run(EurekaConfigApplication.class, args);
	}
	/*
	@Value("${GIT_PASSWORD}")
	private String password;
	
	@Value("${GIT_USERNAME}")
	private String username;

	@Override
	public void run(String... args) throws Exception {
		System.out.println("PASSWORD: " + password);
		System.out.println("USERNAME: " + username);
	}*/
}
