function CalendarActions({
    onRefresh,
    onAddEvent
}) {
    return (
        <div className="button-group">

            <button onClick={onRefresh}>
                Refresh
            </button>

            <button onClick={onAddEvent}>
                Add Event
            </button>

        </div>
    );
}

export default CalendarActions;

