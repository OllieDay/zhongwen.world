FROM alpine:3.8 AS build-zhongwen.world.nginx
WORKDIR /certificates
RUN apk update && \
	apk add --no-cache openssl && \
	rm -rf /var/cache/apk/*
RUN openssl req -x509 -nodes -days 365 -newkey rsa:2048 -keyout zhongwen.world.key -out zhongwen.world.crt -subj "/C=GB"

FROM nginx:1.15.8-alpine
EXPOSE 443
COPY nginx.conf /etc/nginx/nginx.conf
COPY --from=build-zhongwen.world.nginx /certificates/zhongwen.world.key /certificates/zhongwen.world.crt /etc/nginx/ssl/
