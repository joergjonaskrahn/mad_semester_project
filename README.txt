--- Backend ---
- Einfaches Webinterface erreichbar unter: http://localhost:8080/
- Logindaten: email: s@bht.de, pwd: 000000
- Die Anwendung erstellt zu Beginn TODOs, falls keine vorliegen.
	- Die reset-Operation stellt diesen Zustand im laufenden Betrieb wieder her.
- Quellcode: https://github.com/dieschnittstelle/org.dieschnittstelle.mobile.samplewebapi

Operationen auf TODO: https://github.com/dieschnittstelle/org.dieschnittstelle.mobile.samplewebapi/blob/master/src/main/java/org/dieschnittstelle/mobile/samplewebapi/ITodoCRUDOperations.java
- Liest und erhält JSON

- GET: api/todos
- GET: api/todos/{itemId}
- POST: api/todos
- PUT: api/todos/{itemID} (mit TODO als JSON)
- DELETE (Löschen aller TODOs): api/todos
- DELETE: api/todos/{itemId}
- PUT: api/todos/reset

Authentifizierungs-Operationen: https://github.com/dieschnittstelle/org.dieschnittstelle.mobile.samplewebapi/blob/master/src/main/java/org/dieschnittstelle/mobile/samplewebapi/IUserOperations.java
- PUT: api/users/auth (Erhält User) --> boolean
- PUT (Zum Ändern der Credentials): api/users/prepare (Erhält User) --> boolean