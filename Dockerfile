FROM java:8-jre

COPY ./target/RaidSignUpBot-1.0.jar /
COPY ./.env /

CMD ["java", "-Xmx200m", "-jar", "/RaidSignUpBot-1.0.jar"]