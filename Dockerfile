FROM java:8-jre

COPY ./target/RaidSignupBot-1.0.jar /
COPY ./.env /

CMD ["java", "-Xmx200m", "-jar", "/RaidSignupBot-1.0.jar"]