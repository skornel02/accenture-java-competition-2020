import React, {useContext, useEffect, useState} from "react";
import moment, {Moment} from "moment";
import {isDateAfterDate, isDateBeforeDate, isSameDateMoment} from "../utility/DateUtils";

const DateContext = React.createContext<Moment>(moment());

const WorkerDateSelector: React.FunctionComponent = props => {
    const [date, setDate] = useState<Moment>(moment());

    useEffect(() => {
        document.addEventListener("keydown", handleKeyPress);
        return () => {
            document.removeEventListener("keydown", handleKeyPress);
        };
    });

    const handleKeyPress = (event: KeyboardEvent) => {
        switch (event.code) {
            case "ArrowRight":
                if(!isDateBeforeDate(date, moment().add(1, "month")))
                    break;

                nextDate();
                break;
            case "ArrowLeft":
                lastDate();
                break;
            default:
                break;
        }
    };

    const lastDate = () => {
        setDate(moment(date).add(-1, "day"));
    }
    const nextDate = () => {
        setDate(moment(date).add(1, "day"));
    }

    return (
        <>
            <div style={{display: "flex", margin: "0 auto", alignItems: "center"}}>
                <div>
                    <button className="text-right text-3xl pr-2 md:text-4xl"
                            onClick={lastDate}
                    >
                        {"<"}
                    </button>
                </div>
                <div style={{width: "5px"}}/>
                <div className="inline w-auto text-center text-3xl md:text-4xl">
                    {date.format("YYYY-MM-DD")}
                </div>
                <div style={{width: "5px"}}/>
                <div>
                    <button className="text-left text-3xl pl-2 md:text-4xl"
                            hidden={isSameDateMoment(date, moment().add(1, "month"))}
                            onClick={nextDate}
                    >
                        {">"}
                    </button>
                </div>
            </div>
            <DateContext.Provider value={date}>
                {props.children}
            </DateContext.Provider>
        </>
    );
};

export const useSelectedDate = (): Moment => {
    return useContext(DateContext);
};
export default WorkerDateSelector;