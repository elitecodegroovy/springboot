package com.biostime.swisse;

import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.mock.http.MockHttpOutputMessage;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Arrays;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = GameApiApplication.class)
@WebAppConfiguration
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class GameApiApplicationTests {

	private final String SPRING_BOOT_MATCH = "Spring Boot";
	private final String CLOUD_MATCH = "Cloud";
	@SuppressWarnings("rawtypes")
	private HttpMessageConverter mappingJackson2HttpMessageConverter;
	private MediaType contentType = new MediaType(MediaType.APPLICATION_JSON.getType(),
			MediaType.APPLICATION_JSON.getSubtype(),
			Charset.forName("utf8"));
	private MockMvc mockMvc;

	@Test
	public void contextLoads() {
	}

	@Autowired
	private WebApplicationContext webApplicationContext;
	@Autowired
	void setConverters(HttpMessageConverter<?>[] converters) {
		this.mappingJackson2HttpMessageConverter = Arrays.asList(converters).stream().
				filter(
						converter -> converter instanceof MappingJackson2HttpMessageConverter).
				findAny().get();
	}
	@Before
	public void setup() throws Exception {
		this.mockMvc = webAppContextSetup(webApplicationContext).build();
	}

	/**
	 * http://docs.spring.io/spring-framework/docs/current/spring-framework-reference/html/integration-testing.html#spring-mvc-test-framework
	 *
	 * jsonPath reference :https://github.com/jayway/JsonPath
	 * @throws Exception
     */
	@Test
	public void getIndex() throws Exception {
		System.out.println(mockMvc.perform(get("/"))
				.andExpect(status().isOk())
				.andExpect(content().contentType(contentType))
						.andExpect(jsonPath("$.giveyou").value(10000))
		        );
	}


	@SuppressWarnings("unchecked")
	protected String toJsonString(Object obj) throws IOException {
		MockHttpOutputMessage mockHttpOutputMessage = new MockHttpOutputMessage();
		this.mappingJackson2HttpMessageConverter.write(obj, MediaType.APPLICATION_JSON,
				mockHttpOutputMessage);
		return mockHttpOutputMessage.getBodyAsString();
	}
}
