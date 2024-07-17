FROM git.phinion.com/phinion/dependency_proxy/containers/node:18-alpine AS build-stage

# Dependencies
COPY ./yarn.lock ./yarn.lock
COPY ./package.json ./package.json
RUN yarn

# Our code
COPY ./src ./src
COPY ./public ./public
COPY ./tsconfig.json ./tsconfig.json
COPY ./.editorconfig ./.editorconfig
COPY ./index.html ./index.html

ARG VITE_REACT_APP_GIT_SHA
ENV VITE_REACT_APP_GIT_SHA=${VITE_REACT_APP_GIT_SHA}

ARG VITE_REACT_APP_VERSION_NUMBER
ENV VITE_REACT_APP_VERSION_NUMBER=${VITE_REACT_APP_VERSION_NUMBER}

RUN yarn build

# This date is behind the yarn build command on purpose as ti would slow down the build a lot
ARG VITE_REACT_APP_DEPLOY_DATE
ENV VITE_REACT_APP_DEPLOY_DATE=${VITE_REACT_APP_DEPLOY_DATE}

RUN echo "{ \
    \"git hash\": \"$VITE_REACT_APP_GIT_SHA\",\
    \"build date\": \"$VITE_REACT_APP_DEPLOY_DATE\"\
    }" > ./dist/version.json

# production stage
FROM nginx:1.24-alpine AS production-stage

COPY --from=build-stage ./dist /usr/share/nginx/html

EXPOSE 80
