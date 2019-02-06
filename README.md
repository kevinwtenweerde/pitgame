# pitgame
pit game


- het actieve bord wordt opgehaald
- de pits op het bord worden opgehaald
- de actieve speler wordt opgehaald
- de geselecteerde kuil wordt opgehaald
- er wordt gekeken hoeveel stenen er in de geselecteerde kuil zitten
- de kuil wordt leeg gemaakt
    - movestones
- wanneer de laaste kuil waar een steen in gelegd is geen grote kuil is:
    - capture stones

- het bord wordt opgeslagen in de repo en terug gegeven aan de controller

- het bordt wordt naar de gamestate service gebracht
- de actieve game wordt opgehaald
- er wordt bepaald of de game afgelopen is
- als de gebruiker op een grote pit is gekomen en de game niet afgelopen is dan wordt de game niet aangepast en terug gegeven
- als de game afgelopen is wordt de game state afgerond en terug gegeven
- anders wordt de actieve speler gewijzigd en word de game terug gegeven
