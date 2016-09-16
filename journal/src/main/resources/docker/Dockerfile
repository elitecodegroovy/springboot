FROM java:8
VOLUME /opt/docker/spring-boot
ADD  journal.jar journal.jar
RUN sh -c 'touch /journal.jar'
EXPOSE 8443
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/journal.jar"]