@echo off
echo Compilation du projet Framework...

REM Créer les dossiers nécessaires
if not exist "build\classes" mkdir "build\classes"
if not exist "WEB-INF\lib" mkdir "WEB-INF\lib"

REM Compiler le code Java
echo Compilation de FrontServlet...
javac -cp "servlet-api.jar;jakarta.servlet-api_5.0.0.jar" -d build/classes framework/servlet/FrontServlet.java

if %ERRORLEVEL% neq 0 (
    echo Erreur de compilation !
    pause
    exit /b 1
)

REM Créer le JAR
echo Creation du JAR...
cd build/classes
jar -cf ../framework.jar framework/
cd ../..

REM Copier le JAR dans WEB-INF/lib
echo Copie du JAR...
copy build\framework.jar WEB-INF\lib\framework.jar

echo Deploiement termine !
echo Application accessible sur : http://localhost:8080/framework/
pause
