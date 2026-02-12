# 基础镜像
FROM maven:3.8-openjdk-17 AS builder

# 作者
LABEL maintainer="zengshagnzi"

# 配置
ENV PARAMS=""

# 时区
ENV TZ=PRC
RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone

# 添加应用
ADD target/product-service-app.jar /product-service.jar

ENTRYPOINT ["sh","-c","java -jar $JAVA_OPTS /product-service.jar $PARAMS"]
#docker build -t zszadmin/product-service:1.0.0 -f ./Dockerfile .