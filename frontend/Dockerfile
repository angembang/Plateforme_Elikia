# ===============================
# Étape 1 : Build Angular
# ===============================
FROM node:24-alpine AS build

WORKDIR /app

# Copier les fichiers de dépendances
COPY package*.json ./

# Installer les dépendances
RUN npm install

# Copier le code source
COPY . .

# Build Angular (production)
RUN npm run build

# ===============================
# Étape 2 : Serveur Nginx
# ===============================
FROM nginx:alpine

# Supprimer la configuration par défaut
RUN rm /etc/nginx/conf.d/default.conf

# Ajouter la configuration Nginx
COPY nginx.conf /etc/nginx/conf.d/default.conf

# Copier le build Angular
COPY --from=build /app/dist /usr/share/nginx/html

# Exposer le port
EXPOSE 80

CMD ["nginx", "-g", "daemon off;"]
