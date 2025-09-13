FROM nginx:alpine

# Angular build output
COPY dist/angular-ecommerce /usr/share/nginx/html

# Custom NGINX config
COPY nginx.conf /etc/nginx/nginx.conf

EXPOSE 80

CMD ["nginx", "-g", "daemon off;"]
