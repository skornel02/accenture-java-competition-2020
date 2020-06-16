import React from "react";
import style from './resource/style/footer.module.css';
import logo from './resource/img/logo.png';

const Footer: React.FunctionComponent<{
    handleLogout: () => void
}> = (props => {
    return (
        <div className={style.Logout}>
            <a onClick={props.handleLogout} href="#logout">Kijelentkezés</a>
            <img src={logo} alt="logo" className={style.LogoutImage}/>
            <small>
                KIBe {new Date().getFullYear()} &copy;
            </small>
        </div>
    );
});

export default React.memo(Footer, () => true);