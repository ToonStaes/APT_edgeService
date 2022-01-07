# README
Dit is de combinedServices repository voor het Advanced Programming Topics-project van Arne Hus, Toon Staes en Niels Verheyen.

Swagger-url: [Swagger](https://edge-service-server-arnehus.cloud.okteto.net/swagger-ui.html#/complete-bestelling-controller)
API base-url: [API base](https://edge-service-server-arnehus.cloud.okteto.net) (Op deze pagina gaat niets te zien zijn.)

Hieronder nog enkele screenshots van de resultaten van een raadpleging van de endpoints:

## Get

### Gerechten
url: [Get gerechten](https://edge-service-server-arnehus.cloud.okteto.net/gerechten)
![Get gerechten](img_readMe/get_Gerechten.png)

### Gerecht
url: [Get gerecht by gerechtNummer](https://edge-service-server-arnehus.cloud.okteto.net/gerechten/20200103PH)
![Get gerecht by gerechtNummer](img_readMe/get_GerechtenByGerechtNummer.png)

### Personeel
url: [Get personeel](https://edge-service-server-arnehus.cloud.okteto.net/personeel)
![Get personeel](img_readMe/get_Personeel.png)

### Personeelslid
url: [Get personeel by personeelsNummer](https://edge-service-server-arnehus.cloud.okteto.net/personeel/K20220103AH)
![Get personeel by personeelsNummer](img_readMe/get_PersoneelByPersoneelNummer.png)

### Keukenpersoneel
url: [Get keukenpersoneel](https://edge-service-server-arnehus.cloud.okteto.net/personeel/functie/keuken)
![Get keukenpersoneel](img_readMe/get_KeukenPersoneel.png)

### Zaalpersoneel
url: [Get zaalpersoneel](https://edge-service-server-arnehus.cloud.okteto.net/personeel/functie/zaal)
![Get zaalpersoneel](img_readMe/get_ZaalPersoneel.png)

### Bestellingen
url: [Get bestellingen](https://edge-service-server-arnehus.cloud.okteto.net/bestellingen)
![Get bestellingen](img_readMe/get_Bestellingen.png)

### Bestelling
url: [Get bestelling by bestelNummer](https://edge-service-server-arnehus.cloud.okteto.net/bestelling/20220107092822AHc363fcac-6be6-4427-a96c-d5ddb90cc96e)
![Get bestelling by bestelNummer](img_readMe/get_BestellingByBestelnummer.png)

## Post/Put
### Bestelling
post: [Post bestelling](https://edge-service-server-arnehus.cloud.okteto.net/bestellingen)
put: [Post bestelling](https://edge-service-server-arnehus.cloud.okteto.net/bestellingen)
Deze acties zijn enkel uit te voeren op de frontend door de CORS-beveiliging die is ingesteld op de edge-service
![Post/put bestelling](img_readMe/post_put_Bestelling.png)

## Delete
### Bestelling
[Delete bestelling](https://edge-service-server-arnehus.cloud.okteto.net/bestellingen/bestelnummer/20220107092822AHc363fcac-6be6-4427-a96c-d5ddb90cc96e)
Ook deze actie kan enkel uitgevoerd worden in de frontend en deze actie geeft niets terug.
