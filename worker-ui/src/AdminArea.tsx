import React from 'react';
import {useAuthInformation} from "./SecuredArea";

const AdminArea: React.FunctionComponent = props => {
    const info = useAuthInformation();
    if (info.permission !== "ADMIN" && info.permission !== "SUPER_ADMIN")
        return null;

    return (
        <div>
            <p>
                Admin {info.admin!.name}
            </p>
        </div>
    )
};

export default AdminArea;