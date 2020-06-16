# KIBe 
A KIBe projekt törekszik a jelen járványügyi helyzeben arra, hogy mindenki problémáját a lehető legjobban oldja meg. Ebben a leírásban felhasználási szempontok szerint fogjuk bemutatni a szoftvercsomagot.
## Alkalmazott
A szoftver alap koncepciója, hogy minden alkalmazottnak lehetősége van időpontot foglalni, hogy biztonságosan léphessen az épület területére.
A rendszer ezentúl azt is jelzi, hogy az alkalmazott éppen bemehetne-e az irodába, és ha sorbááll, vagy most állna sorba, akkor mennyi idő múlva várhatna a beengedésig. Ezt a bent lévők és a sorban állók átlagos benntöltött ideje alapján vetíti előre a rendszer.
### Webkliens
A rendszer össes funckiója elérhető a KIBe webkliensén keresztül. Ezt az eszközt a Spring Framework hajtja, és Thymeleaf végzi a renderelést a Google Material dizájnelve szerint.
### PWA
A KIBe rendelkezik egy progresszív webapplikációval. Ennek a koncepciója az, hogy egy mindenhol elérhető, reszponzív eszközt adunk a felhasználó kezébe, ami a válogatott funkcionalitásért cserébe egyszerűbb és jobb felhasználó élményt nyújt. Ez a kliens a React könyvtár felhasználásával készült. 
## Adminisztrátor
Az adminisztrátor (HR-es) számára elérhetőek hasznos eszközök, amivel meg tudja mondani mi történik az irodában, anélkül, hogy ott lenne. Egyik kedvenc funkciónk, hogy meg lehet tekinteni az irodában jelenleg tartozkodó alkalmazottakat, a belépésre várakozó alkalmazottakat, akiknek rögtön levelet is lehet küldeni, továbbá probléma esetén távolról ki- vagy beléptethetőek az alkalmazottak (logikailag, nem fizikailag).
Ezen túl lehetőségük van az összes felhasználó kilistázására, az adataik és foglalásaik módosítására, illetve azok törlésére. Továbbá a rendszergazdának lehetősége van ezen a felületen az adminisztrátorok kilistázására és az adataik módosítására.
## Statisztika
Az épület használatának előrevetítése érdekében minden adat lekérhető az API segítségével.
## Rendszer tervező
A rendszer kiépítőjének az életét ez a szoftvercsomag úgy könnyíti meg, hogy minden egyes program Docker támogatott, ezen túl előre be van konfigurálva a Docker Compose, ami lehetővé teszi az egyszerű telepítést.
Ezen túl a backendet alkotó szoftver alkalmas horizontális skálázásra, ami a felmerülő teljesítmény kérdéseket megválaszolja.
## Szoftver fejlesztő
Egy új fejlesztőnek megkönnyíti a projekten való munkáját az, hogy a backend a REST elveket a harmadik szintjén követi, így a tartalmak egymásra szisztéma szerint hivatkoznak. A platform megismerését továbbá segítí a beépített Swagger felület, ahol ki is lehet próbálni a funkciókat. Mindezen túl elérhető a fejlesztők által készített [Postman dokumentáció](https://documenter.getpostman.com/view/5139955/Szzg9yhv) is.
## Szoftver fenntartó 
Az elvárásoknak megfelelően a kód a clean kód elvek szerint készült, és kritikus rendszereknél unit tesztelve van.
## Külön elismerés:
Máté csapattársunk gépének, aki a verseny ideje alatt villámcsapás által vesztette életét (meg lett villámcsapva).
