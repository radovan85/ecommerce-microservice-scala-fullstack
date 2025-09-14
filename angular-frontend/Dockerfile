# Stage 1: Build Angular app
FROM node:22 as builder

WORKDIR /app

# Install dependencies
COPY package*.json ./
RUN npm install

# Copy source and build
COPY . .

# Use locally installed Angular CLI to build
RUN npx @angular/cli build --configuration production

# Stage 2: Serve with NGINX
FROM nginx:alpine

# Copy Angular 17 build output from browser folder
COPY --from=builder /app/dist/ecommerce-frontend/browser /usr/share/nginx/html

# Copy custom NGINX config
COPY nginx.conf /etc/nginx/nginx.conf

EXPOSE 8005
CMD ["nginx", "-g", "daemon off;"]
