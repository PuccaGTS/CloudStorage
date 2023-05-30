package ru.netology.cloudstorage;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.util.MimeTypeUtils;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import ru.netology.cloudstorage.dto.authentication.AuthRequest;
import ru.netology.cloudstorage.model.File;
import ru.netology.cloudstorage.model.Role;
import ru.netology.cloudstorage.model.User;
import ru.netology.cloudstorage.repository.AuthorizationRepository;
import ru.netology.cloudstorage.repository.FileRepository;
import ru.netology.cloudstorage.repository.UserRepository;
import ru.netology.cloudstorage.repository.impl.AuthorizationRepositoryImpl;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class CloudStorageApplicationTests {
	@Autowired
	TestRestTemplate restTemplate;
	@Autowired
	UserRepository userRepository;
	@Autowired
	FileRepository fileRepository;
	@Autowired
	AuthorizationRepositoryImpl authorizationRepository;
	@Container
	public static MySQLContainer<?> mySql = new MySQLContainer<>("mysql:latest")
			.withExposedPorts(3306);
	@Container
	private GenericContainer<?> cloudeStorage = new GenericContainer<>("storage_cloud_backend:latest")
			.withExposedPorts(8080)
			.dependsOn(mySql);

	//TODO MOCK
	MockMultipartFile multipartFile = new MockMultipartFile(
			"file",
			"sample.txt",
			MimeTypeUtils.TEXT_PLAIN_VALUE,
			"test_data_mock_file".getBytes()
	);

	@BeforeEach
	public void setUp(){
		mySql.start();
		cloudeStorage.start();
	}
	@Test
	void contextLoadsCloudStorage() {
		Integer myAppPort = cloudeStorage.getMappedPort(8080);

		final String urlLogin = "http://localhost:" + myAppPort + "/login";
		final String urlLogout = "http://localhost:" + myAppPort + "/logout";
		final String urlFile = "http://localhost:" + myAppPort + "/file";
		final String urlList = "http://localhost:" + myAppPort + "/list";

		try {
			URI uriLogin = new URI(urlLogin);
			URI uriFile = new URI(urlFile);
			URI uriList = new URI(urlList);
			URI uriLogout = new URI(urlLogout);

			//TODO Test for login
			AuthRequest authRequest = new AuthRequest("admin@test.com", "pass");
			HttpEntity<AuthRequest> loginRequest = new HttpEntity<>(authRequest);
			ResponseEntity<String> resultLogin = restTemplate.postForEntity(uriLogin, loginRequest, String.class);
			Assertions.assertEquals(HttpStatus.OK, resultLogin.getStatusCode());

		} catch (URISyntaxException e){
			e.printStackTrace();
		}
	}

}
