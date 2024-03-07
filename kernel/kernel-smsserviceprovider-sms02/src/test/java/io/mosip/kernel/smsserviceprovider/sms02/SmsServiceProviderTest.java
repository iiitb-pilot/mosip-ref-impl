package io.mosip.kernel.smsserviceprovider.sms02;

import static org.mockito.Mockito.when;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.ConfigFileApplicationContextInitializer;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import io.mosip.kernel.core.notification.exception.InvalidNumberException;
import io.mosip.kernel.core.notification.model.SMSResponseDto;
import io.mosip.kernel.smsserviceprovider.sms02.constant.SmsPropertyConstant;
import io.mosip.kernel.smsserviceprovider.sms02.dto.SmsServerResponseDto;
import io.mosip.kernel.smsserviceprovider.sms02.impl.SMSServiceProviderImpl;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = { ConfigFileApplicationContextInitializer.class, SmsServiceProviderTest.config.class,
		SMSServiceProviderImpl.class })
public class SmsServiceProviderTest {

	@Configuration
	static class config {

		@Bean
		public PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
			PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer = new PropertySourcesPlaceholderConfigurer();
			propertySourcesPlaceholderConfigurer.setLocations(new ClassPathResource("application.properties"));
			return propertySourcesPlaceholderConfigurer;
		}
	}

	@Autowired
	SMSServiceProviderImpl service;

	@MockBean
	RestTemplate restTemplate;

	@Value("${mosip.kernel.sms.enabled:false}")
	boolean smsEnabled;

	@Value("${mosip.kernel.sms.country.code}")
	String countryCode;

	@Value("${mosip.kernel.sms.number.length}")
	int numberLength;

	@Value("${mosip.kernel.sms.api}")
	String api;

	@Value("${mosip.kernel.sms.username}")
	private String username;

	@Value("${mosip.kernel.sms.password}")
	private String password;

	@Value("${mosip.kernel.sms.type}")
	private String type;

	@Value("${mosip.kernel.sms.dlr}")
	private String dlr;

	@Value("${mosip.kernel.sms.source}")
	private String source;

	@Test
	public void sendSmsTest() {

		UriComponentsBuilder sms = UriComponentsBuilder.fromHttpUrl(api)
				.queryParam(SmsPropertyConstant.USERNAME.getProperty(), username)
				.queryParam(SmsPropertyConstant.PASSWORD.getProperty(), password)
				.queryParam(SmsPropertyConstant.TYPE.getProperty(), type)
				.queryParam(SmsPropertyConstant.DLR.getProperty(), dlr)
				.queryParam(SmsPropertyConstant.DESTINATION.getProperty(), "260771610704")
				.queryParam(SmsPropertyConstant.SOURCE.getProperty(), source)
				.queryParam(SmsPropertyConstant.SMS_MESSAGE.getProperty(), "your otp is 4646");

		SmsServerResponseDto serverResponse = new SmsServerResponseDto();
		serverResponse.setType("success");
		SMSResponseDto dto = new SMSResponseDto();
		dto.setStatus(serverResponse.getType());
		dto.setMessage("Sms Request Sent");

		when(restTemplate.getForEntity(sms.toUriString(), String.class))
				.thenReturn(new ResponseEntity<>(serverResponse.toString(), HttpStatus.OK));

		when(restTemplate.postForEntity(Mockito.anyString(), Mockito.eq(Mockito.any()), Object.class))
				.thenReturn(new ResponseEntity<>(serverResponse, HttpStatus.OK));

		// assertThat(service.sendSms("8987876473", "your otp is 4646"),
		// is(dto));

	}

	@Test(expected = InvalidNumberException.class)
	public void invalidContactNumberTest() {
		service.sendSms("jsbchb", "hello your otp is 45373");
	}

	@Test(expected = InvalidNumberException.class)
	public void contactNumberMinimumThresholdTest() {
		service.sendSms("78978976", "hello your otp is 45373");
	}

	@Test(expected = InvalidNumberException.class)
	public void contactNumberMaximumThresholdTest() {
		service.sendSms("7897897458673484376", "hello your otp is 45373");
	}

	@Test
	public void validGateWayTest() {
		service.sendSms("260771610704", "hello your otp is 45373");
	}

}