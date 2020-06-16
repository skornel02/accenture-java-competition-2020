import i18n from 'i18next'
import LanguageDetector from "i18next-browser-languagedetector"
import {initReactI18next} from 'react-i18next'
import XHR from 'i18next-xhr-backend'
import languageEN from './en/translate.json'
import languageHU from './hu/translate.json'

i18n
    .use(XHR)
    .use(LanguageDetector)
    .use(initReactI18next)
    .init({
        resources: {
            en: languageEN,
            hu: languageHU
        },
        lng: "en",
        fallbackLng: "en",
        debug: process.env.NODE_ENV === "development",
        ns: ["translations"],
        defaultNS: "translations",
        keySeparator: ".",
        interpolation: {
            escapeValue: false,
            formatSeparator: ","
        },
        react: {
            wait: true,
            bindI18n: 'languageChanged loaded',
            bindStore: 'added removed',
            nsMode: 'default'
        }
    }, () => {})

export default i18n;