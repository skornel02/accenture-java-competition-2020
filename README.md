# KIBe 
A KIBe projekt törekszik a jelen járványügyi helyzeben arra, hogy mindenki problémáját a lehető legjobban oldja meg.

## Kibe részei

### Alkalmazotti PWA
Ez egy alacsony hálózati kapcsolatt mellett is jól működő reszponzív, progresszív web alkalmazás, melynek célja hogy az alkalmazottak a lehető legkevesebbet ütközzenek a rendszerrel, és azzal foglalkozhassanak, ami tényleg fontos. *(React, Typescript)*

### Bejelentkeztető rendszer
Egy naiv webprogram, ami az alkalmazottak ki- és bemenetelét kezeli aranyos képek segítségével. *(React, Typescript)*

### Webfelület
Ezen a felületen lehetőség nyílik a rendszer összes funkcióját kezelni, mind az alkalmazottaknak, mind az adminisztrátoroknak. *(Spring, Tymeleaf)*

## Funkciók
A KIBe lehetőséget nyújt az iroda körüli élet hatékony irányítására, kezelésére. Ezt erőteljes alkalmazotti és adminisztrátori eszközökkel éri el. Az alkalmazottak könnyű szerrel kérhetnek hozzáférést az irodához, amely alapján a rendszer sorba állítja őket, és automatikusan megtippeli a várakozási időt. Amint bejutott az alkalmazott az irodába, a KIBe automatikusan választ nekik egy asztalt, a többi jelen lévő személytől biztonságos távolságra. Ezt egy választható 2 vagy 3D-s térképen 

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
git clone https://github.com/skornel02/accenture-java-competition-2020
cd accenture-java-competition-2020
docker-compose up docker-compose.yaml
```
A szoftverek elindulásával egyidejűleg az end-to-end Selenium teszt is le fog futni.

## Dokumentáció
 - [Swagger](http://localhost:8080/swagger-ui.html) *(indítás után)*
 - [Postman](https://documenter.getpostman.com/view/5139955/Szzg9yhv)

## Külön elismerés:
Máté csapattársunk gépének, aki a verseny ideje alatt villámcsapás által vesztette életét (meg lett villámcsapva).