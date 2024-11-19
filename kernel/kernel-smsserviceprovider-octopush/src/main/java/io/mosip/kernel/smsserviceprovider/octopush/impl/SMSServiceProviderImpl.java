/**
 * 
 */
package io.mosip.kernel.smsserviceprovider.octopush.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import io.mosip.kernel.core.notification.exception.InvalidNumberException;
import io.mosip.kernel.core.notification.model.SMSResponseDto;
import io.mosip.kernel.core.notification.spi.SMSServiceProvider;
import io.mosip.kernel.core.util.StringUtils;
import io.mosip.kernel.smsserviceprovider.octopush.constant.SmsExceptionConstant;
import io.mosip.kernel.smsserviceprovider.octopush.constant.SmsPropertyConstant;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.JsonProcessingException;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.MediaType;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;


/**
 * @author Ritesh Sinha
 * @since 1.0.0
 */
@Component
public class SMSServiceProviderImpl implements SMSServiceProvider {

	@Autowired
    private RestTemplate restTemplate;

	@Value("${mosip.kernel.sms.enabled:false}")
    private boolean smsEnabled;

	@Value("${mosip.kernel.sms.country.code}")
    private String countryCode;

	@Value("${mosip.kernel.sms.number.length}")
    private int numberLength;

	@Value("${mosip.kernel.sms.password:null}")
	private String password;

	@Value("${mosip.kernel.sms.route:null}")
    private String route;

	@Value("${mosip.kernel.sms.unicode:1}")
    private String unicode;

	@Value("${mosip.kernel.sms.api}")
    private String api;

	@Value("${mosip.kernel.sms.apiLogin:null}")
    private String apiLogin;

	@Value("${mosip.kernel.sms.authkey:null}")
    private String authKey;

	@Value("${mosip.kernel.sms.sender}")
    private String sender;

	@Value("${mosip.kernel.sms.type:null}")
    private String type;

	@Value("${mosip.kernel.sms.purpose:null}")
	private String purpose;

    @PostConstruct
    public void init() {
        // Ensure UTF-8 encoding for the RestTemplate
        restTemplate.getMessageConverters().add(0, new StringHttpMessageConverter(StandardCharsets.UTF_8));
    }

	@Override
	public SMSResponseDto sendSms(String contactNumber, String message) {
		SMSResponseDto smsResponseDTO = new SMSResponseDto();
		validateInput(contactNumber);
		try {
			// Prepare headers
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON);
			headers.set(SmsPropertyConstant.AUTH_KEY.getProperty(), authKey);
			headers.set(SmsPropertyConstant.API_LOGIN.getProperty(), apiLogin);

			// Prepare body
			Map<String, Object> body = new HashMap<>();
			body.put(SmsPropertyConstant.SENDER_ID.getProperty(), sender);
			body.put(SmsPropertyConstant.SMS_MESSAGE.getProperty(), message);
			body.put(SmsPropertyConstant.TYPE.getProperty(), type);
			body.put(SmsPropertyConstant.PURPOSE.getProperty(), purpose);

			List<Map<String, String>> recipients = new ArrayList<>();
			Map<String, String> recipient = new HashMap<>();
			recipient.put(SmsPropertyConstant.RECIPIENT_NUMBER.getProperty(), countryCode+contactNumber);
			recipients.add(recipient);

			body.put(SmsPropertyConstant.RECIPIENTS.getProperty(),recipients);

			// Convert body to JSON
			ObjectMapper objectMapper = new ObjectMapper();
			String requestBody = objectMapper.writeValueAsString(body);

			// Create HttpEntity
			HttpEntity<String> entity = new HttpEntity<>(requestBody, headers);

			// Send POST request and process response
			ResponseEntity<String> response = restTemplate.postForEntity(api, entity, String.class);

		} catch (HttpClientErrorException | HttpServerErrorException e) {
        System.err.println("HTTP Error: " + e.getResponseBodyAsString());
			throw new RuntimeException(e.getResponseBodyAsString());
		} catch (JsonProcessingException e) {
			throw new RuntimeException(e.getMessage());
		}
		smsResponseDTO.setMessage(SmsPropertyConstant.SUCCESS_RESPONSE.getProperty());
		smsResponseDTO.setStatus("success");
		return smsResponseDTO;
	}

	private void validateInput(String contactNumber) {
		if (!StringUtils.isNumeric(contactNumber) || contactNumber.length() < numberLength
				|| contactNumber.length() > numberLength) {
			throw new InvalidNumberException(SmsExceptionConstant.SMS_INVALID_CONTACT_NUMBER.getErrorCode(),
					SmsExceptionConstant.SMS_INVALID_CONTACT_NUMBER.getErrorMessage() + numberLength
							+ SmsPropertyConstant.SUFFIX_MESSAGE.getProperty());
		}
	}

}