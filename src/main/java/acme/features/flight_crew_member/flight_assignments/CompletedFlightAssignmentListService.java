
package acme.features.flight_crew_member.flight_assignments;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.helpers.MomentHelper;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.flight_assignment.FlightAssignment;
import acme.entities.flight_crew_member.FlightCrewMember;

@GuiService
public class CompletedFlightAssignmentListService extends AbstractGuiService<FlightCrewMember, FlightAssignment> {

	@Autowired
	private FlightCrewMemberFlightAssignmentRepository repository;


	@Override
	public void authorise() {
		super.getResponse().setAuthorised(true);
	}

	@Override
	public void load() {
		Collection<FlightAssignment> flightAssignments;
		int flightCrewMemberId;

		flightCrewMemberId = super.getRequest().getPrincipal().getActiveRealm().getId();
		flightAssignments = this.repository.findCompletedFlightAssignmentsByFlightCrewMemberId(MomentHelper.getCurrentMoment(), flightCrewMemberId);

		super.getBuffer().addData(flightAssignments);
	}

	@Override
	public void unbind(final FlightAssignment flightAssignment) {
		Dataset dataset;

		dataset = super.unbindObject(flightAssignment, "flightCrewDuty", "lastUpdate", "assignmentStatus", "draftMode");
		dataset.put("leg.legLabel", flightAssignment.getLeg().getFlightNumber());
		super.getResponse().addData(dataset);
	}
}
