import React from "react";
import {useSelectedDate} from "./WorkerDateSelector";
import moment from "moment";
import {isDateAfterDate, isDateBeforeDate} from "../utility/DateUtils";
import WorkerOfficeHours from "./WorkerOfficeHours";
import WorkerTickets from "./WorkerTickets";
import WorkerToday from "./WorkerToday";

const WorkerUIChooser: React.FunctionComponent = () => {
    const selectedDate = useSelectedDate();
    const now = moment();
    if (isDateBeforeDate(selectedDate, now)) {
        return <WorkerOfficeHours/>
    }
    if (isDateAfterDate(selectedDate, now)) {
        return <WorkerTickets/>
    }

    return <WorkerToday/>;
};

export default WorkerUIChooser;