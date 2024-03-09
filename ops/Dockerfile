FROM maven:3-openjdk-8 as builder

##根据git项目名称填写
ARG APP_NAME=pp-case-backend
ARG BUILD_JAR=target/pp-case-backend.jar


ENV PROJECT_ROOT=/project

WORKDIR $PROJECT_ROOT

COPY . $PROJECT_ROOT


#RUN mvn clean package  '-Dmaven.test.skip=true'

RUN  mvn clean package -Dmaven.test.skip=true -U \
	 &&  mkdir -pv output/ \
	 &&  cp -v ${BUILD_JAR} output/${APP_NAME}.jar

#FROM openjdk:8-jre

#构建运行镜像
FROM registry.pphr.com/ops/base:java

ENV COMMIT_SHA=CI_COMMIT_ID
ENV COMMIT_BRANCH=CI_BRANCH
ENV COMMIT_TAG=CI_TAG
ENV PROJECT_ROOT=/project
WORKDIR $PROJECT_ROOT

RUN java -version

#COPY --from=builder $PROJECT_ROOT/target/app.jar /app.jar
COPY  --from=builder $PROJECT_ROOT/output  $PROJECT_ROOT

EXPOSE 8080

#COPY ops/entrypoint.sh /entrypoint.sh
#
#ENTRYPOINT  ["/bin/bash","-x","/entrypoint.sh"]
CMD ["java","-jar","pp-case-backend.jar","--spring.profiles.active=dev"]
