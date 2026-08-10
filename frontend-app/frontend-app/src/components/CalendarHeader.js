function CalendarHeader({
    user,
    keycloak,
    success,
    error
}) {
    return (
        <>
            <h1>
                Calendar Application
            </h1>

            <div className="user-box">
                <p>
                    Welcome, <b>{user?.username}</b>
                </p>

                <button onClick={() => keycloak.logout()}>
                    Logout
                </button>
            </div>

            <hr />

            <h2>
                Events
            </h2>

            {success && (
                <p className="success">
                    {success}
                </p>
            )}

            {error && (
                <p className="error">
                    {error}
                </p>
            )}
        </>
    );
}

export default CalendarHeader;