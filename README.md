# MultiSite

MultiSite is een Minecraft plugin die een brug vormt tussen je Minecraft server en een webgebaseerd beheerderspaneel. De plugin verzamelt servergegevens zoals spelerstatistieken, serverinstellingen en live status, en stelt deze beschikbaar via een REST API.

## Functies

- **REST API**: Biedt verschillende endpoints voor het ophalen en beheren van servergegevens
- **Spelerstatistieken**: Houdt gedetailleerde statistieken bij van alle spelers
- **Serverstatus**: Real-time monitoring van server performance en status
- **Configuratiebeheer**: Beheer serverinstellingen via de API
- **Webintegratie**: Eenvoudige integratie met een webgebaseerd beheerderspaneel

## Installatie

1. Download het laatste MultiSite.jar bestand uit de [Releases](https://github.com/MilanVos/multisite/releases) sectie
2. Plaats het .jar bestand in de `plugins` map van je Minecraft server
3. Start of herstart je server
4. De plugin zal automatisch de benodigde configuratiebestanden aanmaken

## Configuratie

Na de eerste keer opstarten wordt er een `config.yml` bestand aangemaakt in de `plugins/MultiSite` map. Hier zijn de belangrijkste configuratie-opties:

```yaml
# API Settings
api:
  # Port voor de REST API server
  port: 8080
  # API key voor authenticatie (verander dit naar een veilige waarde!)
  key: "change-me-to-a-secure-key"
  # Inschakelen/uitschakelen van API endpoints
  endpoints:
    status: true
    players: true
    player: true
    stats: true
    config: true
```

**Belangrijk**: Verander de standaard API key naar een veilige, unieke waarde voordat je de plugin in productie gebruikt!

## Commando's

De plugin biedt verschillende commando's voor beheer:

- `/multisite help` - Toont het help menu
- `/multisite reload` - Herlaadt de configuratie
- `/multisite stats` - Toont server statistieken
- `/multisite api` - Toont API status
- `/multisite api restart` - Herstart de API server

## Permissies

- `multisite.admin` - Geeft toegang tot alle MultiSite commando's
- `multisite.admin.reload` - Staat toe om de configuratie te herladen
- `multisite.admin.stats` - Staat toe om server statistieken te bekijken
- `multisite.admin.api` - Staat toe om de API server te beheren

## API Endpoints

De plugin biedt de volgende API endpoints:

### GET /api/status
Geeft de huidige status van de server terug, inclusief aantal online spelers, TPS, etc.

### GET /api/players
Geeft een lijst van alle online spelers met hun basisinformatie.

### GET /api/player?uuid=<uuid> of /api/player?name=<name>
Geeft gedetailleerde informatie over een specifieke speler.

### GET /api/stats
Geeft uitgebreide server statistieken, inclusief geheugengebruik, performance metrics, etc.

### GET /api/config
Geeft de huidige configuratie van de plugin.

### POST /api/config?key=<api_key>
Werkt de configuratie bij. Vereist authenticatie met de API key.

## Webintegratie

De plugin is ontworpen om samen te werken met een webgebaseerd beheerderspaneel. Je kunt je eigen frontend ontwikkelen die communiceert met de API endpoints, of gebruik maken van onze voorbeeldimplementatie.

### Voorbeeld webpaneel setup:

1. Clone de [MultiSite-Web](https://github.com/MilanVos/multisite.git) repository
2. Installeer de vereiste dependencies: `npm install`
3. Configureer de API URL en key in het `.env` bestand
4. Start de webserver: `npm start`
5. Open je browser en ga naar `http://localhost:3000`

## Ontwikkeling

### Vereisten

- Java 8 of hoger
- Maven
- Bukkit/Spigot server (1.16 of hoger)

### Bouwen vanuit broncode

1. Clone de repository: `git clone https://github.com/MilanVos/multisite.git`
2. Navigeer naar de project directory: `cd multisite`
3. Bouw het project met Maven: `mvn clean package`
4. Het gebouwde .jar bestand is te vinden in de `target` map

## Contact

Voor vragen of ondersteuning, neem contact op via:
- GitHub Issues: [https://github.com/MilanVos/multisite/issues](https://github.com/MilanVos/multisite/issues)
- Email: MilanV2005@outlook.com
- Discord: multitime
