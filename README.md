# KIBe 
A KIBe projekt törekszik a jelen járványügyi helyzetben arra, hogy mindenki problémáját a lehető legjobban oldja meg.
## Kibe részei
### Alkalmazotti PWA
Ez egy alacsony hálózati kapcsolat mellett is jól működő reszponzív, progresszív web alkalmazás, melynek célja hogy az alkalmazottak a lehető legkevesebbet ütközzenek a rendszerrel, és azzal foglalkozhassanak, ami tényleg fontos. *(React, Typescript)*
### Bejelentkeztető rendszer
Egy naiv webprogram, ami az alkalmazottak ki- és bemenetelét kezeli aranyos képek segítségével. *(React, Typescript)*
### Webfelület
Ezen a felületen lehetőség nyílik a rendszer összes funkcióját kezelni, mind az alkalmazottaknak, mind az adminisztrátoroknak. *(Spring, Tymeleaf)*
## Lehetőségek
A KIBe lehetőséget nyújt az iroda körüli élet hatékony irányítására, kezelésére. Ezt erőteljes alkalmazotti és adminisztrátori eszközökkel éri el. Az alkalmazottak könnyű szerrel kérhetnek hozzáférést az irodához, amely alapján a rendszer sorba állítja őket, és automatikusan előrevetíti a várakozási időt. Amint bejutott az alkalmazott az irodába, a KIBe automatikusan választ nekik egy munkaállomást, a többi jelen lévő személytől biztonságos távolságra. Ezt egy választható két vagy háromdimenziós térképen is jelzi, a könnyű tájékozódás érdekében. Az egész folyamat néhány gombnyomsással lezajlik. 
Mindezt az adminisztrátorok manuálisan, az otthonuk kényelméből is tudják kezelni. Beállíthatják az irodában tartózkodók létszámát és távolságát is. Joguk van továbbá az alkalmazottak be- és kiléptetésére, illetve bizonyos épületrészek lezárására és feloldására. Ezeket az kézenfekvő eszközöket egy könnyen irányítható webes felületen érhetik el.
## Kinek miért jó ez a szoftver
### Alkalmazottnak:
 - Könnyű kezelés
 - Offline multiplatformos PWA
### Adminisztrátornak:
 - Egyszerű irányítás
 - Teljes átláthatóság
### Statisztikusnak:
 - Funkció gazdag API
### Rendszer tervezőnek:
 - Docker virtualizáció
 - Docker Compose horizontális skálázáshoz
### Szoftver fejlesztőnek:
 - REST 3. szintű api + HATEOAS
 - Swagger dokumentáció
 - Postman dokumentáció
### Szoftver fenntartónak:
 - Clean code elvek
 - Modul, integrációs és end-to-end tesztek
## Projekt elindítása
Az egyszerű telepítéshez és kipróbáláshoz nem kell több, mint egy telepített [docker](https://docs.docker.com/get-docker/) a rendszerünkön.
```
./mvnw package
docker-compose up --build
```
A szoftverek elindulásával egyidejűleg az end-to-end Selenium teszt is le fog futni.
## Dokumentáció
 - [Swagger](http://localhost:8080/swagger-ui.html) *(indítás után)*
 - [Postman](https://documenter.getpostman.com/view/5139955/Szzg9yhv)
## Szolgáltatás elérhetőség
 - Backend  :8080 
 - Frontend :8081 :80 :447
 - Worker-ui :5000
 - Entry-ui :5001
## Külön elismerés:
Máté csapattársunk gépének, aki a verseny ideje alatt villámcsapás által vesztette életét (meg lett villámcsapva).
