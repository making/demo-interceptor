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
public class TvShowControllerTest {
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
	public void getTvShowWithValidToken() throws Exception {
		given(this.authorizer.access("read", "rec.tvshow", "valid-token"))
				.willReturn(true);

		RestAssured.given(this.documentationSpec).filter(document("api/get-tvshow",
				preprocessRequest(modifyUris().scheme("https").host("api.example.com")
						.removePort()),
				responseFields(fieldWithPath("name").description("The name of TV show"),
						fieldWithPath("channel").description("The channel of TV show"))))
				.when().header("X-Athenz-Principal-Auth", "valid-token").port(this.port)
				.get("/tvshow").then().assertThat().statusCode(200)
				.body("name", is("Middle")).body("channel", is("ABC"));
	}

	@Test
	public void getTvShowWithInValidToken() throws Exception {
		given(this.authorizer.access("read", "rec.tvshow", "invalid-token"))
				.willReturn(false);

		RestAssured.given(this.documentationSpec).when()
				.header("X-Athenz-Principal-Auth", "invalid-token").port(this.port)
				.get("/tvshow").then().assertThat().statusCode(403)
				.body("status", is(403)).body("error", is("Forbidden"))
				.body("message",
						is("Athenz Authorization Rejected resource=rec.tvshow, action=read"))
				.body("path", is("/tvshow"));
	}

}