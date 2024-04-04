Per tal de crear el xat entre el client i el servidor, hem decidit utilitzar dos threads per a cada programa. Un s'encarrega de rebre dades de l'emissor llegir-les i escriure-les 
per pantalla, mentre que l'altre thread s'encarrega d'enviar-les llegint el missatge de l'usuari. El Client i el Servidor utilitzen la mateixa estratègia de threads independents
per tal de separar-se feina. Això és eficient ja que amb els 2 threads/programa ambdós poden enviar i rebre missatges simultàniament.
