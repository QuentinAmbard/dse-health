'use strict';

var webpack = require('webpack'),
    jsPath  = 'app/assets/javascripts',
    path = require('path'),
    srcPath = path.join(__dirname, 'app/assets/javascripts');

var config = {
    target: 'web',
    entry: {
        app: path.join(srcPath, 'app.jsx')
        //, common: ['react-dom', 'react']
    },
    resolve: {
        alias: {},
        //root: srcPath,
        extensions: ['.js'],
        modules: ['node_modules', jsPath]
    },
    output: {
        path:path.resolve(__dirname, jsPath, 'build'),
        publicPath: '',
        filename: '[name].js'
        //pathInfo: true
    },

    module: {
        rules: [
            {
                test: /\.jsx?$/,
                exclude: /node_modules/,
                loader: 'babel-loader'
            },
            {
                test: /\.css$/,
                use: [ 'style-loader', 'css-loader' ]
            },
            {
                test: /\.woff($|\?)|\.woff2($|\?)|\.ttf($|\?)|\.eot($|\?)|\.svg($|\?)/,
                loader: 'url-loader'
            }]
    },
    plugins: [
        //new webpack.optimize.CommonsChunkPlugin('common', 'common.js'),
        new webpack.optimize.UglifyJsPlugin({
            compress: { warnings: false },
            output: { comments: false }
        }),
        new webpack.NoEmitOnErrorsPlugin()
    ]
};

module.exports = config;