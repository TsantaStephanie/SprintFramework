# Framework Tomcat

## Description
Application web Java avec servlet FrontServlet déployée sur Apache Tomcat.

## Structure du projet
```
framework/
├── WEB-INF/
│   ├── web.xml              # Configuration du servlet
│   └── lib/
│       └── framework.jar    # JAR compilé de l'application
├── framework/
│   └── servlet/
│       └── FrontServlet.java # Code source du servlet
├── build/
│   └── classes/             # Classes compilées
├── index.html               # Page d'accueil
└── deploy.bat              # Script de déploiement
```

## Prérequis
- Java JDK 8+
- Apache Tomcat 10.1.34
- JARs d'API Servlet (jakarta.servlet-api_5.0.0.jar, servlet-api.jar)

## Installation et déploiement

### 1. Télécharger les JARs d'API
Téléchargez et placez dans le dossier racine :
- `jakarta.servlet-api_5.0.0.jar`
- `servlet-api.jar`

### 2. Compiler et déployer
```bash
deploy.bat
```

### 3. Accéder à l'application
- URL : http://localhost:8080/framework/
- Test : http://localhost:8080/framework/test

## Fonctionnalités
- Servlet FrontServlet mappé sur `/*`
- Gestion des ressources statiques
- Affichage du chemin demandé si ressource non trouvée
- Page d'accueil index.html

## Configuration
Le servlet est configuré dans `WEB-INF/web.xml` :
- Nom : FrontServlet
- Classe : framework.servlet.FrontServlet
- Pattern : /* (toutes les URLs)