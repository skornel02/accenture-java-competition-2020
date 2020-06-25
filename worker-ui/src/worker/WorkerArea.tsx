import React from 'react';
import {useAuthInformation} from "../authentication/SecuredArea";
import {RemainingTime} from "../resource/Resources";
import useSWR from "swr";
import Backend from "../resource/Backend";
import 'react-calendar/dist/Calendar.css';
import WorkerCountDown from "./WorkerCountDown";
import WorkerCalendar from "./WorkerCalendar";
import WorkerDateSelector from "./WorkerDateSelector";
import WorkerUIChooser from "./WorkerUIChooser";


const WorkerArea: React.FunctionComponent = () => {
    return (
        <div style={{display: "flex", flexDirection: "column"}}>
            <WorkerDateSelector>
                <WorkerUIChooser/>
            </WorkerDateSelector>
        </div>
    )
};

export default WorkerArea;