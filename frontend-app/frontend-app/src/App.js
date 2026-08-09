import { useEffect, useState } from "react";
import { useAuth } from "./auth/useAuth";
import api from "./services/api";
import "./App.css";

function App() {
    const { user, keycloak, authenticated } = useAuth();
    const [events, setEvents] = useState([]);
    const [error, setError] = useState("");

    useEffect(() => {
        if (authenticated) {
            loadCalendar();
        }
    }, [authenticated]);


    async function loadCalendar() {
        try {
            const response = await api.get("/calendar");
            setEvents(response.data);

        } catch (error) {
            if(error.response?.status===403){
                setError(error.response.data.message);
            }else{
                console.error(
                    "Calendar API failed",
                    error
                );
            }

        }
    }

    if (!authenticated) {
        return <h2 className="loading">Loading...</h2>;
    }
    return (
        <div className="container">
            <h1>
                Calendar Application
            </h1>
            <div className="user-box">
                <p>
                    Welcome, <b>{user?.username}</b>
                </p>
                <button
                    onClick={() => keycloak.logout()}
                >
                    Logout
                </button>
            </div>
            <hr/>
            <h2>
                Events
            </h2>
            {
                error&&error.length > 0?(
                    <p>
                        {error}
                     </p>
                ): events.length===0?(

                    <p>
                        No events available
                    </p>
                ):
                (
                    <ul className="events">
                        {
                            events.map((event, index) => (
                                <li key={index}>
                                    <b>
                                        {event.Title}
                                    </b>
                                    <span>
                                        {" - "}
                                        {event.Time}
                                    </span>
                                </li>
                            ))
                        }
                    </ul>
                )
            }

        </div>
    );
}

export default App;