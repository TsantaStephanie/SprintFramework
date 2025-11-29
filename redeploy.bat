@echo off
echo Nettoyage et redéploiement complet...
echo.

REM Étape 1: Nettoyage
echo 1. Nettoyage des anciens fichiers...
if exist "build\classes" rmdir /s /q "build\classes"
if exist "build\framework.jar" del "build\framework.jar"
if exist "testFramework\WEB-INF\lib\framework.jar" del "testFramework\WEB-INF\lib\framework.jar"

REM Étape 2: Création des répertoires
echo 2. Création des répertoires...
if not exist "build\classes" mkdir "build\classes"
if not exist "testFramework\WEB-INF\lib" mkdir "testFramework\WEB-INF\lib"

REM Étape 3: Compilation du framework
echo 3. Compilation des sources du framework...

REM Compiler les annotations
javac -d "build\classes" framework\annotation\Controller.java framework\annotation\GetMapping.java

REM Compiler les utilitaires
javac -classpath "build\classes" -d "build\classes" framework\utilitaire\MappingInfo.java
javac -classpath "build\classes" -d "build\classes" framework\utilitaire\ConfigLoader.java
javac -classpath "build\classes" -d "build\classes" framework\utilitaire\ClassScanner.java
javac -classpath "build\classes" -d "build\classes" framework\utilitaire\UrlMappingRegistry.java
javac -classpath "build\classes" -d "build\classes" framework\annotation\AnnotationReader.java

REM Compiler ModelView AVANT FrontServlet
javac -classpath "build\classes" -d "build\classes" framework\model\ModelView.java

REM Compiler les controllers
javac -classpath "jakarta.servlet-api_5.0.0.jar;build\classes" -d "build\classes" framework\controller\HomeController.java

REM Compiler les servlets
echo Compilation des servlets...
javac -classpath "jakarta.servlet-api_5.0.0.jar;build\classes" -d "build\classes" framework\servlet\FrontServlet.java
javac -classpath "jakarta.servlet-api_5.0.0.jar;build\classes" -d "build\classes" framework\utilitaire\ResourceFilter.java framework\utilitaire\UrlTestServlet.java

REM Étape 4: Création du JAR
echo 4. Création du JAR...
cd build
jar cvf framework.jar -C classes .
cd ..

REM Étape 5: Copie du JAR
echo 5. Copie du JAR...
copy "build\framework.jar" "testFramework\WEB-INF\lib\"

echo.
echo ✅ Compilation terminée avec succès!
pause