import { useState } from "react";
import api from "../services/api";

function AddEvent({ onEventAdded, onClose }) {

    const [title, setTitle] = useState("");
    const [time, setTime] = useState("");
    const [error, setError] = useState("");
    const [success, setSuccess] = useState("");

    async function addEvent(event) {

        event.preventDefault();

        try {
            setError("");
            setSuccess("");

            const response = await api.post(
                "/calendar/addEvent",
                {
                    title: title,
                    time: time
                }
            );

            if (!response.data.error) {
                setSuccess(
                    response.data.message ||
                    "Event saved successfully"
                );
                setTitle("");
                setTime("");
                /*
                 * Tell App.js that an event was added.
                 */
                await onEventAdded();
                /*
                 * Close the form after successful save.
                 */
                onClose();
            } else {
                setError(
                    response.data.message ||
                    "Failed to save event"
                );
            }

        } catch (error) {
            console.error(
                "Failed to add event:",
                error
            );
            setError(
                error.response?.data?.message ||
                "Failed to save event"
            );
        }
    }

    return (
        <div className="add-event">
            <h2>
                Add Event
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

            <form onSubmit={addEvent}>
                <div className="form-group">
                    <label>
                        Event Title
                    </label>
                    <input type="text" value={title} onChange={(e) =>
                            setTitle(e.target.value)
                        }
                        placeholder="Enter event title"
                        required />
                </div>

                <div className="form-group">
                    <label>
                        Date and Time
                    </label>

                    <input type="datetime-local" value={time} onChange={(e) =>
                            setTime(e.target.value)
                        }
                        required/>
                </div>

                <div className="button-group">
                    <button type="submit">
                        Submit
                    </button>
                    <button type="button" onClick={onClose}>
                        Cancel
                    </button>
                </div>
            </form>
        </div>
    );
}

export default AddEvent;