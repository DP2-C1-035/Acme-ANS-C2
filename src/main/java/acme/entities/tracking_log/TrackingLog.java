
package acme.entities.tracking_log;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.Valid;

import acme.client.components.basis.AbstractEntity;
import acme.client.components.mappings.Automapped;
import acme.client.components.validation.Mandatory;
import acme.client.components.validation.Optional;
import acme.client.components.validation.ValidMoment;
import acme.client.components.validation.ValidScore;
import acme.client.components.validation.ValidString;
import acme.constraints.ValidTrackingLog;
import acme.entities.claim.Claim;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@ValidTrackingLog
@Table(name = "TrackingLog", indexes = {
	@Index(columnList = "claim_id"), @Index(columnList = "indicator"), @Index(columnList = "resolutionPercentage")
})
public class TrackingLog extends AbstractEntity {

	// Serialisation version --------------------------------------------------

	private static final long		serialVersionUID	= 1L;

	// Attributes -------------------------------------------------------------

	@Mandatory
	@ValidMoment(past = true)
	@Temporal(TemporalType.TIMESTAMP)
	private Date					lastUpdateMoment;

	@Mandatory
	@ValidMoment(past = true)
	@Temporal(TemporalType.TIMESTAMP)
	private Date					creationMoment;

	@Mandatory
	@ValidString(min = 1, max = 50)
	@Automapped
	private String					step;

	@Mandatory
	@ValidScore
	@Automapped
	private Double					resolutionPercentage;

	@Mandatory
	@Valid
	@Automapped
	private TrackingLogIndicator	indicator;

	@Optional
	@ValidString(min = 0, max = 255)
	@Automapped
	private String					resolution;

	@Mandatory
	@Automapped
	private boolean					draftMode;

	// Derived attributes -----------------------------------------------------

	// Relationships ----------------------------------------------------------

	@Mandatory
	@Valid
	@ManyToOne(optional = false)
	private Claim					claim;
}
