FROM java:8-jre

COPY ./target/RaidSignUpBOt-1.0.jar /app/
CMD ["java", "-Xmx200m", "-jar", "/app/GW2-Raid-Bot-1.1-SNAPSHOT.jar"]