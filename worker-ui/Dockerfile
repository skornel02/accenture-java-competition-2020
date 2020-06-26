FROM node:12
COPY . /
RUN npm ci
RUN npm run build --release
ENTRYPOINT ["npx", "serve", "-s", "build"]