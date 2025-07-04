<%-- 
- form.jsp
- 
- Copyright (C) 2012-2025 Rafael Corchuelo.
- 
- In keeping with the traditional purpose of furthering education and research, it is
- the policy of the copyright owner to permit non-commercial use and redistribution of
- this software. It has been tested carefully, but it is not guaranteed for any particular
- purposes.  The copyright owner does not offer any warranties or representations, nor do
- they accept any liabilities with respect to them.
--%>

<%@page%>

<%@taglib prefix="jstl" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="acme" uri="http://acme-framework.org/"%>

<acme:form>
    <acme:input-textbox code="customer.booking.form.label.locatorCode" path="locatorCode"/>
    <acme:input-textbox code="customer.booking.form.label.travelClass" path="travelClass" placeholder="customer.booking.form.placeholder.travelClass"/>
    <acme:input-money code="customer.booking.form.label.price" path="price"/>
    <acme:input-textbox code="customer.booking.form.label.creditCardNibble" path="creditCardNibble" placeholder="customer.booking.form.placeholder.creditCardNibble"/>
    <acme:input-select code="customer.booking.form.label.flight" path="flight" choices="${flights}"/>
    
    <jstl:if test="${_command != 'create'}">
        <acme:input-textbox code="customer.booking.form.label.purchaseMoment" path="purchaseMoment" readonly="true"/>
    </jstl:if>

    <jstl:choose>	 
        <jstl:when test="${_command == 'show' && draftMode == false}">
            <acme:button code="customer.booking.form.button.passengers" action="/customer/booking-record/list?masterId=${id}"/>	
        </jstl:when>
        <jstl:when test="${acme:anyOf(_command, 'show|delete|update|publish') && draftMode == true}">
            <acme:button code="customer.booking.form.button.passengers" action="/customer/booking-record/list?masterId=${id}"/>
            <acme:submit code="customer.booking.form.button.delete" action="/customer/booking/delete"/>
            <acme:button code="customer.booking.form.button.passenger.delete" action="/customer/booking-record/delete?bookingId=${id}"/>
            <acme:submit code="customer.booking.form.button.update" action="/customer/booking/update"/>
            <acme:submit code="customer.booking.form.button.publish" action="/customer/booking/publish"/>
        </jstl:when>
        <jstl:when test="${_command == 'create'}">
            <acme:submit code="customer.booking.form.button.create" action="/customer/booking/create"/>
        </jstl:when>	
    </jstl:choose>

    <jstl:if test="${_command != 'create'}">
        <acme:button code="customer.booking.form.show.passengers" action="/customer/passenger/list?bookingId=${id}"/>
    </jstl:if>
</acme:form>
