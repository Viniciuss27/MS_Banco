package vinix;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@EnableDiscoveryClient
@SpringBootApplication
public class WalletCoreApplication {

	public static void main(String[] args) {
		SpringApplication.run(WalletCoreApplication.class, args);
	}

}
