import React from 'react';
import 'react-calendar/dist/Calendar.css';
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