@echo off
echo Compilation des annotations et du test...

REM Créer les répertoires de build s'ils n'existent pas
if not exist "build\classes" mkdir "build\classes"

REM Compilation des annotations Controller (types @Controller)
echo Compilation des annotations Controller...
javac -d "build\classes" framework\controller\*.java

if errorlevel 1 (
    echo Erreur de compilation des annotations Controller!
    pause
    exit /b 1
)

REM Compilation des utilitaires d'annotations (GetMapping, AnnotationReader, etc.)
echo Compilation des utilitaires d'annotations...
javac -classpath "build\classes" -d "build\classes" framework\annotation\*.java

if errorlevel 1 (
    echo Erreur de compilation des utilitaires d'annotations!
    pause
    exit /b 1
)

REM Compilation des servlets
echo Compilation des servlets...
javac -classpath "jakarta.servlet-api_5.0.0.jar;build\classes" -d "build\classes" framework\servlet\*.java

if errorlevel 1 (
    echo Erreur de compilation des servlets!
    pause
    exit /b 1
)

REM Compilation du test
echo Compilation du test...
javac -classpath "build\classes" -d "build\classes" testFramework\*.java

if errorlevel 1 (
    echo Erreur de compilation du test!
    pause
    exit /b 1
)

echo Compilation réussie!
echo.
echo Pour tester les annotations:
echo java -cp "build\classes" testFramework.TestFramework
echo.
pause
