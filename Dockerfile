FROM java:8-jre

COPY ./target/RaidSignUpBot-1.0.jar /app/
COPY ./.env /app/

RUN echo /app/.env

CMD ["java", "-Xmx200m", "-jar", "/app/RaidSignUpBot-1.0.jar"]