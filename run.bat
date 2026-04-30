@echo off
REM Load .env file if it exists
if exist .env (
    for /f "delims== tokens=1,2" %%a in (.env) do (
        if not "%%a"=="" if not "%%a:~0,1%"=="#" (
            set %%a=%%b
        )
    )
)

REM Run Spring Boot application
mvn spring-boot:run
