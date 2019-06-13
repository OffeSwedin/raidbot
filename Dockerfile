FROM java:8-jre

COPY ./target/RaidSignUpBOt-1.0.jar /app/
COPY ./.env /app/

RUN echo /app/.env

CMD ["java", "-Xmx200m", "-jar", "/app/RaidSignUpBOt-1.0.jar"]