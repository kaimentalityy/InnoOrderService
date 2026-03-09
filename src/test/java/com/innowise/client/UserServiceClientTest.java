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
                {"id":"user-1","email":"test@example.com","name":"John","surname":"Doe"}
                """;
        mockWebServer.enqueue(new MockResponse()
                .setBody(body)
                .addHeader("Content-Type", "application/json"));

        UserInfoDto dto = userServiceClient.getUserById("user-1");

        assertNotNull(dto);
        assertEquals("user-1", dto.id());
        assertEquals("test@example.com", dto.email());
        assertEquals("John", dto.name());
        assertEquals("Doe", dto.surname());

        RecordedRequest recordedRequest = mockWebServer.takeRequest();
        assertEquals("/api/users/internal/user-1", recordedRequest.getPath());
        assertEquals("GET", recordedRequest.getMethod());
    }

    @Test
    void getUserByEmail_ShouldReturnUserInfo() throws Exception {
        String body = """
                {"id":"user-2","email":"another@example.com","name":"Alice","surname":"Smith"}
                """;
        mockWebServer.enqueue(new MockResponse()
                .setBody(body)
                .addHeader("Content-Type", "application/json"));

        UserInfoDto dto = userServiceClient.getUserByEmail("another@example.com");

        assertNotNull(dto);
        assertEquals("user-2", dto.id());
        assertEquals("another@example.com", dto.email());
        assertEquals("Alice", dto.name());
        assertEquals("Smith", dto.surname());

        RecordedRequest recordedRequest = mockWebServer.takeRequest();
        assertTrue(recordedRequest.getPath().contains("/api/users/internal/search?email=another@example.com"));
        assertEquals("GET", recordedRequest.getMethod());
    }

    @Test
    void userFallbackById_ShouldHandleStringKey() throws Exception {
        Method method = UserServiceClient.class.getDeclaredMethod("userFallbackById", String.class,
                Throwable.class);
        method.setAccessible(true);

        UserInfoDto dto = (UserInfoDto) method.invoke(userServiceClient, "user-123",
                new RuntimeException("Simulated"));
        assertEquals("user-123", dto.id());
        assertEquals("unknown@example.com", dto.email());
        assertEquals("Unknown", dto.name());
        assertEquals("User", dto.surname());
    }

    @Test
    void userFallbackByEmail_ShouldHandleStringKey() throws Exception {
        Method method = UserServiceClient.class.getDeclaredMethod("userFallbackByEmail", String.class,
                Throwable.class);
        method.setAccessible(true);

        UserInfoDto dto = (UserInfoDto) method.invoke(userServiceClient, "test@example.com",
                new RuntimeException("Simulated"));
        assertEquals("unknown-id", dto.id());
        assertEquals("test@example.com", dto.email());
        assertEquals("Unknown", dto.name());
        assertEquals("User", dto.surname());
    }

    @Test
    void createFallbackDto_ShouldReturnCorrectUser() throws Exception {
        Method method = UserServiceClient.class.getDeclaredMethod("createFallbackDto", String.class, String.class);
        method.setAccessible(true);

        UserInfoDto dto = (UserInfoDto) method.invoke(userServiceClient, "user-999", "fail@example.com");
        assertEquals("user-999", dto.id());
        assertEquals("fail@example.com", dto.email());
        assertEquals("Unknown", dto.name());
        assertEquals("User", dto.surname());
    }
}
