package com.example.kalah.controller;

import com.example.kalah.KalahApp;
import com.example.kalah.dto.KalahGameDTO;
import org.flywaydb.test.annotation.FlywayTest;
import org.flywaydb.test.junit.FlywayTestExecutionListener;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.context.embedded.LocalServerPort;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;

import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.List;

import static com.example.kalah.domain.ActivePlayer.FIRST_PLAYER;
import static com.example.kalah.domain.ActivePlayer.SECOND_PLAYER;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = KalahApp.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Rollback
@TestExecutionListeners({DependencyInjectionTestExecutionListener.class, FlywayTestExecutionListener.class})
@FlywayTest
public class KalahControllerIntegrationTest {

    @LocalServerPort
    private int port;

    private final TestRestTemplate restTemplate = new TestRestTemplate();

    private final HttpHeaders headers = new HttpHeaders();

    @BeforeClass
    public static void beforeClass() {
        System.setProperty("java.net.useSystemProxies", "true");
    }

    @Before
    public void beforeEachTest() {
        System.setProperty("java.net.useSystemProxies", "true");
    }

    @Test
    public void pingRoot() throws Exception {
        final ResponseEntity<String> response = restTemplate.exchange(
                buildUri("/"),
                HttpMethod.GET,
                HttpEntity.EMPTY,
                String.class);

        assertThat(response, notNullValue());
        System.out.println("\n" + response + "\n");
        assertThat(response.getStatusCode(), is(HttpStatus.OK));

        final HttpHeaders headers = response.getHeaders();
        assertThat(headers, notNullValue());

        final List<String> location = headers.get(HttpHeaders.LOCATION);
        assertThat(location, notNullValue());
        assertThat(location.size(), greaterThan(0));
        System.out.println(location.get(0));
    }

    @Test
    public void pingKalah() throws Exception {
        final ResponseEntity<String> response = restTemplate.exchange(
                buildUri("/kalah"),
                HttpMethod.GET,
                HttpEntity.EMPTY,
                String.class);

        assertThat(response, notNullValue());
        System.out.println("\n" + response + "\n");
        assertThat(response.getStatusCode(), is(HttpStatus.OK));

        final HttpHeaders headers = response.getHeaders();
        assertThat(headers, notNullValue());
    }

    @Test
    @FlywayTest
    public void create() throws Exception {
        final ResponseEntity<KalahGameDTO> response = restTemplate.exchange(
                buildUri("/kalah"),
                HttpMethod.POST,
                HttpEntity.EMPTY,
                KalahGameDTO.class);

        assertThat(response, notNullValue());
        System.out.println("\n" + response + "\n");
        assertThat(response.getStatusCode(), is(HttpStatus.CREATED));

        final HttpHeaders headers = response.getHeaders();
        assertThat(headers, notNullValue());

        final List<String> location = headers.get(HttpHeaders.LOCATION);
        assertThat(location, notNullValue());
        assertThat(location.size(), greaterThan(0));
        System.out.println(location.get(0));
    }

    @Test
    @FlywayTest
    public void testThreeMoves() throws Exception {
        ResponseEntity<KalahGameDTO> response = restTemplate.exchange(
                buildUri("/kalah"),
                HttpMethod.POST,
                HttpEntity.EMPTY,
                KalahGameDTO.class);

        assertThat(response, notNullValue());
        System.out.println("\n" + response + "\n");
        assertThat(response.getStatusCode(), is(HttpStatus.CREATED));

        final KalahGameDTO game = response.getBody();
        final String url = buildUri("/kalah") + "/" + game.getGameId();

        verifyMoveResult(url + "/0/" + FIRST_PLAYER + "/1", "[0, 7, 7, 7, 7, 7] 1 " + "[6, 6, 6, 6, 6, 6] 0");
        verifyMoveResult(url + "/1/" + FIRST_PLAYER + "/2", "[0, 0, 8, 8, 8, 8] 2 " + "[7, 7, 6, 6, 6, 6] 0");
        verifyMoveResult(url + "/2/" + SECOND_PLAYER + "/1", "[1, 0, 8, 8, 8, 8] 2 " + "[0, 8, 7, 7, 7, 7] 1");
    }

    // TODO: test the whole game till the end and winner selection

    /* ----------------
     * PRIVATE METHODS
     */

    private void verifyMoveResult(@Nonnull final String url,
                                  @Nonnull final String expectedResult) {
        System.out.println(url);
        final ResponseEntity<KalahGameDTO> response;
        response = restTemplate.exchange(
                url,
                HttpMethod.PUT,
                RequestEntity.EMPTY,
                KalahGameDTO.class);
        assertThat(response.getStatusCode(), is(HttpStatus.OK));

        final KalahGameDTO game = response.getBody();
        System.out.println(game);
        assertThat("" +
                        Arrays.toString(game.getFirstPits()) + " " + game.getFirstStore() + " " +
                        Arrays.toString(game.getSecondPits()) + " " + game.getSecondStore(),
                is(expectedResult));
    }

    private String buildUri(@Nonnull final String uri) {
        return "http://localhost:" + port + uri;
    }

}