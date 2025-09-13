# Stage 1: Build Angular app
FROM node:22 as builder

WORKDIR /app
COPY package*.json ./
RUN npm install
COPY . .
RUN npx ng build ecommerce-frontend --configuration production

# Stage 2: Serve with NGINX
FROM nginx:alpine

COPY --from=builder /app/dist/ecommerce-frontend /usr/share/nginx/html
COPY nginx.conf /etc/nginx/nginx.conf

EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]
