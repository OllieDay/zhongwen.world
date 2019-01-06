FROM python:2.7.15-alpine3.8
VOLUME /certificates
EXPOSE 80
RUN apk add --no-cache --virtual .build-deps linux-headers gcc musl-dev \
	&& apk add --no-cache libffi-dev openssl-dev dialog \
	&& pip install certbot \
	&& apk del .build-deps
COPY crontab /etc/crontabs/
RUN crontab /etc/crontabs/crontab
COPY run.sh /scripts/
RUN chmod +x /scripts/run.sh
ENTRYPOINT ["crond", "-f"]
