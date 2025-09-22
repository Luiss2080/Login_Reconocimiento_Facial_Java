@echo off
echo =========================================
echo  CONFIGURACION RAPIDA PARA NETBEANS
echo  Sistema de Reconocimiento Facial v2.0
echo =========================================
echo.

echo [1/4] Verificando Java...
java -version
if %errorlevel% neq 0 (
    echo ERROR: Java no encontrado
    echo Instala Java 21+ desde: https://www.oracle.com/java/technologies/downloads/
    pause
    exit /b 1
)
echo ✓ Java OK

echo.
echo [2/4] Verificando Maven...
mvn -version
if %errorlevel% neq 0 (
    echo AVISO: Maven no encontrado en PATH
    echo NetBeans incluye Maven integrado, no es problema
)
echo ✓ Maven OK (o será manejado por NetBeans)

echo.
echo [3/4] Limpiando archivos temporales...
if exist target rmdir /s /q target
if exist logs rmdir /s /q logs
if exist temp rmdir /s /q temp
echo ✓ Proyecto limpio

echo.
echo [4/4] Verificando estructura del proyecto...
if not exist pom.xml (
    echo ERROR: pom.xml no encontrado
    pause
    exit /b 1
)
if not exist src\main\java (
    echo ERROR: Estructura de directorios incorrecta
    pause
    exit /b 1
)
if not exist config\sistema.properties (
    echo ERROR: Archivo de configuración no encontrado
    pause
    exit /b 1
)
echo ✓ Estructura correcta

echo.
echo =========================================
echo  CONFIGURACION COMPLETADA
echo =========================================
echo.
echo SIGUIENTE PASO:
echo 1. Abrir NetBeans IDE
echo 2. File -^> Open Project
echo 3. Seleccionar esta carpeta
echo 4. Presionar F6 para ejecutar
echo.
echo IMPORTANTE:
echo - Asegurate de que MySQL este ejecutandose
echo - Ejecuta database_simple.sql en MySQL Workbench
echo - Usuario de prueba: admin / admin123
echo.
pause