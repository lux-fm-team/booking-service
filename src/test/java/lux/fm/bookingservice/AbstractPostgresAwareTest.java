package lux.fm.bookingservice;

import java.time.LocalTime;
import org.junit.jupiter.api.BeforeAll;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.utility.DockerImageName;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public abstract class AbstractPostgresAwareTest {
    @Container
    @ServiceConnection
    private static final PostgreSQLContainer<?> postgreSQLContainer =
            new PostgreSQLContainer<>(DockerImageName.parse("postgres:16.1"))
                    .withCreateContainerCmdModifier(
                            cmd -> cmd.withName("accommodation-booking-test-"
                                    + LocalTime.now().getNano())
                    )
                    .withReuse(true)
                    .withDatabaseName("test_booking");

    @BeforeAll
    public static void beforeAll() {
        postgreSQLContainer.start();
    }
}
