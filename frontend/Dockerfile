
# Frontend image used for `npm run dev` inside Docker.
# Production builds are baked into the backend jar by running
# `npm run build` on the host (output goes into backend/src/main/resources/static/admin).
FROM node:20-bookworm-slim

WORKDIR /workspace

ENV CI=1 \
    NODE_ENV=development \
    HOST=0.0.0.0 \
    PORT=9528 \
    NPM_CONFIG_REGISTRY=https://registry.npmmirror.com

COPY package.json ./
RUN npm install --no-audit --no-fund

COPY . .

EXPOSE 9528

CMD ["npm", "run", "dev"]