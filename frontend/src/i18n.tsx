import i18next from 'i18next';
import { initReactI18next } from 'react-i18next';

import en from './assets/languages/en.json';
import nl from './assets/languages/nl.json';

const url = new URL(document.location.href);
let params = url.searchParams;
const lng = params.get('lang') ?? 'en';

i18next
    .use(initReactI18next)
    .init({
        fallbackLng: 'nl',
        lng: lng,
        resources: {
            en,
            nl
        }
    });
