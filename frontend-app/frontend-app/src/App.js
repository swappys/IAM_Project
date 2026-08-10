import { useEffect, useState } from "react";
import { useAuth } from "./auth/useAuth";
import api from "./services/api";
import EventList from "./components/EventList";
import AddEvent from "./components/AddEvent";
import "./App.css";

function App() {

    const {user,keycloak,authenticated} = useAuth();

    const [events, setEvents] = useState([]);
    const [error, setError] = useState("");
    const [success, setSuccess] = useState("");
    const [showAddEvent, setShowAddEvent] = useState(false);

    /*
     * Get ACR value from Keycloak token
     */
    const acr = keycloak?.tokenParsed?.acr;

    useEffect(() => {

        if (authenticated) {
            loadCalendar();
        }

    }, [authenticated]);


    async function loadCalendar() {
        try {
            setError("")
            const response = await api.get(
                "/calendar/getEvents"
            );
            setEvents(response.data);

        } catch (error) {
            if (error.response?.status === 403) {
                setError(
                    error.response.data?.message ||
                    "You are not authorized to view the calendar."
                );

            } else {
                console.error(
                    "Calendar API failed",
                    error
                );
                setError(
                    "Failed to load calendar."
                );
            }
        }
    }


    function formatDateTime(dateTime) {
        return new Date(dateTime).toLocaleString("en-IE", {
            weekday: "long",
            day: "numeric",
            month: "long",
            year: "numeric",
            hour: "2-digit",
            minute: "2-digit"

        });
    }


    function handleEventAdded() {
        setSuccess(
            "Event saved successfully"
        );
        return loadCalendar();
    }


    if (!authenticated) {
        return (
            <h2 className="loading">
                Loading...
            </h2>
        );
    }

    return (
        <div className="container">
            <h1>
                Calendar Application
            </h1>
            {/* User information */}
            <div className="user-box">
                <p>
                    Welcome, <b>{user?.username}</b>
                </p>

                <button onClick={() => keycloak.logout()}>
                    Logout
                </button>
            </div>
            <hr/>

            <h2>
                Events
            </h2>

            {/* Success message */}

            {success && (
                <p className="success">
                    {success}
                </p>
            )}

            {/* Error message */}
            {error && (
                <p className="error">
                    {error}
                </p>
            )}

            {/* Events */}

            <EventList
                events={events}
                acr={acr}
                formatDateTime={formatDateTime}
            />

            {/* Refresh and Add Event buttons */}
            {acr === "otp" && (

                <div className="button-group">

                    <button onClick={() => loadCalendar()}>
                        Refresh
                    </button>

                    <button onClick={() => { setShowAddEvent(true); 
                            setError("");
                            setSuccess("");
                        }}> Add Event </button>
                </div>

            )}


            {/* Add Event component */}

            {showAddEvent && acr === "otp" && (

                <AddEvent
                    onEventAdded={handleEventAdded}
                    onClose={() =>
                        setShowAddEvent(false)
                    }
                />

            )}

        </div>
    );
}

export default App;