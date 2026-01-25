package com.innowise.client;

import com.innowise.model.dto.UserInfoDto;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import org.junit.jupiter.api.*;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.IOException;
import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.*;

class UserServiceClientTest {

    private static MockWebServer mockWebServer;
    private UserServiceClient userServiceClient;
    private static final String TEST_JWT_TOKEN = "test-jwt-token";

    @BeforeAll
    static void startServer() throws IOException {
        mockWebServer = new MockWebServer();
        mockWebServer.start();
    }

    @AfterAll
    static void stopServer() throws IOException {
        mockWebServer.shutdown();
    }

    @BeforeEach
    void setup() {
        WebClient.Builder builder = WebClient.builder();
        String baseUrl = mockWebServer.url("/").toString();
        userServiceClient = new UserServiceClient(baseUrl, builder);
    }

    @Test
    void getUserById_ShouldReturnUserInfo() throws Exception {
        String body = """
                {"id":1,"email":"test@example.com","name":"John","surname":"Doe"}
                """;
        mockWebServer.enqueue(new MockResponse()
                .setBody(body)
                .addHeader("Content-Type", "application/json"));

        UserInfoDto dto = userServiceClient.getUserById(1L, TEST_JWT_TOKEN);

        assertNotNull(dto);
        assertEquals(1L, dto.id());
        assertEquals("test@example.com", dto.email());
        assertEquals("John", dto.name());
        assertEquals("Doe", dto.surname());

        RecordedRequest recordedRequest = mockWebServer.takeRequest();
        assertEquals("/api/users/1", recordedRequest.getPath());
        assertEquals("GET", recordedRequest.getMethod());
        assertTrue(recordedRequest.getHeader("Authorization").contains("Bearer " + TEST_JWT_TOKEN));
    }

    @Test
    void getUserByEmail_ShouldReturnUserInfo() throws Exception {
        String body = """
                {"id":2,"email":"another@example.com","name":"Alice","surname":"Smith"}
                """;
        mockWebServer.enqueue(new MockResponse()
                .setBody(body)
                .addHeader("Content-Type", "application/json"));

        UserInfoDto dto = userServiceClient.getUserByEmail("another@example.com", TEST_JWT_TOKEN);

        assertNotNull(dto);
        assertEquals(2L, dto.id());
        assertEquals("another@example.com", dto.email());
        assertEquals("Alice", dto.name());
        assertEquals("Smith", dto.surname());

        RecordedRequest recordedRequest = mockWebServer.takeRequest();
        assertTrue(recordedRequest.getPath().contains("/api/users/search?email=another@example.com"));
        assertEquals("GET", recordedRequest.getMethod());
        assertTrue(recordedRequest.getHeader("Authorization").contains("Bearer " + TEST_JWT_TOKEN));
    }

    @Test
    void userFallbackById_ShouldHandleLongKey() throws Exception {
        Method method = UserServiceClient.class.getDeclaredMethod("userFallbackById", Long.class, String.class,
                Throwable.class);
        method.setAccessible(true);

        UserInfoDto dto = (UserInfoDto) method.invoke(userServiceClient, 123L, TEST_JWT_TOKEN,
                new RuntimeException("Simulated"));
        assertEquals(123L, dto.id());
        assertEquals("unknown@example.com", dto.email());
        assertEquals("Unknown", dto.name());
        assertEquals("User", dto.surname());
    }

    @Test
    void userFallbackByEmail_ShouldHandleStringKey() throws Exception {
        Method method = UserServiceClient.class.getDeclaredMethod("userFallbackByEmail", String.class, String.class,
                Throwable.class);
        method.setAccessible(true);

        UserInfoDto dto = (UserInfoDto) method.invoke(userServiceClient, "test@example.com", TEST_JWT_TOKEN,
                new RuntimeException("Simulated"));
        assertEquals(-1L, dto.id());
        assertEquals("test@example.com", dto.email());
        assertEquals("Unknown", dto.name());
        assertEquals("User", dto.surname());
    }

    @Test
    void createFallbackDto_ShouldReturnCorrectUser() throws Exception {
        Method method = UserServiceClient.class.getDeclaredMethod("createFallbackDto", Long.class, String.class);
        method.setAccessible(true);

        UserInfoDto dto = (UserInfoDto) method.invoke(userServiceClient, 999L, "fail@example.com");
        assertEquals(999L, dto.id());
        assertEquals("fail@example.com", dto.email());
        assertEquals("Unknown", dto.name());
        assertEquals("User", dto.surname());
    }
}
