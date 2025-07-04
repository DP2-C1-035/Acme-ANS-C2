
package acme.features.flight_crew_member.flight_assignments;

import java.util.Collection;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.components.views.SelectChoices;
import acme.client.helpers.MomentHelper;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.flight_assignment.AssignmentStatus;
import acme.entities.flight_assignment.FlightAssignment;
import acme.entities.flight_assignment.FlightCrewDuty;
import acme.entities.flight_crew_member.AvailabilityStatus;
import acme.entities.flight_crew_member.FlightCrewMember;
import acme.entities.leg.Leg;

@GuiService
public class FlightCrewMemberFlightAssignmentPublishService extends AbstractGuiService<FlightCrewMember, FlightAssignment> {

	@Autowired
	private FlightCrewMemberFlightAssignmentRepository repository;


	@Override
	public void authorise() {
		boolean status;
		int masterId;
		FlightAssignment flightAssignment;
		FlightCrewMember flightCrewMember;

		masterId = super.getRequest().getData("id", int.class);
		flightAssignment = this.repository.findFlightAssignmentById(masterId);
		flightCrewMember = flightAssignment == null ? null : flightAssignment.getFlightCrewMember();
		status = flightAssignment != null && super.getRequest().getPrincipal().hasRealm(flightCrewMember);

		if (status) {
			String method;
			int legtId;

			method = super.getRequest().getMethod();
			if (method.equals("GET"))
				status = true;
			else {
				legtId = super.getRequest().getData("leg", int.class);
				Leg leg = this.repository.findLegById(legtId);
				Collection<Leg> uncompletedLegs = this.repository.findUncompletedLegs(MomentHelper.getCurrentMoment());
				status = legtId == 0 || uncompletedLegs.contains(leg);
			}
		}

		super.getResponse().setAuthorised(status);
	}

	@Override
	public void load() {
		FlightAssignment flightAssignment;
		int id;

		id = super.getRequest().getData("id", int.class);
		flightAssignment = this.repository.findFlightAssignmentById(id);
		super.getBuffer().addData(flightAssignment);
	}

	@Override
	public void bind(final FlightAssignment flightAssignment) {
		FlightCrewMember flightCrewMember;
		int legId;
		Leg leg;

		flightCrewMember = (FlightCrewMember) super.getRequest().getPrincipal().getActiveRealm();
		legId = super.getRequest().getData("leg", int.class);
		leg = this.repository.findLegById(legId);

		super.bindObject(flightAssignment, "flightCrewDuty", "remarks");
		flightAssignment.setFlightCrewMember(flightCrewMember);
		flightAssignment.setLeg(leg);
		flightAssignment.setLastUpdate(MomentHelper.getCurrentMoment());
	}

	@Override
	public void validate(final FlightAssignment flightAssignment) {
		if (flightAssignment.getAssignmentStatus() != null && flightAssignment.getFlightCrewDuty() != null && flightAssignment.getLeg() != null) {
			if (flightAssignment.getFlightCrewDuty() == FlightCrewDuty.PILOT || flightAssignment.getFlightCrewDuty() == FlightCrewDuty.CO_PILOT) {
				Collection<FlightAssignment> assignmentsOfLeg = this.repository.findPublishedFlightAssignmentsByLegId(flightAssignment.getLeg().getId());
				for (FlightAssignment fa : assignmentsOfLeg)
					if (fa.getFlightCrewDuty() == FlightCrewDuty.PILOT && flightAssignment.getFlightCrewDuty() == FlightCrewDuty.PILOT
						|| fa.getFlightCrewDuty() == FlightCrewDuty.CO_PILOT && flightAssignment.getFlightCrewDuty() == FlightCrewDuty.CO_PILOT) {
						super.state(false, "flightCrewDuty", "flight-crew-member.flight-assignment.validation.duty.publish");
						break;
					}
			}
			if (flightAssignment.getFlightCrewMember().getAvailabilityStatus() != AvailabilityStatus.AVAILABLE)
				super.state(false, "member", "flight-crew-member.flight-assignment.validation.availability-status.publish");
			Collection<FlightAssignment> currentUserAssignments = this.repository.findPublishedUncompletedFlightAssignmentsByFlightCrewMemberId(MomentHelper.getCurrentMoment(), flightAssignment.getFlightCrewMember().getId());
			for (FlightAssignment fa : currentUserAssignments)
				if (fa.getLeg().getFlightNumber() == flightAssignment.getLeg().getFlightNumber()) {
					super.state(false, "leg", "flight-crew-member.flight-assignment.validation.flight-number.publish");
					break;
				}
			Date newDeparture = flightAssignment.getLeg().getScheduledDeparture();
			Date newArrival = flightAssignment.getLeg().getScheduledArrival();
			Collection<FlightAssignment> overlapping = this.repository.findOverlappingPublishedFlightAssignments(flightAssignment.getFlightCrewMember().getId(), newDeparture, newArrival);
			if (!overlapping.isEmpty())
				super.state(false, "leg", "flight-crew-member.flight-assignment.validation.overlapping-leg.publish");
			if (flightAssignment.getAssignmentStatus() == AssignmentStatus.PENDING)
				super.state(false, "assignmentStatus", "flight-crew-member.flight-assignment.validation.assignment-status.publish");
		}
	}

	@Override
	public void perform(final FlightAssignment flightAssignment) {
		flightAssignment.setDraftMode(false);
		this.repository.save(flightAssignment);
	}

	@Override
	public void unbind(final FlightAssignment flightAssignment) {
		Collection<Leg> legs;
		SelectChoices legChoices;
		SelectChoices dutyChoices;
		SelectChoices statusChoices;
		Dataset dataset;
		FlightCrewMember flightCrewMember;
		flightCrewMember = (FlightCrewMember) super.getRequest().getPrincipal().getActiveRealm();

		legs = this.repository.findUncompletedLegs(MomentHelper.getCurrentMoment());

		if (!legs.contains(flightAssignment.getLeg()))
			legChoices = SelectChoices.from(legs, "flightNumber", null);

		else
			legChoices = SelectChoices.from(legs, "flightNumber", flightAssignment.getLeg());

		dutyChoices = SelectChoices.from(FlightCrewDuty.class, flightAssignment.getFlightCrewDuty());
		statusChoices = SelectChoices.from(AssignmentStatus.class, flightAssignment.getAssignmentStatus());

		dataset = super.unbindObject(flightAssignment, "lastUpdate", "remarks", "draftMode");
		dataset.put("member", flightCrewMember.getIdentity().getFullName());
		dataset.put("leg", legChoices.getSelected().getKey());
		dataset.put("legs", legChoices);
		dataset.put("flightCrewDuty", dutyChoices.getSelected().getKey());
		dataset.put("duties", dutyChoices);
		dataset.put("assignmentStatus", statusChoices);

		super.getResponse().addData(dataset);
	}

}
