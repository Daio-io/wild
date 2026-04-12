const TerserPlugin = require("terser-webpack-plugin");

config.output = config.output || {};
config.output.publicPath = "auto";

config.optimization = config.optimization || {};
config.optimization.minimize = true;
config.optimization.minimizer = [
    new TerserPlugin({
        terserOptions: {
            mangle: true,
            compress: false,
            output: {
                beautify: false,
            },
        },
    }),
];