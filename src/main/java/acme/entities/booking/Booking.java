
package acme.entities.booking;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Positive;

import acme.client.components.basis.AbstractEntity;
import acme.client.components.mappings.Automapped;
import acme.client.components.validation.Mandatory;
import acme.client.components.validation.Optional;
import acme.client.components.validation.ValidMoment;
import acme.client.components.validation.ValidString;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Booking extends AbstractEntity {

	private static final long	serialVersionUID	= 1L;

	@Mandatory
	@Pattern(regexp = "^[A-Z0-9]{6,8}$", message = "Locator code must be 6 to 8 characters long with uppercase letters and digits")
	@Automapped
	private String				locatorCode;

	@Mandatory
	@ValidMoment(past = true)
	@Temporal(TemporalType.TIMESTAMP)
	@Automapped
	private Date				purchaseMoment;

	@Mandatory
	@ValidString(pattern = "^(ECONOMY|BUSINESS)$", message = "Travel class must be either ECONOMY or BUSINESS")
	@Automapped
	private String				travelClass;

	@Mandatory
	@Positive(message = "Price must be a positive value")
	@Automapped
	private Double				price;

	@Optional
	@ValidString(pattern = "^[0-9A-Fa-f]{1}$", message = "Last nibble of credit card should be a single hexadecimal character")
	@Automapped
	private String				creditCardNibble;  // Optional field for the last nibble of the credit card used for payment
}
