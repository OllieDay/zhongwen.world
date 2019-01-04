FROM clojure:openjdk-8-lein-alpine AS build-zhongwen.api
WORKDIR /app
COPY . .
RUN lein uberjar

FROM java:openjdk-8-jre-alpine
EXPOSE 80
WORKDIR /app
COPY --from=build-zhongwen.api /app/target/*-standalone.jar .
COPY --from=build-zhongwen.api /app/cedict_ts.u8 .
ENTRYPOINT [ "java", "-jar", "*-standalone.jar"]
