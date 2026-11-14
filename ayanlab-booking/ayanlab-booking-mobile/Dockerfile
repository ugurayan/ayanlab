# Dev/preview only — use `expo start` on host for device builds
FROM node:22-alpine
WORKDIR /app
COPY package.json ./
RUN npm install
COPY . .
EXPOSE 19006
CMD ["npx", "expo", "start", "--web", "--non-interactive"]
