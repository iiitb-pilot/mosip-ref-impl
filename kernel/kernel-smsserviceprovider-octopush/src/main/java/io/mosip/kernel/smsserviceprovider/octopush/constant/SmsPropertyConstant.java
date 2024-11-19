package io.mosip.kernel.smsserviceprovider.octopush.constant;

/**
 * This enum provides all the constant for sms notification.
 * 
 * @author Ritesh sinha
 * @since 1.0.0
 *
 */
public enum SmsPropertyConstant {

	AUTH_KEY("api-key"),API_LOGIN("api-login"),TYPE("type"),PURPOSE("purpose"),RECIPIENTS("recipients"), SMS_MESSAGE("text"), RECIPIENT_NUMBER("phone_number"), COUNTRY_CODE("countryCode"), ROUTE("route"),
	SENDER_ID("sender"), VENDOR_RESPONSE_SUCCESS("success"), SUCCESS_RESPONSE("Sms Request Sent"), UNICODE("unicode"),
	PROJECT_NAME("kernel-notification-sms"), SUFFIX_MESSAGE(" digits"), NUMBERS("numbers");

	/**
	 * The property for sms notification.
	 */
	private String property;

	/**
	 * The constructor to set sms property.
	 * 
	 * @param property the property to set.
	 */
	private SmsPropertyConstant(String property) {
		this.property = property;
	}

	/**
	 * Getter for sms property.
	 * 
	 * @return the sms property.
	 */
	public String getProperty() {
		return property;
	}

}
