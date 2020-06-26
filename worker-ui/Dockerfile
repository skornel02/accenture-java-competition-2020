FROM node:12
COPY package*.json ./
RUN npm install --no-audit --prefer-offline
COPY . .
RUN npm run build
ENTRYPOINT ["npx", "serve", "-s", "build"]