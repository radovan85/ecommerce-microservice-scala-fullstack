# Stage 1: Build Angular app
FROM node:22 as builder

WORKDIR /app

# Install dependencies
COPY package*.json ./
RUN npm install

# Copy source and build
COPY . .
RUN npx ng build --configuration production --output-path=dist

# Stage 2: Serve with NGINX
FROM nginx:alpine

# Copy Angular build output directly from dist/
COPY --from=builder /app/dist /usr/share/nginx/html

# Copy custom NGINX config
COPY nginx.conf /etc/nginx/nginx.conf

EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]
