# Les Aventuriers Du Disque Perdu

Un stage par Eddy Briere et Thomas Turti

Le but de ce stage est d'ajouter à la base de donnée existante une application de gestion de prêt des disques durs


Nous devrons donc concecvoir le moteur de l'application, l'interface WEB administrateur et utilisateur puis l'integrer dans un conteneur Linux


# Cahier des charges


  Le cdc contient les objectifs du projets, les fonctionnalités attendues de l'app, les contraintes et les differentes étapes de développement

# UML

  Le fichier addpUML.mdj est consultable et modifiable sur StarUML. Il contient le diagramme de cas d'utilisation, les diagrammes d'activités et le diagramme de classe.

# MCD-MLD

  Le fchier addpMCD.loo est consultable et modifiable sur Looping. Il contient le MCD et le MLD du projet.

# PhotoSearch

  PhotoSearch est un projet de moteur de recherche de photo sur un serveur.
Mon objectifs dans ce projet est de récuperer les données fournies par le script gettrees (voir doc)
Ce script parcourt l'arborescence du serveur photo et produit un fichier XML contenant les données que je dois traiter
  Nous avons fait une première tentative d'écriture de script en python, mais ne connaissant pas bien le langage nous avons décidé
de créer le programme en java.
Ce programme va donc servir a parcourir les fichier xml fournis par le script gettrees, a traiter les données reçues puis à les inscrire
dans une base de données PhotoCatalog (mcd-mld WIP)
  Ces scripts s'integreront ensuite à un UI (déjà créée)
