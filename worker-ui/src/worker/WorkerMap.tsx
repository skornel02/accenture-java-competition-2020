import React, {useEffect, useState} from "react";
import workerStyle from "../resource/style/worker.module.css";

// language=CSS
const svgCSS = `svg {
    max-height: 80vh;
}

.restricted {
    fill: black;
    fill-opacity: 0.34538203;
}

.disabled {
    fill: darkgrey;
    fill-opacity: 0.34538203;
}`

const WorkerMap: React.FunctionComponent<{
    svg: string | undefined
}> = props => {
    const [color, setColor] = useState<string>("green");
    useEffect(() => {
        const timeout = setTimeout(() => {
            if (color === "green")
                setColor("orange");
            if (color === "orange")
                setColor("green");
        }, 300)
        return () => {
            clearTimeout(timeout);
        }
    })

    if (props.svg === undefined)
        return null;

    return (<>
        <style>
            {svgCSS}
        </style>
        <style>
            {".ws-occupiable {fill: " + color + "; }"}
        </style>
        <div className={workerStyle.WorkerMapContainer} dangerouslySetInnerHTML={{__html: props.svg}}/>
    </>)
};

export default WorkerMap;