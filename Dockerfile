FROM node:9.11.1-alpine as build-zhongwen.world.web
WORKDIR /app
COPY ./zhongwen.world.web/package*.json ./
RUN npm install
COPY ./zhongwen.world.web .
RUN npm run build

FROM clojure:openjdk-8-lein-alpine AS build-zhongwen.world.api
WORKDIR /app
COPY ./zhongwen.world.api .
COPY --from=build-zhongwen.world.web /app/dist ./resources/public
RUN lein uberjar

FROM java:openjdk-8-jre-alpine
EXPOSE 80
WORKDIR /app
COPY --from=build-zhongwen.world.api /app/target/*-standalone.jar .
COPY --from=build-zhongwen.world.api /app/cedict_ts.u8 .
ENTRYPOINT ["java", "-jar", "zhongwen-0.1.0-standalone.jar"]
