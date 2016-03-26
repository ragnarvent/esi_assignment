package rentit.com.sales.domain.validation;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

import rentit.com.sales.domain.model.ContactPerson;

public class ContactPersonValidator implements Validator {

	public boolean supports(Class<?> clazz) {
		return ContactPerson.class.equals(clazz);
	}

	public void validate(Object object, Errors errors) {
		ContactPerson cont = (ContactPerson) object;

		ValidationUtils.rejectIfEmptyOrWhitespace(errors, cont.getName(), "'Contact name' cannot be empty");
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, cont.getEmail(), "'Contact email' cannot be empty");

		if (!validate(cont.getEmail()))
			errors.reject("email", "'email' is wrongly formatted");
	}

	public static final Pattern VALID_EMAIL_ADDRESS_REGEX = Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$",
			Pattern.CASE_INSENSITIVE);

	public static boolean validate(String emailStr) {
		Matcher matcher = VALID_EMAIL_ADDRESS_REGEX.matcher(emailStr);
		return matcher.find();
	}
}