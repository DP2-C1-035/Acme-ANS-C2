
package acme.features.flight_crew_member.flight_assignments;

import java.util.Collection;
import java.util.Date;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import acme.client.repositories.AbstractRepository;
import acme.entities.activity_log.ActivityLog;
import acme.entities.flight_assignment.FlightAssignment;
import acme.entities.flight_crew_member.AvailabilityStatus;
import acme.entities.flight_crew_member.FlightCrewMember;
import acme.entities.leg.Leg;

@Repository
public interface FlightCrewMemberFlightAssignmentRepository extends AbstractRepository {

	@Query("select fa from FlightAssignment fa where fa.leg.scheduledArrival < :now and fa.flightCrewMember.id = :flightCrewMemberId")
	Collection<FlightAssignment> findCompletedFlightAssignmentsByFlightCrewMemberId(Date now, int flightCrewMemberId);

	@Query("select fa from FlightAssignment fa where fa.leg.scheduledArrival > :now and fa.flightCrewMember.id = :flightCrewMemberId")
	Collection<FlightAssignment> findUncompletedFlightAssignmentsByFlightCrewMemberId(Date now, int flightCrewMemberId);

	@Query("select fa from FlightAssignment fa where fa.leg.scheduledArrival > :now and fa.flightCrewMember.id = :flightCrewMemberId and fa.draftMode = false")
	Collection<FlightAssignment> findPublishedUncompletedFlightAssignmentsByFlightCrewMemberId(Date now, int flightCrewMemberId);

	@Query("select fa from FlightAssignment fa where fa.id = :id")
	FlightAssignment findFlightAssignmentById(int id);

	@Query("select fcm from FlightCrewMember fcm where fcm.workingFor.id = :airlineId and fcm.availabilityStatus = :status")
	Collection<FlightCrewMember> findAvailableFlightCrewMembersFromAirline(int airlineId, AvailabilityStatus status);

	@Query("select l from Leg l where l.scheduledDeparture>:now and l.draftMode = false")
	Collection<Leg> findUncompletedLegs(Date now);

	@Query("select al from ActivityLog al where al.flightAssignment.id = :flightAssignmentId")
	Collection<ActivityLog> findActivityLogsByflightAssignmentId(int flightAssignmentId);

	@Query("select fcm from FlightCrewMember fcm where fcm.id = :flightCrewMemberId")
	FlightCrewMember findFlightCrewMemberById(int flightCrewMemberId);

	@Query("select l from Leg l where l.id = :legId")
	Leg findLegById(int legId);

	@Query("select fa from FlightAssignment fa where fa.leg.id = :legId and fa.draftMode = false")
	Collection<FlightAssignment> findPublishedFlightAssignmentsByLegId(int legId);

	@Query("select fa from FlightAssignment fa where fa.flightCrewMember.id = :memberId and fa.draftMode = false and fa.leg.scheduledDeparture < :newArrival and fa.leg.scheduledArrival > :newDeparture")
	Collection<FlightAssignment> findOverlappingPublishedFlightAssignments(int memberId, Date newDeparture, Date newArrival);

	@Query("SELECT fa FROM FlightAssignment fa WHERE fa.flightCrewMember.id = :id")
	Collection<FlightAssignment> findFlightAssignmentsByFlightCrewMemberId(int id);

}
