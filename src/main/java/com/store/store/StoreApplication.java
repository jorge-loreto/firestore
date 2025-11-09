package com.store.store;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

import com.google.cloud.spring.data.firestore.repository.config.EnableReactiveFirestoreRepositories;

@SpringBootApplication(scanBasePackages = "com.store.store")
public class StoreApplication {

	public static void main(String[] args) {
		// SpringApplication.run(StoreApplication.class, args);
		ConfigurableApplicationContext context = SpringApplication.run(StoreApplication.class, args);
		System.out.println(">>> GCP credentials location: " +
				context.getEnvironment().getProperty("spring.cloud.gcp.credentials.location"));
	}

}
