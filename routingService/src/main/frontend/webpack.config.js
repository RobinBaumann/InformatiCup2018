const path = require('path'),
    webpack = require('webpack'),
    outputFileName = 'bundle',
    BundleAnalyzerPlugin = require('webpack-bundle-analyzer').BundleAnalyzerPlugin,
    HtmlWebpackPlugin = require('html-webpack-plugin');

const target = path.resolve(__dirname, '..', 'resources', 'public');

const config = {
    context: __dirname,
    entry: ['babel-polyfill', "./src/index.ts"],
    output: {
        path: target,
        filename: outputFileName + '.js',
    },
    devServer: {
        hot: true,
        inline: true,
        port: 8080
    },
    module: {
        rules: [
            {
                test: /\.html$/,
                loader: 'html-loader'
            },
            {
                test: /\.css$/,
                loader: 'style-loader!css-loader'
            },
            {
                test: /\.(woff2|eot|ttf|woff)$/,
                loader: 'file-loader?name=fonts/[name].[ext]'
            },
            {
                test: /\.vue$/,
                loader: 'vue-loader'
            }
        ]
    },
    plugins: [
        new webpack.HotModuleReplacementPlugin(),
        new HtmlWebpackPlugin({
            inject: true,
            template: './src/index.html'
        })
    ]
};

if (process.env.NODE_ENV === 'production') {
    config.output.filename = outputFileName + '.min.js';
    config.devtool = '#cheap-module-source-map';
    config.module.rules = config.module.rules.concat(
        [
            {
                test: /\.ts$/,
                loader: 'ts-loader'
            }
        ]
    );
    config.plugins = config.plugins.concat(
        [
            new webpack.DefinePlugin({
                'process.env': {
                    NODE_ENV: '"production"'
                },
                HOST: '""'
            }),
            new webpack.optimize.UglifyJsPlugin({
                sourceMap: true,
                compress: {
                    warnings: false
                }
            }),
            new webpack.optimize.OccurrenceOrderPlugin()
        ]
    );
    config.resolve = {
        extensions: ['.ts', '.js', '.vue'],
        alias: {
            'vue$': 'vue/dist/vue.min.js',
        }
    };
} else {
    config.devtool = '#source-map';
    config.module.rules = config.module.rules.concat(
        [
            {
                test: /\.ts$/,
                loader: 'ts-loader'
            }
        ]
    );
    config.plugins = config.plugins.concat(
        [
            new webpack.DefinePlugin({
                HOST: '"http://localhost:4567"'
            }),
            new BundleAnalyzerPlugin()
        ]
    );
    config.resolve = {
        extensions: ['.ts', '.js', '.vue'],
            alias: {
            'vue$': 'vue/dist/vue.js',
        }
    };
}

module.exports = config;