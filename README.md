Texas hold'em Botgame
=====================

ToDo:

- Add optimized settings for Netty
- Better sharing of code between client and web-client
- Better handling of connection failures
X Disconnect player after game in room finished
X Implement Tournament room
- Admin gui for starting and inspecting tournaments
- Collect fun statistics
- Create better and more real playing bots for training
X Performance testing


Thursday

X Disconnect clients after table done
X On table start include table id (atomic counter)
X Show Game view
    X GET instead of POST
    X Connect GameLogs with table ID
    X (wont do) Do not publish GameLogs until table is done
    X Ability to change table ID for shown games
    X Store games i map instead (TableID -> List<GameLog>)
X StatisticsCollector
    X List table IDs
    X Get GameLog for choosen tableID
    X Throw away oldest tables (store only 250 tables)
X Fix SLF4J warnings
X Favicon


Mejla regler till Tommy (lista över händer)
Mejla krav för uppgiften (java, maven? IDE med m2eclipse plugin etc)
Router att skaffa
Köp 5st 10m ethernetkablar
Förbered paketering av jarar och webcliektn etc
Kom igång instruktioner
Förebered presentation
Ta med projektor
Fundera på vilka sourcepaket som ska med
Köra maven repo på servern
Köp priser till vinnare
Rensa i requests
Testramverket JS
Serverpaketering web-client (och parametrar startup, local port, server host, server port, webdirectory)
http://stackoverflow.com/questions/3718221/add-resources-to-jetty-programmatically

Ta ut Json exempel på alla anrop och events

MAX_NOOF_PLAYERS=5 per IP (new DeniedConnectionResponse)

CurrentPlayState
    - players (with current chip amount, and investment in pot)
    - chips in pot total
    - community cards
    - my cards
    - game state
    - hasPlayerFolded('emil') or playerEmil.hasFolded()
    - big/small blind amount
    - dealer player, bigblind player, small blind player
    -

Ajax tournament status page (auto update ranklist)

Bättre server-bot spelare med tydligare karaktär.



