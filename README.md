# 🚀 Projet BUT Bank

Un projet universitaire déployé sur **Tomcat** et développé en **Java** avec **IntelliJ IDEA**.

---

## 📋 Prérequis
Avant de commencer, assurez-vous d’avoir installé :
- ☕ **Java JDK 11**
- 🐱 **Apache Tomcat 9**
- 🖥️ **IntelliJ IDEA Ultimate**

---

## ⚙️ Installation et configuration

### 1️⃣ Installer IntelliJ IDEA
👉 [Télécharger IntelliJ IDEA](https://www.jetbrains.com/fr-fr/idea/)
### 2️⃣ Cloner le projet
```bash
git clone <url-du-repo>
```
ou 

Bouton **"Clone repository"** directement sur **Intellij IDEA**
### 3️⃣ Installer le JDK
File -> Project Structure -> SDK : installer **MS11**
### 4️⃣. Compiler le projet en .war
Run -> Edit configurations -> New -> **Maven [Clean install]**

Exécuter **Maven [clean install]**
### 5️⃣ Télécharger TomCat 9.0.10
👉 [Télécharger TomCat 9.0.10](https://tomcat.apache.org/download-90.cgi) 
### 6️⃣ Configurer TomCat et lancer le serveur
Run → Edit Configurations → New → **Tomcat Server (local)**
* Before launch → ajouter Build Artifact → sélectionner **_00_ASBank2023.war**
* Deployment → ajouter Artifact → sélectionner **_00_ASBank2023.war exploded**

Run -> **TomCat**