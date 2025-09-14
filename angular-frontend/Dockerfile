# Stage 2: Serve with NGINX
FROM nginx:alpine

# Angular 17 outputs to browser folder
COPY --from=builder /app/dist/ecommerce-frontend/browser /usr/share/nginx/html

# OBRIŠI default.conf inače će override-ovati tvoj nginx.conf
RUN rm -f /etc/nginx/conf.d/default.conf

COPY nginx.conf /etc/nginx/nginx.conf

EXPOSE 8005    # Možeš promeniti i ovo na 8005 za dokumentaciju
CMD ["nginx", "-g", "daemon off;"]