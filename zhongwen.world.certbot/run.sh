#!/bin/sh

certbot certonly --agree-tos --renew-by-default -n \
	--text --server https://acme-v01.api.letsencrypt.org/directory \
	--email $EMAIL -d zhongwen.world --standalone --preferred-challenges http-01
cp /etc/letsencrypt/live/zhongwen.world/fullchain.pem /certificates/zhongwen.world.pem
cp /etc/letsencrypt/live/zhongwen.world/privkey.pem /certificates/zhongwen.world.key
