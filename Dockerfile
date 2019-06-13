FROM java:8-jre

COPY ./target/GW2-Raid-Bot-1.1-SNAPSHOT.jar /app/
CMD ["java", "-Xmx200m", "-jar", "/app/GW2-Raid-Bot-1.1-SNAPSHOT.jar"]