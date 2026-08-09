function EventList({ events, acr, formatDateTime }) {

    if (events.length === 0 && acr === "otp") {
        return (
            <p>
                No events available
            </p>
        );
    }

    if (events.length === 0) {
        return null;
    }

    return (
        <ul className="events">

            {events.map((event) => (

                <li key={event.id}>
                    <b>
                        {event.title}
                    </b>
                    <span>
                        {" - "}
                        {formatDateTime(event.time)}
                    </span>
                </li>

            ))}

        </ul>
    );
}

export default EventList;