package ru.netology.cloudstorage;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.containers.Network;
import org.testcontainers.junit.jupiter.Container;
import ru.netology.cloudstorage.dto.authentication.AuthRequest;

import java.net.URI;
import java.net.URISyntaxException;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class CloudStorageApplicationTests {
	@Autowired
	TestRestTemplate restTemplate;

	private static final Network NETWORK = Network.newNetwork();
	@Container
    public static MySQLContainer<?> mySql = new MySQLContainer<>("mysql:latest")
            .withDatabaseName("cloud_storage")
            .withUsername("root")
            .withPassword("mysql")
            .withNetwork(NETWORK);
	@Container
	public static GenericContainer<?> cloudStorage = new GenericContainer<>("storage_cloud_backend:latest")
			.withExposedPorts(8080)
			.withNetwork(NETWORK)
			.dependsOn(mySql);
	static {
		mySql.start();
		cloudStorage.start();
	}

	@Test
	void contextDatabase() {
		Assertions.assertTrue(mySql.isRunning());
	}

	@Test
	void contextServer() {
		Assertions.assertFalse(cloudStorage.isRunning());
	}
	@Test
	void contextLoadsCloudStorage() {
		final String urlLogin = "http://localhost:8080/login";

		try {
			URI uriLogin = new URI(urlLogin);

			AuthRequest authRequest = new AuthRequest("admin@test.com", "pass");
			HttpEntity<AuthRequest> loginRequest = new HttpEntity<>(authRequest);
			ResponseEntity<String> resultLogin = restTemplate.postForEntity(uriLogin, loginRequest, String.class);
			Assertions.assertEquals(HttpStatus.OK, resultLogin.getStatusCode());


		} catch (URISyntaxException e){
			e.printStackTrace();
		}
	}

}
