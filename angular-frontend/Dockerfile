FROM node:24 as builder

WORKDIR /app

COPY package*.json ./
RUN npm install

COPY . .

RUN npx @angular/cli build --configuration production

FROM nginx:alpine

COPY --from=builder /app/dist/ecommerce-frontend/browser /usr/share/nginx/html

COPY nginx.conf /etc/nginx/nginx.conf

EXPOSE 4200
CMD ["nginx", "-g", "daemon off;"]