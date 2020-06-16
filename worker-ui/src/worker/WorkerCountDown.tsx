import React from "react";
import {RemainingTime, WorkerResource} from "../resource/Resources";
import {useTranslation} from "react-i18next";
import workerStyle from "../resource/style/worker.module.css";

const WorkerCountDown: React.FunctionComponent<{
    worker: WorkerResource,
    remainingTime: RemainingTime,
}> = props => {
    const {t} = useTranslation();

    const projectedTime = props.remainingTime.projectedEntryTime === "00:00:00" ?
        t("worker.instantly")
        : props.remainingTime.projectedEntryTime;

    switch (props.remainingTime.status) {
        case "InOffice":
            return (
                <div>
                    <h1 className={workerStyle.BigText}>
                        {t("worker.currentlyInside")}
                    </h1>
                </div>
            );
        case "OnList":
            return (
                <div>
                    <h1 className={workerStyle.BigText}>
                        {t("worker.currentlyWaiting")}
                    </h1>
                    <h2 className={workerStyle.SmallText}>
                        {t("worker.estimatedTimeToEntry")}
                    </h2>
                    <h3 className={workerStyle.TimeText}>
                        {projectedTime}
                    </h3>
                </div>
            );
        case "WorkingFromHome":
            return (
                <div>
                    <h1 className={workerStyle.BigText}>
                        {t("worker.currentlyNotWaiting")}
                    </h1>
                    <h2 className={workerStyle.SmallText}>
                        {t("worker.estimatedTimeToEntry")}
                        <span className={workerStyle.HintText}>
                            ({t("worker.entryMightChange")})
                        </span>
                    </h2>
                    <h3 className={workerStyle.TimeText}>
                        {projectedTime}
                    </h3>
                </div>
            );
    }
    return null;
}

export default WorkerCountDown;