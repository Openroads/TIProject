# TIProject
Repository for TI project


How to run React application
1) Download and install Node.js 8.12.0 LTS (Be sure that npm is checked to install)
2) Go to the reactapplication folder in ReactClient directory
3) Execute command in CMD/PowerShell or Bash: npm start



######################################## Wytyczne do projektu   #######################

Idea projektu:
(w skrócie, ponieważ szczegółowo była omawiana na ostatnich zajęciach)

W ramach zajęć będą realizowane elementy rozproszonego i wielowarstwowego systemu wspomagającego edycję plików tekstowych przez Internet z możliwością pracy offline (dotyczy tylko aplikacji natywnej).

Plan realizacji.

lab 1 - Zajęcia wprowadzające. Omówienie jak będą przebiegać zajęcia.

lab 2 - Wykonanie prototypu umożliwiającego wysłanie oraz odbiór przykładowego komunikatu z wykorzystaniem REST API i WebSocket.

lab 3 - Podstawowa funkcjonalność klienta natywnego: pobranie listy plików z serwera, wyświetlenie pliku, edycja pliku i wysłanie zmian na serwer, utworzenie nowego pliku. Komunikacja z serwerem powinna odbywać się za pomocą REST API.

lab 4 - Podstawowa funkcjonalność klienta webowego (implementacja w technologii "aktywnej", czyli bez odświeżania strony): wyświetlanie aktualnej listy plików; tworzenie, wyświetlenie i edycja pliku.

lab 5 - Użycie komunikacji za pomocą protokołu Web Socket do informowania o różnych aktywnościach w systemie typu: użytkownik zablokował (edytuje) plik, pojawił się nowy plik (lub zmieniła się lista plików), wewnętrzny chat pozwalający na komunikację użytkowników między sobą (np. w celu poproszenia o zwolnienie pliku). 

lab 6 - Synchronizacja plików w kliencie natywnym oraz zapewnienie możliwości pracy w trybie offline. Proszę pozwolić również na edycję plików offline i przesłanie go na serwer kiedy pojawi się Internet oraz jeśli na serwerze nikt nie zmienił pliku.

lab 7 - Implementacja dodatkowej funkcjonalności poprawiającej użyteczność systemu lub ergonomię pracy z systemem. Przykład: implementacja powiadomień natywnych (mogą to być również powiadomienia z przeglądarki internetowej). Można również zgłaszać własne propozycje. 

Uwagi:
* Oczekuję od wszystkich zespołów zrealizowania pełnej funkcjonalności wskazanej w punktach lab 3 - lab 6. Za wykonanie tego zakresu prac maksymalna ocena to 4,5. Realizacja lab 7 nie jest obowiązkowa jednak pozwoli na uzyskanie oceny 5,0.
* Pomimo tego, że projekt UI strony i aplikacji nie jest najważniejszy, to powinien być on przygotowany starannie z zachowaniem obowiązujących standardów (np. warto skorzystać z Bootstrapa lub innego frameworka CSSowego do zagwarantowania responsywności).
* Zastrzegam sobie możliwość precyzowania wymagań na kolejnych zajęciach. Wspólnie będziemy mogli dyskutować jak dane zagadnienie zrealizować. Proszę się zatem nie zamykać na opis, który jest powyżej.
* Proszę o terminowe realizowanie kolejnych etapów. Dany etap powinien być najpóźniej zrealizowany do końca kolejnych zajęć. Przykład: zalecane jest, aby do końca 3 zajęć był zrealizowany 3 temat. Jeśli jednak nie będzie skończony to nie będzie z tego powodu żadnych konsekwencji. Problemem jednak będzie to, że ktoś przykładowo na 3 zajęciach nie będzie miał etapu 2. 
* Do zaliczenia projektu niezbędne będzie pokazanie swojej aktywności w repozytorium kodu.
* Informuję również Państwa, że podczas zaliczenia projektu będą mogły pojawić się pytania nie koniecznie z obszaru, który ktoś indywidualnie realizował. Chcę w ten sposób uniknąć sytuacji, że ktoś mimo iż napracuje się w projekcie nie wyjdzie poza zakres pewnych prostych technologi typu html, css, jquery.

Jeśli mają Państwo jakieś pytania lub uwagi proszę je zgłaszać na maila.