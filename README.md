Texas hold'em Botgame
=====================

ToDo:

- Add optimized settings for Netty
- Better sharing of code between client and web-client
- Better handling of connection failures
X Disconnect player after game in room finished
- Implement Tournament room
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
- Fix SLF4J warnings
X Favicon
