import React from "react";
import workerStyle from "../resource/style/worker.module.css";
import moment from "moment";
import {useTranslation} from "react-i18next";
import useSWR from "swr";
import {OfficeHoursResource} from "../resource/Resources";
import Backend from "../resource/Backend";
import {useAuthInformation} from "../authentication/SecuredArea";
import {isSameDate} from "../utility/DateUtils";
import {useSelectedDate} from "./WorkerDateSelector";

const WorkerOfficeHours: React.FunctionComponent = () => {
    const info = useAuthInformation();
    const worker = info.worker!;
    const selectedDate = useSelectedDate();
    const {t} = useTranslation();
    const {data: officeHours} = useSWR<OfficeHoursResource[]>(
        () => "/workers/" + worker.id + "/office-hours",
        () => Backend.getOfficeHours(worker.id),
        {suspense: true}
    );

    const officeHoursToday = officeHours?.filter(officeHour => isSameDate(selectedDate.toDate(), new Date(officeHour.leave))) ?? [];

    return (
        <div>
            <h1 className={workerStyle.BigText}>
                {t("worker.officeHours")}
            </h1>
            <div>
                {
                    officeHoursToday.length !== 0 ? (
                        officeHoursToday?.map((officeHour, i) => {
                            return (
                                <p className={workerStyle.SmallText} key={"timespan:" + i}>
                                    {moment(officeHour.arrive).format("HH:MM:SS")}
                                     ->
                                    {moment(officeHour.leave).format("HH:MM:SS")}
                                </p>
                            )
                        })
                    ) : (
                        <p className={workerStyle.SmallText}>
                            {t("worker.noOfficeHours")}
                        </p>
                    )
                }
            </div>
        </div>
    );
};

export default WorkerOfficeHours;