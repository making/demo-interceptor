package com.example.api;

import static org.hamcrest.Matchers.is;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.restassured.RestAssuredRestDocumentation.document;
import static org.springframework.restdocs.restassured.RestAssuredRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.restassured.operation.preprocess.RestAssuredPreprocessors.modifyUris;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.context.embedded.LocalServerPort;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.restdocs.JUnitRestDocumentation;
import org.springframework.test.context.junit4.SpringRunner;

import com.example.authenz.Authorizer;
import com.jayway.restassured.RestAssured;
import com.jayway.restassured.builder.RequestSpecBuilder;
import com.jayway.restassured.specification.RequestSpecification;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class MovieControllerTest {
	@Rule
	public final JUnitRestDocumentation restDocumentation = new JUnitRestDocumentation(
			"target/generated-snippets");

	RequestSpecification documentationSpec;
	@LocalServerPort
	int port;
	@MockBean
	Authorizer authorizer;

	@Before
	public void setUp() {
		this.documentationSpec = new RequestSpecBuilder()
				.addFilter(documentationConfiguration(this.restDocumentation)).build();
	}

	@Test
	public void getMovieWithValidToken() throws Exception {
		given(this.authorizer.access("read", "rec.movie", "valid-token"))
				.willReturn(true);

		RestAssured.given(this.documentationSpec).filter(document("api/get-movie",
				preprocessRequest(modifyUris().scheme("https").host("api.example.com")
						.removePort()),
				responseFields(fieldWithPath("name").description("The name of movie"),
						fieldWithPath("director")
								.description("Director's name of the movie"))))
				.when().header("X-Athenz-Principal-Auth", "valid-token").port(this.port)
				.get("/movie").then().assertThat().statusCode(200)
				.body("name", is("Slap Shot")).body("director", is("George Roy Hill"));
	}

	@Test
	public void getMovieWithInValidToken() throws Exception {
		given(this.authorizer.access("read", "rec.movie", "invalid-token"))
				.willReturn(false);

		RestAssured.given(this.documentationSpec).filter(document(
				"api/get-movie-rejected",
				preprocessRequest(modifyUris().scheme("https").host("api.example.com")
						.removePort()),
				responseFields(
						fieldWithPath("error").description(
								"The HTTP error that occurred, e.g. `Bad Request`"),
						fieldWithPath("message")
								.description("A description of the cause of the error"),
						fieldWithPath("path")
								.description("The path to which the request was made"),
						fieldWithPath("status")
								.description("The HTTP status code, e.g. `400`"),
						fieldWithPath("timestamp").description(
								"The time, in milliseconds, at which the error occurred"),
						fieldWithPath("exception")
								.description("An exception of the cause of the error")
								.optional())))
				.when().header("X-Athenz-Principal-Auth", "invalid-token").port(this.port)
				.get("/movie").then().assertThat().statusCode(403).body("status", is(403))
				.body("error", is("Forbidden"))
				.body("message",
						is("Athenz Authorization Rejected resource=rec.movie, action=read"))
				.body("path", is("/movie"));
	}

}