const { createProxyMiddleware } = require("http-proxy-middleware");

const proxy = createProxyMiddleware({
    target: "http://localhost:8080",
    changeOrigin: true,
    router: function (req) {
        return {
            protocol: "http:",
            host: "localhost",
            port: 8080,
        };
    },
});

module.exports = function (app) {
    app.use("/rest/*", proxy);
};
