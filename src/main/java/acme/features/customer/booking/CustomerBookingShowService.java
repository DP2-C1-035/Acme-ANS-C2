
package acme.features.customer.booking;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.components.views.SelectChoices;
import acme.client.helpers.MomentHelper;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.booking.Booking;
import acme.entities.flight.Flight;
import acme.realms.customer.Customer;

@GuiService
public class CustomerBookingShowService extends AbstractGuiService<Customer, Booking> {

	@Autowired
	private CustomerBookingRepository repository;


	@Override
	public void authorise() {
		boolean status;
		int masterId;
		Booking booking;
		Customer customer;

		masterId = super.getRequest().getData("id", int.class);
		booking = this.repository.findBookingById(masterId);
		customer = null;
		if (booking != null)
			customer = booking.getCustomer();
		status = super.getRequest().getPrincipal().hasRealm(customer) && booking != null;

		super.getResponse().setAuthorised(status);
	}

	@Override
	public void load() {
		Booking booking;
		int id;

		id = super.getRequest().getData("id", int.class);
		booking = this.repository.findBookingById(id);

		super.getBuffer().addData(booking);
	}
	@Override
	public void unbind(final Booking booking) {
		Dataset dataset;
		Collection<Flight> flights;
		SelectChoices choices;
		Date moment;
		Flight selectedFlight;

		moment = MomentHelper.getCurrentMoment();
		selectedFlight = booking.getFlight();

		flights = this.repository.findFlightsWithFirstLegAfter(moment);

		if (selectedFlight != null && !flights.contains(selectedFlight)) {
			flights = new ArrayList<>(flights);
			flights.add(selectedFlight);
		}

		// Filtrar vuelos con flightRoute nulo y eliminar duplicados
		Set<String> seenRoutes = new HashSet<>();
		Iterator<Flight> iter = flights.iterator();
		while (iter.hasNext()) {
			Flight f = iter.next();
			String route = f.getFlightRoute();
			if (route == null || !seenRoutes.add(route))
				iter.remove(); // eliminar vuelo con route nulo o duplicado
		}

		choices = SelectChoices.from(flights, "flightRoute", selectedFlight);

		dataset = super.unbindObject(booking, "locatorCode", "travelClass", "price", "creditCardNibble", "purchaseMoment", "draftMode");
		dataset.put("flight", choices.getSelected().getKey());
		dataset.put("flights", choices);

		super.getResponse().addData(dataset);
	}

}
