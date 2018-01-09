const path = require('path'),
    webpack = require('webpack'),
    outputFileName = 'bundle',
    HtmlWebpackPlugin = require('html-webpack-plugin');

const target = path.resolve(__dirname, '..', 'resources', 'public');

const config = {
    context: __dirname,
    entry: "./src/index.ts",
    output: {
        path: target,
        filename: outputFileName + '.js',
    },
    devServer: {
        hot: true,
        inline: true,
        port: 8080
    },
    devtool: '#eval-source-map',
    resolve: {
        extensions: ['.ts', '.js'],
        alias: {
            'vue$': 'vue/dist/vue.js'
        }
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
    config.module.rules = config.module.rules.concat(
        [
            {
                test: /\.ts$/,
                loader: 'babel-loader?presets[]=es2015!ts-loader'
            },
            {
                test: /\.js$/,
                loader: 'babel-loader',
                query: {presets: ['es2015']}
            }
        ]
    );
    config.plugins = config.plugins.concat(
        [
            new webpack.DefinePlugin({
                'process.env': {
                    NODE_ENV: '"production"'
                }
            }),
            new webpack.optimize.UglifyJsPlugin({
                compress: {
                    warnings: false
                }
            }),
            new webpack.optimize.OccurrenceOrderPlugin()
        ]
    );
} else {
    config.module.rules = config.module.rules.concat(
        [
            {
                test: /\.ts$/,
                loader: 'ts-loader'
            }
        ]
    );
}

module.exports = config;